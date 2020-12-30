package fr.umlv.localkube.services;

import com.google.cloud.tools.jib.api.CacheDirectoryCreationException;
import com.google.cloud.tools.jib.api.InvalidImageReferenceException;
import com.google.cloud.tools.jib.api.RegistryException;
import fr.umlv.localkube.manager.DockerManager;
import fr.umlv.localkube.model.Application;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service class for the auto-scale.
 */
@Service
public class AutoScaleService {

    private final ApplicationService applicationService;
    private final DockerManager dockerManager;
    /**
     * For each application name -> the number of instances to run.
     */
    private Map<String, Integer> instances = new HashMap<>();

    public AutoScaleService(ApplicationService applicationService, DockerManager dockerManager) {
        this.applicationService = applicationService;
        this.dockerManager = dockerManager;
    }


    /* Public methods */


    /**
     * Removes an application from the auto-scale service.
     *
     * @param name application name.
     */
    public void remove(String name) {
        instances.remove(name);
    }

    /**
     * Returns the actions left to do to respect the scaling asked.
     *
     * @return actions to do.
     */
    public Map<String, String> status() {
        var actualInstances = createMapInstances(instances, s -> {
            try {
                return dockerManager.countRunningTasks(applicationService.findById(applicationService.findIdByName(s).getAsInt()).get().getDockerInstance());
            } catch (IOException | InterruptedException exception) {
                throw new RuntimeException(exception);
            }
        });
        return statusOutput(actualInstances);
    }

    /**
     * Stops the scaling service.
     *
     * @return instances that were managed by the auto-scale service.
     * @throws IOException if docker command fails
     * @throws InterruptedException if docker command fails
     */
    public Map<String, Integer> stop() throws IOException, InterruptedException {
        dockerManager.stopAutoScale();
        return instances;
    }

    /**
     * Start the auto-scale service. Update the service if it was already running.
     *
     * @param params applications to scale.
     * @return actions to do for the scale asked.
     * @throws IOException          if docker command fail.
     * @throws InterruptedException if docker command fail.
     */
    public Map<String, String> update(Map<String, Integer> params) throws IOException, InterruptedException {
        var actualInstances = instances;
        checkPositiveNumberInstances(params);
        this.instances = params;
        var output = statusOutput(actualInstances);
        dockerManager.startAutoScale();
        scaleAll();
        return output;
    }


    /* Private methods */


    private void checkPositiveNumberInstances(Map<String, Integer> data){
        if(data.values().stream().anyMatch(i -> i<0)){
            throw new IllegalArgumentException("Cannot scale an application with negative number of instances");
        }
    }

    /**
     * Checks if application name has a correct format.
     *
     * @param name the application name
     */
    private void checkName(String name) {
        if (!name.matches(".*:[0-9]*")) {
            throw new IllegalArgumentException("Wrong format for app name, must be <NAME>:<PORT>.");
        }
    }

    private Map<String, Integer> createMapInstances(Map<String, Integer> mapInstances, Function<String, Integer> function) {
        return mapInstances.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> function.apply(e.getKey())));
    }

    private void fillActionsMap(Map<String, Integer> actualInstances, Map<String, String> actions, String name, Integer instancesToCreate) {
        var appCount = getNumberOfInstances(actualInstances, name);
        switch (instancesToCreate.compareTo(appCount)) {
            case 0 -> actions.put(name, "no action");
            case 1 -> actions.put(name, "need to start " + (instancesToCreate - appCount) + " instance(s)");
            case -1 -> actions.put(name, "need to stop " + (appCount - instancesToCreate) + " instance(s)");
            default -> throw new IllegalStateException("issue with compareTo");
        }
    }


    private int getNumberOfInstances(Map<String, Integer> actualInstances, String name) {
        var value = actualInstances.getOrDefault(name, 0);
        if (value == 0) {
            if (applicationService.findIdByName(name).isPresent()) {
                return 1;
            }
        }
        return value;
    }

    private void scaleAll() {
        instances.forEach((key, value) -> applicationService.findIdByName(key).ifPresentOrElse(
                id -> scaleExistingApplication(applicationService.findById(id).get(), value),
                () -> scaleNewApplication(key, value)));
    }

    private void scaleExistingApplication(Application application, int numberOfInstance) {
        try {
            application.scaleApplication(numberOfInstance, dockerManager);
        } catch (IOException | InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void scaleNewApplication(String name, int numberOfInstance) {
        try {
            applicationService.start(Application.initializeApp(name, applicationService.getNextId()), numberOfInstance);
        } catch (InterruptedException | ExecutionException | IOException | InvalidImageReferenceException | CacheDirectoryCreationException | RegistryException exception) {
            throw new RuntimeException(exception);
        }
    }

    private Map<String, String> statusOutput(Map<String, Integer> actualInstances) {
        var actions = new HashMap<String, String>();
        instances.forEach((key, value) -> {
            checkName(key);
            fillActionsMap(actualInstances, actions, key, value);
        });
        return actions;
    }

}
