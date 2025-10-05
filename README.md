# UP Parser for Java

[![CI](https://github.com/uplang/java/workflows/CI/badge.svg)](https://github.com/uplang/java/actions)
[![Maven Central](https://img.shields.io/maven-central/v/com.uplang/up-parser.svg)](https://search.maven.org/artifact/com.uplang/up-parser)
[![Documentation](https://img.shields.io/badge/docs-javadoc-blue.svg)](https://uplang.github.io/java/)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

Official Java implementation of the UP (Unified Properties) language parser.

📚 **[API Documentation](https://uplang.github.io/java/)** | 🧪 **[Test Status](https://github.com/uplang/java/actions)** | 📖 **[Specification](https://github.com/uplang/spec)**

> **Modern Java 21+** - Uses records, sealed types, and pattern matching

## Features

- ✅ **Full UP Syntax Support** - Scalars, blocks, lists, tables, multiline strings
- ✅ **Type Annotations** - Parse and preserve type hints (`!int`, `!bool`, etc.)
- ✅ **Modern Java 21+** - Records, sealed interfaces, pattern matching
- ✅ **Immutable Data Structures** - Thread-safe, functional design
- ✅ **Type-Safe** - Exhaustive pattern matching with sealed types
- ✅ **Well-Tested** - Comprehensive JUnit 5 test suite
- ✅ **Zero Dependencies** - No external runtime dependencies
- ✅ **Maven-Based** - Standard Java build tooling

## Requirements

- Java 21 or later
- Maven 3.6+ (for building)

## Installation

### Maven

```xml
<dependency>
    <groupId>com.uplang</groupId>
    <artifactId>up-parser</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Build from Source

```bash
git clone https://github.com/uplang/java
cd java
mvn clean install
```

## Quick Start

```java
import com.uplang.parser.*;

public class Example {
    public static void main(String[] args) throws Exception {
        // Create a parser
        Parser parser = new Parser();

        // Parse UP content
        Document doc = parser.parseString("""
            name Alice
            age!int 30
            config {
              debug!bool true
            }
            """);

        // Access values
        String name = doc.getScalar("name").orElse("Unknown");
        System.out.println("Name: " + name);

        // Pattern matching (Java 21+)
        for (Node node : doc.nodes()) {
            switch (node.value()) {
                case Value.Scalar s -> System.out.println(s.value());
                case Value.Block b -> System.out.println("Block: " + b.entries().size());
                default -> System.out.println("Other type");
            }
        }
    }
}
```

**📖 For detailed examples and tutorials, see [QUICKSTART.md](QUICKSTART.md)**

## Documentation

- **[QUICKSTART.md](QUICKSTART.md)** - Getting started guide with examples
- **[DESIGN.md](DESIGN.md)** - Architecture and design decisions
- **[UP Specification](https://github.com/uplang/spec)** - Complete language specification

## API Overview

### Core Classes

- **`Parser`** - Main parser for converting UP text into documents
- **`Document`** - Immutable collection of nodes with convenient access methods
- **`Node`** - Key-value pair with optional type annotation (record)
- **`Value`** - Sealed interface with subtypes (Scalar, Block, List, Table, Multiline)

### Basic Usage

```java
Parser parser = new Parser();

// Parse from String
Document doc = parser.parseString(content);

// Parse from Reader
Document doc = parser.parse(new FileReader("config.up"));

// Access values
Optional<String> name = doc.getScalar("name");
Optional<Value.Block> server = doc.getBlock("server");
Optional<Value.List> tags = doc.getList("tags");
```

**See [DESIGN.md](DESIGN.md) for complete API documentation and implementation details.**

## Testing

```bash
# Run all tests
mvn test

# Run with verbose output
mvn test -Dsurefire.printSummary=true

# Run specific test
mvn test -Dtest=ParserTest
```

## Project Structure

```
java/
├── src/main/java/com/uplang/parser/
│   ├── Value.java          # Sealed value types
│   ├── Node.java           # Key-value record
│   ├── Document.java       # Parsed document
│   ├── Parser.java         # Main parser
│   ├── Scanner.java        # Line scanner
│   ├── ParseException.java # Error handling
│   └── Example.java        # Usage examples
├── src/test/java/com/uplang/parser/
│   └── ParserTest.java     # Comprehensive tests
├── pom.xml                 # Maven configuration
├── README.md               # This file
├── QUICKSTART.md           # Getting started guide
├── DESIGN.md               # Architecture documentation
└── LICENSE                 # GNU GPLv3
```

## Contributing

Contributions are welcome! Please see the main [CONTRIBUTING.md](https://github.com/uplang/spec/blob/main/CONTRIBUTING.md) for guidelines.

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## Links

- **[UP Language Specification](https://github.com/uplang/spec)** - Official language spec
- **[Syntax Reference](https://github.com/uplang/spec/blob/main/SYNTAX-REFERENCE.md)** - Quick syntax guide
- **[UP Namespaces](https://github.com/uplang/ns)** - Official namespace plugins

### Other Implementations

- **[Go](https://github.com/uplang/go)** - Reference implementation
- **[JavaScript/TypeScript](https://github.com/uplang/js)** - Browser and Node.js support
- **[Python](https://github.com/uplang/py)** - Pythonic implementation with dataclasses
- **[Rust](https://github.com/uplang/rust)** - Zero-cost abstractions and memory safety
- **[C](https://github.com/uplang/c)** - Portable C implementation

## Support

- **Issues**: [github.com/uplang/java/issues](https://github.com/uplang/java/issues)
- **Discussions**: [github.com/uplang/spec/discussions](https://github.com/uplang/spec/discussions)
