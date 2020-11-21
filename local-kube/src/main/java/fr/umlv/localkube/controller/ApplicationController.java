package fr.umlv.localkube.controller;

import fr.umlv.localkube.configuration.LocalKubeConfiguration;
import fr.umlv.localkube.manager.DockerManager;
import fr.umlv.localkube.model.*;
import fr.umlv.localkube.repository.ApplicationRepository;
import fr.umlv.localkube.utils.OperatingSystem;
import org.apache.catalina.LifecycleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;

@RestController
public class ApplicationController  {

    private final ApplicationRepository service;
    private final DockerManager dockerManager;
    private final LocalKubeConfiguration configuration;

    public ApplicationController(ApplicationRepository service, LocalKubeConfiguration configuration) {
        this.service = service;
        this.dockerManager = new DockerManager(OperatingSystem.checkOS());
        this.configuration = configuration;
    }

    @PostMapping(path = "/app/start")
    public ResponseEntity start(@RequestBody ApplicationDataRecord data) throws IOException {
        var application = new Application(data);
        try {
            dockerManager.start(application);
            configuration.addServicePort(application.getPortService());
        } catch (Exception e) { //changer pour réussir à renvoyer une exception
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
        return ResponseEntity.status(HttpStatus.OK).body(service.save(application));
    }

    @GetMapping("/app/list")
    public List<ApplicationRecord> list() {
        return service.getAll();
    }

    @PostMapping(path = "/app/stop")
    public ResponseEntity<ApplicationRecord> stop(@RequestBody ApplicationDataRecord data) {
        var id = data.id();
        var application = service.findById(id);
        if (application.isPresent()) {
            var appFound = application.get();
            try {
                dockerManager.stopContainer(appFound);
                configuration.removeServicePort(appFound.getPortService());
            } catch (IOException | InterruptedException | LifecycleException ioe) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            service.remove(appFound);
            return new ResponseEntity<>(appFound.toApplicationRecord(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PreDestroy
    public void onShutdown() throws IOException, InterruptedException {
        for(var application : service.applicationList() ){
            dockerManager.stopContainer(application);
        }
    }
}
