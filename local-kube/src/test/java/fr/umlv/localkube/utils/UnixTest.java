package fr.umlv.localkube.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UnixTest {
    private final OperatingSystem os = new Unix();

    @Test
    void shouldReturnCorrectCMD() {
        assertEquals("bash",
                os.getCMD());
    }

    @Test
    void shouldReturnCorrectOption() {
        assertEquals("-c",
                os.getOption());
    }

    @Test
    void shouldReturnCorrectHostOption() {
        assertEquals("--host host.docker.internal:$(ip addr show docker0 | grep -Po 'inet \\K[\\d.]+')",
                os.getHostOption("--host"));
    }

    @Test
    void shouldReturnCorrectWlo1IpAddress() {
        assertEquals("--advertise-addr $(ip -4 addr show wlo1 | grep -oP '(?<=inet\\s)\\d+(.\\d+){3}')",
                os.getWlo1IpAddress());
    }

}
