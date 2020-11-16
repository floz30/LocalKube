package fr.umlv.localkubeapi.model;

import java.sql.Timestamp;
import java.util.Objects;

// on ne met pas id ni app_id car ils seront gérés par local-kube
public record Log(String message, Timestamp timestamp) {
    public Log {
        Objects.requireNonNull(message);
        Objects.requireNonNull(timestamp);
    }
}
