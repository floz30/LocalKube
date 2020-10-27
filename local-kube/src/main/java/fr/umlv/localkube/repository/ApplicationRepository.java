package fr.umlv.localkube.repository;

import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.services.ApplicationService;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ApplicationRepository implements ApplicationService {
    private Map<Long, Application> apps = new HashMap<>();
    @Override
    public List<Application> getAll() {
        return new ArrayList<>(apps.values());
    }

    @Override
    public Optional<Application> findById(long id) {
        return Optional.of(apps.get(id));
    }

    @Override
    public void save(Application app) {
        apps.put(app.getId(), app);
    }

    public void remove(Application app) {
        apps.remove(app.getId());
    }

    public long getMaxId() {
        return apps.keySet().stream().mapToLong(a -> a).max().orElse(0);
    }
}
