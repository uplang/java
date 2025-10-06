package com.uplang.parser;

/**
 * Example demonstrating usage of the UP parser.
 */
public class Example {

    public static void main(String[] args) {
        Parser parser = new Parser();

        // Example 1: Basic scalars
        basicScalars(parser);

        // Example 2: Blocks
        blocks(parser);

        // Example 3: Lists
        lists(parser);

        // Example 4: Pattern matching
        patternMatching(parser);
    }

    private static void basicScalars(Parser parser) {
        System.out.println("=== Example 1: Basic Scalars ===");

        try {
            String input = """
                    name Alice
                    age!int 30
                    active!bool true
                    email alice@example.com
                    """;

            Document doc = parser.parseString(input);

            System.out.println("Name: " + doc.getScalar("name").orElse("Unknown"));
            System.out.println("Age: " + doc.getScalar("age").orElse("Unknown"));
            System.out.println("Active: " + doc.getScalar("active").orElse("Unknown"));
            System.out.println("Email: " + doc.getScalar("email").orElse("Unknown"));

            // Check type annotations
            doc.getNode("age").ifPresent(node -> {
                if (node.hasType()) {
                    System.out.println("Age type: " + node.type().get());
                }
            });

        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
        }

        System.out.println();
    }

    private static void blocks(Parser parser) {
        System.out.println("=== Example 2: Blocks ===");

        try {
            String input = """
                    server {
                      host localhost
                      port!int 8080
                      ssl!bool true
                    }
                    database {
                      host db.example.com
                      port!int 5432
                      name myapp
                    }
                    """;

            Document doc = parser.parseString(input);

            // Access server block
            doc.getBlock("server").ifPresent(server -> {
                System.out.println("Server:");
                server.entries().forEach((key, value) -> {
                    if (value instanceof Value.Scalar scalar) {
                        System.out.println("  " + key + ": " + scalar.value());
                    }
                });
            });

            System.out.println();

            // Access database block
            doc.getBlock("database").ifPresent(db -> {
                System.out.println("Database:");
                db.entries().forEach((key, value) -> {
                    if (value instanceof Value.Scalar scalar) {
                        System.out.println("  " + key + ": " + scalar.value());
                    }
                });
            });

        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
        }

        System.out.println();
    }

    private static void lists(Parser parser) {
        System.out.println("=== Example 3: Lists ===");

        try {
            String input = """
                    tags [production, web, api]
                    environments [
                      development
                      staging
                      production
                    ]
                    """;

            Document doc = parser.parseString(input);

            // Inline list
            doc.getList("tags").ifPresent(tags -> {
                System.out.println("Tags:");
                tags.items().forEach(item -> {
                    if (item instanceof Value.Scalar scalar) {
                        System.out.println("  - " + scalar.value());
                    }
                });
            });

            System.out.println();

            // Multiline list
            doc.getList("environments").ifPresent(envs -> {
                System.out.println("Environments:");
                envs.items().forEach(item -> {
                    if (item instanceof Value.Scalar scalar) {
                        System.out.println("  - " + scalar.value());
                    }
                });
            });

        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
        }

        System.out.println();
    }

    private static void patternMatching(Parser parser) {
        System.out.println("=== Example 4: Pattern Matching ===");

        try {
            String input = """
                    name Alice
                    config {
                      debug!bool true
                    }
                    tags [web, api]
                    description ```
                    This is a multiline
                    description.
                    ```
                    """;

            Document doc = parser.parseString(input);

            // Use pattern matching to handle different value types
            for (Node node : doc.nodes()) {
                String description = switch (node.value()) {
                    case Value.Scalar s -> "Scalar: " + s.value();
                    case Value.Block b -> "Block with " + b.entries().size() + " entries";
                    case Value.List l -> "List with " + l.items().size() + " items";
                    case Value.Table t -> "Table with " + t.rows().size() + " rows";
                    case Value.Multiline m -> "Multiline (" + m.content().length() + " chars)";
                };

                String type = node.hasType() ? "!" + node.type().get() : "";
                System.out.println(node.key() + type + ": " + description);
            }

        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
        }

        System.out.println();
    }
}

