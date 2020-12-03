package fr.umlv.localkube.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.sql.Timestamp;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY) // see here https://github.com/FasterXML/jackson-future-ideas/issues/46
public record Log(@ColumnName("id") int id,
                  @ColumnName("app_id") int appId,
                  @ColumnName("message") String message,
                  @ColumnName("timestamp") Timestamp timestamp) {
    public Log {
        Objects.requireNonNull(message);
        Objects.requireNonNull(timestamp);
    }
}

