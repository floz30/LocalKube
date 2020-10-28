package fr.umlv.localkube.controller;

import fr.umlv.localkube.model.ApplicationRecord;
import fr.umlv.localkube.model.ApplicationStartRecord;
import fr.umlv.localkube.model.ApplicationStopRecord;
import fr.umlv.localkube.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/*")
public class ApplicationController {

    @Autowired
    private ApplicationRepository service;

    @PostMapping(path="/app/start")
    public ResponseEntity<ApplicationStartRecord> start(@RequestBody ApplicationStartRecord app) {
        return new ResponseEntity<>(service.save(app), HttpStatus.OK);
    }

    @GetMapping("/app/list")
    public List<ApplicationRecord> list() {
        return service.getAll();
    }

    @PostMapping(path="/app/stop")
    public ResponseEntity<ApplicationRecord> stop(@RequestBody ApplicationStopRecord app) {
        var id = app.id();
        var application = service.findById(id);
        if (application.isPresent()) {
            var appFind = application.orElseThrow();
            service.remove(appFind);
            return new ResponseEntity<>(appFind.toApplicationRecord(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
