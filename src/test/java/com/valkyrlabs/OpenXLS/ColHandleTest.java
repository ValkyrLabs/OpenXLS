package com.valkyrlabs.OpenXLS;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ColHandleTest {

    @Test
    void testColHandleCreation() throws SheetNotFoundException, ColumnNotFoundException {
        WorkBookHandle book = new WorkBookHandle();
        WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
        ColHandle col = sheet.getCol("A");
        assertNotNull(col);
    }

    @Test
    void testGetColWidth() throws SheetNotFoundException, ColumnNotFoundException {
        WorkBookHandle book = new WorkBookHandle();
        WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
        ColHandle col = sheet.getCol("A");
        assertNotNull(col.getWidth());
    }

    // Add more tests here for other ColHandle methods
}
