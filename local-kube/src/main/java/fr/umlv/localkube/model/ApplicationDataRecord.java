package fr.umlv.localkube.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApplicationDataRecord(@JsonProperty("id") int id, @JsonProperty("app") String app,
                                    @JsonProperty("port") int portApp, @JsonProperty("service-port") int portService,
                                    @JsonProperty("docker-instance") String dockerInstance) {
}
