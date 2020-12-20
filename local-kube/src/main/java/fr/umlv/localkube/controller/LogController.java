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

@RestController
public class LogController {
    private final LogService logService;
    private final ApplicationService applicationService;

    public LogController(LogService service, ApplicationService applicationRepository) {
        this.logService = service;
        this.applicationService = applicationRepository;
    }

    /**
     * Create - Add a new {@code Log}
     * @param request
     * @param message the log message
     * @return
     * @throws IOException if you call this method on the wrong port
     */
    @PostMapping("/log")
    public String addLog(HttpServletRequest request, @RequestBody String message) throws IOException {
        if (request.getLocalPort() == 8080) {
            throw new IOException("You can't add log on public port 8080, try with service port.");
        }
        var appId = applicationService.findIdByPortService(request.getLocalPort());
        logService.save(appId, message, Instant.now());
        return "New log add successfully for application : " + appId;
    }

    /**
     * Read - Get all logs saved.
     * @return a list of {@code Log}
     */
    @GetMapping("/logs")
    public List<Log> list() {
        return logService.findAll();
    }

    /**
     * Read - Get all logs saved since last {@code time} minutes.
     * @param time in minutes
     * @return a list of {@code Log}
     */
    @GetMapping("/logs/{time}")
    public List<Log> listLogsSinceLastTimeMinutes(@PathVariable int time) {
        return logService.selectAllFromDuration(Duration.ofMinutes(time));
    }

    /**
     * Read - Get all logs saved since last {@code time} minutes and corresponding to the {@code filter}.
     * {@code filter} can be the ID, the name or the docker instance of an application.
     * <p>
     * Examples :
     * <ul>
     *     <li>/logs/5/203 - returns logs of the last five minutes for the application with an id equals to "203"</li>
     *     <li>/logs/5/demo_8081 - returns logs of the last five minutes for the application with a docker instance equals to "demo_8081"</li>
     *     <li>/logs/5/demo:8081 - returns logs of the last five minutes for the application with a name equals to "demo:8081"</li>
     * </ul>
     *
     * @param time   in minutes
     * @param filter filter to test
     * @return a list of {@code Log}
     */
    @GetMapping(path = "/logs/{time}/{filter}")
    public List<Log> listLogsSinceLastTimeMinutesAndFilter(@PathVariable int time,
                                                           @PathVariable String filter) {
        Duration minutes = Duration.ofMinutes(time);
        if (filter.contains(":")) {
            return logService.selectAllFromDurationById(minutes, applicationService.findIdByName(filter).orElse(-1));
        } else if (filter.contains("_")) {
            return logService.selectAllFromDurationById(minutes, applicationService.findIdByDockerInstance(filter).orElse(-1));
        }
        return logService.selectAllFromDurationById(minutes, Integer.parseInt(filter));
    }

    /**
     * Read - Get all logs saved since last {@code time} minutes and corresponding to the {@code filter} and this {@code value}.
     * {@code filter} can be "byId", "byApp" or "byInstance".
     * {@code value} can be the ID, the name or the docker instance of an application.
     * <p>
     * Examples :
     * <ul>
     *     <li>/logs/5/byId/203 - returns logs of the last five minutes for the application with an id equals to "203"</li>
     *     <li>/logs/5/byInstance/demo_8081 - returns logs of the last five minutes for the application with a docker instance equals to "demo_8081"</li>
     *     <li>/logs/5/byApp/demo:8081 - returns logs of the last five minutes for the application with a name equals to "demo:8081"</li>
     * </ul>
     *
     * @param time   in minutes
     * @param filter the filter to test
     * @param value  the value of the filter
     * @return a list of {@code Log}
     */
    @GetMapping(path = "/logs/{time}/{filter}/{value}")
    public List<Log> listLogsSinceLastTimeMinutesAndFilter(@PathVariable int time,
                                                           @PathVariable String filter,
                                                           @PathVariable String value) {
        Duration minutes = Duration.ofMinutes(time);
        return switch (filter) {
            case "byId" -> logService.selectAllFromDurationById(minutes, Integer.parseInt(value));
            case "byApp" -> logService.selectAllFromDurationById(minutes, applicationService.findIdByName(value).orElse(-1));
            case "byInstance" -> logService.selectAllFromDurationById(minutes, applicationService.findIdByDockerInstance(value).orElse(-1));
            default -> throw new IllegalArgumentException("Filter name unknown");
        };
    }

}
