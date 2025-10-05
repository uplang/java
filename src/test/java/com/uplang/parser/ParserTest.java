package com.uplang.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void testParseEmpty() {
        Parser.Document doc = Parser.parse("");
        assertNotNull(doc);
    }

    @Test
    void testParseSimple() {
        Parser.Document doc = Parser.parse("name John Doe");
        assertNotNull(doc);
    }

    @Test
    void testDocumentData() {
        Parser.Document doc = Parser.parse("");
        assertNotNull(doc.getData());
    }
}
