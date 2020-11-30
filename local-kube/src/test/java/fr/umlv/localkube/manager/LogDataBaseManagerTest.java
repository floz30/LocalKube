package fr.umlv.localkube.manager;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogDataBaseManagerTest {

    @Test
    void shouldNotFailOnInitialization() throws IOException {
        LogDataBaseManager.initialize();
    }

    @Test
    void selectAllShouldWork() throws IOException {
        var manager = LogDataBaseManager.initialize();
        manager.insertLog(0,"test",new Timestamp(1));
        assertEquals("Log[appId=0, message=test, timestamp=1970-01-01 01:00:00.001]",manager.selectAll().get(0).toString());
    }

    @Test
    void dataBaseTableMustBeEmptyOnInitialization() throws IOException {
        var manager = LogDataBaseManager.initialize();
        assertEquals(0,manager.selectAll().size());
    }

    @Test
    void insertLogShouldInsertNewLogInDataBase() throws IOException {
        var manager = LogDataBaseManager.initialize();
        manager.insertLog(0,"test",new Timestamp(1));
        assertEquals(1,manager.selectAll().size());
    }


}
