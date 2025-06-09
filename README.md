# commons-xml

[![CI Build](https://github.com/dataliquid/commons-xml/actions/workflows/ci.yml/badge.svg)](https://github.com/dataliquid/commons-xml/actions/workflows/ci.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.dataliquid/commons-xml/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.dataliquid/commons-xml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A lightweight and efficient Java library for XML processing and manipulation. This library provides comprehensive utilities for parsing, creating, validating, and transforming XML documents with a focus on simplicity and performance.

## Features

- **XML Parsing**: Easy-to-use utilities for parsing XML documents from files, resources, or strings
- **XML Creation**: Programmatic XML document creation with fluent API
- **XPath Support**: Advanced XPath expression support with namespace handling
- **Schema Validation**: Validate XML documents against XSD schemas
- **DOM Manipulation**: Comprehensive DOM utilities for working with XML elements and attributes
- **Namespace Support**: Built-in namespace context management for complex XML documents

## Requirements

- Java 8 or higher (supports Java 8, 11, 17, and 21)
- Maven 3.6 or higher

## Installation

### Maven
```xml
<dependency>
    <groupId>com.dataliquid</groupId>
    <artifactId>commons-xml</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Gradle
```gradle
implementation 'com.dataliquid:commons-xml:1.1.0'
```

## Quick Start

Here's a simple example demonstrating XML document creation and manipulation:

```java
import com.dataliquid.commons.xml.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Example {
    public static void main(String[] args) {
        // Create an empty XML document using DomUtils
        Document document = DomUtils.createDocument();

        // Create the root element
        Element rootElement = DomUtils.createElement(document, "root");
        DomUtils.appendChild(document, rootElement);

        // Add a child element
        Element childElement = DomUtils.createElement(document, "child");
        DomUtils.setTextContent(childElement, "Hello, World!");
        DomUtils.appendChild(rootElement, childElement);

        // Represent the document as xml string using DomUtils
        String xml = DomUtils.asXml(document);
        System.out.println("XML: " + xml);
    }
}
```

For more detailed examples including XPath queries, namespace handling, and schema validation, see [EXAMPLES.md](EXAMPLES.md).

## Building from Source

To build the project from source:

```bash
git clone https://github.com/dataliquid/commons-xml.git
cd commons-xml
mvn clean install
```

## Running Tests

```bash
mvn test
```

## Contributing

We welcome contributions to the commons-xml project! If you have found a bug, want to propose an improvement, or add a new feature, please follow these steps:

1. Fork the repository
2. Create a new branch for your changes: `git checkout -b feature/my-feature`
3. Make your changes and add tests
4. Run tests to ensure everything works: `mvn test`
5. Commit your changes: `git commit -m 'Add my feature'`
6. Push to your fork: `git push origin feature/my-feature`
7. Create a Pull Request

Please ensure your code follows the existing code style and includes appropriate tests.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.