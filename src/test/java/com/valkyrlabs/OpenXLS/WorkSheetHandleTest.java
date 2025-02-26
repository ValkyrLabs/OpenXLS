package com.valkyrlabs.OpenXLS;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WorkSheetHandleTest {

    @Test
    void testWorkSheetHandleCreation() throws SheetNotFoundException {
        WorkBookHandle book = new WorkBookHandle();
        WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
        assertNotNull(sheet);
    }

    @Test
    void testGetSheetName() throws SheetNotFoundException {
        WorkBookHandle book = new WorkBookHandle();
        WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
        assertEquals("Sheet1", sheet.getSheetName());
    }

    // Add more tests here for other WorkSheetHandle methods
}
