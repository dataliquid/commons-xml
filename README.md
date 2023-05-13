# commons-xml

Welcome to the `commons-xml` project! This repository contains a Java library that provides various utility classes and functions for working with XML documents. The library is designed to make common tasks related to XML processing and manipulation easier.

## Features

- **XML Parsing**: The library provides an easy way to parse XML documents and convert them into a structured data format.
- **XML Creation**: You can programmatically create XML documents while maintaining the desired structure and hierarchy.
- **XPath Support**: The library allows you to use XPath expressions to selectively access specific elements or attributes in an XML document.
- **Validation**: You can validate XML documents against an XML schema and ensure they adhere to the specified rules.

## Usage

To use the `commons-xml` library in your project, you can add it as a Maven dependency. Add the following section to your `pom.xml` file:

```xml
<dependencies>
    <dependency>
        <groupId>com.dataliquid.commons</groupId>
        <artifactId>commons-xml</artifactId>
        <version>1.0.0</version>
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

public class DomUtilsExample {
    public static void main(String[] args) {
        // Create an empty XML document
        Document document = DomUtils.createDocument();

        // Create the root element
        Element rootElement = document.createElement("root");
        document.appendChild(rootElement);

        // Add a child element
        Element childElement = document.createElement("child");
        childElement.setTextContent("Hello, World!");
        rootElement.appendChild(childElement);

        // Represent the document as xml string
        String xml = DomUtils.asXml(document);
        System.out.println("XML: " + xml);
    }
}
```

This example demonstrates the usage of the `DomUtils` class to create an XML document, add a root element and a child element to it. Then, it retrieves and prints the text content of the child element.

Please note that you need to add the `commons-xml` library to your project in order to use the `DomUtils` class.


## Contribution
We welcome contributions to the commons-xml project! If you have found a bug, want to propose an improvement, or add a new feature, please follow these steps:

- Fork the repository.
- Create a new branch for your changes: git checkout -b my-feature.
- Make your changes and run tests.
- Commit your changes: git commit -m 'Add my feature'.