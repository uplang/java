package com.uplang.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a parsed UP document.
 */
public record Document(List<Node> nodes) {

    public Document {
        nodes = Collections.unmodifiableList(new ArrayList<>(nodes));
    }

    public Document() {
        this(new ArrayList<>());
    }

    public static Document empty() {
        return new Document();
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public int size() {
        return nodes.size();
    }
}
