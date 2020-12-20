package fr.umlv.localkube.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Data class for the database properties, based on the "application.properties" file.
 */
@Configuration
@ConfigurationProperties(prefix = "database")
public class DataBaseProperties {

    /**
     * Database url property.
     */
    private String url;
    /**
     * Database username property.
     */
    private String username;
    /**
     * Database password property.
     */
    private String password;

    /**
     * Returns the database password.
     * @return Database password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the database username.
     * @return Database username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the database url.
     * @return Database url.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the password property.
     * @param password Database password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the url property.
     * @param url Database url.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Sets the username property.
     * @param username Database username.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
