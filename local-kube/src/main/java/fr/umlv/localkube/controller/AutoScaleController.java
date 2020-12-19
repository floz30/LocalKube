package fr.umlv.localkube.controller;

import fr.umlv.localkube.services.AutoScaleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AutoScaleController {

    private final AutoScaleService autoScaleService;

    public AutoScaleController(AutoScaleService autoScaleService) {
        this.autoScaleService = autoScaleService;
    }

    @PostMapping("/auto-scale/update")
    public Map<String, String> update(@RequestBody Map<String, Long> params) {
        System.out.println(params);

        return autoScaleService.update(params);
    }

    @GetMapping("/auto-scale/status")
    public Map<String, String> status() {

        return autoScaleService.status();
    }

    @GetMapping("/auto-scale/stop")
    public Map<String, Long> stop() {

        return autoScaleService.stop();
    }
}
