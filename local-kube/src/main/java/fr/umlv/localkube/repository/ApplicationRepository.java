package fr.umlv.localkube.repository;

import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.services.ApplicationService;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ApplicationRepository implements ApplicationService {
    private List<Application> apps = new ArrayList<>();

    public List<Application> getAll() {
        return apps;
    }

    @Override
    public Optional<Application> findById(long id) {
        return apps.stream().filter(a -> a.getId() == id).findFirst();
    }

    public void save(Application app) {
        apps.add(app);
    }
}
