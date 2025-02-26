package com.valkyrlabs.OpenXLS;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NameHandleTest {

    @Test
    void testNameHandleCreation() throws SheetNotFoundException, CellNotFoundException {
        WorkBookHandle book = new WorkBookHandle();
        WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
        sheet.add("test", "A1");
        CellRange range = new CellRange("Sheet1!A1", book);
        NameHandle name = new NameHandle("MyName", range);
        assertNotNull(name);
    }

    // Add more tests here for other NameHandle methods
}
