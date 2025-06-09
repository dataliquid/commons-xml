# Commons-XML Examples

This document provides comprehensive examples of using the commons-xml library for various XML processing tasks. The library's main utility class, `DomUtils`, offers a wide range of methods for parsing, manipulating, validating, and querying XML documents.

## Parsing XML

### Parse XML from String

```java
import com.dataliquid.commons.xml.DomUtils;

String xmlString = "<root><element>Value</element></root>";

Document document = DomUtils.parse(xmlString);
// You can further process the parsed Document object here
```

## DOM Manipulation

### Insert Element with Ordering

```java
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.dataliquid.commons.xml.DomUtils;

// Assume we have a parent Node (parentNode) and an element to insert (elementToInsert)
Node parentNode = ...; // Your parent Node
Element elementToInsert = ...; // The element to insert

// Assume we have a List of ordered node names
List<String> orderedNodeNames = Arrays.asList("node1", "node2", "node3");

Element insertedElement = DomUtils.insertElement(parentNode, elementToInsert, orderedNodeNames);
// The element is inserted into the parent node according to the ordered node names
// You can further process the insertedElement here
```

### Insert Element Before Reference Node

```java
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.dataliquid.commons.xml.DomUtils;

// Assume we have a reference Node and an Element to insert
Node referenceNode = ...; // Your reference Node
Element elementToInsert = ...; // The Element to insert

Element insertedElement = DomUtils.insertElementBefore(referenceNode, elementToInsert);
// The method inserts the Element before the reference Node and returns the inserted Element
```

### Insert Element After Reference Node

```java
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.dataliquid.commons.xml.DomUtils;

// Assume we have a reference Node and an Element to insert
Node referenceNode = ...; // Your reference Node
Element elementToInsert = ...; // The Element to insert

Element insertedElement = DomUtils.insertElementAfter(referenceNode, elementToInsert);
// The method inserts the Element after the reference Node and returns the inserted Element
```

### Delete Multiple Nodes

```java
import org.w3c.dom.Node;
import com.dataliquid.commons.xml.DomUtils;

// Assume we have a List of nodes to delete
List<Node> nodesToDelete = ...; // Your List of nodes to delete

DomUtils.delete(nodesToDelete);
// The nodes in the list are deleted from their parent nodes
```

## XPath Queries

### Select String Value with XPath

```java
import org.w3c.dom.Node;
import com.dataliquid.commons.xml.DomUtils;

// Assume we have a node and an XPath expression
Node node = ...; // Your node
String xpath = "..."; // Your XPath expression

String selectedString = DomUtils.selectString(node, xpath);
// The method selects the string value based on the provided XPath expression

// You can further process the selectedString here
```

### Select Child Nodes

```java
import org.w3c.dom.Node;
import com.dataliquid.commons.xml.DomUtils;

// Assume we have a parent node
Node parentNode = ...; // Your parent node

List<Node> childNodes = DomUtils.selectChildNodes(parentNode);
// The method selects and returns a list of child nodes of the parent node

// You can further process the childNodes list here
```

## Node Operations

### Copy Attributes Between Elements

```java
import org.w3c.dom.Element;
import com.dataliquid.commons.xml.DomUtils;

// Assume we have a source element and a destination element
Element sourceElement = ...; // Your source element
Element destinationElement = ...; // Your destination element

DomUtils.copyAttributes(sourceElement, destinationElement);
// The method copies the attributes from the source element to the destination element
```

### Copy Children Between Elements

```java
import org.w3c.dom.Element;
import com.dataliquid.commons.xml.DomUtils;

// Assume we have a source element and a destination element
Element sourceElement = ...; // Your source element
Element destinationElement = ...; // Your destination element

DomUtils.copyChildren(sourceElement, destinationElement);
// The method copies the children nodes from the source element to the destination element
```

### Rename Node

```java
import org.w3c.dom.Node;
import com.dataliquid.commons.xml.DomUtils;

// Assume we have a node and a new name
Node node = ...; // Your node
String newName = "..."; // The new name for the node

Node renamedNode = DomUtils.renameNode(node, newName);
// The method renames the node with the new name and returns the renamed node
```

## Validation

### Validate Document Against Schema

```java
import org.w3c.dom.Document;
import javax.xml.validation.Schema;
import com.dataliquid.commons.xml.DomUtils;

// Assume we have a Document and a Schema
Document document = ...; // Your Document to validate
Schema schema = ...; // Your Schema for validation

boolean isValid = DomUtils.validate(document, schema);
// The method validates the Document against the provided Schema and returns true if it's valid

if (isValid) {
    // Document is valid
    // You can further process the validated document here
} else {
    // Document is invalid
    // Handle the validation failure
}
```

## Serialization

### Convert Node to XML String

```java
import org.w3c.dom.Node;
import com.dataliquid.commons.xml.DomUtils;

// Assume we have a Node and a flag for indentation
Node node = ...; // Your Node
boolean indent = true; // Whether to indent the XML output

String xmlString = DomUtils.asXml(node, indent);
// The method converts the Node to its XML representation as a String

// You can further process the xmlString here
```

## Additional Examples

### Parse XML from File

```java
import com.dataliquid.commons.xml.DomUtils;
import java.io.File;

File xmlFile = new File("path/to/your/file.xml");
Document document = DomUtils.parse(xmlFile);
```

### Parse XML from Resource

```java
import com.dataliquid.commons.xml.DomUtils;

// Parse XML from classpath resource
Document document = DomUtils.parseResource("xml/config.xml");
```

### Working with Namespaces

```java
import com.dataliquid.commons.xml.DomUtils;
import com.dataliquid.commons.xml.ns.DefaultNamespaceContext;
import javax.xml.namespace.NamespaceContext;

// Create namespace context
DefaultNamespaceContext nsContext = new DefaultNamespaceContext();
nsContext.addNamespace("ns", "http://example.com/namespace");

// Use namespace in XPath queries
Node node = ...; // Your node
String value = DomUtils.selectString(node, "//ns:element/text()", nsContext);
```

### Create Document with Elements

```java
import com.dataliquid.commons.xml.DomUtils;
import org.w3c.dom.*;

// Create new document
Document doc = DomUtils.createDocument();

// Create root element with namespace
Element root = DomUtils.createElement(doc, "root", "http://example.com/ns");
doc.appendChild(root);

// Add child elements
Element child = DomUtils.createElement(doc, "child");
DomUtils.setTextContent(child, "Some content");
root.appendChild(child);

// Add attributes
DomUtils.setAttribute(child, "id", "123");
DomUtils.setAttribute(child, "type", "example");
```

### XPath with Multiple Results

```java
import com.dataliquid.commons.xml.DomUtils;
import org.w3c.dom.NodeList;

Document doc = ...; // Your document
NodeList nodes = DomUtils.selectNodes(doc, "//item[@active='true']");

for (int i = 0; i < nodes.getLength(); i++) {
    Node node = nodes.item(i);
    String value = DomUtils.getTextContent(node);
    System.out.println("Item value: " + value);
}
```

### Schema Validation with Error Handling

```java
import com.dataliquid.commons.xml.DomUtils;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.XMLConstants;
import java.io.File;

try {
    // Load schema
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = factory.newSchema(new File("schema.xsd"));
    
    // Validate document
    Document document = DomUtils.parse(new File("document.xml"));
    boolean isValid = DomUtils.validate(document, schema);
    
    if (isValid) {
        System.out.println("Document is valid");
    } else {
        System.out.println("Document validation failed");
    }
} catch (Exception e) {
    System.err.println("Validation error: " + e.getMessage());
}
```