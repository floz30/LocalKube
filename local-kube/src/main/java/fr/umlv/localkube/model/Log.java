package fr.umlv.localkube.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonView;
import fr.umlv.localkube.services.ApplicationService;
import fr.umlv.localkube.services.LogService;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.springframework.lang.Nullable;

import java.lang.reflect.AnnotatedParameterizedType;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

public record Log(int app_id,String message,Instant timestamp) {

    public Log {
        Objects.requireNonNull(message);
        Objects.requireNonNull(timestamp);
    }

    public LogApplication toLogApplication(ApplicationService applicationService){
        var application = applicationService.findById(app_id).orElseThrow();
        return new LogApplication(app_id,application.getApp(),application.getPortApp(),application.getPortService(),application.getDockerInstance(),message,timestamp);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY) // see here https://github.com/FasterXML/jackson-future-ideas/issues/46
    public static record LogApplication(@ColumnName("id") int id,
                                        @ColumnName("app") String app,
                                        @ColumnName("port") int port,
                                        @ColumnName("service-port") int portService,
                                        @ColumnName("docker-instance") String dockerInstance,
                                        @ColumnName("message") String message,
                                        @ColumnName("timestamp") Instant timestamp){

    }

}

