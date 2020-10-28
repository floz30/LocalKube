package fr.umlv.localkube.repository;

import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.services.ApplicationService;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ApplicationRepository implements ApplicationService {
    private final Map<Integer, Application> apps = new HashMap<>();

    @Override
    public List<Application> getAll() {
        var list = new ArrayList<>(apps.values());
        list.forEach(Application::setElapsedTime);
        return list;
    }

    @Override
    public Optional<Application> findById(int id) {
        return Optional.ofNullable(apps.get(id));
    }

    @Override
    public void save(Application app) {
        apps.put(app.getId(), app);
    }

    public void remove(Application app) {
        apps.remove(app.getId());
    }

    public int getMaxId() {
        return apps.keySet().stream().mapToInt(a -> a).max().orElse(0);
    }
}
