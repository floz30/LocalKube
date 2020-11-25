package fr.umlv.localkube.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationRepositoryTest {

    private ApplicationRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ApplicationRepository();
    }

    @Test @Tag("save")
    void shouldThrowNullPointerExceptionWhenSaveNullApp() {
        assertThrows(NullPointerException.class, () -> repository.save(null));
        assertEquals(0, repository.size());
    }

    @Test @Tag("remove")
    void shouldThrowNullPointerExceptionWhenRemoveNull() {
        assertThrows(NullPointerException.class, () -> repository.remove(null));
    }

    @Test @Tag("getNextId")
    void shouldReturnOneIfAppsIsEmpty() {
        assertEquals(1, repository.getNextId());
    }


    @Test @Tag("findById")
    void shouldNotFindAppWithFalseId() {
        var result = repository.findById(-1);

        assertTrue(result.isEmpty());
    }


}
