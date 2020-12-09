package fr.umlv.localkube.services;

import fr.umlv.localkube.model.Application;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApplicationService {
    private final Map<Integer, Application> apps = new HashMap<>();

    /**
     * Deletes a given application.
     * @param app application to delete
     * @throws NullPointerException in case the given application is null
     */
    public void delete(Application app) {
        Objects.requireNonNull(app);
        app.kill();
    }

    /**
     * Returns a list of all applications launched.
     * @return a list of Application
     */
    public List<Application> findAll() {
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
     * @param instances array of dockerInstance
     */
    public void removeAllByDockerInstanceName(String[] instances) {
        for (String instance : instances){
            apps.values().stream()
                    .filter(application -> application.getDockerInstance().equals(instance))
                    .findFirst()
                    .ifPresent(application -> apps.remove(application.getId()));
        }
    }

    /**
     * Saves a given application.
     * @param app application to save
     * @return the saved application
     * @throws NullPointerException in case the given application is null
     */
    public Application save(Application app) {
        Objects.requireNonNull(app);
        apps.put(app.getId(), app);
        return app;
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
