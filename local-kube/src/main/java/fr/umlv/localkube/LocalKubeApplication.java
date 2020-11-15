package fr.umlv.localkube;

import fr.umlv.localkube.repository.LogRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;

@SpringBootApplication
@RestController
public class LocalKubeApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(LocalKubeApplication.class, args);
    }

}
