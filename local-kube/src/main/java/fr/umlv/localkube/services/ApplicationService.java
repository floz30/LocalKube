package fr.umlv.localkube.services;

import com.google.cloud.tools.jib.api.CacheDirectoryCreationException;
import com.google.cloud.tools.jib.api.InvalidImageReferenceException;
import com.google.cloud.tools.jib.api.RegistryException;
import fr.umlv.localkube.configuration.DockerProperties;
import fr.umlv.localkube.configuration.LocalKubeConfiguration;
import fr.umlv.localkube.manager.DockerManager;
import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.utils.OperatingSystem;
import org.apache.catalina.LifecycleException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ApplicationService {
    private final Map<Integer, Application> apps = new HashMap<>();
    private final DockerManager dockerManager;
    private final LocalKubeConfiguration configuration;

    public ApplicationService(@Lazy LocalKubeConfiguration configuration, DockerProperties dockerProperties){
        this.dockerManager = new DockerManager(OperatingSystem.checkOS(),dockerProperties);
        this.configuration = configuration;
    }

    /**
     * Saves a given application.
     * @param app application to save
     * @return the saved application
     * @throws NullPointerException in case the given application is null
     */
    public Application start(Application app) throws InterruptedException, ExecutionException, IOException, InvalidImageReferenceException, CacheDirectoryCreationException, RegistryException {
        Objects.requireNonNull(app);
        dockerManager.startContainer(app);
        configuration.addServicePort(app.getPortService());
        apps.put(app.getId(), app);
        return app;
    }

    /**
     * Deletes a given application.
     * @param app application to delete
     * @throws NullPointerException in case the given application is null
     */
    public Application stop(Application app) throws IOException, InterruptedException, LifecycleException {
        Objects.requireNonNull(app);
        dockerManager.stopContainer(app);
        configuration.removeServicePort(app.getPortService());
        app.kill();
        return app;
    }

    /**
     * Returns a list of all applications launched.
     * @return a list of Application
     */
    public List<Application> list() {
        return apps.values().stream().filter(application -> application.isAlive()).collect(Collectors.toList());
    }

    /**
     * Returns the application id whose {@code portService} matches to the specified one.
     * @param portService service port to find
     * @return the application id
     * @throws IllegalStateException if no application has the specified service port
     */
    public int findAppIdByPortService(int portService) {
        for(var app : apps.values()){
            if(app.getPortService() == portService){
                return app.getId();
            }
        }
        throw new IllegalStateException("Application map must contains this port : "+portService);
    }

    /**
     * Returns the application whose id matches to the specified one.
     * @param id id to search
     * @return the entity with the specified id or Optional.empty() if none found
     */
    public Optional<Application> findById(int id) {
        return Optional.ofNullable(apps.get(id));
    }

    /**
     * Returns the application id whose docker_instance matches to the specified one.
     * @param instance docker instance
     * @return the application id
     */
    public OptionalInt findIdByDockerInstance(String instance) {
        return apps.entrySet().stream()
                .filter(a -> a.getValue().getDockerInstance().equals(instance))
                .mapToInt(Map.Entry::getKey)
                .findFirst();
    }

    /**
     * Returns the application id whose name matches to the specified one.
     * @param name application name
     * @return the application id
     */
    public OptionalInt findIdByName(String name) {
        return apps.entrySet().stream()
                .filter(a -> a.getValue().getApp().equals(name))
                .mapToInt(Map.Entry::getKey)
                .findFirst();
    }

    /**
     * Returns the next usable identifier.
     * @return max id + 1
     */
    public int getNextId() {
        return apps.keySet().stream().mapToInt(x -> x).max().orElse(0) + 1;
    }

    /**
     * Removes all applications that have their dockerInstance in array.
     */
    public void removeAllDeadDockerInstance() throws IOException, InterruptedException {
        var instances = dockerManager.listDeadContainers();
        for (String instance : instances){
            apps.values().stream()
                    .filter(application -> application.getDockerInstance().equals(instance))
                    .findFirst()
                    .ifPresent(application -> apps.remove(application.getId()));
        }
        dockerManager.removeAll(instances);
    }

    public void stopAll() throws IOException, InterruptedException {
        for (var application : list()) {
            dockerManager.stopContainer(application);
        }
    }

    /**
     * Returns the number of applications launched.
     * @return the number of applications launched
     */
    public int size() {
        return apps.size();
    }

    @Override
    public String toString() {
        return apps.toString();
    }

}
