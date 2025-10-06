package com.uplang.parser;

/**
 * A key-value node with optional type annotation.
 * Uses Java 21+ record for immutable data.
 */
public record Node(String key, Value value, String typeAnnotation) {

    public Node(String key, Value value) {
        this(key, value, null);
    }

    public boolean hasTypeAnnotation() {
        return typeAnnotation != null;
    }
}
