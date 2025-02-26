package com.valkyrlabs.OpenXLS;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WorkBookHandleTest {

    @Test
    void testCreateWorkBook() {
        WorkBookHandle book = new WorkBookHandle();
        assertNotNull(book);
    }

    @Test
    void testGetVersion() {
        WorkBookHandle book = new WorkBookHandle();
        String version = book.getVersion();
        assertNotNull(version);
        assertFalse(version.isEmpty());
    }

    @Test
    void testGetWorkSheet() throws SheetNotFoundException {
        WorkBookHandle book = new WorkBookHandle();
        WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
        assertNotNull(sheet);
    }

    @Test
    void testCreateWorkSheet() {
        WorkBookHandle book = new WorkBookHandle();
        book.createWorkSheet("NewSheet");
        try {
            book.getWorkSheet("NewSheet");
        } catch (SheetNotFoundException e) {
            fail("Sheet was not created");
        }
    }

    // Add more tests here for other WorkBookHandle methods
}
