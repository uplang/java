package com.uplang.parser;

/**
 * Exception thrown when parsing UP documents fails.
 */
public class ParseException extends Exception {

    private final int lineNumber;
    private final String line;

    public ParseException(String message, int lineNumber, String line) {
        super(formatMessage(message, lineNumber, line));
        this.lineNumber = lineNumber;
        this.line = line;
    }

    public ParseException(String message, int lineNumber, String line, Throwable cause) {
        super(formatMessage(message, lineNumber, line), cause);
        this.lineNumber = lineNumber;
        this.line = line;
    }

    private static String formatMessage(String message, int lineNumber, String line) {
        return String.format("Parse error at line %d: %s%n  Line: %s", lineNumber, message, line);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getLine() {
        return line;
    }
}

