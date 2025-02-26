package com.valkyrlabs.OpenXLS;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ImageHandleTest {

    @Test
    void testImageHandleCreation() throws SheetNotFoundException {
        WorkBookHandle book = new WorkBookHandle();
        WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
        // Need to create an image first to get an ImageHandle
        // This is just a placeholder, more comprehensive tests will be needed
    }

    // Add more tests here for other ImageHandle methods
}
