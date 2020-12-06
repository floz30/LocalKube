package fr.umlv.localkube.services;

import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApplicationService implements ApplicationRepository {
    private final Map<Integer, Application> apps = new HashMap<>();

    @Override
    public List<Application> findAll() {
        return new ArrayList<>(apps.values());
    }

    @Override
    public Optional<Application> findById(int id) {
        return Optional.ofNullable(apps.get(id));
    }

    @Override
    public int findAppIdByPortService(int portService) {
        for(var app : apps.values()){
            if(app.getPortService() == portService){
                return app.getId();
            }
        }
        throw new IllegalStateException("Application map must contains this port : "+portService);
    }

    public OptionalInt findIdByName(String name) {
        return apps.entrySet().stream().filter(a -> a.getValue().getName().equals(name)).mapToInt(Map.Entry::getKey).findFirst();
    }

    public OptionalInt findIdByDockerInstance(String instance) {
        return apps.entrySet().stream().filter(a -> a.getValue().getDockerInstance().equals(instance)).mapToInt(Map.Entry::getKey).findFirst();
    }

    @Override
    public Application save(Application app) {
        Objects.requireNonNull(app);
        apps.put(app.getId(), app);
        return app;
    }

    @Override
    public void delete(Application app) {
        Objects.requireNonNull(app);
        apps.remove(app.getId());
    }

    @Override
    public void removeAllByDockerInstanceName(String[] names) {
        for(String name : names){
            apps.values().stream().filter(application -> application.getDockerInstance().equals(name)).findFirst().ifPresent(application -> apps.remove(application.getId()));
        }
    }

    public int size() {
        return apps.size();
    }

    @Override
    public String toString() {
        return apps.toString();
    }

    public int getNextId() {
        return apps.keySet().stream().mapToInt(x -> x).max().orElse(0) + 1;
    }
}
