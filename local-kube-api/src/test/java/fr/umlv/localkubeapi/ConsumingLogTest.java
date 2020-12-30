package fr.umlv.localkubeapi;

import fr.umlv.localkubeapi.consuming.ConsumingLog;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class ConsumingLogTest {

    @Test
    void shouldFailOnInvalidRange() {
        assertAll(() -> assertThrows(IllegalArgumentException.class, () -> new ConsumingLog(49151)),
                () -> assertThrows(IllegalArgumentException.class, () -> new ConsumingLog(65536)));
    }

    @Test
    void shouldNotFailOnValidRange() {
        assertDoesNotThrow(() -> IntStream.range(49152, 65535).forEach(ConsumingLog::new));
    }

    @Test
    void insertLogShouldFailOnNull(){
        var consumingLog = new ConsumingLog(49152);
        assertThrows(NullPointerException.class,() -> consumingLog.insertLog(null));
    }
}
