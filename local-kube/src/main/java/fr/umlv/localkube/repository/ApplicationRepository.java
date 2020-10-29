package fr.umlv.localkube.repository;

import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.model.ApplicationRecord;
import fr.umlv.localkube.model.ApplicationDataRecord;
import fr.umlv.localkube.services.ApplicationService;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ApplicationRepository implements ApplicationService {
    private final Map<Integer, Application> apps = new HashMap<>();

    @Override
    public List<ApplicationRecord> getAll() {
        return apps.values().stream().map(app -> app.toApplicationRecord()).collect(Collectors.toList());
    }

    @Override
    public Optional<Application> findById(int id) {
        return Optional.ofNullable(apps.get(id));
    }

    @Override
    public ApplicationDataRecord save(Application app) {
        apps.put(app.getId(),app);
        return app.toApplicationStartRecord();
    }

    public void remove(Application app) {
        apps.remove(app.getId());
    }

    public int getMaxId() {
        return apps.keySet().stream().mapToInt(a -> a).max().orElse(0);
    }
}
