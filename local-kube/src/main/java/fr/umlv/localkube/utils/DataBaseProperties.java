package fr.umlv.localkube.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public record DataBaseProperties(String username, String password, String path) {
    public static DataBaseProperties credentialsFromPropertiesFile() throws IOException {
        try (var file = new FileInputStream(Paths.get("src", File.separator, "main", File.separator, "resources", File.separator, "application.properties").toString())) {
            var properties = new Properties();
            properties.load(file);
            return new DataBaseProperties(properties.getProperty("database.username"),
                    properties.getProperty("database.password"),
                    Paths.get(Paths.get("").toAbsolutePath().getParent().toString(), Paths.get(File.separator, "logs", File.separator, "logs.db").toString()).toString());
        }
    }
}
