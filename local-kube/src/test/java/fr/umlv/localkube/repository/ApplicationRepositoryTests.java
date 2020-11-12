package fr.umlv.localkube.repository;

import fr.umlv.localkube.model.Application;
import fr.umlv.localkube.model.ApplicationDataRecord;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ApplicationRepositoryTests {
    private final ApplicationRepository repository = new ApplicationRepository();

    @Test
    void save_ArgumentNull() {
        assertThrows(NullPointerException.class, () -> repository.save(null));
        assertEquals(0, repository.size());
    }

    @Test
    void save_OK() {
        repository.save(new Application(new ApplicationDataRecord(0, "app:8000", 0, 0, "")));
        assertEquals(1, repository.size());
    }
}
