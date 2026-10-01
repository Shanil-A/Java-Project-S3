# Java-Project-S3

A Java Swing desktop application for managing second-hand book purchases, sales, inventory, and profit using SQLite, JDBC, and SQL with a simple, modern interface.

## Requirements

- **Java JDK 21** or higher ([Eclipse Temurin](https://adoptium.net/) recommended)

## Quick Start

```bash
# Compile
javac -cp lib/sqlite-jdbc-3.42.0.0.jar -d out src/main/java/oldbookstore/*.java

# Run
java -cp out;lib/sqlite-jdbc-3.42.0.0.jar oldbookstore.Main

# Run Tests
java -cp out;lib/sqlite-jdbc-3.42.0.0.jar oldbookstore.TestAll
```

> **Note:** On Linux/Mac, use `:` instead of `;` in the classpath.

## Building with Maven (optional)

If Apache Maven is installed:

```bash
mvn compile        # Compile
mvn test           # Run tests
mvn package        # Package JAR
```

The `pom.xml` is configured for Java 21.
