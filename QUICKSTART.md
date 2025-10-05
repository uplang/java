# Quick Start Guide

Get started with the UP Parser for Java in 5 minutes!

## Prerequisites

- Java 21 or later
- Maven 3.6+ (optional, for building)

## Installation

### Option 1: Add to Existing Maven Project

Add to your `pom.xml`:

```xml
<dependency>
    <groupId>com.uplang</groupId>
    <artifactId>up-parser</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Option 2: Build from Source

```bash
git clone https://github.com/uplang/java
cd java
mvn clean install
```

## Your First Program

Create `MyFirstParser.java`:

```java
import com.uplang.parser.*;

public class MyFirstParser {
    public static void main(String[] args) throws Exception {
        // Create a parser
        Parser parser = new Parser();

        // Parse some UP content
        String config = """
            name "My App"
            port!int 8080
            debug!bool true
            """;

        Document doc = parser.parseString(config);

        // Read values
        System.out.println("Name: " + doc.getScalar("name").orElse("Unknown"));
        System.out.println("Port: " + doc.getScalar("port").orElse("8080"));
        System.out.println("Debug: " + doc.getScalar("debug").orElse("false"));
    }
}
```

Compile and run:

```bash
javac -cp up-parser-1.0.0.jar MyFirstParser.java
java -cp .:up-parser-1.0.0.jar MyFirstParser
```

## Common Use Cases

### 1. Configuration Files

```java
Parser parser = new Parser();

// Parse configuration
try (FileReader reader = new FileReader("app.up")) {
    Document config = parser.parse(reader);

    // Access nested configuration
    config.getBlock("server").ifPresent(server -> {
        String host = ((Value.Scalar) server.entries().get("host")).value();
        String port = ((Value.Scalar) server.entries().get("port")).value();
        System.out.println("Server: " + host + ":" + port);
    });
}
```

### 2. Data Serialization

```java
// Parse data from string
String data = """
    users [
      Alice
      Bob
      Charlie
    ]
    """;

Document doc = parser.parseString(data);
doc.getList("users").ifPresent(users -> {
    users.items().forEach(user -> {
        if (user instanceof Value.Scalar s) {
            System.out.println("User: " + s.value());
        }
    });
});
```

### 3. Type-Safe Access with Pattern Matching

```java
Document doc = parser.parseString(input);

for (Node node : doc.nodes()) {
    switch (node.value()) {
        case Value.Scalar s ->
            System.out.println(node.key() + " = " + s.value());
        case Value.Block b ->
            System.out.println(node.key() + " is a block with " + b.entries().size() + " entries");
        case Value.List l ->
            System.out.println(node.key() + " is a list with " + l.items().size() + " items");
        default ->
            System.out.println(node.key() + " has complex value");
    }
}
```

## Running the Example

The repository includes a complete example:

```bash
cd java
mvn compile exec:java -Dexec.mainClass="com.uplang.parser.Example"
```

## Running Tests

```bash
mvn test
```

## What's Next?

- Read the [full README](README.md) for comprehensive documentation
- Check out the [UP Specification](https://github.com/uplang/spec)
- Explore example files in `src/test/resources/`
- Try parsing your own UP files!

## Need Help?

- 📚 [Documentation](README.md)
- 💬 [Discussions](https://github.com/uplang/spec/discussions)
- 🐛 [Report Issues](https://github.com/uplang/java/issues)

