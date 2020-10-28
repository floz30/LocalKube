package fr.umlv.localkube.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApplicationRecord(@JsonProperty("id") int id, @JsonProperty("app") String app, @JsonProperty("port") int portApp, @JsonProperty("service-port") int portService, @JsonProperty("docker-instance") String dockerInstance, @JsonProperty("elapsed-time") String elapsedTime)  {
}
