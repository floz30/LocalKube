package fr.umlv.localkube.controller;

import fr.umlv.localkube.model.Log;
import fr.umlv.localkube.services.ApplicationService;
import fr.umlv.localkube.services.LogService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.OptionalInt;

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
        if (request.getLocalPort() == 8080){
            throw new IOException("Tried to add log on public port 8080");
        }
        var appId = applicationService.findAppIdByPortService(request.getLocalPort());
        service.save(appId, message, new Timestamp(System.currentTimeMillis()));
        return "New log add successfully for application : " + appId;
    }

    @GetMapping("/logs")
    public List<Log> list() {
        return service.findAll();
    }


    @GetMapping("/logs/{time}")
    public List<Log> listLogsSinceLastTimeMinutes(@PathVariable int time) {
        return service.selectAllFromDuration(time);
    }

    @GetMapping(path = "/logs/{time}/{filter}")
    @ResponseBody
    public List<Log> listLogsSinceLastTimeMinutesAndFilter(@PathVariable int time,
                                                               @PathVariable String filter) {
        try {
             var value = Integer.parseInt(filter);
             return service.selectAllFromDurationById(time, value);
        } catch (NumberFormatException nfe) {
            return filterByString(time, filter);
        }
    }

    private List<Log> filterByString(int time, String filter) {
        OptionalInt app_id;
        if (filter.contains(":")) {
            app_id = applicationService.findIdByName(filter);
        } else {
            app_id = applicationService.findIdByDockerInstance(filter);
        }
        if (app_id.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return service.selectAllFromDurationById(time, app_id.getAsInt());
    }

    /*@ResponseBody
    @GetMapping(path = "/logs/{time}/byApp/{value}")
    public List<Log> listLogsSinceLastTimeMinutesAndFilterByApp(@PathVariable int time,
                                                                @PathVariable String value) {

    }*/



}
