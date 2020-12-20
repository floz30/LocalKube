package fr.umlv.localkube.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.cloud.tools.jib.api.CacheDirectoryCreationException;
import com.google.cloud.tools.jib.api.InvalidImageReferenceException;
import com.google.cloud.tools.jib.api.RegistryException;
import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.services.ApplicationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@RestController
@Validated
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * Contains data from JSON when we want start a new application.
     */
    record StartApplicationData(@JsonProperty("app") String app) {
        StartApplicationData {
            Objects.requireNonNull(app);
        }
    }

    /**
     * Contains data from JSON when we want stop an application.
     */
    private record StopApplicationData(@JsonProperty("id") int id) {
    }

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * Create and start a new application.
     *
     * @param data the name for the new application
     * @return the new application
     * @throws IOException                     if an I/O exception occurs
     * @throws InvalidImageReferenceException  when attempting to parse an invalid image reference
     * @throws InterruptedException            if the execution was interrupted
     * @throws ExecutionException              if some other exception occurred during execution
     * @throws RegistryException               if some other error occurred while interacting with a registry
     * @throws CacheDirectoryCreationException if a directory to be used for the cache could not be created
     */
    @JsonView(Application.View.OnStart.class)
    @PostMapping(path = "/app/start")
    public Application start( @RequestBody StartApplicationData data) throws IOException, InterruptedException, ExecutionException, RegistryException, CacheDirectoryCreationException, InvalidImageReferenceException {
        var application = Application.initializeApp(data.app(), applicationService.getNextId());
        return applicationService.start(application,1);
    }

    /**
     * Read - Get all applications started and still alive.
     *
     * @return a list of {@code Application} running
     */
    @JsonView(Application.View.OnListAndStop.class)
    @GetMapping("/app/list")
    public List<Application> list() {
        return applicationService.listLaunchedApplications();
    }

    /**
     * Update - Stop an application.
     *
     * @param data ID of the application to stop
     * @return the application stopped
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the execution was interrupted
     */
    @JsonView(Application.View.OnListAndStop.class)
    @PostMapping(path = "/app/stop")
    public Application stop(@RequestBody StopApplicationData data) throws IOException, InterruptedException {
        var application = applicationService.findById(data.id()).orElseThrow();
        return applicationService.stop(application);
    }

    /**
     * Shutdown all application still alive when LocalKube is stopped.
     *
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the execution was interrupted
     */
    @PreDestroy
    public void onShutdown() throws IOException, InterruptedException {
        applicationService.stopAll();
    }
}
