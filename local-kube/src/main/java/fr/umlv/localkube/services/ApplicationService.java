package fr.umlv.localkube.services;

import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.model.ApplicationRecord;
import fr.umlv.localkube.model.ApplicationStartRecord;

import java.util.List;
import java.util.Optional;

public interface ApplicationService {
    List<ApplicationRecord> getAll();
    Optional<Application> findById(int id);
    ApplicationStartRecord save(ApplicationStartRecord app);
}
