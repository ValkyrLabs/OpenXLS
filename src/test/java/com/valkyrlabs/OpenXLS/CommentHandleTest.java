package com.valkyrlabs.OpenXLS;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommentHandleTest {

    @Test
    void testCommentHandleCreation() throws SheetNotFoundException {
        WorkBookHandle book = new WorkBookHandle();
        WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
        // Need to create a comment first to get a CommentHandle
        // This is just a placeholder, more comprehensive tests will be needed
    }

    // Add more tests here for other CommentHandle methods
}
