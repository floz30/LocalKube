package fr.umlv.localkube.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.time.Instant;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY) // see here https://github.com/FasterXML/jackson-future-ideas/issues/46
public record Log(@ColumnName("id") int id,
                  @ColumnName("app") String app,
                  @ColumnName("port") int port,
                  @ColumnName("service-port") int portService,
                  @ColumnName("docker-instance") String dockerInstance,
                  @ColumnName("message") String message,
                  @ColumnName("timestamp") Instant timestamp) {

    public Log {
        Objects.requireNonNull(message);
        Objects.requireNonNull(timestamp);
    }

}

