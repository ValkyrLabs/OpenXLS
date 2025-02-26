package com.valkyrlabs.OpenXLS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

public class FormulaHandleTest {

    @Test
    void testFormulaHandleCreation() throws SheetNotFoundException, CellNotFoundException, FormulaNotFoundException {
        WorkBookHandle book = new WorkBookHandle();
        WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
        sheet.add("=SUM(A1:A10)", "B1");
        FormulaHandle formula = sheet.getFormula("B1");
        assertNotNull(formula);
    }

    @Test
    void testGetFormulaString() throws SheetNotFoundException, CellNotFoundException, FormulaNotFoundException {
        WorkBookHandle book = new WorkBookHandle();
        WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
        sheet.add("=SUM(A1:A10)", "B1");
        FormulaHandle formula = sheet.getFormula("B1");
        assertEquals("=SUM(A1:A10)", formula.getFormulaString());
    }

    // Add more tests here for other FormulaHandle methods
}
