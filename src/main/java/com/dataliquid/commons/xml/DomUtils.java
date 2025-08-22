/*
 * Copyright © 2019 dataliquid GmbH | www.dataliquid.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dataliquid.commons.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.dataliquid.commons.xml.exception.XpathException;
import com.dataliquid.commons.xml.ns.DefaultNamespaceContext;

import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.xpath.XPathFactoryImpl;

/**
 * This class provides utility methods for working with DOM (Document Object
 * Model) in XML.
 */
public class DomUtils
{

    private static DocumentBuilderFactory getDocumentBuilderFactory()
    {
        return getDocumentBuilderFactory(true);
    }

    private static DocumentBuilderFactory getDocumentBuilderFactory(boolean namespaceAware)
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(namespaceAware);
        return factory;
    }

    /**
     * Parses the given XML string and returns a Document object representing the
     * parsed XML.
     *
     * @param xml
     *            the XML string to parse
     * @return the parsed Document object
     * @throws ParserConfigurationException
     *             if a DocumentBuilder cannot be created
     * @throws IOException
     *             if an I/O error occurs
     * @throws SAXException
     *             if any parse errors occur
     */
    public static Document parse(String xml)
    {
        return parse(xml, true);
    }

    /**
     * Parses the given XML string and returns a Document object representing the
     * parsed XML.
     *
     * @param xml
     *            the XML string to parse
     * @param namespaceAware
     *            a boolean indicating whether the parser should be namespace aware
     * @return the parsed Document object
     * @throws ParserConfigurationException
     *             if a DocumentBuilder cannot be created
     * @throws IOException
     *             if an I/O error occurs
     * @throws SAXException
     *             if any parse errors occur
     */
    public static Document parse(String xml, boolean namespaceAware)
    {
        try
        {
            return parse(new ByteArrayInputStream(xml.getBytes()), namespaceAware);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Unable to parse '" + xml + "'", e);
        }
    }

    /**
     * Parses the XML file denoted by the given File object and returns a Document
     * object representing the parsed XML.
     *
     * @param file
     *            the XML file to parse
     * @return the parsed Document object
     * @throws FileNotFoundException
     *             if the specified file does not exist
     * @throws ParserConfigurationException
     *             if a DocumentBuilder cannot be created
     * @throws IOException
     *             if an I/O error occurs
     * @throws SAXException
     *             if any parse errors occur
     */
    public static Document parse(File file) throws FileNotFoundException
    {
        return parse(file, true);
    }

    /**
     * Parses the XML file denoted by the given File object and returns a Document
     * object representing the parsed XML.
     *
     * @param file
     *            the XML file to parse
     * @param namespaceAware
     *            a boolean indicating whether the parser should be namespace aware
     * @return the parsed Document object
     * @throws FileNotFoundException
     *             if the specified file does not exist
     * @throws ParserConfigurationException
     *             if a DocumentBuilder cannot be created
     * @throws IOException
     *             if an I/O error occurs
     * @throws SAXException
     *             if any parse errors occur
     */
    public static Document parse(File file, boolean namespaceAware) throws FileNotFoundException
    {
        return parse(new FileInputStream(file), namespaceAware);
    }

    /**
     * Parses the XML data from the given InputStream and returns a Document object
     * representing the parsed XML.
     *
     * @param inputStream
     *            the InputStream containing the XML data to parse
     * @return the parsed Document object
     * @throws ParserConfigurationException
     *             if a DocumentBuilder cannot be created
     * @throws IOException
     *             if an I/O error occurs
     * @throws SAXException
     *             if any parse errors occur
     */
    public static Document parse(InputStream inputStream)
    {
        return parse(inputStream, true);
    }

    /**
     * Parses the XML data from the given InputStream and returns a Document object
     * representing the parsed XML.
     *
     * @param inputStream
     *            the InputStream containing the XML data to parse
     * @param namespaceAware
     *            a boolean indicating whether the parser should be namespace aware
     * @return the parsed Document object
     * @throws ParserConfigurationException
     *             if a DocumentBuilder cannot be created
     * @throws IOException
     *             if an I/O error occurs
     * @throws SAXException
     *             if any parse errors occur
     */
    public static Document parse(InputStream inputStream, boolean namespaceAware)
    {
        try
        {
            DocumentBuilderFactory factory = getDocumentBuilderFactory(namespaceAware);
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            return builder.parse(inputStream);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Unable to parse from input stream", e);
        }
    }

    /**
     * Parses the XML resource with the given name and returns a Document object
     * representing the parsed XML.
     *
     * @param name
     *            the name of the XML resource to parse
     * @return the parsed Document object
     * @throws ParserConfigurationException
     *             if a DocumentBuilder cannot be created
     * @throws IOException
     *             if an I/O error occurs
     * @throws SAXException
     *             if any parse errors occur
     */
    public static Document parseResource(String name)
    {
        return parseResource(name, true);
    }

    /**
     * Parses the XML resource with the given name and returns a Document object
     * representing the parsed XML.
     *
     * @param name
     *            the name of the XML resource to parse
     * @param namespaceAware
     *            a boolean indicating whether the parser should be namespace aware
     * @return the parsed Document object
     * @throws ParserConfigurationException
     *             if a DocumentBuilder cannot be created
     * @throws IOException
     *             if an I/O error occurs
     * @throws SAXException
     *             if any parse errors occur
     */
    public static Document parseResource(String name, boolean namespaceAware)
    {
        try
        {
            return parse(Thread.currentThread().getContextClassLoader().getResourceAsStream(name), namespaceAware);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Unable to parse from resource: '" + name + "'", e);
        }
    }

    /**
     * Creates a new Document object with the specified document element name.
     *
     * @param name
     *            the name of the document element
     * @return the newly created Document object
     * @throws ParserConfigurationException
     *             if a DocumentBuilder cannot be created
     */
    public static Document createDocument(String name)
    {
        return createDocument(name, null);
    }

    /**
     * Creates a new Document object with the specified document element name and
     * namespace URI.
     *
     * @param name
     *            the name of the document element
     * @param namespaceUri
     *            the namespace URI of the document element
     * @return the newly created Document object
     * @throws ParserConfigurationException
     *             if a DocumentBuilder cannot be created
     */
    public static Document createDocument(String name, String namespaceUri)
    {
        Document doc = createDocument();
        if (StringUtils.isBlank(namespaceUri))
        {
            doc.appendChild(doc.createElement(name));
        }
        else
        {
            doc.appendChild(doc.createElementNS(namespaceUri, name));
        }
        return doc;
    }

    /**
     * Creates a new Document object.
     *
     * @return the newly created Document object
     * @throws ParserConfigurationException
     *             if a DocumentBuilder cannot be created
     */
    public static Document createDocument()
    {
        DocumentBuilderFactory dbf = getDocumentBuilderFactory();
        DocumentBuilder db;
        Document result;
        try
        {
            db = dbf.newDocumentBuilder();
            result = db.newDocument();
        }
        catch (ParserConfigurationException e)
        {
            throw new IllegalStateException("Unable to create document", e);
        }
        return result;
    }

    /**
     * Creates a new Document object with the specified document element.
     *
     * @param documentElement
     *            the document element node to set as the root of the document
     * @return the newly created Document object
     * @throws ParserConfigurationException
     *             if a DocumentBuilder cannot be created
     */
    public static Document createDocument(Node documentElement)
    {
        Document toReturn = createDocument();
        Node importedNode = importNode(toReturn, documentElement);
        toReturn.appendChild(importedNode);
        return toReturn;
    }

    /**
     * Creates a new Element with the specified name and namespace URI in the given
     * Document.
     *
     * @param doc
     *            the Document in which to create the Element
     * @param name
     *            the name of the Element
     * @param namespaceURI
     *            the namespace URI of the Element
     * @return the newly created Element
     */
    public static Element createElement(Document doc, String name, String namespaceURI)
    {
        Element created;
        if (StringUtils.isNotBlank(namespaceURI))
        {
            created = doc.createElementNS(namespaceURI, name);
        }
        else
        {
            created = doc.createElement(name);
        }
        return created;
    }

    /**
     * Appends the specified Element as a child to the given parent Node.
     *
     * @param parent
     *            the parent Node to which the Element should be appended
     * @param child
     *            the Element to be appended as a child
     * @return the appended Element
     */
    public static Element appendElement(Node parent, Element child)
    {
        enforceNoNamespaceMixes(parent, child);
        Element newChild = importNode(getOwnerDocument(parent), child);
        parent.appendChild(newChild);
        return newChild;
    }

    /**
     * Inserts the specified Element into the given parent Node in a specific order
     * based on the provided list of node names.
     *
     * @param parent
     *            the parent Node into which the Element should be inserted
     * @param element
     *            the Element to be inserted
     * @param orderedNodeNames
     *            the list of node names specifying the desired order of insertion
     * @return the inserted Element
     */
    public static Element insertElement(Node parent, Element element, List<String> orderedNodeNames)
    {
        Element successor = selectSuccessorElementFromOrder((Element) parent, orderedNodeNames, element.getNodeName());

        return (successor != null) ? insertElementBefore(successor, element) : appendElement(parent, element);
    }

    /**
     * Inserts the specified Element as the first child of the given parent Node.
     *
     * @param parent
     *            the parent Node into which the Element should be inserted as the
     *            first child
     * @param child
     *            the Element to be inserted as the first child
     * @return the inserted Element
     */
    public static Element insertElementAsFirst(Node parent, Element child)
    {
        List<Element> children = DomUtils.selectChildren(parent);
        if (children.isEmpty())
        {
            return (Element) parent.appendChild(child);
        }
        else
        {
            return DomUtils.insertElementBefore(children.get(0), child);
        }
    }

    /**
     * Inserts the specified Element before the given Node.
     *
     * @param node
     *            the Node before which the Element should be inserted
     * @param element
     *            the Element to be inserted
     * @return the inserted Element
     */
    public static Element insertElementBefore(Node node, Element element)
    {
        enforceNoNamespaceMixes(node.getParentNode(), element);
        Element result = importNode(node, element);
        node.getParentNode().insertBefore(result, node);
        return result;
    }

    /**
     * Inserts the specified Element after the given Node.
     *
     * @param node
     *            the Node after which the Element should be inserted
     * @param element
     *            the Element to be inserted
     * @return the inserted Element
     */
    public static Element insertElementAfter(Node node, Element element)
    {
        enforceNoNamespaceMixes(node.getParentNode(), element);
        Element result = importNode(node, element);
        Element sibling = selectElementAfter(node);
        if (sibling != null)
        {
            sibling.getParentNode().insertBefore(result, sibling);
        }
        else
        {
            node.getParentNode().appendChild(result);
        }
        return result;
    }

    /**
     * Selects the Element that appears immediately before the given Node.
     *
     * @param node
     *            the Node for which to select the preceding Element
     * @return the Element appearing before the given Node, or null if not found or
     *         if the preceding sibling is not an Element
     */
    public static Element selectElementBefore(Node node)
    {
        Node sibling = node.getPreviousSibling();
        while (sibling != null && (sibling.getNodeType() != Node.ELEMENT_NODE))
        {
            sibling = sibling.getPreviousSibling();
        }
        return (Element) sibling;
    }

    /**
     * Selects the Element that appears immediately after the given Node.
     *
     * @param node
     *            the Node for which to select the succeeding Element
     * @return the Element appearing after the given Node, or null if not found or
     *         if the succeeding sibling is not an Element
     */
    public static Element selectElementAfter(Node node)
    {
        Node sibling = node.getNextSibling();
        while (sibling != null && (sibling.getNodeType() != Node.ELEMENT_NODE))
        {
            sibling = sibling.getNextSibling();
        }
        return (Element) sibling;
    }

    /**
     * Inserts the specified Element into the given parent Node by squeezing it in
     * between the existing child elements. The new Element will be inserted in the
     * correct order based on its position relative to the existing child elements.
     *
     * @param parent
     *            the parent Node into which the Element should be inserted
     * @param element
     *            the Element to be squeezed in
     * @return the inserted Element
     */
    public static Element squeezeInElement(Node parent, Element element)
    {
        List<Node> childrenToMove = new ArrayList<>();
        for (Node node : translateListOfNodes(parent.getChildNodes()))
        {
            short type = node.getNodeType();
            switch (type)
            {
            case Node.ATTRIBUTE_NODE:
                break;
            default:
                childrenToMove.add(node);
                break;
            }
        }
        Element result = appendElement(parent, element);
        for (Node node : childrenToMove)
        {
            result.appendChild(node);
        }
        return result;
    }

    /**
     * Deletes the specified Node from its parent.
     *
     * @param node
     *            the Node to be deleted
     */
    public static void delete(Node node)
    {
        node.getParentNode().removeChild(node);
    }

    /**
     * Deletes the specified list of Nodes from their respective parents.
     *
     * @param nodes
     *            the list of Nodes to be deleted
     */
    public static void delete(List<Node> nodes)
    {
        for (Node node : nodes)
        {
            delete(node);
        }
    }

    /**
     * Deletes the Nodes matching the given XPath expression from the specified
     * Node.
     *
     * @param node
     *            the Node from which to delete matching Nodes
     * @param xpath
     *            the XPath expression to select Nodes for deletion
     * @param removeWhitespace
     *            a boolean indicating whether to remove whitespace-only Text nodes
     *            during deletion
     * @param namespaceContext
     *            optional NamespaceContext for resolving namespace prefixes in the
     *            XPath expression
     */
    public static void delete(Node node, String xpath, boolean removeWhitespace, NamespaceContext... namespaceContext)
    {
        List<Node> nodes = selectNodes(node, xpath, namespaceContext);
        delete(nodes);
    }

    /**
     * Deletes the Nodes matching the given XPath expression from the specified
     * Node.
     *
     * @param node
     *            the Node from which to delete matching Nodes
     * @param xpath
     *            the XPath expression to select Nodes for deletion
     * @param namespaceContext
     *            optional NamespaceContext for resolving namespace prefixes in the
     *            XPath expression
     */
    public static void delete(Node node, String xpath, NamespaceContext... namespaceContext)
    {
        delete(node, xpath, false, namespaceContext);
    }

    /**
     * Checks if Nodes matching the given XPath expression exist in the specified
     * Node.
     *
     * @param node
     *            the Node to check for matching Nodes
     * @param xpath
     *            the XPath expression to select Nodes
     * @param namespaceContext
     *            optional NamespaceContext for resolving namespace prefixes in the
     *            XPath expression
     * @return true if matching Nodes exist, false otherwise
     */
    public static boolean exists(Node node, String xpath, NamespaceContext... namespaceContext)
    {
        return !selectNodes(node, xpath, namespaceContext).isEmpty();
    }

    /**
     * Selects a single Node matching the given XPath expression from the specified
     * Node.
     *
     * @param node
     *            the Node from which to select a matching Node
     * @param xpath
     *            the XPath expression to select a Node
     * @param namespaceContext
     *            optional NamespaceContext for resolving namespace prefixes in the
     *            XPath expression
     * @param <T>
     *            the type of Node to be selected
     * @return the selected Node, or null if no match is found
     */
    public static <T extends Node> T selectNode(Node node, String xpath, NamespaceContext... namespaceContext)
    {
        T result = null;
        List<T> nodes = selectNodes(node, xpath, namespaceContext);
        if (nodes.size() == 1)
        {
            result = nodes.get(0);
        }
        else if (nodes.size() > 1)
        {
            throw new IllegalArgumentException("XPath result is more than 1 element - xpath: '" + xpath + "' size: " + nodes.size());
        }
        return result;
    }

    /**
     * Adds a namespace declaration to the specified Element using the given alias
     * and NamespaceContext.
     *
     * @param element
     *            the Element to which the namespace declaration should be added
     * @param alias
     *            the namespace alias to be used in the declaration
     * @param nsc
     *            the NamespaceContext providing the namespace URI for the given
     *            alias
     * @return the modified Element with the added namespace declaration
     */
    public static Element addNamespace(Element element, String alias, NamespaceContext nsc)
    {
        addNamespace(element, alias, nsc.getNamespaceURI(alias));
        return element;
    }

    /**
     * Adds a namespace declaration to the specified Element using the given alias
     * and URI.
     *
     * @param element
     *            the Element to which the namespace declaration should be added
     * @param alias
     *            the namespace alias to be used in the declaration
     * @param uri
     *            the namespace URI to be associated with the alias
     * @return the modified Element with the added namespace declaration
     */
    public static Element addNamespace(Element element, String alias, String uri)
    {
        element.setAttributeNS(DefaultNamespaceContext.NAMESPACE_XMLNS, DefaultNamespaceContext.NAMESPACE_ALIAS_XMLNS + ":" + alias, uri);
        return element;
    }

    /**
     * Selects a single Node using the given XPathExpression from the specified
     * Node.
     *
     * @param node
     *            the Node from which to select a matching Node
     * @param xpath
     *            the XPathExpression to select a Node
     * @param <T>
     *            the type of Node to be selected
     * @return the selected Node, or null if no match is found
     */
    public static <T extends Node> T selectNode(Node node, XPathExpression xpath)
    {
        T result = null;
        List<T> nodes = selectNodes(node, xpath);
        if (nodes.size() == 1)
        {
            result = nodes.get(0);
        }
        else if (nodes.size() > 1)
        {
            throw new IllegalArgumentException("XPath result is more than 1 element - xpath: '" + xpath + "' size: " + nodes.size());
        }
        return result;
    }

    /**
     * Iterates over the Nodes matching the given XPath expression in the specified
     * Node and applies a NodeProcessor to each matching Node.
     *
     * @param node
     *            the Node from which to iterate over matching Nodes
     * @param xPath
     *            the XPath expression to select Nodes for iteration
     * @param nodeProcessor
     *            the NodeProcessor to apply to each matching Node
     * @param namespaceContext
     *            optional NamespaceContext for resolving namespace prefixes in the
     *            XPath expression
     * @param <T>
     *            the type of objects returned by the NodeProcessor
     * @return a List of objects generated by the NodeProcessor
     */
    public static <T> List<T> iterate(Node node, String xPath, NodeProcessor<T> nodeProcessor, NamespaceContext... namespaceContext)
    {
        List<T> result = new ArrayList<>();
        NodeList nodeList = evaluateXpath(node, xPath, XPathConstants.NODESET, fromNamespaceContextList(namespaceContext));
        List<Node> nodes = translateListOfNodes(nodeList);

        for (Node n : nodes)
        {
            T processorResult = nodeProcessor.process(n);
            if (processorResult != null)
            {
                result.add(processorResult);
            }
        }
        return result;
    }

    /**
     * Iterates over the Nodes matching the given XPath expression in the specified
     * Node and applies a NodeProcessorParameterized to each matching Node, passing
     * an additional parameter.
     *
     * @param node
     *            the Node from which to iterate over matching Nodes
     * @param xPath
     *            the XPath expression to select Nodes for iteration
     * @param nodeProcessor
     *            the NodeProcessorParameterized to apply to each matching Node
     * @param param
     *            the additional parameter to be passed to the
     *            NodeProcessorParameterized
     * @param namespaceContext
     *            optional NamespaceContext for resolving namespace prefixes in the
     *            XPath expression
     * @param <T>
     *            the type of object returned by the NodeProcessorParameterized
     * @return the result of the NodeProcessorParameterized for each matching Node
     */
    public static <T> T iterate(Node node, String xPath, NodeProcessorParameterized<T> nodeProcessor, T param,
            NamespaceContext... namespaceContext)
    {
        NodeList nodeList = evaluateXpath(node, xPath, XPathConstants.NODESET, fromNamespaceContextList(namespaceContext));
        List<Node> nodes = translateListOfNodes(nodeList);

        for (Node n : nodes)
        {
            nodeProcessor.process(n, param);
        }
        return param;
    }

    /**
     * Selects a list of Nodes matching the given XPath expression from the
     * specified Node.
     *
     * @param node
     *            the Node from which to select matching Nodes
     * @param xpath
     *            the XPath expression to select Nodes
     * @param namespaceContext
     *            optional NamespaceContext for resolving namespace prefixes in the
     *            XPath expression
     * @param <T>
     *            the type of Node to be selected
     * @return a list of Nodes matching the XPath expression
     */
    public static <T extends Node> List<T> selectNodes(Node node, String xpath, NamespaceContext... namespaceContext)
    {
        List<T> result = new ArrayList<>();

        NodeList nodeList;

        NamespaceContext nsc = fromNamespaceContextList(namespaceContext);
        if (nsc != null)
        {
            nodeList = evaluateXpath(node, xpath, XPathConstants.NODESET, nsc);
        }
        else
        {
            nodeList = evaluateXpath(node, xpath, XPathConstants.NODESET);
        }

        for (int i = 0; i < nodeList.getLength(); i++)
        {
            result.add((T) nodeList.item(i));
        }
        return result;
    }

    /**
     * Selects a list of Nodes using the given XPathExpression from the specified
     * Node.
     *
     * @param node
     *            the Node from which to select matching Nodes
     * @param xpath
     *            the XPathExpression to select Nodes
     * @param <T>
     *            the type of Node to be selected
     * @return a list of Nodes matching the XPath expression
     */
    public static <T extends Node> List<T> selectNodes(Node node, XPathExpression xpath)
    {
        List<T> result = new ArrayList<>();

        NodeList nodeList = evaluateXpath(node, xpath, XPathConstants.NODESET);

        for (int i = 0; i < nodeList.getLength(); i++)
        {
            result.add((T) nodeList.item(i));
        }
        return result;
    }

    /**
     * Selects a list of String values from the Nodes matching the given XPath
     * expression in the specified Node.
     *
     * @param node
     *            the Node from which to select matching Nodes
     * @param xPath
     *            the XPath expression to select Nodes
     * @param namespaceContext
     *            optional NamespaceContext for resolving namespace prefixes in the
     *            XPath expression
     * @return a list of String values from the matching Nodes
     */
    public static List<String> selectStrings(Node node, String xPath, NamespaceContext... namespaceContext)
    {
        NamespaceContext nsc = fromNamespaceContextList(namespaceContext);
        if (nsc != null)
        {
            return iterate(node, xPath, new NodeProcessor<String>()
            {
                @Override
                public String process(Node node)
                {
                    return node.getTextContent();
                }
            }, nsc);

        }
        else
        {
            return iterate(node, xPath, new NodeProcessor<String>()
            {
                @Override
                public String process(Node node)
                {
                    return node.getTextContent();
                }
            });
        }

    }

    /**
     * Selects a single String value from the Node matching the given XPath
     * expression in the specified Node.
     *
     * @param node
     *            the Node from which to select a matching Node
     * @param xpath
     *            the XPath expression to select a Node
     * @param namespaceContext
     *            optional NamespaceContext for resolving namespace prefixes in the
     *            XPath expression
     * @return the selected String value, or null if no match is found
     */
    public static String selectString(Node node, String xpath, NamespaceContext... namespaceContext)
    {
        return evaluateXpath(node, xpath, XPathConstants.STRING, fromNamespaceContextList(namespaceContext));
    }

    /**
     * Selects a single Integer value from the Node matching the given XPath
     * expression in the specified Node.
     *
     * @param node
     *            the Node from which to select a matching Node
     * @param xpath
     *            the XPath expression to select a Node
     * @param namespaceContext
     *            optional NamespaceContext for resolving namespace prefixes in the
     *            XPath expression
     * @return the selected Integer value, or null if no match is found or the
     *         selected value is not a valid Integer
     */
    public static Integer selectInteger(Node node, String xpath, NamespaceContext... namespaceContext)
    {
        return selectInteger(node, xpath, 0, namespaceContext);
    }

    /**
     * Selects a single Integer value from the Node matching the given XPath
     * expression in the specified Node. If no match is found or the selected value
     * is not a valid Integer, the defaultValue is returned.
     *
     * @param node
     *            the Node from which to select a matching Node
     * @param xpath
     *            the XPath expression to select a Node
     * @param defaultValue
     *            the default value to be returned if no match is found or the
     *            selected value is not a valid Integer
     * @param namespaceContext
     *            optional NamespaceContext for resolving namespace prefixes in the
     *            XPath expression
     * @return the selected Integer value, or the defaultValue if no match is found
     *         or the selected value is not a valid Integer
     */
    public static Integer selectInteger(Node node, String xpath, int defaultValue, NamespaceContext... namespaceContext)
    {
        return NumberUtils.toInt(selectString(node, xpath, namespaceContext), defaultValue);
    }

    /**
     * Selects a single Boolean value from the Node matching the given XPath
     * expression in the specified Node.
     *
     * @param node
     *            the Node from which to select a matching Node
     * @param xpath
     *            the XPath expression to select a Node
     * @param namespaceContext
     *            optional NamespaceContext for resolving namespace prefixes in the
     *            XPath expression
     * @return the selected Boolean value, or null if no match is found or the
     *         selected value is not a valid Boolean
     */
    public static Boolean selectBoolean(Node node, String xpath, NamespaceContext... namespaceContext)
    {
        return evaluateXpath(node, xpath, XPathConstants.BOOLEAN, fromNamespaceContextList(namespaceContext));
    }

    /**
     * Creates an XPathExpression object from the given XPath expression and
     * optional NamespaceContext.
     *
     * @param xpath
     *            the XPath expression
     * @param namespaceContext
     *            optional NamespaceContext for resolving namespace prefixes in the
     *            XPath expression
     * @return an XPathExpression object representing the compiled XPath expression
     * @throws XPathExpressionException
     *             if the XPath expression compilation fails
     */
    public static XPathExpression createXPathExpression(String xpath, NamespaceContext... namespaceContext) throws XPathExpressionException
    {
        XPath newXPath = new XPathFactoryImpl().newXPath();
        NamespaceContext nsc = fromNamespaceContextList(namespaceContext);
        if (nsc != null)
        {
            newXPath.setNamespaceContext(nsc);
        }
        return newXPath.compile(xpath);
    }

    /**
     * Evaluates the XPath expression on the specified Node and returns the result
     * of the evaluation as the expected type.
     *
     * @param node
     *            the Node on which to evaluate the XPath expression
     * @param xpath
     *            the XPath expression to evaluate
     * @param expectedType
     *            the expected type of the evaluation result
     * @param namespaceContext
     *            optional NamespaceContext for resolving namespace prefixes in the
     *            XPath expression
     * @param <T>
     *            the expected type of the evaluation result
     * @return the result of the XPath expression evaluation as the expected type
     * @throws XPathExpressionException
     *             if the evaluation of the XPath expression fails
     */
    public static <T> T evaluateXpath(Node node, String xpath, QName expectedType, NamespaceContext... namespaceContext)
    {
        try
        {
            return (T) createXPathExpression(xpath, namespaceContext).evaluate(node, expectedType);
        }
        catch (XPathExpressionException e)
        {
            throw new XpathException("XPath failure on node: " + node.getNodeName() + ": " + xpath, e);
        }
    }

    /**
     * Evaluates the provided XPathExpression on the specified Node and returns the
     * result of the evaluation as the expected type.
     *
     * @param node
     *            the Node on which to evaluate the XPath expression
     * @param xpath
     *            the pre-compiled XPathExpression to evaluate
     * @param expectedType
     *            the expected type of the evaluation result
     * @param <T>
     *            the expected type of the evaluation result
     * @return the result of the XPath expression evaluation as the expected type
     * @throws XPathExpressionException
     *             if the evaluation of the XPath expression fails
     */
    public static <T> T evaluateXpath(Node node, XPathExpression xpath, QName expectedType)
    {
        try
        {
            return (T) xpath.evaluate(node, expectedType);
        }
        catch (XPathExpressionException e)
        {
            throw new XpathException("XPath failure on node: " + node.getNodeName() + ": " + xpath, e);
        }
    }

    /**
     * Returns a list of children Nodes of the specified parent Node, filtered by
     * the given nodeType.
     *
     * @param parent
     *            the parent Node from which to retrieve children
     * @param nodeType
     *            the type of child Nodes to include in the list
     * @param <T>
     *            the type of Nodes to be included in the list
     * @return a list of children Nodes of the specified parent Node, filtered by
     *         the given nodeType
     */
    public static <T> List<T> children(Node parent, short nodeType)
    {
        List<T> result = new ArrayList<T>();

        List<Node> nodes = translateListOfNodes(parent.getChildNodes());

        for (Node n : nodes)
        {
            if (n.getNodeType() == nodeType)
            {
                result.add((T) n);
            }
        }
        return result;
    }

    /**
     * Translates a NodeList into a List of Nodes.
     *
     * @param nodeList
     *            the NodeList to be translated
     * @return a List of Nodes containing the same Nodes as the original NodeList
     */
    public static List<Node> translateListOfNodes(NodeList nodeList)
    {
        List<Node> result = new ArrayList<Node>();
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            result.add(nodeList.item(i));
        }
        return result;
    }

    /**
     * Returns the owner Document of the specified Node.
     *
     * @param node
     *            the Node for which to retrieve the owner Document
     * @return the owner Document of the specified Node
     */
    public static Document getOwnerDocument(Node node)
    {
        if (node == null)
        {
            return null;
        }

        if (node instanceof Document)
        {
            return (Document) node;
        }
        else
        {
            return node.getOwnerDocument();
        }
    }

    /**
     * Imports the specified Node into the specified parent Node.
     *
     * @param parent
     *            the parent Node into which to import the child Node
     * @param child
     *            the child Node to be imported
     * @param <T>
     *            the type of the imported Node
     * @return the imported Node of the specified type
     */
    public static <T> T importNode(Node parent, Node child)
    {
        Document doc = getOwnerDocument(parent);
        Node node = doc.importNode(child, true);
        return (T) node;
    }

    /**
     * Enforces the specified node name on the given Node.
     *
     * @param node
     *            the Node on which to enforce the node name
     * @param nodeName
     *            the desired node name to be enforced
     * @throws IllegalArgumentException
     *             if the node name of the Node does not match the desired node name
     */
    public static void enforceNodeName(Node node, String nodeName)
    {
        if (!isNodeName(node, nodeName))
        {
            throw new IllegalArgumentException("Expecting node of type \"" + nodeName + "\" - got \"" + node.getNodeName() + "\"");
        }
    }

    /**
     * Enforces that the specified nodes do not have mixed namespaces.
     *
     * @param node1
     *            the first Node to check for namespace mixing
     * @param node2
     *            the second Node to check for namespace mixing
     * @throws IllegalArgumentException
     *             if the nodes have mixed namespaces
     */
    public static void enforceNoNamespaceMixes(Node node1, Node node2)
    {
        if (StringUtils.isBlank(node1.getNamespaceURI()) != StringUtils.isBlank(node2.getNamespaceURI()))
        {
            throw new IllegalArgumentException("Mixing non-namespaces-aware node with namespace-aware node not allowed");
        }
    }

    /**
     * Checks if the given Node has the specified node name.
     *
     * @param node
     *            the Node to check for the node name
     * @param nodeName
     *            the node name to compare against
     * @return true if the Node has the specified node name, false otherwise
     */
    public static boolean isNodeName(Node node, String nodeName)
    {
        return node.getNodeName().equals(nodeName);
    }

    /**
     * Checks if the given Node has the specified attribute.
     *
     * @param node
     *            the Node to check for the attribute
     * @param attributeName
     *            the attribute name to check
     * @return true if the Node has the specified attribute, false otherwise
     */
    public static boolean nodeHasAttribute(Node node, String attributeName)
    {
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++)
        {
            if (attributes.item(i).getNodeName().equals(attributeName))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the names of all attributes of the given Node.
     *
     * @param node
     *            the Node from which to retrieve attribute names
     * @return a List of attribute names of the given Node
     */
    public static List<String> getAttributeNames(Node node)
    {
        List<String> attrs = new LinkedList<String>();

        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++)
        {

            attrs.add(attributes.item(i).getNodeName());
        }
        return attrs;
    }

    /**
     * Retrieves the value of the attribute with the specified name from the given
     * Node.
     *
     * @param node
     *            the Node from which to retrieve the attribute value
     * @param name
     *            the name of the attribute to retrieve
     * @return the value of the attribute, or null if the attribute is not found
     */
    public static String getAttribute(Node node, String name)
    {
        return ((Element) node).getAttribute(name);
    }

    /**
     * Sets the value of the attribute with the specified name on the given Node.
     *
     * @param node
     *            the Node on which to set the attribute value
     * @param name
     *            the name of the attribute to set
     * @param value
     *            the value to set for the attribute
     */
    public static void setAttribute(Node node, String name, String value)
    {
        ((Element) node).setAttribute(name, value);
    }

    /**
     * Converts the given Node to its XML representation as a String.
     *
     * @param node
     *            the Node to convert to XML
     * @return the XML representation of the Node as a String
     */
    public static String asXml(Node node)
    {
        return asXml(node, false, null);
    }

    /**
     * Converts the given Node to its XML representation as a String.
     *
     * @param node
     *            the Node to convert to XML
     * @param indent
     *            a boolean flag indicating whether to include indentation in the
     *            XML output
     * @return the XML representation of the Node as a String
     */
    public static String asXml(Node node, boolean indent)
    {
        return asXml(node, indent, null);
    }

    /**
     * Converts the given Node to its XML representation as a String, with
     * additional formatting options provided by the properties map.
     *
     * @param node
     *            the Node to convert to XML
     * @param indent
     *            a boolean flag indicating whether to include indentation in the
     *            XML output
     * @param properties
     *            a map of additional formatting options for the XML output
     * @return the XML representation of the Node as a String
     */
    public static String asXml(Node node, boolean indent, Map<String, String> properties)
    {
        Properties outputProperties = new Properties();
        if (indent)
        {
            outputProperties.setProperty(OutputKeys.INDENT, "yes");
            outputProperties.setProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        }

        if (properties != null)
        {
            for (Map.Entry<String, String> entry : properties.entrySet())
            {
                outputProperties.put(entry.getKey(), entry.getValue());
            }
        }

        StringWriter writer = new StringWriter();
        write(node, writer, outputProperties);
        String result = writer.toString();

        // Saxon 12+ adds a trailing newline when indenting is enabled, which wasn't
        // present in Saxon 9.6
        // To maintain backward compatibility, we remove it if present
        if (indent && result.endsWith("\n"))
        {
            result = result.substring(0, result.length() - 1);
        }

        return result;

    }

    /**
     * Creates an XML declaration string based on the provided output properties.
     *
     * @param outputProperties
     *            the properties specifying the output format of the XML declaration
     * @return the XML declaration string
     */
    public static String createXMLDeclarationString(Properties outputProperties)
    {
        String indentStr = outputProperties.getProperty(OutputKeys.INDENT, null);
        if (indentStr != null)
        {
            if (!indentStr.trim().toLowerCase().equals("yes"))
            {
                return StringUtils.EMPTY;
            }
        }
        else
        {
            return StringUtils.EMPTY;
        }

        String omitStr = outputProperties.getProperty(OutputKeys.OMIT_XML_DECLARATION, null);
        if (omitStr != null)
        {
            if (!omitStr.trim().toLowerCase().equals("yes"))
            {
                return StringUtils.EMPTY;
            }
        }

        String encoding = outputProperties.getProperty(OutputKeys.ENCODING, "UTF-8");
        String version = outputProperties.getProperty(OutputKeys.VERSION, "1.0");
        return String.format("<?xml version=\"%s\" encoding=\"%s\"?>", version, encoding);

    }

    /**
     * Writes the XML representation of the given Node to the specified Writer, with
     * additional formatting options provided by the output properties.
     *
     * @param node
     *            the Node to write to XML
     * @param writer
     *            the Writer to which the XML should be written
     * @param outputProperties
     *            the properties specifying the output format of the XML
     * @throws IOException
     *             if an I/O error occurs while writing to the Writer
     */
    public static void write(Node node, Writer writer, Properties outputProperties)
    {
        try
        {
            synchronized (node)
            {
                StreamResult streamResult = new StreamResult(writer);
                DOMSource domSource = new DOMSource(node);

                TransformerFactory tf = new TransformerFactoryImpl();
                Transformer serializer = tf.newTransformer();

                if (outputProperties != null && outputProperties.getProperty(OutputKeys.INDENT, null) != null)
                {
                    String xmlDecl = createXMLDeclarationString(outputProperties);
                    if (xmlDecl.length() > 0)
                    {
                        writer.write(xmlDecl);
                        writer.write('\n');

                        serializer.setOutputProperty("omit-xml-declaration", "yes");
                    }
                }

                if (outputProperties != null)
                {

                    for (Object o : outputProperties.entrySet())
                    {
                        Map.Entry entry = (Map.Entry) o;
                        serializer.setOutputProperty((String) entry.getKey(), (String) entry.getValue());
                    }
                }
                serializer.transform(domSource, streamResult);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("unable to transform dom to xml", e);
        }
    }

    /**
     * Selects a child Element of the specified parent Node with the given name.
     *
     * @param parent
     *            the parent Node from which to select the child Element
     * @param name
     *            the name of the child Element to select
     * @return the selected child Element, or null if no matching child Element is
     *         found
     */
    public static Element selectChild(Node parent, String name)
    {
        List<Element> children = selectChildren(parent, name);
        return (children.size() > 0) ? children.get(0) : null;
    }

    /**
     * Selects child Elements of the specified parent Node with the given name.
     *
     * @param parent
     *            the parent Node from which to select the child Elements
     * @param name
     *            the name of the child Elements to select
     * @return a List of selected child Elements, or an empty List if no matching
     *         child Elements are found
     */
    public static List<Element> selectChildren(Node parent, String name)
    {
        List<Element> result = new ArrayList<Element>();
        List<Node> nodes = translateListOfNodes(parent.getChildNodes());

        for (Node n : nodes)
        {
            if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(name))
            {
                result.add((Element) n);
            }
        }
        return result;
    }

    /**
     * Selects all child Elements of the specified parent Node.
     *
     * @param parent
     *            the parent Node from which to select the child Elements
     * @return a List of all child Elements of the parent Node, or an empty List if
     *         there are no child Elements
     */
    public static List<Element> selectChildren(Node parent)
    {
        return children(parent, Node.ELEMENT_NODE);
    }

    /**
     * Dumps the XML representation of the given Node to the console.
     *
     * @param node
     *            the Node to dump
     */
    public static void dump(Node node)
    {
        System.out.println(DomUtils.asXml(node, true));
    }

    /**
     * Checks if the given Node has a namespace.
     *
     * @param node
     *            the Node to check for a namespace
     * @return true if the Node has a namespace, false otherwise
     */
    public static boolean hasNamespace(Node node)
    {
        return node.getNamespaceURI() != null;
    }

    /**
     * Appends a Text node with the specified text content to the given element
     * Node.
     *
     * @param element
     *            the element Node to which the Text node should be appended
     * @param textContent
     *            the text content of the Text node
     * @return the appended Text node
     */
    public static Text appendText(Node element, String textContent)
    {
        Text textNode = getOwnerDocument(element).createTextNode(textContent);
        return (Text) element.appendChild(textNode);
    }

    /**
     * Appends a CDATASection node with the specified content to the given element
     * Node.
     *
     * @param element
     *            the element Node to which the CDATASection node should be appended
     * @param content
     *            the content of the CDATASection
     * @return the appended CDATASection node
     */
    public static CDATASection appendCDATA(Node element, String content)
    {
        CDATASection cdataSection = getOwnerDocument(element).createCDATASection(content);
        return (CDATASection) element.appendChild(cdataSection);
    }

    /**
     * Appends a Comment node with the specified comment text to the given element
     * Node.
     *
     * @param element
     *            the element Node to which the Comment node should be appended
     * @param comment
     *            the text of the Comment
     * @return the appended Comment node
     */
    public static Comment appendComment(Node element, String comment)
    {
        Comment node = getOwnerDocument(element).createComment(comment);
        return (Comment) element.appendChild(node);
    }

    /**
     * Renames the given Node with the specified name.
     *
     * @param node
     *            the Node to rename
     * @param name
     *            the new name for the Node
     * @return the renamed Node
     */
    public static Node renameNode(Node node, String name)
    {
        return renameNode(node, node.getNamespaceURI(), name);
    }

    /**
     * Renames the given Node with the specified namespace URI and name.
     *
     * @param node
     *            the Node to rename
     * @param namespaceUri
     *            the new namespace URI for the Node
     * @param name
     *            the new name for the Node
     * @return the renamed Node
     */
    public static Node renameNode(Node node, String namespaceUri, String name)
    {
        return node.getOwnerDocument().renameNode(node, namespaceUri, name);
    }

    /**
     * Renames all occurrences of elements or attributes with the specified name
     * from the given Node and its descendants to the specified new name.
     *
     * @param node
     *            the Node from which to start renaming
     * @param from
     *            the current name to be replaced
     * @param to
     *            the new name to replace with
     */
    public static void renameAll(Node node, String from, String to)
    {
        if (StringUtils.equalsIgnoreCase(node.getNodeName(), from))
        {
            renameNode(node, to);
        }
        for (Node child : DomUtils.translateListOfNodes(node.getChildNodes()))
        {
            renameAll(child, from, to);
        }
    }

    /**
     * Selects the child Nodes of the specified Node.
     *
     * @param node
     *            the Node from which to select the child Nodes
     * @return a List of child Nodes
     */
    public static List<Node> selectChildNodes(Node node)
    {
        return DomUtils.translateListOfNodes(node.getChildNodes());
    }

    /**
     * Checks if the given Node is an Element.
     *
     * @param node
     *            the Node to check
     * @return true if the Node is an Element, false otherwise
     */
    public static boolean isElement(Node node)
    {
        return isType(node, Node.ELEMENT_NODE);
    }

    /**
     * Checks if the given Node is a Text node.
     *
     * @param node
     *            the Node to check
     * @return true if the Node is a Text node, false otherwise
     */
    public static boolean isText(Node node)
    {
        return isType(node, Node.TEXT_NODE);
    }

    /**
     * Checks if the given Node is an Attribute node.
     *
     * @param node
     *            the Node to check
     * @return true if the Node is an Attribute node, false otherwise
     */
    public static boolean isAttribute(Node node)
    {
        return isType(node, Node.ATTRIBUTE_NODE);
    }

    /**
     * Checks if the given Node is a CDATASection node.
     *
     * @param node
     *            the Node to check
     * @return true if the Node is a CDATASection node, false otherwise
     */
    public static boolean isCData(Node node)
    {
        return isType(node, Node.CDATA_SECTION_NODE);
    }

    /**
     * Checks if the given Node is a Comment node.
     *
     * @param node
     *            the Node to check
     * @return true if the Node is a Comment node, false otherwise
     */
    public static boolean isComment(Node node)
    {
        return isType(node, Node.COMMENT_NODE);
    }

    /**
     * Checks if the given Node is a Document node.
     *
     * @param node
     *            the Node to check
     * @return true if the Node is a Document node, false otherwise
     */
    public static boolean isDocument(Node node)
    {
        return isType(node, Node.DOCUMENT_NODE);
    }

    /**
     * Checks if the given Node has the specified node type.
     *
     * @param node
     *            the Node to check
     * @param type
     *            the node type to compare against
     * @return true if the Node has the specified node type, false otherwise
     */
    public static boolean isType(Node node, short type)
    {
        return node != null && node.getNodeType() == type;
    }

    /**
     * Validates the specified Document against the given Schema.
     *
     * @param doc
     *            the Document to validate
     * @param schema
     *            the Schema to validate against
     * @return true if the Document is valid according to the Schema, false
     *         otherwise
     */
    public static boolean validate(Document doc, Schema schema)
    {
        Source source = new DOMSource(doc);
        return validate(source, schema);
    }

    /**
     * Validates the specified XML string against the given Schema.
     *
     * @param xml
     *            the XML string to validate
     * @param schema
     *            the Schema to validate against
     * @return true if the XML string is valid according to the Schema, false
     *         otherwise
     */
    public static boolean validate(String xml, Schema schema)
    {
        Source source = new StreamSource(new StringReader(xml));
        return validate(source, schema);
    }

    /**
     * Validates the specified XML source against the given Schema.
     *
     * @param source
     *            the XML source to validate
     * @param schema
     *            the Schema to validate against
     * @return true if the XML source is valid according to the Schema, false
     *         otherwise
     */
    public static boolean validate(Source source, Schema schema)
    {
        boolean result = false;
        Validator validator = schema.newValidator();
        try
        {
            validator.validate(source);
            result = true;
        }
        catch (Exception e)
        {
            result = false;
        }

        return result;
    }

    /**
     * Selects the next Element from the ordered list of element names, based on the
     * current element name.
     *
     * @param parent
     *            the parent Element from which to select the next Element
     * @param orderedElementNames
     *            the ordered list of element names
     * @param elementName
     *            the current element name
     * @return the next Element from the ordered list, or null if the current
     *         element is the last one
     */
    public static Element selectSuccessorElementFromOrder(Element parent, List<String> orderedElementNames, String elementName)
    {
        Element successor = null;

        Set<String> predecessors = selectPredecessors(orderedElementNames, elementName);
        for (Element child : selectChildren(parent))
        {
            if (!predecessors.contains(child.getNodeName()))
            {
                successor = child;
                break;
            }
        }
        return successor;
    }

    /**
     * Selects the predecessor elements from the ordered list of node names, based
     * on the current node name.
     *
     * @param orderedNodeNames
     *            the ordered list of node names
     * @param nodeName
     *            the current node name
     * @return a Set of predecessor node names from the ordered list
     */
    public static Set<String> selectPredecessors(List<String> orderedNodeNames, String nodeName)
    {
        Set<String> result = new HashSet<String>();
        for (String name : orderedNodeNames)
        {
            result.add(name);
            if (name.equals(nodeName))
            {
                break;
            }
        }
        return result;
    }

    /**
     * Combines multiple NamespaceContext objects into a single NamespaceContext.
     *
     * @param namespaceContexts
     *            the array of NamespaceContext objects to combine
     * @return a single NamespaceContext object that represents the combination of
     *         the provided NamespaceContext objects
     */
    public static NamespaceContext fromNamespaceContextList(NamespaceContext... namespaceContexts)
    {
        if (namespaceContexts.length == 0)
        {
            return null;
        }

        if (namespaceContexts.length > 1)
        {
            throw new IllegalStateException("Number of NamespaceContext must not exceed 1");
        }
        return namespaceContexts[0];
    }

    /**
     * Creates a deep clone of the specified Element node.
     *
     * @param element
     *            the Element node to clone
     * @return a deep clone of the Element node
     */
    public static Element cloneElement(Element element)
    {
        return DomUtils.parse(DomUtils.asXml(element)).getDocumentElement();
    }

    /**
     * Creates a deep clone of the specified Document.
     *
     * @param doc
     *            the Document to clone
     * @return a deep clone of the Document
     */
    public static Document cloneDocument(Document doc)
    {
        return DomUtils.parse(DomUtils.asXml(doc));
    }

    /**
     * An interface for processing Nodes during iteration.
     *
     * @param <T>
     *            the type of result returned by the processor
     */
    public interface NodeProcessor<T>
    {
        T process(Node node);
    }

    /**
     * An interface for processing Nodes during iteration, with an additional
     * parameter.
     *
     * @param <T>
     *            the type of result returned by the processor
     */
    public interface NodeProcessorParameterized<T>
    {
        void process(Node node, T param);
    }

    /**
     * Copies the attributes from the source Element to the destination Element.
     *
     * @param src
     *            the source Element from which to copy attributes
     * @param dest
     *            the destination Element to which attributes should be copied
     * @return the destination Element with copied attributes
     */
    public static Element copyAttributes(Element src, Element dest)
    {
        NamedNodeMap attributes = src.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++)
        {
            Node node = attributes.item(i);
            dest.setAttribute(node.getNodeName(), node.getTextContent());
        }
        return dest;
    }

    /**
     * Copies the children nodes from the source Element to the destination Element.
     *
     * @param src
     *            the source Element from which to copy children nodes
     * @param dest
     *            the destination Element to which children nodes should be copied
     * @return the destination Element with copied children nodes
     */
    public static Element copyChildren(Element src, Element dest)
    {
        for (Element child : DomUtils.selectChildren(src))
        {
            DomUtils.appendElement(dest, child);
        }
        return dest;
    }

    /**
     * Appends a new Text node with the specified text content to the given parent
     * Element.
     *
     * @param parent
     *            the parent Element to which the Text node should be appended
     * @param text
     *            the text content of the Text node
     * @return the appended Text node
     */
    public static Element appendTextNode(Element parent, String text)
    {
        parent.appendChild(parent.getOwnerDocument().createTextNode(text));
        return parent;
    }

}
