package fr.umlv.localkube.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Data class for the docker properties, based on the "application.properties" file.
 */
@Configuration
@ConfigurationProperties(prefix = "docker")
public class DockerProperties {
    /**
     * Path to jar applications folder.
     */
    private String apps;
    /**
     * Path to localKube library.
     */
    private String lib;
    /**
     * Path to docker images folder.
     */
    private String images;

    /**
     * Returns the jar applications folder path.
     * @return Jar applications folder path.
     */
    public String getApps() {
        return apps;
    }

    /**
     * Returns the localKube library path.
     * @return LocalKube library path.
     */
    public String getLib() {
        return lib;
    }

    /**
     * Returns the docker images folder path.
     * @return Docker images folder path.
     */
    public String getImages() {
        return images;
    }

    /**
     * Sets the apps property.
     * @param apps Apps property.
     */
    public void setApps(String apps) {
        this.apps = apps;
    }

    /**
     * Sets the images property.
     * @param images Images property.
     */
    public void setImages(String images) {
        this.images = images;
    }

    /**
     * Sets the library property.
     * @param lib Lib property.
     */
    public void setLib(String lib) {
        this.lib = lib;
    }
}
