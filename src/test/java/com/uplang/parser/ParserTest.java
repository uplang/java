package com.uplang.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void testParseEmpty() {
        Document doc = Parser.parse("");
        assertNotNull(doc);
    }

    @Test
    void testParseSimple() {
        Document doc = Parser.parse("name John Doe");
        assertNotNull(doc);
    }

    @Test
    void testDocumentIsEmpty() {
        Document doc = Parser.parse("");
        assertTrue(doc.isEmpty());
    }
}
