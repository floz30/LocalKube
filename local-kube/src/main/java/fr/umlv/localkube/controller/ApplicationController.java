package fr.umlv.localkube.controller;

import com.google.cloud.tools.jib.api.CacheDirectoryCreationException;
import com.google.cloud.tools.jib.api.InvalidImageReferenceException;
import com.google.cloud.tools.jib.api.RegistryException;
import fr.umlv.localkube.DockerManager;
import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.model.ApplicationDataRecord;
import fr.umlv.localkube.model.ApplicationRecord;
import fr.umlv.localkube.repository.ApplicationRepository;
import fr.umlv.localkube.utils.OperatingSystem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/app/*")
public class ApplicationController {

    private final ApplicationRepository service;
    private final DockerManager dockerManager;

    public ApplicationController(ApplicationRepository service){
        this.service = service;
        this.dockerManager = new DockerManager(OperatingSystem.checkOS());
    }

    @PostMapping(path="/app/start")
    public ResponseEntity<ApplicationDataRecord> start(@RequestBody ApplicationDataRecord app) {
        // on check si l'image docker existe déjà dans /docker-images
        var appName = app.app().split(":")[0]; // à mettre dans une méthode

        if (dockerManager.checkIfJarFileExists(appName + ".jar")) {
            if (dockerManager.checkIfDockerImageExists(appName)) {
                // si oui on l'utilise pour lancer le jar
            }
            else {
                // sinon on le créé
                try {
                    dockerManager.createContainer(appName + ".jar", appName);
                } catch (Exception e) { // exception à traiter, ici juste pour des tests
                    e.printStackTrace();
                }
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            //throw new FileNotFoundException();
        }

        return new ResponseEntity<>(service.save(new Application(app)), HttpStatus.OK);
    }

    @GetMapping("/app/list")
    public List<ApplicationRecord> list() {
        return service.getAll();
    }

    @PostMapping(path="/app/stop")
    public ResponseEntity<ApplicationRecord> stop(@RequestBody ApplicationDataRecord app) {
        var id = app.id();
        var application = service.findById(id);
        if (application.isPresent()) {
            var appFound = application.orElseThrow();
            service.remove(appFound);
            return new ResponseEntity<>(appFound.toApplicationRecord(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
