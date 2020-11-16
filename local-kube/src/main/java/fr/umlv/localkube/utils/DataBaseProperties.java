package fr.umlv.localkube.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

public record DataBaseProperties(String username, String password,String path) {
    public static DataBaseProperties credentialsFromPropertiesFile() throws IOException {
        try (InputStream input = DataBaseProperties.class.getClassLoader().getResourceAsStream("application.properties")) {
            var properties = new Properties();
            if (input == null) {
                throw new IOException("unable to find properties file");
            }
            properties.load(input);
            return new DataBaseProperties(properties.getProperty("database.username"),
                    properties.getProperty("database.password"),
                    new File("").getAbsolutePath().concat(File.separator + "logs" + File.separator + "logs.db"));
        }
    }
}
