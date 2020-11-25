package fr.umlv.localkube.model;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationTest {

    @Test @Tag("initializeApp")
    void shouldThrowNullPointerExceptionInInitialize() {
        assertThrows(NullPointerException.class, () -> Application.initializeApp(null, 1));
    }

    @Test @Tag("initializeApp")
    void shouldThrowIllegalArgumentExceptionInInitialize() {
        assertThrows(IllegalArgumentException.class, () -> Application.initializeApp(null, 0));
    }

}
