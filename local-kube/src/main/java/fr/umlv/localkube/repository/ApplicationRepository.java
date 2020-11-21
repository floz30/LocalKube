package fr.umlv.localkube.repository;

import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.model.ApplicationDataRecord;
import fr.umlv.localkube.model.ApplicationRecord;
import fr.umlv.localkube.services.ApplicationService;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ApplicationRepository implements ApplicationService {
    private final Map<Integer, Application> apps = new HashMap<>();

    @Override
    public List<ApplicationRecord> getAll() {
        return apps.values().stream().map(Application::toApplicationRecord).collect(Collectors.toList());
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
    public ApplicationDataRecord save(Application app) {
        Objects.requireNonNull(app);
        apps.put(app.getId(), app);
        return app.toApplicationStartRecord();
    }

    @Override
    public List<Application> applicationList(){
        return apps.values().stream().collect(Collectors.toList());
    }

    @Override
    public void remove(Application app) {
        apps.remove(app.getId());
    }

    public int size() {
        return apps.size();
    }

    @Override
    public String toString() {
        return apps.toString();
    }
}
