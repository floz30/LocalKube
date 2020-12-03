package fr.umlv.localkube.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class LogTest {

    @Test
    void shouldThrowNullPointerExceptionWhenTimestampIsNull() {
        assertThrows(NullPointerException.class, () -> new Log(0, 0, "", null));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenMessageIsNull() {
        assertThrows(NullPointerException.class, () -> new Log(0, 0, null, new Timestamp(1)));
    }

}
