package fr.umlv.localkube.controller;

import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app/*")
public class ApplicationController {
    @Autowired
    private ApplicationRepository service;

    @PostMapping(path="/app/start")
    public ResponseEntity<Application> start(@RequestBody Application app) {
        service.save(app);
        return new ResponseEntity<>(app, HttpStatus.OK);
    }

    @GetMapping("/app/list")
    public List<Application> list() {
        return service.getAll();
    }

    @PostMapping(path="/app/stop")
    public ResponseEntity<Application> stop(@RequestBody Map<String, Object> content) {
        var id = (int)content.get("id");
        var app = service.findById(id);
        if (app.isPresent()) {
            var appFind = app.get();
            appFind.setElapsedTime();
            service.remove(appFind);
            return new ResponseEntity<>(appFind, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
