package fr.umlv.localkube.controller;

import fr.umlv.localkube.configuration.DockerProperties;
import fr.umlv.localkube.configuration.LocalKubeConfiguration;
import fr.umlv.localkube.manager.DockerManager;
import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.repository.ApplicationRepository;
import fr.umlv.localkube.utils.OperatingSystem;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Disabled
@ExtendWith(MockitoExtension.class)
public class ApplicationControllerTest {

    @Mock
    private ApplicationRepository repository ;//= mock(ApplicationRepository.class);
    @Mock
    private DockerManager manager ;//= mock(DockerManager.class);
    @Mock
    private LocalKubeConfiguration configuration ;//= mock(LocalKubeConfiguration.class);

   /* @BeforeAll
    static void setUp() {
        MockitoAnnotations.openMocks(ApplicationControllerTest);
    }*/

    @Test
    void shouldStartNewApp2() throws IOException {
        // Arrange
        var app = new Application.ApplicationBuilder().setId(2).setApp("toto:8081").build();
        when(repository.getNextId()).thenReturn(35);
        when(repository.save(any(Application.class))).thenReturn(app);
        try {
            doNothing().when(manager).start(any());
            doNothing().when(configuration).addServicePort(anyInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
        var controller = new ApplicationController(repository, configuration, manager);

        // Act
        var result = controller.start(new ApplicationController.StartApplicationData("todo:8081"));

        // Assert
        var expected = ResponseEntity.status(HttpStatus.OK).body(app);
        assertEquals(expected, result);
    }

    @Test
    void shouldStartNewApp() throws IOException {
        // Arrange
        var app = new Application.ApplicationBuilder().setId(2).setApp("toto:8081").build();
        var repository = spy(new ApplicationRepository());
        var manager = spy(new DockerManager(mock(OperatingSystem.class),new DockerProperties()));
        var configuration = spy(new LocalKubeConfiguration(mock(ServletWebServerApplicationContext.class)));

        doReturn(1).when(repository).getNextId();
        doReturn(app).when(repository).save(any());

        try {
            doNothing().when(manager).start(any());
            doNothing().when(configuration).addServicePort(anyInt());
        } catch (Exception e) {
            e.printStackTrace();
        }

        var controller = new ApplicationController(repository, configuration, manager);



        // Act
        var result = controller.start(new ApplicationController.StartApplicationData("todo:8081"));

        // Assert
        var expected = ResponseEntity.status(HttpStatus.OK).body(app);
        assertEquals(expected, result);
    }
}
