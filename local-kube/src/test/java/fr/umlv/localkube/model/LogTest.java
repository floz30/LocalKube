package fr.umlv.localkube.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class LogTest {

    //TODO revoir les tests de la classe log
    @Test
    void shouldThrowNullPointerExceptionWhenTimestampIsNull() {
        assertThrows(NullPointerException.class, () -> new Log(0, "", null));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenMessageIsNull() {
        assertThrows(NullPointerException.class, () -> new Log(0, null, Instant.now()));
    }


}
