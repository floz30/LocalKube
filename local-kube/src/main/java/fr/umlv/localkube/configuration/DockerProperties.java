package fr.umlv.localkube.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "docker")
public class DockerProperties {
    private String apps;
    private String lib;
    private String images;

    public String getApps() {
        return apps;
    }

    public String getLib() {
        return lib;
    }

    public String getImages() {
        return images;
    }

    public void setApps(String apps) {
        this.apps = apps;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public void setLib(String lib) {
        this.lib = lib;
    }
}
