package fr.umlv.localkubeapi.consuming;

import org.springframework.web.client.RestTemplate;

public class ConsumingLog implements LocalKubeConsuming {

    private final RestTemplate restTemplate;
    private final int servicePort;

    public ConsumingLog(int servicePort) {
        this.restTemplate = new RestTemplate();
        this.servicePort = servicePort;
    }

    @Override
    public void insertLog(String message) {
        restTemplate.postForEntity("http://host.docker.internal:" + servicePort + "/log", message, String.class);
    }

}
