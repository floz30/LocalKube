package fr.umlv.localkube.services;

import fr.umlv.localkube.model.Application;

import java.util.List;
import java.util.Optional;

public interface ApplicationService {
    /**
     * Return a list of all applications launched.
     * @return a list of Application
     */
    List<Application> getAll();

    Optional<Application> findById(int id);

    int findAppIdByPortService(int portService);

    Application save(Application app);

    /**
     * Delete application if exists.
     * @param app application to delete
     */
    void remove(Application app);
}
