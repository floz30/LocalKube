package fr.umlv.localkube.model;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationTest {
/*
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

    @Test @Tag("initializeApp")
    void shouldFailOnWrongFormatName(){
        assertThrows(IllegalArgumentException.class,() -> Application.initializeApp("demo",1));
    }

    @Test @Tag("initializeApp")
    void shouldBeAliveOnCreation(){
        assertTrue(Application.initializeApp("demo:8081",1).isAlive());
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

    @Test @Tag("getId")
    void shouldReturnCorrectId(){
        var app = Application.initializeApp("demo:8081",1);
        assertEquals(1,app.getId());
    }

    @Test @Tag("getApp")
    void shouldReturnCorrectAppName(){
        var app = Application.initializeApp("demo:8081",1);
        assertEquals("demo:8081",app.getApp());
    }

    @Test @Tag("kill")
    void shouldKillApp(){
        var app = Application.initializeApp("demo:8081",1);
        app.kill();
        assertFalse(app.isAlive());
    }

    @Test @Tag("getPortService")
    void shouldReturnCorrectPortService(){
        var app = new Application.ApplicationBuilder()
                .setApp("demo:8081")
                .setId(1)
                .setportService(49152)
                .build();
        assertEquals(49152,app.getPortService());
    }

    @Test @Tag("getPortApp")
    void shouldReturnCorrectPortApp(){
        var app = Application.initializeApp("demo:8081",1);
        assertEquals(8081,app.getPortApp());
    }

    @Test @Tag("getElapsedTime")
    void shouldReturnCorrectElapsedTime(){
        var app = Application.initializeApp("demo:8081",1);
        assertEquals("0m0s",app.getElapsedTime());
    }
*/
}
