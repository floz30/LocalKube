package fr.umlv.localkube.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.umlv.localkube.configuration.DockerProperties;
import fr.umlv.localkube.configuration.LocalKubeConfiguration;
import fr.umlv.localkube.manager.DockerManager;
import fr.umlv.localkube.model.*;
import fr.umlv.localkube.services.ApplicationService;
import fr.umlv.localkube.utils.OperatingSystem;
import org.apache.catalina.LifecycleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
public class ApplicationController  {

    private final ApplicationService repository;
    private final DockerManager dockerManager;
    private final LocalKubeConfiguration configuration;

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
    private record StopApplicationData(@JsonProperty("id") int id) { }

    @Autowired
    public ApplicationController(ApplicationService repository, LocalKubeConfiguration configuration, DockerProperties properties) {
        this.repository = repository;
        this.dockerManager = new DockerManager(OperatingSystem.checkOS(),properties);
        this.configuration = configuration;
    }

    public ApplicationController(ApplicationService repository, LocalKubeConfiguration configuration, DockerManager dockerManager) {
        this.repository = repository;
        this.dockerManager = dockerManager;
        this.configuration = configuration;
    }

    @PostMapping(path = "/app/start")
    public ResponseEntity start(@RequestBody StartApplicationData data) throws IOException {
        int id = repository.getNextId();
        var application = Application.initializeApp(data.app(), id);

        try {
            dockerManager.start(application);
            configuration.addServicePort(application.getPortService());
        } catch (Exception e) { //changer pour réussir à renvoyer une exception
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
        return ResponseEntity.status(HttpStatus.OK).body(repository.save(application));
    }

    @GetMapping("/app/list")
    public List<Application> list() {
        return repository.findAll();
    }

    @PostMapping(path = "/app/stop")
    public ResponseEntity<Application> stop(@RequestBody StopApplicationData data) {
        var id = data.id();
        var application = repository.findById(id);
        if (application.isPresent()) {
            var appFound = application.get();
            try {
                dockerManager.stopContainer(appFound);
                configuration.removeServicePort(appFound.getPortService());
            } catch (IOException | InterruptedException | LifecycleException ioe) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            repository.delete(appFound);
            return new ResponseEntity<>(appFound, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PreDestroy
    public void onShutdown() throws IOException, InterruptedException {
        for (var application : repository.findAll()){
            dockerManager.stopContainer(application);
        }
    }
}
