package com.uplang.parser;

/**
 * Sealed interface representing any UP value.
 * Uses Java 21 sealed types for exhaustive pattern matching.
 */
public sealed interface Value permits
        Value.Scalar,
        Value.Block,
        Value.List,
        Value.Table,
        Value.Multiline {

    /**
     * Scalar value (string, number, boolean, etc.)
     */
    record Scalar(String value) implements Value {
        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Block value (nested map structure)
     */
    record Block(java.util.Map<String, Value> entries) implements Value {
        public Block {
            entries = java.util.Collections.unmodifiableMap(new java.util.LinkedHashMap<>(entries));
        }

        @Override
        public String toString() {
            return "Block" + entries;
        }
    }

    /**
     * List value (ordered collection)
     */
    record List(java.util.List<Value> items) implements Value {
        public List {
            items = java.util.Collections.unmodifiableList(new java.util.ArrayList<>(items));
        }

        @Override
        public String toString() {
            return "List" + items;
        }
    }

    /**
     * Table value (structured data with columns and rows)
     */
    record Table(java.util.List<String> columns, java.util.List<java.util.List<Value>> rows) implements Value {
        public Table {
            columns = java.util.Collections.unmodifiableList(new java.util.ArrayList<>(columns));
            rows = java.util.Collections.unmodifiableList(
                    rows.stream()
                            .map(java.util.ArrayList::new)
                            .map(java.util.Collections::unmodifiableList)
                            .toList()
            );
        }

        @Override
        public String toString() {
            return String.format("Table{columns=%s, rows=%d}", columns, rows.size());
        }
    }

    /**
     * Multiline string value
     */
    record Multiline(String content) implements Value {
        @Override
        public String toString() {
            return "Multiline{" + content.length() + " chars}";
        }
    }
}

