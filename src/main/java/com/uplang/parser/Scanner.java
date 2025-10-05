package com.uplang.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

/**
 * Line scanner for UP documents.
 * Wraps a BufferedReader and tracks line numbers.
 */
public class Scanner implements AutoCloseable {

    private final BufferedReader reader;
    private int lineNumber;
    private String currentLine;

    public Scanner(Reader reader) {
        this.reader = reader instanceof BufferedReader br ? br : new BufferedReader(reader);
        this.lineNumber = 0;
        this.currentLine = null;
    }

    /**
     * Read the next line from the input.
     * @return Optional containing the line, or empty if end of input
     */
    public Optional<String> nextLine() throws IOException {
        currentLine = reader.readLine();
        if (currentLine != null) {
            lineNumber++;
        }
        return Optional.ofNullable(currentLine);
    }

    /**
     * Get the current line number (1-indexed).
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Get the current line text.
     */
    public String getCurrentLine() {
        return currentLine;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}

