package com.valkyrlabs.OpenXLS;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChartSeriesHandleTest {

    @Test
    void testChartSeriesHandleCreation() throws SheetNotFoundException {
        WorkBookHandle book = new WorkBookHandle();
        WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
        // Need to create a chart and series first to get a ChartSeriesHandle
        // This is just a placeholder, more comprehensive tests will be needed
        // Since we can't create a chart, we can't test ChartSeriesHandle creation
    }

    // Add more tests here for other ChartSeriesHandle methods
}
