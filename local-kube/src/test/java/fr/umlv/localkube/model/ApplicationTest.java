package fr.umlv.localkube.model;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationTest {

    @Test @Tag("initializeApp")
    void shouldThrowNullPointerExceptionInInitialize() {
        assertThrows(NullPointerException.class, () -> Application.initializeApp(null, 1));
    }

    @Test @Tag("initializeApp")
    void shouldThrowIllegalArgumentExceptionInInitialize() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> Application.initializeApp(null, 0)),
                () -> assertThrows(IllegalArgumentException.class, () -> Application.initializeApp(null, -5)),
                () -> assertThrows(IllegalArgumentException.class, () -> Application.initializeApp("demo:8081", 0))
        );
    }

    @Test @Tag("getJarName")
    void shouldReturnCorrectJarFilename() {
        var app = new Application.ApplicationBuilder()
                .setApp("demo:8081")
                .setId(1)
                .build();

        var actual = app.getJarName();

        assertEquals("demo.jar", actual);
    }

    @Test @Tag("getName")
    void shouldReturnCorrectName() {
        var app = new Application.ApplicationBuilder()
                .setApp("demo:8081")
                .setId(1)
                .build();

        var actual = app.getName();

        assertEquals("demo", actual);
    }

}
