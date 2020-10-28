package fr.umlv.localkube.services;

import fr.umlv.localkube.model.Application;

import java.util.List;
import java.util.Optional;

public interface ApplicationService {
    List<Application> getAll();
    Optional<Application> findById(int id);
    void save(Application app);
}
