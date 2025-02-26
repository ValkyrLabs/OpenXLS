package com.valkyrlabs.OpenXLS;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CellHandleTest {

    @Test
    void testCellHandleCreation() throws SheetNotFoundException, CellNotFoundException {
        WorkBookHandle book = new WorkBookHandle();
        WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
        sheet.add("test", "A1");
        CellHandle cell = sheet.getCell("A1");
        assertNotNull(cell);
    }

    @Test
    void testGetVal() throws SheetNotFoundException, CellNotFoundException {
        WorkBookHandle book = new WorkBookHandle();
        WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
        sheet.add("test", "A1");
        CellHandle cell = sheet.getCell("A1");
        assertEquals("test", cell.getVal());
    }

    // Add more tests here for other CellHandle methods
}
