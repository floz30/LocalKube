package fr.umlv.localkubeapi.consuming;

import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ConsumingLog {

    private final RestTemplate restTemplate;

    public ConsumingLog() {
        this.restTemplate = new RestTemplate();
    }

    public void insertLog(String message) {
        restTemplate.postForEntity("http://localhost:8080/logs/insert", createLog(message), String.class);
    }

    private LogDataRecord createLog(String message) {
        //à modifier en trouvant une technique pour trouver le port de l'application qui fait un insert log
        return new LogDataRecord(message,8081,new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS'Z'").format(Calendar.getInstance().getTime()));
    }

}
