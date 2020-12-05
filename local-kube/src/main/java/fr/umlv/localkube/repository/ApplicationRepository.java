package fr.umlv.localkube.repository;

import fr.umlv.localkube.model.Application;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository {
    /**
     * Return a list of all applications launched.
     * @return a list of Application
     */
    List<Application> findAll();

    /**
     * Retrieves an application by its id.
     * @param id id to search
     * @return the entity with the specified id or Optional.empty() if none found
     */
    Optional<Application> findById(int id);

    int findAppIdByPortService(int portService);

    /**
     * Saves a given application.
     * @param app application to save
     * @return the saved application
     * @throws NullPointerException in case the given application is null
     */
    Application save(Application app);

    /**
     * Delete a given application.
     * @param app application to delete
     * @throws NullPointerException in case the given application is null
     */
    void delete(Application app);

    void removeAllByDockerInstanceName(String[] names);
}
