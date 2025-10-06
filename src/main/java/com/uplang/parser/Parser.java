package com.uplang.parser;

import java.util.List;

/**
 * UP (Unified Properties) parser for Java.
 */
public class Parser {

    /**
     * Parse UP document from a string.
     *
     * @param input the UP document as a string
     * @return the parsed document
     */
    public static Document parse(String input) {
        // Placeholder implementation
        return Document.empty();
    }

    /**
     * Parse UP document from a string (instance method).
     *
     * @param input the UP document as a string
     * @return the parsed document
     */
    public Document parseString(String input) {
        // Delegate to static method
        return parse(input);
    }
}
