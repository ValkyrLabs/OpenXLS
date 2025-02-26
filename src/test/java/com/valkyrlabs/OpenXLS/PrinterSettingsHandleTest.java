package com.valkyrlabs.OpenXLS;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PrinterSettingsHandleTest {

    @Test
    void testPrinterSettingsHandleCreation() throws SheetNotFoundException {
        WorkBookHandle book = new WorkBookHandle();
        WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
        PrinterSettingsHandle printerSettings = sheet.getPrinterSettings();
        assertNotNull(printerSettings);
    }

    // Add more tests here for other PrinterSettingsHandle methods
}
