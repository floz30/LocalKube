package fr.umlv.localkube.services;

import com.google.cloud.tools.jib.api.CacheDirectoryCreationException;
import com.google.cloud.tools.jib.api.InvalidImageReferenceException;
import com.google.cloud.tools.jib.api.RegistryException;
import fr.umlv.localkube.configuration.LocalKubeConfiguration;
import fr.umlv.localkube.manager.DockerManager;
import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.repository.ApplicationRepository;
import fr.umlv.localkube.utils.DockerContainer;
import fr.umlv.localkube.utils.DockerService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ApplicationService {
    private final DockerManager dockerManager;
    private final LocalKubeConfiguration configuration;
    private final ApplicationRepository applicationRepository;
    private final AutoScaleService autoScaleService;

    public ApplicationService(DockerManager dockerManager,LocalKubeConfiguration configuration, ApplicationRepository applicationRepository,@Lazy AutoScaleService autoScaleService) {
        this.dockerManager = dockerManager;
        this.configuration = configuration;
        this.applicationRepository = applicationRepository;
        this.autoScaleService = autoScaleService;
    }

    /**
     * Start a given application.
     *
     * @param app the application to start
     * @return the launched application
     * @throws IOException                     if an I/O exception occurs
     * @throws InvalidImageReferenceException  when attempting to parse an invalid image reference
     * @throws InterruptedException            if the execution was interrupted
     * @throws ExecutionException              if some other exception occurred during execution
     * @throws RegistryException               if some other error occurred while interacting with a registry
     * @throws CacheDirectoryCreationException if a directory to be used for the cache could not be created
     * @see #dockerManager
     * @see #configuration
     */
    public Application start(Application app,int numberOfInstance) throws InterruptedException, ExecutionException, IOException, InvalidImageReferenceException, CacheDirectoryCreationException, RegistryException {
        Objects.requireNonNull(app);
        if (numberOfInstance == 1){
            dockerManager.startContainer(app);
            app.setDockerType(new DockerContainer());
        } else {
            dockerManager.createService(app,numberOfInstance);
            app.setDockerType(new DockerService());
        }
        applicationRepository.save(app);
        configuration.addServicePort(app.getPortService());
        return app;
    }

    /**
     * Shutdown a given application.
     *
     * @param app application to stop
     * @return the stopped application
     * @throws NullPointerException in case the given application is null
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the execution was interrupted
     * @see #dockerManager
     * @see #configuration
     */
    public Application stop(Application app) throws IOException, InterruptedException {
        Objects.requireNonNull(app);
        app.removeApplication(dockerManager);
        autoScaleService.remove(app.getApp());
        configuration.removeServicePort(app.getPortService());
        return app;
    }

    /**
     * Returns a list of all applications launched and still alive.
     *
     * @return a list of Application
     */
    public List<Application> listLaunchedApplications() {
        return applicationRepository.applicationList().stream()
                .filter(Application::isAlive)
                .collect(Collectors.toList());
    }

    /**
     * Returns the application whose id matches to the specified one.
     *
     * @param id    id to search
     * @return      the entity with the specified id or Optional.empty() if none found
     */
    public Optional<Application> findById(int id) {
        return applicationRepository.findById(id);
    }

    /**
     * Returns the application id whose {@code portService} matches to the specified one.
     *
     * @param portService               service port to find
     * @return                          the application id
     * @throws IllegalStateException    if no application has the specified service port
     */
    public int findIdByPortService(int portService) {
        return applicationRepository.findId(entry -> entry.getValue().getPortService() == portService)
                .orElseThrow(() -> { throw new IllegalStateException("Application map must contains this port : " +portService); });
    }

    /**
     * Returns the application id whose {@code docker_instance} matches to the specified one.
     *
     * @param instance  docker instance
     * @return          the application id
     */
    public OptionalInt findIdByDockerInstance(String instance) {
        return applicationRepository.findId(entry -> entry.getValue().getDockerInstance().equals(instance));
    }

    /**
     * Returns the application id whose {@code name} matches to the specified one.
     *
     * @param name  application name
     * @return      the application id
     */
    public OptionalInt findIdByName(String name) {
        return applicationRepository.findId(entry -> entry.getValue().getApp().equals(name));
    }

    /**
     * Returns the next usable identifier.
     *
     * @return max id + 1
     */
    public int getNextId() {
        return applicationRepository.getMaxId() + 1;
    }


    /**
     * Removes all applications that have their dockerInstance in array.
     *
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the execution was interrupted
     */
    public void removeAllDeadDockerInstance() throws IOException, InterruptedException {
        var instances = dockerManager.listDeadContainers();
        for (String instance : instances){
            applicationRepository.removeAllDeadDockerInstance(instance);
        }
        dockerManager.removeAll(instances);
    }

    /**
     * Shutdown all applications still alive.
     *
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the execution was interrupted
     */
    public void stopAll() throws IOException, InterruptedException {
        for (var application : listLaunchedApplications()) {
            dockerManager.stopContainer(application);
        }
    }

}
