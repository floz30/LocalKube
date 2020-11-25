package fr.umlv.localkube.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WindowsTest {
    private final Windows os = new Windows();

    @Test
    void getSeparator_Success() {
        assertEquals("\\\\", os.getSeparator());
    }

    @Test
    void getCMD_Success() {
        assertEquals("cmd.exe", os.getCMD());
    }

    @Test
    void getParent_Success() {
        assertEquals("..", os.getParent());
    }

    @Test
    void getOption_Success() {
        assertEquals("/c", os.getOption());
    }

    @Test
    void getHostOption_Success() {
        assertEquals("", os.getHostOption());
    }

}
