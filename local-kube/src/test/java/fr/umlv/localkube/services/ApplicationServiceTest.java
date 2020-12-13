package fr.umlv.localkube.services;

import com.google.cloud.tools.jib.api.CacheDirectoryCreationException;
import com.google.cloud.tools.jib.api.InvalidImageReferenceException;
import com.google.cloud.tools.jib.api.RegistryException;
import fr.umlv.localkube.configuration.DockerProperties;
import fr.umlv.localkube.configuration.LocalKubeConfiguration;
import fr.umlv.localkube.model.Application;
import org.apache.catalina.LifecycleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationServiceTest {
    /*
    private ApplicationService service;
    private DockerProperties properties;
    private LocalKubeConfiguration configuration;

    @BeforeEach
    void setUp() {
        service = new ApplicationService(configuration,properties);
    }

    @Test @Tag("delete")
    void shouldThrowNullPointerExceptionWhenRemoveNull() {
        assertThrows(NullPointerException.class, () -> service.stop(null));
    }

    @Test @Tag("delete")
    void shouldRemoveSpecifiedApplication() throws RegistryException, InterruptedException, ExecutionException, IOException, CacheDirectoryCreationException, InvalidImageReferenceException, LifecycleException {
        var app = new Application.ApplicationBuilder().buildRandom();
        service.start(app);

        service.stop(app);

        assertEquals(0, service.size());
    }

    @Test @Tag("delete")
    void shouldRemoveOnlySpecifiedApplication() throws RegistryException, InterruptedException, ExecutionException, IOException, CacheDirectoryCreationException, InvalidImageReferenceException, LifecycleException {
        var app_1 = new Application.ApplicationBuilder().setId(1).build();
        var app_2 = new Application.ApplicationBuilder().setId(2).build();
        service.start(app_1);
        service.start(app_2);

        service.stop(app_1);

        var expected = new ArrayList<Application>();
        expected.add(app_2);
        assertAll(
                () -> assertEquals(1, service.size()),
                () -> assertEquals(expected, service.list())
        );
    }

    @Test @Tag("findAll")
    void shouldReturnEmptyList() {
        assertEquals(new ArrayList<Application>(), service.list());
    }

    @Test @Tag("findAll")
    void shouldReturnAllSavedApplication() throws RegistryException, InterruptedException, ExecutionException, IOException, CacheDirectoryCreationException, InvalidImageReferenceException {
        var expected = new ArrayList<Application>();
        for (var i = 1; i < 20; i++) {
            var app = new Application.ApplicationBuilder().setId(i).build();
            expected.add(app);
            service.start(app);
        }

        assertAll(
                () -> assertEquals(19, service.size()),
                () -> assertEquals(expected, service.list())
        );
    }

    @Test @Tag("findAppIdByPortService")
    void shouldThrowIllegalStateExceptionWhenThereAreNoSavedApp() {
        assertThrows(IllegalStateException.class, () -> service.findAppIdByPortService(300));
    }

    @Test @Tag("findAppIdByPortService")
    void shouldThrowIllegalStateExceptionWhenNoAppMatches() throws RegistryException, InterruptedException, ExecutionException, IOException, CacheDirectoryCreationException, InvalidImageReferenceException {
        for (var i = 1; i < 20; i++) {
            var app = new Application.ApplicationBuilder().setId(i).setportService(i+5000).build();
            service.start(app);
        }

        assertThrows(IllegalStateException.class, () -> service.findAppIdByPortService(300));
    }

    @Test @Tag("findById")
    void shouldNotFindAppWhenAppsIsEmpty() {
        var result = service.findById(5);

        assertTrue(result.isEmpty());
    }

    @Test @Tag("findById")
    void shouldNotFindAppWithFalseId() throws RegistryException, InterruptedException, ExecutionException, IOException, CacheDirectoryCreationException, InvalidImageReferenceException {
        for (var i = 1; i < 20; i++) {
            var app = new Application.ApplicationBuilder().setId(i).build();
            service.start(app);
        }

        var result = service.findById(99);

        assertTrue(result.isEmpty());
    }

    @Test @Tag("findById")
    void shouldReturnApp() throws RegistryException, InterruptedException, ExecutionException, IOException, CacheDirectoryCreationException, InvalidImageReferenceException {
        var app = new Application.ApplicationBuilder().buildRandom();
        service.start(app);

        var result = service.findById(app.getId());

        assertTrue(result.isPresent());
    }

    @Test @Tag("findById")
    void shouldReturnCorrectApp() throws RegistryException, InterruptedException, ExecutionException, IOException, CacheDirectoryCreationException, InvalidImageReferenceException {
        for (var i = 1; i < 20; i++) {
            var app = new Application.ApplicationBuilder().setId(i).build();
            service.start(app);
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
    void shouldNotFindIdByInstanceWithFalseInstance() throws RegistryException, InterruptedException, ExecutionException, IOException, CacheDirectoryCreationException, InvalidImageReferenceException {
        for (var i = 1; i < 20; i++) {
            var app = new Application.ApplicationBuilder().setId(i).setDockerInstance("demo_"+(i+8000)).build();
            service.start(app);
        }

        var result = service.findIdByDockerInstance("demo_8081");

        assertTrue(result.isEmpty());
    }

    @Test @Tag("findIdByDockerInstance")
    void shouldReturnIdByInstance() throws RegistryException, InterruptedException, ExecutionException, IOException, CacheDirectoryCreationException, InvalidImageReferenceException {
        var app = new Application.ApplicationBuilder().buildRandom();
        service.start(app);

        var result = service.findIdByDockerInstance(app.getDockerInstance());

        assertTrue(result.isPresent());
    }

    @Test @Tag("findIdByName")
    void shouldNotFindIdByNameWhenAppsIsEmpty() {
        var result = service.findIdByName("demo:8081");

        assertTrue(result.isEmpty());
    }

    @Test @Tag("findIdByName")
    void shouldNotFindIdByNameWithFalseInstance() throws RegistryException, InterruptedException, ExecutionException, IOException, CacheDirectoryCreationException, InvalidImageReferenceException {
        for (var i = 1; i < 20; i++) {
            var app = new Application.ApplicationBuilder().setId(i).setApp("demo:"+(i+8000)).build();
            service.start(app);
        }

        var result = service.findIdByName("demo:8081");

        assertTrue(result.isEmpty());
    }

    @Test @Tag("findIdByName")
    void shouldReturnIdByName() throws RegistryException, InterruptedException, ExecutionException, IOException, CacheDirectoryCreationException, InvalidImageReferenceException {
        var app = new Application.ApplicationBuilder().buildRandom();
        service.start(app);

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
    void shouldReturnReturnSix() throws RegistryException, InterruptedException, ExecutionException, IOException, CacheDirectoryCreationException, InvalidImageReferenceException {
        service.start(new Application.ApplicationBuilder().setId(5).build());
        var result = service.getNextId();

        assertEquals(6, result);
    }

    //TODO les tests pour removeAllByDockerInstanceName()

    @Test @Tag("save")
    void shouldThrowNullPointerExceptionWhenSaveNullApp() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> service.start(null)),
                () -> assertEquals(0, service.size())
        );
    }

    @Test @Tag("save")
    void shouldSaveAndReturnApplication() throws RegistryException, InterruptedException, ExecutionException, IOException, CacheDirectoryCreationException, InvalidImageReferenceException {
        var app = new Application.ApplicationBuilder().buildRandom();

        var actual = service.start(app);

        assertAll(
                () -> assertEquals(actual, app),
                () -> assertEquals(1, service.size())
        );
    }

    @Test @Tag("size")
    void shouldReturnGoodSize() throws RegistryException, InterruptedException, ExecutionException, IOException, CacheDirectoryCreationException, InvalidImageReferenceException {
        for (var i = 1; i < 20; i++) {
            service.start(new Application.ApplicationBuilder().buildRandom());
        }

        assertEquals(19, service.size());
    }
    */
}
