package fr.umlv.localkube.controller;

import fr.umlv.localkube.DockerManager;
import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.model.ApplicationDataRecord;
import fr.umlv.localkube.model.ApplicationRecord;
import fr.umlv.localkube.repository.ApplicationRepository;
import fr.umlv.localkube.utils.OperatingSystem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/app/*")
public class ApplicationController {

    private final ApplicationRepository service;
    private final DockerManager dockerManager;

    public ApplicationController(ApplicationRepository service) {
        this.service = service;
        this.dockerManager = new DockerManager(OperatingSystem.checkOS());
    }

    @PostMapping(path = "/app/start")
    public ResponseEntity start(@RequestBody ApplicationDataRecord data) {
        var application = new Application(data);
        try {
            dockerManager.start(application);
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
            } catch (IOException | InterruptedException ioe) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            service.remove(appFound);
            return new ResponseEntity<>(appFound.toApplicationRecord(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
