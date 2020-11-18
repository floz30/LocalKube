package fr.umlv.localkube.controller;

import fr.umlv.localkube.model.Log;
import fr.umlv.localkube.repository.ApplicationRepository;
import fr.umlv.localkube.repository.LogRepository;
import fr.umlv.localkube.services.ApplicationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

@RestController
public class LogController {
    private final LogRepository service;
    private final ApplicationService applicationService;


    public LogController(LogRepository service, ApplicationRepository applicationRepository) {
        this.service = service;
        this.applicationService = applicationRepository;
    }

    @PostMapping("/log")
    public String addLog(@RequestBody String message) {
        // service port à trouver dynamiquement
        service.insertLog(new Log(applicationService.findAppIdByPortService(15000),message,new Timestamp(System.currentTimeMillis())));
        return "New log add successfully on app : " + applicationService.findAppIdByPortService(15000);
    }

    @RequestMapping("/logs")
    public List<Log> list() {
        return service.selectAll();
    }

}
