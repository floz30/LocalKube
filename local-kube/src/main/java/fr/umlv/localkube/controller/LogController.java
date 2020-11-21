package fr.umlv.localkube.controller;

import fr.umlv.localkube.model.Log;
import fr.umlv.localkube.repository.ApplicationRepository;
import fr.umlv.localkube.repository.LogRepository;
import fr.umlv.localkube.services.ApplicationService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    public String addLog(HttpServletRequest request, @RequestBody String message) throws IOException {
        if(request.getLocalPort() == 8080){
            throw new IOException("Tried to add log on public port 8080");
        }
        var id = applicationService.findAppIdByPortService(request.getLocalPort());
        service.insertLog(new Log(id,message,new Timestamp(System.currentTimeMillis())));
        return "New log add successfully for application : " + id;
    }

    @GetMapping("/logs")
    public List<Log> list() {
        return service.selectAll();
    }

}
