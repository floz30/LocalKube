package fr.umlv.localkube.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

@Tag("application")
public class ApplicationTest {

    @Test
    void shouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Application(null));
        assertThrows(NullPointerException.class, () -> new Application(new ApplicationDataRecord(0, null, 0, 0, null)));
    }

    @Test
    void application_Constructor() throws IOException {
        var appRecord = new ApplicationDataRecord(0, "demo:8081", 0, 0, "");

        var result = new Application(appRecord);

        Assertions.assertAll("app state invalid",
                () -> assertEquals(1, result.getId()),
                () -> assertEquals("demo:8081", result.getApp()),
                () -> assertEquals(8081, result.getPortApp()),
                () -> assertEquals(49153, result.getPortService()),
                () -> assertEquals("demo_8081", result.getDockerInstance()));
    }
}
