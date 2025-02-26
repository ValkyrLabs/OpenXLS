package com.valkyrlabs.OpenXLS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

public class AutoFilterHandleTest {

    @Test
    void testAutoFilterHandleCreation() throws SheetNotFoundException {
        try (// Need a WorkSheetHandle to create an AutoFilterHandle
        WorkBookHandle book = new WorkBookHandle()) {
            WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
            AutoFilterHandle[] autoFilters = sheet.getAutoFilterHandles();
            assertNotNull(autoFilters);
            assertEquals(0, autoFilters.length); // Initially empty if no autofilter is set
        }
    }

    // Add more tests here for other AutoFilterHandle methods
}
