package fr.umlv.localkube.controller;

import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.model.ApplicationDataRecord;
import fr.umlv.localkube.model.ApplicationRecord;
import fr.umlv.localkube.repository.ApplicationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/*")
public class ApplicationController {

    private final ApplicationRepository service;

    public ApplicationController(ApplicationRepository service){
        this.service=service;
    }

    @PostMapping(path="/app/start")
    public ResponseEntity<ApplicationDataRecord> start(@RequestBody ApplicationDataRecord app) {
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
