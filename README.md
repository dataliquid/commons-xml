# commons-xml

[![CI Build](https://github.com/dataliquid/commons-xml/actions/workflows/ci.yml/badge.svg)](https://github.com/dataliquid/commons-xml/actions/workflows/ci.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.dataliquid/commons-xml/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.dataliquid/commons-xml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Welcome to the `commons-xml` project! This repository contains a Java library that provides various utility classes and functions for working with XML documents. The library is designed to make common tasks related to XML processing and manipulation easier.

## Features

- **XML Parsing**: The library provides an easy way to parse XML documents and convert them into a structured data format.
- **XML Creation**: You can programmatically create XML documents while maintaining the desired structure and hierarchy.
- **XPath Support**: The library allows you to use XPath expressions to selectively access specific elements or attributes in an XML document.
- **Validation**: You can validate XML documents against an XML schema and ensure they adhere to the specified rules.

## Requirements

- Java 8 or higher (supports Java 8, 11, 17, and 21)
- Maven 3.6 or higher

## Usage

To use the `commons-xml` library in your project, you can add it as a Maven dependency. Add the following section to your `pom.xml` file:

```xml
<dependencies>
    <dependency>
        <groupId>com.dataliquid</groupId>
        <artifactId>commons-xml</artifactId>
        <version>1.0.2</version>
    </dependency>
</dependencies>
```

## Example

The given example utilizes the DomUtils class from the commons-xml library to create and manipulate an XML document. It performs the following steps:

- Creates an empty XML document using DomUtils.createDocument().
- Creates a root element named "root" and adds it to the document.
- Creates a child element named "child" and sets its text content to "Hello, World!". The child element is appended to the root element.
- Converts the document to an XML string representation using DomUtils.asXml(document).
- Prints the generated XML string to the console.

In summary, this example demonstrates how to create an XML document, add elements to it, and obtain the XML representation of the document using the DomUtils class.

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

This example demonstrates the usage of the `DomUtils` class to create an XML document, add a root element and a child element to it. Then, it retrieves the xml as string and print it out.

Please note that you need to add the `commons-xml` library to your project in order to use the `DomUtils` class.

Follow this link, you will be taken to the [EXAMPLES.md](EXAMPLES.md) file where you can find more examples. These examples are intended to help you understand and utilize the functionality of the Commons-XML project.

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