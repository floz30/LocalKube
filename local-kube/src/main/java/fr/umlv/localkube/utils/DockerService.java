package fr.umlv.localkube.utils;

import fr.umlv.localkube.manager.DockerManager;
import fr.umlv.localkube.model.Application;

import java.io.IOException;

public class DockerService implements DockerType{

    @Override
    public void removeApplication(Application application, DockerManager dockerManager) throws IOException, InterruptedException {
        dockerManager.removeService(application.getDockerInstance());
    }

    @Override
    public DockerType scaleApplication(Application application, int numberOfInstance, DockerManager dockerManager) throws IOException {
        dockerManager.scaleService(application, numberOfInstance);
        return this;
    }

}
