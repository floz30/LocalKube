package fr.umlv.localkubeapi.consuming;

import org.springframework.web.client.RestTemplate;

public class ConsumingLog implements LocalKubeConsuming {

    private final RestTemplate restTemplate;

    public ConsumingLog() {
        this.restTemplate = new RestTemplate();
    }

    public void insertLog(String message) {
        // numéro de port à revoir
        restTemplate.postForEntity("http://localhost:8080/log", message, String.class);
    }
}
