package fr.umlv.localkube.repository;

import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.model.Log;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations="classpath:test.properties")
public class LogRepositoryTest {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @BeforeEach
    void initApplicationRepository(){
        applicationRepository.save(Application.initializeApp("demo:8081",1));
        applicationRepository.save(Application.initializeApp("demo:8082",2));
    }


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
    void shouldSaveLog(){
        logRepository.init();
        logRepository.save(1,"test", Instant.now());
        assertEquals(1,logRepository.findAll().size());
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
    void shouldFindAllLog(){
        logRepository.init();
        logRepository.save(1,"test", Instant.now());
        logRepository.save(2,"test", Instant.now());
        assertEquals(2,logRepository.findAll().size());
    }

    @Test
    @Tag("findAll")
    void shouldFindAllLogByTime(){
        logRepository.init();
        logRepository.save(1,"test", Instant.now());
        logRepository.save(2,"test", Instant.now().minusMillis(Duration.ofMinutes(4).toMillis()));
        assertEquals(1,logRepository.findAll(Instant.now().minusMillis(Duration.ofMinutes(2).toMillis())).size());
    }

    @Test
    @Tag("findAll")
    void shouldNotFailOnNullTime(){
        logRepository.init();
        assertAll(() -> assertDoesNotThrow(() -> logRepository.findAll(null)),() -> assertDoesNotThrow(() -> logRepository.findAllFilterById(null,1)));
    }

    @Test
    @Tag("findAllFilterById")
    void shouldFindAllLogByTimeAndById(){
        logRepository.init();
        logRepository.save(1,"test",Instant.now().minusMillis(Duration.ofMinutes(4).toMillis()));
        logRepository.save(2,"test",Instant.now());
        assertEquals(2,logRepository.findAllFilterById(Instant.now().minusMillis(Duration.ofMinutes(2).toMillis()),2).get(0).id());
    }

}
