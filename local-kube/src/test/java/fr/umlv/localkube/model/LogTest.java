package fr.umlv.localkube.model;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LogTest {

    @Test @Tag("constructor") @Tag("exception")
    void shouldFailOnNull() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> new Log(1, null, 0, 0, "test", "test", Instant.now())),
                () -> assertThrows(NullPointerException.class, () -> new Log(1, "test", 0, 0, null, "test", Instant.now())),
                () -> assertThrows(NullPointerException.class, () -> new Log(1, "test", 0, 0, "test", null, Instant.now())),
                () -> assertThrows(NullPointerException.class, () -> new Log(1, "test", 0, 0, "test", "test", null))
        );
    }

    @Test @Tag("constructor") @Tag("exception")
    void shouldFailOnNegativeId() {
        assertThrows(IllegalArgumentException.class, () -> new Log(-1, "test", 0, 0, "test", "test", Instant.now()));
    }

    @Test @Tag("constructor") @Tag("exception")
    void shouldFailOnNegativePort() {
        assertThrows(IllegalArgumentException.class, () -> new Log(1, "test", -1, 0, "test", "test", Instant.now()));
    }

    @Test @Tag("constructor") @Tag("exception")
    void shouldFailOnNegativePortService() {
        assertThrows(IllegalArgumentException.class, () -> new Log(1, "test", 0, -1, "test", "test", Instant.now()));
    }

}
