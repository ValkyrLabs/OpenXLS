package com.valkyrlabs.OpenXLS;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class RowHandleTest {

    @Test
    void testRowHandleCreation() throws SheetNotFoundException, RowNotFoundException {
        WorkBookHandle book = new WorkBookHandle();
        WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
        sheet.add("Hello World", "A1").getRow();
        
        RowHandle row0 = sheet.getRow(0);
        assertNotNull(row0);
    }

    // Add more tests here for other RowHandle methods
}
