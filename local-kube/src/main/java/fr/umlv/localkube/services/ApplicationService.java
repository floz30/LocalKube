package fr.umlv.localkube.services;

import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.*;

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
        throw new IllegalStateException("application map must contains this port : "+portService);
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
