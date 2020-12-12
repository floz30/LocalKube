package fr.umlv.localkube.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.cloud.tools.jib.api.CacheDirectoryCreationException;
import com.google.cloud.tools.jib.api.InvalidImageReferenceException;
import com.google.cloud.tools.jib.api.RegistryException;
import fr.umlv.localkube.configuration.DockerProperties;
import fr.umlv.localkube.configuration.LocalKubeConfiguration;
import fr.umlv.localkube.manager.DockerManager;
import fr.umlv.localkube.model.*;
import fr.umlv.localkube.services.ApplicationService;
import fr.umlv.localkube.utils.OperatingSystem;
import org.apache.catalina.LifecycleException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@RestController
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

    @JsonView(Application.View.OnStart.class)
    @PostMapping(path = "/app/start")
    public Application start(@RequestBody StartApplicationData data) throws IOException, InterruptedException, ExecutionException, RegistryException, CacheDirectoryCreationException, InvalidImageReferenceException {
        var application = Application.initializeApp(data.app(), applicationService.getNextId());
        return applicationService.start(application);
    }

    @JsonView(Application.View.OnListAndStop.class)
    @GetMapping("/app/list")
    public List<Application> list() {
        return applicationService.list();
    }

    @JsonView(Application.View.OnListAndStop.class)
    @PostMapping(path = "/app/stop")
    public Application stop(@RequestBody StopApplicationData data) throws IOException, InterruptedException, LifecycleException {
        var application = applicationService.findById(data.id()).orElseThrow();
        return applicationService.stop(application);
    }

    @PreDestroy
    public void onShutdown() throws IOException, InterruptedException {
        applicationService.stopAll();
    }
}
