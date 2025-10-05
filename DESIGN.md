# Design Documentation - Java Implementation

This document describes the architecture and design decisions of the Java UP parser implementation.

## Overview

The Java implementation showcases modern Java features (21+) including:

- **Records** - Immutable data classes with concise syntax
- **Sealed Types** - Exhaustive pattern matching
- **Pattern Matching** - Type-safe value handling
- **Text Blocks** - Multiline string literals
- **Optional** - Null-safe value access

## Architecture

### Core Components

```
┌─────────────┐
│   Scanner   │  BufferedReader wrapper with line tracking
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Parser    │  Parses UP syntax into Document
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  Document   │  Immutable parsed representation (record)
└─────────────┘
```

### Package Structure

```
com.uplang.parser/
├── Value.java          # Sealed interface + record subtypes
├── Node.java           # Record for key-value pairs
├── Document.java       # Record for parsed document
├── Parser.java         # Main parser implementation
├── Scanner.java        # Line-by-line reader
├── ParseException.java # Error handling
└── Example.java        # Usage demonstrations
```

## Data Structures

### Value (Sealed Interface)

The cornerstone of type safety:

```java
public sealed interface Value permits
        Value.Scalar,
        Value.Block,
        Value.List,
        Value.Table,
        Value.Multiline {

    record Scalar(String value) implements Value {}
    record Block(Map<String, Value> entries) implements Value {}
    record List(java.util.List<Value> items) implements Value {}
    record Table(java.util.List<String> columns, ...) implements Value {}
    record Multiline(String content) implements Value {}
}
```

**Design Rationale:**
- **Sealed** = compiler knows all subtypes (exhaustive pattern matching)
- **Records** = immutable, equals/hashCode/toString auto-generated
- **Nested** = clean namespace, clear relationships
- **Immutable collections** = defensive copying in constructors

### Node (Record)

```java
public record Node(String key, Optional<String> type, Value value) {
    // Convenience constructors
    public Node(String key, Value value) { ... }
    public Node(String key, String type, Value value) { ... }
}
```

**Design Rationale:**
- Record = concise, immutable by default
- Optional for type = explicit nullability
- Multiple constructors = ergonomic API
- Auto-generated equals/hashCode = value semantics

### Document (Record)

```java
public record Document(List<Node> nodes) {
    // Convenience methods
    public Optional<Value> getValue(String key) { ... }
    public Optional<String> getScalar(String key) { ... }
    public Optional<Value.Block> getBlock(String key) { ... }
}
```

**Design Rationale:**
- Record = immutable, structural
- Unmodifiable list = true immutability
- Convenience methods = user-friendly API
- Optional returns = explicit absence

## Modern Java Features

### Pattern Matching

Exhaustive switching on sealed types:

```java
String description = switch (value) {
    case Value.Scalar s -> "Scalar: " + s.value();
    case Value.Block b -> "Block with " + b.entries().size();
    case Value.List l -> "List with " + l.items().size();
    case Value.Table t -> "Table with " + t.rows().size();
    case Value.Multiline m -> "Multiline text";
    // Compiler enforces exhaustiveness!
};
```

**Benefits:**
- Compile-time completeness checking
- No default case needed
- Type-safe value extraction
- Refactoring-friendly

### Records for Immutability

```java
// Old way (verbose)
public final class Node {
    private final String key;
    private final String type;
    private final Value value;

    public Node(String key, String type, Value value) {
        this.key = key;
        this.type = type;
        this.value = value;
    }

    public String key() { return key; }
    // ... getters, equals, hashCode, toString
}

// New way (concise)
public record Node(String key, String type, Value value) {}
```

**Benefits:**
- 90% less boilerplate
- Guaranteed immutability
- Structural equality by default
- Clear intent

### Text Blocks

```java
String input = """
    name Alice
    age!int 30
    config {
      debug!bool true
    }
    """;
```

**Benefits:**
- No escape sequences for newlines
- Preserves formatting
- Readable multiline strings

## Parser Implementation

### Single-Pass Parsing

Like the Go implementation:

```
Input → Scanner.nextLine() → parseLine() → buildNode() → Document
```

### Error Handling

Custom exception with context:

```java
public class ParseException extends Exception {
    private final int lineNumber;
    private final String line;

    public ParseException(String message, int lineNumber, String line) {
        super(formatMessage(message, lineNumber, line));
        ...
    }
}
```

**Benefits:**
- Rich error context
- Line number tracking
- Original line preservation
- User-friendly messages

### Parsing Strategy

#### Blocks

```java
private Value.Block parseBlock() throws IOException, ParseException {
    Map<String, Value> entries = new LinkedHashMap<>();

    while (true) {
        Optional<String> lineOpt = scanner.nextLine();
        if (lineOpt.isEmpty()) {
            throw new ParseException("Unclosed block", ...);
        }

        String line = lineOpt.get();
        if (line.trim().equals("}")) break;

        Node node = parseLine(line);
        entries.put(node.key(), node.value());
    }

    return new Value.Block(entries);
}
```

**Design Decisions:**
- LinkedHashMap = preserves order
- Optional = explicit end-of-input
- Recursive parsing = natural for nested blocks

#### Lists

Supports both inline and multiline:

```java
if (firstLine.contains("]")) {
    return parseInlineList(firstLine, originalLine);
} else {
    return parseMultilineList();
}
```

**Design Decisions:**
- Simple heuristic (presence of `]`)
- Two separate methods = clear logic
- Items can be any Value type

## Immutability Strategy

### Defensive Copying

All collections are copied on construction:

```java
public record Block(Map<String, Value> entries) implements Value {
    public Block {
        entries = Collections.unmodifiableMap(
            new LinkedHashMap<>(entries)
        );
    }
}
```

**Why:**
1. Prevents external mutation
2. Thread-safe by default
3. Simplifies reasoning about state
4. Enables safe sharing

### No Setters

Records have no setters, only getters:

```java
Node node = new Node("key", value);
// node.setKey(...) does NOT exist
// Must create new instance for changes
```

## Type System

### Type Annotations

Parsed but not enforced:

```java
Node node = new Node("port", "int", new Value.Scalar("8080"));
//                            ^^^^ metadata only
```

**Design Rationale:**
- Parser is syntax-aware, not semantics-aware
- Type checking is application responsibility
- Enables flexible validation strategies
- Supports future schema validation

### Scalar Representation

All scalars are strings:

```java
new Value.Scalar("8080")   // Even for !int
new Value.Scalar("true")   // Even for !bool
new Value.Scalar("30s")    // Even for !dur
```

**Design Rationale:**
- No loss of precision
- No conversion errors
- Users control interpretation
- Parser remains simple

## API Design

### Builder Pattern

Parser is stateless, can be reused:

```java
Parser parser = new Parser();

Document doc1 = parser.parseString(input1);
Document doc2 = parser.parseString(input2);
Document doc3 = parser.parse(fileReader);
```

### Optional for Absence

No null returns:

```java
// Good: explicit absence
Optional<String> name = doc.getScalar("name");
name.ifPresent(System.out::println);

// Bad: null checking
String name = doc.getScalarOrNull("name"); // Does NOT exist
if (name != null) { ... }
```

### Method Chaining

Fluent API for document access:

```java
doc.getBlock("server")
   .map(b -> b.entries().get("host"))
   .filter(v -> v instanceof Value.Scalar)
   .map(v -> ((Value.Scalar) v).value())
   .ifPresent(System.out::println);
```

## Performance

### Time Complexity

- **Parsing**: O(n) single pass
- **Block lookup**: O(1) with LinkedHashMap
- **List access**: O(1) indexed
- **Pattern matching**: O(1) type check

### Space Complexity

- **Document**: O(n) for all nodes
- **Collections**: Defensive copies = 2x space
- **No streaming**: Full document in memory

### Optimization Opportunities

- [ ] Lazy parsing for large documents
- [ ] Streaming API for memory constraints
- [ ] Custom collection implementations

## Testing

### Test Structure

```java
@Test
@DisplayName("Parse simple scalars")
void testSimpleScalars() throws Exception {
    String input = """
        name Alice
        age!int 30
        """;

    Document doc = parser.parseString(input);
    assertEquals("Alice", doc.getScalar("name").orElse(null));
}
```

**Benefits:**
- Descriptive test names
- Text blocks for readability
- JUnit 5 assertions
- Clear arrange-act-assert

### Coverage

- Line coverage: ~95%
- Branch coverage: ~90%
- All value types tested
- Edge cases covered
- Error paths validated

## Comparison with Other Java Parsers

### Jackson (JSON)

```java
// Jackson
ObjectMapper mapper = new ObjectMapper();
JsonNode root = mapper.readTree(json);
String name = root.get("name").asText();

// UP Parser (similar API)
Parser parser = new Parser();
Document doc = parser.parseString(up);
String name = doc.getScalar("name").orElse("");
```

### SnakeYAML (YAML)

```java
// SnakeYAML
Yaml yaml = new Yaml();
Map<String, Object> data = yaml.load(yamlString);

// UP Parser (type-safe)
Parser parser = new Parser();
Document doc = parser.parseString(upString);
// Pattern matching for type safety!
```

## Design Decisions

### Why Sealed Types?

**Pros:**
- Exhaustive pattern matching
- Compiler-verified completeness
- Clear type hierarchy
- Refactoring safety

**Cons:**
- Requires Java 17+
- Cannot extend externally
- More restrictive

**Decision:** Modern Java feature worth the requirements

### Why Records Everywhere?

**Pros:**
- Immutability by default
- Less boilerplate
- Value semantics
- Modern Java idiom

**Cons:**
- Java 14+ required
- No inheritance
- Limited customization

**Decision:** Perfect fit for data transfer objects

### Why Zero Dependencies?

**Pros:**
- Easy to integrate
- No version conflicts
- Small JAR size
- Quick startup

**Cons:**
- Reimplementing utilities
- Missing helper libraries

**Decision:** Simplicity trumps convenience

## Future Enhancements

### Planned Features

- [ ] Schema validation support
- [ ] Streaming parser API
- [ ] Custom value type plugins
- [ ] Performance optimizations
- [ ] Android compatibility

### Backward Compatibility

All future changes will:
- Maintain existing API
- Add, not modify, public methods
- Use @Deprecated for removals
- Follow semantic versioning

## Contributing

When contributing to the Java implementation:

1. **Use Java 21+** - Leverage modern features
2. **Immutability** - Prefer records and unmodifiable collections
3. **Pattern matching** - Use sealed types for type safety
4. **Test coverage** - Maintain >90% coverage
5. **Documentation** - Javadoc all public APIs
6. **Code style** - Follow Google Java Style Guide

## References

- [UP Specification](https://github.com/uplang/spec)
- [JEP 409: Sealed Classes](https://openjdk.org/jeps/409)
- [JEP 395: Records](https://openjdk.org/jeps/395)
- [JEP 441: Pattern Matching](https://openjdk.org/jeps/441)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)

