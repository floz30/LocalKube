package fr.umlv.localkube.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public record DataBaseProperties(String username, String password, String path) {

    /**
     * Load properties file and get credentials for database.
     * @return new object initialized
     * @throws IOException if an error occurred when reading from the input stream
     */
    public static DataBaseProperties credentialsFromPropertiesFile() throws IOException {
        try (var file = DataBaseProperties.class.getClassLoader().getResourceAsStream("application.properties")) {
            var properties = new Properties();
            properties.load(file);
            return new DataBaseProperties(properties.getProperty("database.username"),
                    properties.getProperty("database.password"),
                    Path.of(properties.getProperty("database.path")).toAbsolutePath().toString());
        }
    }
}
