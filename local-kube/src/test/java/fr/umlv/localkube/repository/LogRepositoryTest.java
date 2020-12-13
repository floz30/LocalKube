package fr.umlv.localkube.repository;
import fr.umlv.localkube.model.Log;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.Collections;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations="classpath:test.properties")
public class LogRepositoryTest {

    @Autowired
    LogRepository logRepository;

    @Test
    @Tag("init")
    void shouldNotFailOnInit(){
        assertDoesNotThrow(() -> logRepository.init());
    }

    @Test
    @Tag("init")
    void LogTableShouldBeEmptyOnCreation(){
        logRepository.init();
        assertEquals(Collections.<Log>emptyList(),logRepository.findAll());
    }

    @Test
    @Tag("save")
    void shouldFailOnSavingLog(){
        logRepository.init();
        logRepository.save(1,"test", Instant.now());
        assertThrows(NoSuchElementException.class,() -> logRepository.findAll());
    }

    @Test
    @Tag("save")
    void shouldFailOnNullColumn(){
        logRepository.init();
        assertAll(
                () -> assertThrows(UnableToExecuteStatementException.class,() -> {logRepository.save(1,"test",null);logRepository.findAll();}),
                () -> assertThrows(UnableToExecuteStatementException.class,() -> {logRepository.save(1,null,Instant.now());logRepository.findAll();})
        );
    }

    @Test
    @Tag("findAll")
    void shouldGetEmptyList(){
        logRepository.init();
        assertEquals(Collections.<Log>emptyList(),logRepository.findAll());
    }

    @Test
    @Tag("findAll")
    void shouldNotFailOnNullTime(){
        logRepository.init();
        assertAll(() -> assertDoesNotThrow(() -> logRepository.findAll(null)),() -> assertDoesNotThrow(() -> logRepository.findAllFilterById(null,1)));

    }

}
