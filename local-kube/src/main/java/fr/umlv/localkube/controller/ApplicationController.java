package fr.umlv.localkube.controller;

import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/app/*")
public class ApplicationController {
    @Autowired
    private ApplicationRepository service;
    //private final AtomicLong counter = new AtomicLong();

    @PostMapping(path="/app/start")
    public Application start(@RequestBody Application app) {
        service.save(app);
        return app;
    }

    @GetMapping("/app/list")
    public List<Application> list() {
        return service.getAll();
    }

    @PostMapping(path="/app/stop")
    public void stop(@RequestBody Application app) {
        var app2 = service.findById(app.getId());
        // à coder
    }
}
