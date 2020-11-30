package fr.umlv.localkubeapi.consuming;

import org.springframework.web.client.RestTemplate;

import java.util.Objects;

public class ConsumingLog implements LocalKubeConsuming {

    private final RestTemplate restTemplate;
    private final int servicePort;

    public ConsumingLog(int servicePort) {
        this.restTemplate = new RestTemplate();
        if(servicePort<49152 || servicePort>65535){
            throw new IllegalArgumentException("service Port range is 49152 to 65535");
        }
        this.servicePort = servicePort;
    }

    @Override
    public void insertLog(String message) {
        Objects.requireNonNull(message);
        restTemplate.postForEntity("http://host.docker.internal:" + servicePort + "/log", message, String.class);
    }

}
