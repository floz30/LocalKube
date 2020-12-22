package fr.umlv.localkube.utils;

import fr.umlv.localkube.manager.DockerManager;
import fr.umlv.localkube.model.Application;

import java.io.IOException;

public interface DockerType {

    void removeApplication(Application application, DockerManager dockerManager) throws IOException, InterruptedException;

    DockerType scaleApplication(Application application, int numberOfInstance, DockerManager dockerManager) throws IOException, InterruptedException;
}
