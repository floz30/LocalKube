package fr.umlv.localkubeapi;

import fr.umlv.localkubeapi.consuming.ConsumingLogFactory;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class ConsumingLogFactoryTest {

    @Test
    void createShouldFailOnInvalidRange() {
        assertAll(() -> assertThrows(IllegalArgumentException.class, () -> new ConsumingLogFactory().create(49151)),
                () -> assertThrows(IllegalArgumentException.class, () -> new ConsumingLogFactory().create(65536)));
    }

    @Test
    void createShouldNotFailOnValidRange() {
        IntStream.range(49152, 65535).forEach(i -> new ConsumingLogFactory().create(i));
    }

    @Test
    void createNotReturningNull() {
        assertNotNull(new ConsumingLogFactory().create(49153));
    }


}
