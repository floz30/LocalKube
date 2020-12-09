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
    public List<Log> list() {
        return service.findAll();
    }


    @GetMapping("/logs/{time}")
    public List<Log> listLogsSinceLastTimeMinutes(@PathVariable int time) {
        return service.selectAllFromDuration( Duration.ofMinutes(time));
    }

    @GetMapping(path = "/logs/{time}/{filter}")
    @ResponseBody
    public List<Log> listLogsSinceLastTimeMinutesAndFilter(@PathVariable int time,
                                                                          @PathVariable String filter) {
        Duration minutes = Duration.ofMinutes(time);
        if (filter.contains(":")) {
            return service.selectAllFromDurationById(minutes, applicationService.findIdByName(filter).orElse(-1));
        } else if (filter.contains("_")) {
            return service.selectAllFromDurationById(minutes, applicationService.findIdByDockerInstance(filter).orElse(-1));
        }
        return service.selectAllFromDurationById(minutes, Integer.parseInt(filter));
    }

    @GetMapping(path = "/logs/{time}/{filter}/{value}")
    @ResponseBody
    public List<Log> listLogsSinceLastTimeMinutesAndFilter(@PathVariable int time,
                                                                          @PathVariable String filter, @PathVariable String value) {
        Duration minutes = Duration.ofMinutes(time);
        return switch (filter) {
            case "byId" -> service.selectAllFromDurationById(minutes, Integer.parseInt(value));
            case "byApp" -> service.selectAllFromDurationById(minutes, applicationService.findIdByName(value).orElse(-1));
            case "byInstance" -> service.selectAllFromDurationById(minutes, applicationService.findIdByDockerInstance(value).orElse(-1));
            default -> throw new IllegalArgumentException("Filter name unknown");
        };
    }

}
