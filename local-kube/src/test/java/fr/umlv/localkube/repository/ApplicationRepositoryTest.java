package fr.umlv.localkube.repository;

import fr.umlv.localkube.model.Application;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationRepositoryTest {
    /**
    private ApplicationRepository fillRepository(int n) {
        var repository = new ApplicationRepository();
        for (var i = 1; i <= n; i++) {
            var port = 8080 + i;
            repository.save(new Application(i, "demo:" + port, port, 49152 + i, "demo_" + port));
        }
        return repository;
    }

    private List<ApplicationRecord> createListOfApp(int n) {
        var list = new ArrayList<ApplicationRecord>();
        for (var i = 1; i <= n; i++) {
            var port = 8080 + i;
            list.add(new ApplicationRecord(i, "demo:" + port, port, 49152 + i, "demo_" + port, "0m0s"));
        }
        return list;
    }

    @Tag("save")
    @Test
    void shouldNotSaveNullApp() {
        var repository = new ApplicationRepository();

        assertThrows(NullPointerException.class, () -> repository.save(null));
        assertEquals(0, repository.size());
    }

    @Tag("save")
    @Test
    void shouldSaveApp() {
        var repository = new ApplicationRepository();
        var app = new Application(1, "demo:8081", 8081, 49152, "demo_8081");

        repository.save(app);

        assertEquals(1, repository.size());
        assertTrue(repository.findById(1).isPresent());
    }

    @Tag("save")
    @Test
    void shouldNotSaveSameApplicationTwice() {
        var repository = new ApplicationRepository();
        var app = new Application(1, "demo:8081", 8081, 49152, "demo_8081");

        repository.save(app);
        repository.save(app);

        assertEquals(1, repository.size());
        assertTrue(repository.findById(1).isPresent());
    }

    @Tag("findById")
    @Test
    void shouldFindAppThanksToId() {
        var repository = fillRepository(20);
        var expectedResult = new Application(12, "demo:8092", 8092, 49164, "demo_8092");

        var result = repository.findById(12);

        assertTrue(result.isPresent());
        assertEquals(expectedResult, result.get());
    }

    @Tag("findById")
    @Test
    void shouldNotFindAppWithFalseId() {
        var repository = new ApplicationRepository();

        var result = repository.findById(99);

        assertTrue(result.isEmpty());
    }

    @Tag("getAll")
    @Test
    void shouldReturnAllApps() {
        var repository = fillRepository(5);
        var expected = createListOfApp(5);

        var result = repository.getAll();

        Assertions.assertIterableEquals(expected, result);
    }
    **/
}
