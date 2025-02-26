package com.valkyrlabs.OpenXLS;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidationHandleTest {

    @Test
    void testValidationHandleCreation() throws SheetNotFoundException {
        WorkBookHandle book = new WorkBookHandle();
        WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
        // Need to create a validation first to get a ValidationHandle
        // This is just a placeholder, more comprehensive tests will be needed
    }

    // Add more tests here for other ValidationHandle methods
}
