package fr.umlv.localkube.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.sql.Timestamp;
import java.util.Objects;

// see here https://github.com/FasterXML/jackson-future-ideas/issues/46
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record Log(int id, int app_id, String message, Timestamp timestamp) {
    public Log {
        Objects.requireNonNull(message);
        Objects.requireNonNull(timestamp);
    }
}

