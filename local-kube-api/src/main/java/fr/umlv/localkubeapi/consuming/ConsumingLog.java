package fr.umlv.localkubeapi.consuming;

import fr.umlv.localkubeapi.model.Log;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;

public class ConsumingLog {

    private final RestTemplate restTemplate;

    public ConsumingLog() {
        this.restTemplate = new RestTemplate();
    }

    public void insertLog(String message) {
        // numéro de port à revoir
        restTemplate.postForEntity("http://localhost:8080/log", createLog(message), String.class);
    }

    private Log createLog(String message) {
        return new Log(message, new Timestamp(System.currentTimeMillis()));
    }

}
