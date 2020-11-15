package fr.umlv.localkube.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public record DataBaseProperties(String username, String password,String path) {
    public static DataBaseProperties credentialsFromPropertiesFile() throws IOException {
        var properties = new Properties();
        properties.load(new FileInputStream("src/main/resources/application.properties"));
        return new DataBaseProperties(properties.getProperty("database.username"), properties.getProperty("database.password"), properties.getProperty("database.path"));
    }
}
