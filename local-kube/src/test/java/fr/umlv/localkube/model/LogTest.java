package fr.umlv.localkube.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class LogTest {

    @Test
    void log_TimestampNull() {
        assertThrows(NullPointerException.class, () -> new Log(0, "", null));
    }

    @Test
    void log_MessageNull() {
        assertThrows(NullPointerException.class, () -> new Log(0, null, new Timestamp(1)));
    }

}
