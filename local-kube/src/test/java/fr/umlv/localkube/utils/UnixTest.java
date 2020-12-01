package fr.umlv.localkube.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UnixTest {
    private final Unix os = new Unix();

    @Test
    void getCMD_Success() {
        assertEquals("bash", os.getCMD());
    }

    @Test
    void getOption_Success() {
        assertEquals("-c", os.getOption());
    }

    @Test
    void getHostOption_Success() {
        assertEquals("--add-host host.docker.internal:$(ip addr show docker0 | grep -Po 'inet \\K[\\d.]+')",
                os.getHostOption());
    }

}
