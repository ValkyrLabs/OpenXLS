package com.valkyrlabs.OpenXLS;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConditionalFormatHandleTest {

    @Test
    void testConditionalFormatHandleCreation() throws SheetNotFoundException {
        WorkBookHandle book = new WorkBookHandle();
        WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
        // Need to create a conditional format first to get a ConditionalFormatHandle
        // This is just a placeholder, more comprehensive tests will be needed
    }

    // Add more tests here for other ConditionalFormatHandle methods
}
