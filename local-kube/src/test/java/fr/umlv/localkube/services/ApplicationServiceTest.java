package fr.umlv.localkube.services;

import fr.umlv.localkube.model.Application;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationServiceTest {

    private ApplicationService service;

    @BeforeEach
    void setUp() {
        service = new ApplicationService();
    }

    @Test @Tag("delete")
    void shouldThrowNullPointerExceptionWhenRemoveNull() {
        assertThrows(NullPointerException.class, () -> service.delete(null));
    }

    @Test @Tag("delete")
    void shouldRemoveSpecifiedApplication() {
        var app = new Application.ApplicationBuilder().buildRandom();
        service.save(app);

        service.delete(app);

        assertEquals(0, service.size());
    }

    @Test @Tag("delete")
    void shouldRemoveOnlySpecifiedApplication() {
        var app_1 = new Application.ApplicationBuilder().setId(1).build();
        var app_2 = new Application.ApplicationBuilder().setId(2).build();
        service.save(app_1);
        service.save(app_2);

        service.delete(app_1);

        var expected = new ArrayList<Application>();
        expected.add(app_2);
        assertAll(
                () -> assertEquals(1, service.size()),
                () -> assertEquals(expected, service.findAll())
        );
    }

    @Test @Tag("findAll")
    void shouldReturnEmptyList() {
        assertEquals(new ArrayList<Application>(), service.findAll());
    }

    @Test @Tag("findAll")
    void shouldReturnAllSavedApplication() {
        var expected = new ArrayList<Application>();
        for (var i = 1; i < 20; i++) {
            var app = new Application.ApplicationBuilder().setId(i).build();
            expected.add(app);
            service.save(app);
        }

        assertAll(
                () -> assertEquals(19, service.size()),
                () -> assertEquals(expected, service.findAll())
        );
    }

    @Test @Tag("findAppIdByPortService")
    void shouldThrowIllegalStateExceptionWhenThereAreNoSavedApp() {
        assertThrows(IllegalStateException.class, () -> service.findAppIdByPortService(300));
    }

    @Test @Tag("findAppIdByPortService")
    void shouldThrowIllegalStateExceptionWhenNoAppMatches() {
        for (var i = 1; i < 20; i++) {
            var app = new Application.ApplicationBuilder().setId(i).setportService(i+5000).build();
            service.save(app);
        }

        assertThrows(IllegalStateException.class, () -> service.findAppIdByPortService(300));
    }

    @Test @Tag("findById")
    void shouldNotFindAppWhenAppsIsEmpty() {
        var result = service.findById(5);

        assertTrue(result.isEmpty());
    }

    @Test @Tag("findById")
    void shouldNotFindAppWithFalseId() {
        for (var i = 1; i < 20; i++) {
            var app = new Application.ApplicationBuilder().setId(i).build();
            service.save(app);
        }

        var result = service.findById(99);

        assertTrue(result.isEmpty());
    }

    @Test @Tag("findById")
    void shouldReturnApp() {
        var app = new Application.ApplicationBuilder().buildRandom();
        service.save(app);

        var result = service.findById(app.getId());

        assertTrue(result.isPresent());
    }

    @Test @Tag("findById")
    void shouldReturnCorrectApp() {
        for (var i = 1; i < 20; i++) {
            var app = new Application.ApplicationBuilder().setId(i).build();
            service.save(app);
        }

        var result = service.findById(5);

        assertTrue(result.isPresent());
    }

    @Test @Tag("findIdByDockerInstance")
    void shouldNotFindIdByInstanceWhenAppsIsEmpty() {
        var result = service.findIdByDockerInstance("demo_8081");

        assertTrue(result.isEmpty());
    }

    @Test @Tag("findIdByDockerInstance")
    void shouldNotFindIdByInstanceWithFalseInstance() {
        for (var i = 1; i < 20; i++) {
            var app = new Application.ApplicationBuilder().setId(i).setDockerInstance("demo_"+(i+8000)).build();
            service.save(app);
        }

        var result = service.findIdByDockerInstance("demo_8081");

        assertTrue(result.isEmpty());
    }

    @Test @Tag("findIdByDockerInstance")
    void shouldReturnIdByInstance() {
        var app = new Application.ApplicationBuilder().buildRandom();
        service.save(app);

        var result = service.findIdByDockerInstance(app.getDockerInstance());

        assertTrue(result.isPresent());
    }

    @Test @Tag("findIdByName")
    void shouldNotFindIdByNameWhenAppsIsEmpty() {
        var result = service.findIdByName("demo:8081");

        assertTrue(result.isEmpty());
    }

    @Test @Tag("findIdByName")
    void shouldNotFindIdByNameWithFalseInstance() {
        for (var i = 1; i < 20; i++) {
            var app = new Application.ApplicationBuilder().setId(i).setApp("demo:"+(i+8000)).build();
            service.save(app);
        }

        var result = service.findIdByName("demo:8081");

        assertTrue(result.isEmpty());
    }

    @Test @Tag("findIdByName")
    void shouldReturnIdByName() {
        var app = new Application.ApplicationBuilder().buildRandom();
        service.save(app);

        var result = service.findIdByName(app.getApp());

        assertTrue(result.isPresent());
    }

    @Test @Tag("getNextId")
    void shouldReturnOneIfAppsIsEmpty() {
        assertEquals(1, service.getNextId());
    }

    @Test @Tag("getNextId")
    void shouldReturnReturnOne() {
        var result = service.getNextId();

        assertEquals(1, result);
    }

    @Test @Tag("getNextId")
    void shouldReturnReturnSix() {
        service.save(new Application.ApplicationBuilder().setId(5).build());
        var result = service.getNextId();

        assertEquals(6, result);
    }

    //TODO les tests pour removeAllByDockerInstanceName()

    @Test @Tag("save")
    void shouldThrowNullPointerExceptionWhenSaveNullApp() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> service.save(null)),
                () -> assertEquals(0, service.size())
        );
    }

    @Test @Tag("save")
    void shouldSaveAndReturnApplication() {
        var app = new Application.ApplicationBuilder().buildRandom();

        var actual = service.save(app);

        assertAll(
                () -> assertEquals(actual, app),
                () -> assertEquals(1, service.size())
        );
    }

    @Test @Tag("size")
    void shouldReturnGoodSize() {
        for (var i = 1; i < 20; i++) {
            service.save(new Application.ApplicationBuilder().buildRandom());
        }

        assertEquals(19, service.size());
    }
}
