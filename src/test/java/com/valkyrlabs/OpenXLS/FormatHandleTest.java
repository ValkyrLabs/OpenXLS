package com.valkyrlabs.OpenXLS;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FormatHandleTest {

    @Test
    void testFormatHandleCreation() {
        WorkBookHandle book = new WorkBookHandle();
        FormatHandle format = new FormatHandle(book);
        assertNotNull(format);
    }

    @Test
    void testSetFormatPattern() {
        WorkBookHandle book = new WorkBookHandle();
        FormatHandle format = new FormatHandle(book);
        format.setFormatPattern("m/d/yy");
        assertNotNull(format.getFormatPattern());
    }

    // Add more tests here for other FormatHandle methods
}
