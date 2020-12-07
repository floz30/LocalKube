package fr.umlv.localkube.controller;

import fr.umlv.localkube.model.Log;
import fr.umlv.localkube.services.ApplicationService;
import fr.umlv.localkube.services.LogService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LogController {
    private final LogService service;
    private final ApplicationService applicationService;


    public LogController(LogService service, ApplicationService applicationRepository) {
        this.service = service;
        this.applicationService = applicationRepository;
    }

    @PostMapping("/log")
    public String addLog(HttpServletRequest request, @RequestBody String message) throws IOException {
        if (request.getLocalPort() == 8080) {
            throw new IOException("Tried to add log on public port 8080");
        }
        var appId = applicationService.findAppIdByPortService(request.getLocalPort());
        service.save(appId, message, Instant.now());
        return "New log add successfully for application : " + appId;
    }

    @GetMapping("/logs")
    public List<Log.LogApplication> list() {
        return mapToLogApplication(service.findAll());
    }


    @GetMapping("/logs/{time}")
    public List<Log.LogApplication> listLogsSinceLastTimeMinutes(@PathVariable int time) {
        return mapToLogApplication(service.selectAllFromDuration( Duration.ofMinutes(time)));
    }

    @GetMapping(path = "/logs/{time}/{filter}")
    @ResponseBody
    public List<Log.LogApplication> listLogsSinceLastTimeMinutesAndFilter(@PathVariable int time,
                                                                          @PathVariable String filter) {
        Duration minutes = Duration.ofMinutes(time);
        if (filter.contains(":")) {
            return mapToLogApplication(service.selectAllFromDurationById(minutes, applicationService.findIdByName(filter).orElse(-1)));
        } else if (filter.contains("_")) {
            return mapToLogApplication(service.selectAllFromDurationById(minutes, applicationService.findIdByDockerInstance(filter).orElse(-1)));
        }
        return mapToLogApplication(service.selectAllFromDurationById(minutes, Integer.parseInt(filter)));
    }

    @GetMapping(path = "/logs/{time}/{filter}/{value}")
    @ResponseBody
    public List<Log.LogApplication> listLogsSinceLastTimeMinutesAndFilter(@PathVariable int time,
                                                                          @PathVariable String filter, @PathVariable String value) {
        Duration minutes = Duration.ofMinutes(time);
        switch (filter) {
            case "byId":
                return mapToLogApplication(service.selectAllFromDurationById(minutes, Integer.parseInt(value)));
            case "byApp":
                return mapToLogApplication(service.selectAllFromDurationById(minutes, applicationService.findIdByName(value).orElse(-1)));
            case "byInstance":
                return mapToLogApplication(service.selectAllFromDurationById(minutes, applicationService.findIdByDockerInstance(value).orElse(-1)));
            default:
                throw new IllegalArgumentException("Filter name unknown");
        }
    }

    private List<Log.LogApplication> mapToLogApplication(List<Log> logs){
        return logs.stream().map(log -> log.toLogApplication(applicationService)).collect(Collectors.toList());
    }


}
