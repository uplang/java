package com.uplang.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void testParseEmpty() throws ParseException {
        Document doc = Parser.parse("");
        assertNotNull(doc);
        assertTrue(doc.isEmpty());
    }

    @Test
    void testParseSimpleScalar() throws ParseException {
        Document doc = Parser.parse("name John Doe");
        assertEquals(1, doc.size());
        assertEquals("name", doc.nodes().get(0).key());
        assertTrue(doc.nodes().get(0).value() instanceof Value.StringValue);
    }

    @Test
    void testParseTypeAnnotation() throws ParseException {
        Document doc = Parser.parse("age!int 30");
        assertEquals(1, doc.size());
        assertEquals("age", doc.nodes().get(0).key());
        assertEquals("int", doc.nodes().get(0).typeAnnotation());
    }

    @Test
    void testParseBlock() throws ParseException {
        String input = """
            server {
            host localhost
            port!int 8080
            }
            """;
        Document doc = Parser.parse(input);
        assertEquals(1, doc.size());
        assertTrue(doc.nodes().get(0).value() instanceof Value.BlockValue);
    }

    @Test
    void testParseList() throws ParseException {
        String input = """
            fruits [
            apple
            banana
            cherry
            ]
            """;
        Document doc = Parser.parse(input);
        assertEquals(1, doc.size());
        assertTrue(doc.nodes().get(0).value() instanceof Value.ListValue);
    }

    @Test
    void testParseInlineList() throws ParseException {
        Document doc = Parser.parse("colors [red, green, blue]");
        assertEquals(1, doc.size());
        assertTrue(doc.nodes().get(0).value() instanceof Value.ListValue);
        Value.ListValue listVal = (Value.ListValue) doc.nodes().get(0).value();
        assertEquals(3, listVal.value().size());
    }

    @Test
    void testSkipComments() throws ParseException {
        String input = """
            # Comment
            name John
            # Another comment
            age 30
            """;
        Document doc = Parser.parse(input);
        assertEquals(2, doc.size());
    }

    @Test
    void testParseMultiline() throws ParseException {
        String input = """
            description ```
            Line 1
            Line 2
            ```
            """;
        Document doc = Parser.parse(input);
        assertEquals(1, doc.size());
        Value.StringValue val = (Value.StringValue) doc.nodes().get(0).value();
        assertTrue(val.value().contains("Line 1"));
        assertTrue(val.value().contains("Line 2"));
    }

    @Test
    void testDocumentIsEmpty() {
        Document doc = Document.empty();
        assertTrue(doc.isEmpty());
    }
}
