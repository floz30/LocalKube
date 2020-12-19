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
        var mapInstances = createMapInstances(params,s -> {
            var value =  instances.getOrDefault(s,0);
            if(value==0){
                if (applicationService.findIdByName(s).isPresent()) {
                    return 1;
                }
            }
            return value;
        });


        this.instances = params;
        var output = statusOutput(mapInstances);
        dockerManager.startAutoScale();
        instances.entrySet().forEach(e -> {
            var id = applicationService.findIdByName(e.getKey());
            if (id.isPresent()) {
                var application = applicationService.findById(id.getAsInt());
                switch (application.get().getDockerType()) {
                    case CONTAINER -> {
                        try {
                            dockerManager.removeContainer(application.get().getDockerInstance());
                            dockerManager.createService(application.get(), e.getValue());
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        application.get().setDockerType(Application.DockerType.SERVICE);
                    }
                    case SERVICE -> {
                        try {
                            dockerManager.scaleService(application.get(), e.getValue());
                        } catch (IOException ioException) {
                            throw new RuntimeException(ioException);
                        }
                    }
                    default -> throw new IllegalStateException("Unkown docker type");
                }
            } else {
                try {
                    applicationService.start(Application.initializeApp(e.getKey(), applicationService.getNextId()), e.getValue());
                } catch (InterruptedException | ExecutionException | IOException | InvalidImageReferenceException | CacheDirectoryCreationException | RegistryException exception) {
                    throw new RuntimeException(exception);
                }
            }
        });
        return output;
    }

    /**
     * @return
     */
    public Map<String, String> status() {
        var mapInstances = createMapInstances(instances,s -> {
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

    private Map<String, Integer> createMapInstances(Map<String,Integer> mapInstances,Function<String, Integer> function) {
        return mapInstances.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> function.apply(e.getKey())));
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
        // TODO docker operation
        return result;
    }
}
