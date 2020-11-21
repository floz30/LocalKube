package fr.umlv.localkube;

import static org.assertj.core.api.Assertions.assertThat;

import fr.umlv.localkube.controller.ApplicationController;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest
public class LocalKubeApplicationTest {

    @Autowired
    private ApplicationController controller;

    @Test
    void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

}
