package fr.umlv.localkube.controller;

import fr.umlv.localkube.model.Log;
import fr.umlv.localkube.services.ApplicationService;
import fr.umlv.localkube.services.LogService;
import fr.umlv.localkube.repository.ApplicationRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@RestController
public class LogController {
    private final LogService service;
    private final ApplicationRepository applicationService;


    public LogController(LogService service, ApplicationService applicationRepository) {
        this.service = service;
        this.applicationService = applicationRepository;
    }

    @PostMapping("/log")
    public String addLog(HttpServletRequest request, @RequestBody String message) throws IOException {
        if(request.getLocalPort() == 8080){
            throw new IOException("Tried to add log on public port 8080");
        }
        var id = applicationService.findAppIdByPortService(request.getLocalPort());
        service.save(new Log(id,message,new Timestamp(System.currentTimeMillis())));
        return "New log add successfully for application : " + id;
    }

    @GetMapping("/logs")
    public List<Log> list() {
        return service.findAll();
    }

}