package com.uplang.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents a parsed UP document.
 * Provides convenient methods to access nodes.
 */
public record Document(List<Node> nodes) {

    public Document {
        nodes = Collections.unmodifiableList(new ArrayList<>(nodes));
    }

    /**
     * Create an empty document.
     */
    public static Document empty() {
        return new Document(List.of());
    }

    /**
     * Get a node by key.
     */
    public Optional<Node> getNode(String key) {
        return nodes.stream()
                .filter(n -> n.key().equals(key))
                .findFirst();
    }

    /**
     * Get a value by key.
     */
    public Optional<Value> getValue(String key) {
        return getNode(key).map(Node::value);
    }

    /**
     * Get a scalar value by key as a string.
     */
    public Optional<String> getScalar(String key) {
        return getValue(key)
                .filter(v -> v instanceof Value.Scalar)
                .map(v -> ((Value.Scalar) v).value());
    }

    /**
     * Get a block value by key.
     */
    public Optional<Value.Block> getBlock(String key) {
        return getValue(key)
                .filter(v -> v instanceof Value.Block)
                .map(v -> (Value.Block) v);
    }

    /**
     * Get a list value by key.
     */
    public Optional<Value.List> getList(String key) {
        return getValue(key)
                .filter(v -> v instanceof Value.List)
                .map(v -> (Value.List) v);
    }

    /**
     * Check if the document is empty.
     */
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    /**
     * Get the number of nodes in the document.
     */
    public int size() {
        return nodes.size();
    }

    @Override
    public String toString() {
        return "Document{nodes=" + nodes.size() + "}";
    }
}

