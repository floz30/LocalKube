package fr.umlv.localkube.services;

import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.model.ApplicationDataRecord;
import fr.umlv.localkube.model.ApplicationRecord;

import java.util.List;
import java.util.Optional;

public interface ApplicationService {
    List<ApplicationRecord> getAll();

    Optional<Application> findById(int id);

    int findAppIdByPortService(int portService);

    List<Application> applicationList();

    ApplicationDataRecord save(Application app);

    void remove(Application app);
}
