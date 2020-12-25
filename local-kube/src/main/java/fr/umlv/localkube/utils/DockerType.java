package fr.umlv.localkube.utils;

import fr.umlv.localkube.manager.DockerManager;
import fr.umlv.localkube.model.Application;

import java.io.IOException;

/**
 * Represents a docker such as container, service, ...
 */
public interface DockerType {

    /**
     * Removes the application from docker
     * @param application application to remove
     * @param dockerManager docker manager
     * @throws IOException if docker command fails
     * @throws InterruptedException if docker command fails
     */
    void removeApplication(Application application, DockerManager dockerManager) throws IOException, InterruptedException;

    /**
     * Scale the application on docker
     * @param application application to scale
     * @param numberOfInstance number of instance to run on scale
     * @param dockerManager docker manager
     * @return the new DockerType base on scale needed
     * @throws IOException if docker command fails
     * @throws InterruptedException if docker command fails
     */
    DockerType scaleApplication(Application application, int numberOfInstance, DockerManager dockerManager) throws IOException, InterruptedException;
}
