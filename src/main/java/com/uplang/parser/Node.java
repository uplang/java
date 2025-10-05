package com.uplang.parser;

import java.util.Optional;

/**
 * Represents a key-value pair with optional type annotation.
 * Uses Java 21 record for immutability and concise syntax.
 */
public record Node(String key, Optional<String> type, Value value) {

    public Node(String key, String type, Value value) {
        this(key, Optional.ofNullable(type), value);
    }

    public Node(String key, Value value) {
        this(key, Optional.empty(), value);
    }

    /**
     * Get the type annotation or empty if not specified.
     */
    public Optional<String> getType() {
        return type;
    }

    /**
     * Check if this node has a type annotation.
     */
    public boolean hasType() {
        return type.isPresent();
    }

    @Override
    public String toString() {
        String typeStr = type.map(t -> "!" + t).orElse("");
        return key + typeStr + " = " + value;
    }
}

