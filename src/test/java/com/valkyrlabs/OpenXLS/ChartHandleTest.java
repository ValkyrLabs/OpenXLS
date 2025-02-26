package com.valkyrlabs.OpenXLS;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class ChartHandleTest {

    @Test
    void testChartHandleCreation() throws SheetNotFoundException {
        WorkBookHandle book = new WorkBookHandle();
        WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
        // Need to create a chart first to get a ChartHandle
        // This is just a placeholder, more comprehensive tests will be needed
        assertTrue(sheet.getMysheet().getCharts().isEmpty());
        Throwable thrown = assertThrows(NullPointerException.class, () -> new ChartHandle(null, book));
        assertTrue(thrown instanceof NullPointerException);
    }

    // Add more tests here for other ChartHandle methods
}
