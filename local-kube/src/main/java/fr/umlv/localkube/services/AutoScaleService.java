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

@Service
//TODO attention au nombre de ligne
public class AutoScaleService {

    private Map<String, Integer> instances = new HashMap<>();
    private final ApplicationService applicationService;
    private final DockerManager dockerManager;

    public AutoScaleService(ApplicationService applicationService, DockerManager dockerManager) {
        this.applicationService = applicationService;
        this.dockerManager = dockerManager;
    }

    /**
     * @param params
     */
    public Map<String, String> update(Map<String, Integer> params) throws IOException, InterruptedException {
        var mapInstances = getNumberOfExistingInstanceByName(params);
        this.instances = params;
        var output = statusOutput(mapInstances);
        dockerManager.startAutoScale();
        scaleAll();
        return output;
    }

    private Map<String, Integer> getNumberOfExistingInstanceByName(Map<String, Integer> params) {
        return createMapInstances(params, s -> {
            var value = instances.getOrDefault(s, 0);
            if (value == 0) {
                if (applicationService.findIdByName(s).isPresent()) {
                    return 1;
                }
            }
            return value;
        });
    }

    private void scaleAll() {
        // TODO à revoir
        instances.forEach((key, value) -> {
            var id = applicationService.findIdByName(key);
            if (id.isPresent()) {
                try {
                    scaleExistingApplication(applicationService.findById(id.getAsInt()).get(), value);
                } catch (IOException | InterruptedException exception) {
                    throw new RuntimeException(exception);
                }
            } else {
                try {
                    scaleNewApplication(key, value);
                } catch (InterruptedException | ExecutionException | IOException | InvalidImageReferenceException | CacheDirectoryCreationException | RegistryException exception) {
                    throw new RuntimeException(exception);
                }
            }
        });
    }

    private void scaleNewApplication(String name, int numberOfInstance) throws RegistryException, InterruptedException, ExecutionException, IOException, CacheDirectoryCreationException, InvalidImageReferenceException {
        applicationService.start(Application.initializeApp(name, applicationService.getNextId()), numberOfInstance);
    }

    private void scaleExistingContainer(Application application, int numberOfInstance) throws IOException, InterruptedException {
        dockerManager.removeContainer(application.getDockerInstance());
        dockerManager.createService(application, numberOfInstance);
        application.setDockerType(Application.DockerType.SERVICE);
    }

    private void scaleExistingService(Application application, int numberOfInstance) throws IOException {
        dockerManager.scaleService(application, numberOfInstance);
    }

    private void scaleExistingApplication(Application application, int numberOfInstance) throws IOException, InterruptedException {
        switch (application.getDockerType()) {
            case CONTAINER -> scaleExistingContainer(application, numberOfInstance);
            case SERVICE -> scaleExistingService(application, numberOfInstance);
            default -> throw new IllegalStateException("Unknown docker type");
        }
    }

    /**
     * @return
     */
    public Map<String, String> status() {
        //TODO à revoir
        var mapInstances = createMapInstances(instances, s -> {
            try {
                return dockerManager.countRunningTasks(applicationService.findById(applicationService.findIdByName(s).getAsInt()).get().getDockerInstance());
            } catch (IOException | InterruptedException exception) {
                throw new RuntimeException(exception);
            }
        });
        return statusOutput(mapInstances);
    }


    /**
     * @return
     */
    public Map<String, Integer> stop() throws IOException, InterruptedException {
        dockerManager.stopAutoScale();
        return instances;
    }

    private Map<String, Integer> createMapInstances(Map<String, Integer> mapInstances, Function<String, Integer> function) {
        return mapInstances.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> function.apply(e.getKey())));
    }

    /**
     * @return
     */
    private Map<String, String> statusOutput(Map<String, Integer> mapInstances) {
        var result = new HashMap<String, String>();
        for (var param : instances.entrySet()) {
            if (!param.getKey().contains(":")) {
                throw new IllegalArgumentException("Wrong format for app, must be <NAME>:<PORT>.");
            }
            var appCount = mapInstances.getOrDefault(param.getKey(), 0);
            switch (param.getValue().compareTo(appCount)) {
                case 0 -> result.put(param.getKey(), "no action");
                case 1 -> result.put(param.getKey(), "need to start " + (param.getValue() - appCount) + " instance(s)");
                case -1 -> result.put(param.getKey(), "need to stop " + (appCount - param.getValue()) + " instance(s)");
                default -> throw new IllegalStateException("issue with compareTo");
            }
        }
        return result;
    }
}
