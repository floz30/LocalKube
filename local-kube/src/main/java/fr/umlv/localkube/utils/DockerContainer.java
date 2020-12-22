package fr.umlv.localkube.utils;

import fr.umlv.localkube.manager.DockerManager;
import fr.umlv.localkube.model.Application;

import java.io.IOException;

public class DockerContainer implements DockerType {

    @Override
    public void removeApplication(Application application, DockerManager dockerManager) throws IOException, InterruptedException {
        dockerManager.removeContainer(application.getDockerInstance());
    }

    @Override
    public DockerType scaleApplication(Application application, int numberOfInstance, DockerManager dockerManager) throws IOException, InterruptedException {
        dockerManager.removeContainer(application.getDockerInstance());
        dockerManager.createService(application, numberOfInstance);
        return new DockerService();
    }
}
