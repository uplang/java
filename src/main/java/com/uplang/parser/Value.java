package com.uplang.parser;

import java.util.List;
import java.util.Map;

/**
 * Sealed interface representing any UP value.
 * Uses Java 21+ sealed types for exhaustive pattern matching.
 */
public sealed interface Value
    permits Value.StringValue, Value.BlockValue, Value.ListValue {

    record StringValue(String value) implements Value {}
    record BlockValue(Map<String, Value> value) implements Value {}
    record ListValue(List<Value> value) implements Value {}

    static Value ofString(String s) {
        return new StringValue(s);
    }

    static Value ofBlock(Map<String, Value> block) {
        return new BlockValue(block);
    }

    static Value ofList(List<Value> list) {
        return new ListValue(list);
    }
}
