package fr.umlv.localkube.controller;

import fr.umlv.localkube.services.AutoScaleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@Validated
public class AutoScaleController {

    private final AutoScaleService autoScaleService;

    public AutoScaleController(AutoScaleService autoScaleService) {
        this.autoScaleService = autoScaleService;
    }

    /**
     * Start or update the auto-scale service.
     *
     * @param params scale configuration.
     * @return actions left to do.
     * @throws IOException If docker command fails.
     * @throws InterruptedException If docker command fails.
     */
    @PostMapping("/auto-scale/update")
    public Map<String, String> update(@RequestBody Map<String, Integer> params) throws IOException, InterruptedException {
        return autoScaleService.update(params);
    }

    /**
     * Returns actions left to do to respect the scale configuration.
     * @return actions left to do.
     */
    @GetMapping("/auto-scale/status")
    public Map<String, String> status() {
        return autoScaleService.status();
    }

    /**
     * Stops the auto-scale service.
     * @return the stopped configuration.
     * @throws IOException If docker command fails.
     * @throws InterruptedException If docker command fails.
     */
    @GetMapping("/auto-scale/stop")
    public Map<String, Integer> stop() throws IOException, InterruptedException {
        return autoScaleService.stop();
    }
}
