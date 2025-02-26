package com.valkyrlabs.OpenXLS;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DocumentHandleTest {

    @Test
    void testDocumentHandleCreation() {
        WorkBookHandle document = new WorkBookHandle();
        assertNotNull(document);
    }

    // Add more tests here for other DocumentHandle methods
}
