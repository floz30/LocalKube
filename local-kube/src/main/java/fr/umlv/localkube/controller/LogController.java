package fr.umlv.localkube.controller;

import fr.umlv.localkube.model.Log;
import fr.umlv.localkube.model.LogDataRecord;
import fr.umlv.localkube.repository.LogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs/*")
public class LogController {
    private final LogRepository service;

    public LogController(LogRepository service) {
        this.service = service;
    }


}
