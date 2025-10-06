package com.uplang.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UP (Unified Properties) parser for Java.
 * Uses modern Java 21+ features: records, pattern matching, sealed types.
 */
public class Parser {

    /**
     * Parse UP document from a string.
     */
    public static Document parse(String input) throws ParseException {
        return new Parser().parseDocument(input);
    }

    /**
     * Parse UP document from a string (instance method).
     */
    public Document parseDocument(String input) throws ParseException {
        String[] lines = input.split("\n", -1);
        List<Node> nodes = new ArrayList<>();
        int i = 0;

        while (i < lines.length) {
            String line = lines[i];
            String trimmed = line.trim();

            // Skip empty lines and comments
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                i++;
                continue;
            }

            // Skip stray closing braces
            if (trimmed.equals("}") || trimmed.equals("]")) {
                i++;
                continue;
            }

            try {
                ParseResult result = parseLine(lines, i);
                nodes.add(result.node);
                i = result.nextIndex;
            } catch (Exception e) {
                throw new ParseException("line " + (i + 1) + ": " + e.getMessage(), e);
            }
        }

        return new Document(nodes);
    }

    private ParseResult parseLine(String[] lines, int startIndex) {
        String line = lines[startIndex];
        String[] keyVal = splitKeyValue(line);
        String keyPart = keyVal[0];
        String valPart = keyVal[1];

        String[] keyType = parseKeyAndType(keyPart);
        String key = keyType[0];
        String typeAnnotation = keyType[1];

        ValueResult valueResult = parseValue(lines, startIndex, valPart, typeAnnotation);

        Node node = new Node(key, valueResult.value, typeAnnotation);
        return new ParseResult(node, valueResult.nextIndex);
    }

    private String[] splitKeyValue(String line) {
        int idx = -1;
        for (int i = 0; i < line.length(); i++) {
            if (Character.isWhitespace(line.charAt(i))) {
                idx = i;
                break;
            }
        }

        if (idx == -1) {
            return new String[]{line.trim(), ""};
        }

        return new String[]{
            line.substring(0, idx).trim(),
            line.substring(idx).trim()
        };
    }

    private String[] parseKeyAndType(String keyPart) {
        int idx = keyPart.indexOf('!');
        if (idx == -1) {
            return new String[]{keyPart, null};
        }
        return new String[]{
            keyPart.substring(0, idx),
            keyPart.substring(idx + 1)
        };
    }

    private ValueResult parseValue(String[] lines, int startIndex, String valPart, String typeAnnotation) {
        // Multiline string
        if (valPart.startsWith("```")) {
            return parseMultiline(lines, startIndex + 1, typeAnnotation);
        }

        // Block
        if (valPart.equals("{")) {
            return parseBlock(lines, startIndex + 1);
        }

        // List
        if (valPart.equals("[")) {
            return parseList(lines, startIndex + 1);
        }

        // Inline list
        if (valPart.startsWith("[") && valPart.endsWith("]")) {
            List<Value> list = parseInlineList(valPart);
            return new ValueResult(Value.ofList(list), startIndex + 1);
        }

        // Scalar
        return new ValueResult(Value.ofString(valPart), startIndex + 1);
    }

    private ValueResult parseMultiline(String[] lines, int startIndex, String typeAnnotation) {
        List<String> content = new ArrayList<>();
        int i = startIndex;

        while (i < lines.length) {
            String line = lines[i];
            String trimmed = line.trim();

            if (trimmed.equals("```")) {
                i++;
                break;
            }

            content.add(line);
            i++;
        }

        String text = String.join("\n", content);

        // Apply dedenting if type annotation is a number
        if (typeAnnotation != null) {
            try {
                int dedentAmount = Integer.parseInt(typeAnnotation);
                text = dedent(text, dedentAmount);
            } catch (NumberFormatException ignored) {
                // Not a number, skip dedenting
            }
        }

        return new ValueResult(Value.ofString(text), i);
    }

    private ValueResult parseBlock(String[] lines, int startIndex) {
        Map<String, Value> block = new HashMap<>();
        int i = startIndex;

        while (i < lines.length) {
            String line = lines[i];
            String trimmed = line.trim();

            if (trimmed.equals("}")) {
                i++;
                break;
            }

            // Skip empty lines and comments
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                i++;
                continue;
            }

            ParseResult result = parseLine(lines, i);
            block.put(result.node.key(), result.node.value());
            i = result.nextIndex;
        }

        return new ValueResult(Value.ofBlock(block), i);
    }

    private ValueResult parseList(String[] lines, int startIndex) {
        List<Value> list = new ArrayList<>();
        int i = startIndex;

        while (i < lines.length) {
            String line = lines[i];
            String trimmed = line.trim();

            if (trimmed.equals("]")) {
                i++;
                break;
            }

            // Skip empty lines and comments
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                i++;
                continue;
            }

            // Inline list within multiline list
            if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                list.add(Value.ofList(parseInlineList(trimmed)));
                i++;
            }
            // Nested block
            else if (trimmed.equals("{")) {
                ValueResult blockResult = parseBlock(lines, i + 1);
                list.add(blockResult.value);
                i = blockResult.nextIndex;
            }
            // Scalar
            else {
                list.add(Value.ofString(trimmed));
                i++;
            }
        }

        return new ValueResult(Value.ofList(list), i);
    }

    private List<Value> parseInlineList(String s) {
        String content = s.trim();
        content = content.substring(1, content.length() - 1); // Remove [ and ]

        if (content.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String[] items = content.split(",");
        List<Value> result = new ArrayList<>();
        for (String item : items) {
            result.add(Value.ofString(item.trim()));
        }
        return result;
    }

    private String dedent(String text, int amount) {
        String[] lines = text.split("\n", -1);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.length() >= amount) {
                result.append(line.substring(amount));
            } else {
                result.append(line);
            }
            if (i < lines.length - 1) {
                result.append("\n");
            }
        }

        return result.toString();
    }

    // Helper records for internal use
    private record ParseResult(Node node, int nextIndex) {}
    private record ValueResult(Value value, int nextIndex) {}
}
