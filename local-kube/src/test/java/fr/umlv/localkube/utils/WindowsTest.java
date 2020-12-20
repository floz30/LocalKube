package fr.umlv.localkube.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WindowsTest {
    private final Windows os = new Windows();

    @Test
    void getCMD_Success() {
        assertEquals("cmd.exe", os.getCMD());
    }

    @Test
    void getOption_Success() {
        assertEquals("/c", os.getOption());
    }

    /*
    @Test
    void getHostOption_Success() {
        assertEquals("", os.getHostOption());
    }*/

}
