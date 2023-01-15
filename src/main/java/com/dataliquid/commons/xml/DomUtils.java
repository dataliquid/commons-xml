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

    public static Document parse(String xml)
    {
        return parse(xml, true);
    }

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

    public static Document parse(File file) throws FileNotFoundException
    {
        return parse(file, true);
    }

    public static Document parse(File file, boolean namespaceAware) throws FileNotFoundException
    {
        return parse(new FileInputStream(file), namespaceAware);
    }

    public static Document parse(InputStream inputStream)
    {
        return parse(inputStream, true);
    }

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

    public static Document parseResource(String name)
    {
        return parseResource(name, true);
    }

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

    public static Document createDocument(String name)
    {
        return createDocument(name, null);
    }

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

    public static Document createDocument(Node documentElement)
    {
        Document toReturn = createDocument();
        Node importedNode = importNode(toReturn, documentElement);
        toReturn.appendChild(importedNode);
        return toReturn;
    }

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

    public static Element appendElement(Node parent, Element child)
    {
        enforceNoNamespaceMixes(parent, child);
        Element newChild = importNode(getOwnerDocument(parent), child);
        parent.appendChild(newChild);
        return newChild;
    }

    public static Element insertElement(Node parent, Element element, List<String> orderedNodeNames)
    {
        Element successor = selectSuccessorElementFromOrder((Element) parent, orderedNodeNames, element.getNodeName());

        return (successor != null) ? insertElementBefore(successor, element) : appendElement(parent, element);
    }

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

    public static Element insertElementBefore(Node node, Element element)
    {
        enforceNoNamespaceMixes(node.getParentNode(), element);
        Element result = importNode(node, element);
        node.getParentNode().insertBefore(result, node);
        return result;
    }

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

    public static Element selectElementBefore(Node node)
    {
        Node sibling = node.getPreviousSibling();
        while (sibling != null && (sibling.getNodeType() != Node.ELEMENT_NODE))
        {
            sibling = sibling.getPreviousSibling();
        }
        return (Element) sibling;
    }

    public static Element selectElementAfter(Node node)
    {
        Node sibling = node.getNextSibling();
        while (sibling != null && (sibling.getNodeType() != Node.ELEMENT_NODE))
        {
            sibling = sibling.getNextSibling();
        }
        return (Element) sibling;
    }

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

    public static void delete(Node node)
    {
        node.getParentNode().removeChild(node);
    }

    public static void delete(List<Node> nodes)
    {
        for (Node node : nodes)
        {
            delete(node);
        }
    }

    public static void delete(Node node, String xpath, boolean removeWitheSpace, NamespaceContext... namespaceContext)
    {
        List<Node> nodes = selectNodes(node, xpath, namespaceContext);
        delete(nodes);
    }

    public static void delete(Node node, String xpath, NamespaceContext... namespaceContext)
    {
        delete(node, xpath, false, namespaceContext);
    }

    public static boolean exists(Node node, String xpath, NamespaceContext... namespaceContext)
    {
        return !selectNodes(node, xpath, namespaceContext).isEmpty();
    }

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

    public static Element addNamespace(Element element, String alias, NamespaceContext nsc)
    {
        addNamespace(element, alias, nsc.getNamespaceURI(alias));
        return element;
    }

    public static Element addNamespace(Element element, String alias, String uri)
    {
        element.setAttributeNS(DefaultNamespaceContext.NAMESPACE_XMLNS, DefaultNamespaceContext.NAMESPACE_ALIAS_XMLNS + ":" + alias, uri);
        return element;
    }

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

    public static String selectString(Node node, String xpath, NamespaceContext... namespaceContext)
    {
        return evaluateXpath(node, xpath, XPathConstants.STRING, fromNamespaceContextList(namespaceContext));
    }

    public static Integer selectInteger(Node node, String xpath, NamespaceContext... namespaceContext)
    {
        return selectInteger(node, xpath, 0, namespaceContext);
    }

    public static Integer selectInteger(Node node, String xpath, int defaultValue, NamespaceContext... namespaceContext)
    {
        return NumberUtils.toInt(selectString(node, xpath, namespaceContext), defaultValue);
    }

    public static Boolean selectBoolean(Node node, String xpath, NamespaceContext... namespaceContext)
    {
        return evaluateXpath(node, xpath, XPathConstants.BOOLEAN, fromNamespaceContextList(namespaceContext));
    }

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

    public static List<Node> translateListOfNodes(NodeList nodeList)
    {
        List<Node> result = new ArrayList<Node>();
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            result.add(nodeList.item(i));
        }
        return result;
    }

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

    public static <T> T importNode(Node parent, Node child)
    {
        Document doc = getOwnerDocument(parent);
        Node node = doc.importNode(child, true);
        return (T) node;
    }

    public static void enforceNodeName(Node node, String nodeName)
    {
        if (!isNodeName(node, nodeName))
        {
            throw new IllegalArgumentException("Expecting node of type \"" + nodeName + "\" - got \"" + node.getNodeName() + "\"");
        }
    }

    public static void enforceNoNamespaceMixes(Node node1, Node node2)
    {
        if (StringUtils.isBlank(node1.getNamespaceURI()) != StringUtils.isBlank(node2.getNamespaceURI()))
        {
            throw new IllegalArgumentException("Mixing non-namespaces-aware node with namespace-aware node not allowed");
        }
    }

    public static boolean isNodeName(Node node, String nodeName)
    {
        return node.getNodeName().equals(nodeName);
    }

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

    public static String getAttribute(Node node, String name)
    {
        return ((Element) node).getAttribute(name);
    }

    public static void setAttribute(Node node, String name, String value)
    {
        ((Element) node).setAttribute(name, value);
    }

    public static String asXml(Node node)
    {
        return asXml(node, false, null);
    }

    public static String asXml(Node node, boolean indent)
    {
        return asXml(node, indent, null);
    }

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
        return writer.toString();

    }

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

    public static Element selectChild(Node parent, String name)
    {
        List<Element> children = selectChildren(parent, name);
        return (children.size() > 0) ? children.get(0) : null;
    }

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

    public static List<Element> selectChildren(Node parent)
    {
        return children(parent, Node.ELEMENT_NODE);
    }

    public static void dump(Node node)
    {
        System.out.println(DomUtils.asXml(node, true));
    }

    public static boolean hasNamespace(Node node)
    {
        return node.getNamespaceURI() != null;
    }

    public static Text appendText(Node element, String textContent)
    {
        Text textNode = getOwnerDocument(element).createTextNode(textContent);
        return (Text) element.appendChild(textNode);
    }

    public static CDATASection appendCDATA(Node element, String content)
    {
        CDATASection cdataSection = getOwnerDocument(element).createCDATASection(content);
        return (CDATASection) element.appendChild(cdataSection);
    }

    public static Comment appendComment(Node element, String comment)
    {
        Comment node = getOwnerDocument(element).createComment(comment);
        return (Comment) element.appendChild(node);
    }

    public static Node renameNode(Node node, String name)
    {
        return renameNode(node, node.getNamespaceURI(), name);
    }

    public static Node renameNode(Node node, String namespaceUri, String name)
    {
        return node.getOwnerDocument().renameNode(node, namespaceUri, name);
    }

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

    public static List<Node> selectChildNotes(Node node)
    {
        return DomUtils.translateListOfNodes(node.getChildNodes());
    }

    public static boolean isElement(Node node)
    {
        return isType(node, Node.ELEMENT_NODE);
    }

    public static boolean isText(Node node)
    {
        return isType(node, Node.TEXT_NODE);
    }

    public static boolean isAttribute(Node node)
    {
        return isType(node, Node.ATTRIBUTE_NODE);
    }

    public static boolean isCData(Node node)
    {
        return isType(node, Node.CDATA_SECTION_NODE);
    }

    public static boolean isComment(Node node)
    {
        return isType(node, Node.COMMENT_NODE);
    }

    public static boolean isDocument(Node node)
    {
        return isType(node, Node.DOCUMENT_NODE);
    }

    public static boolean isType(Node node, short type)
    {
        return node != null && node.getNodeType() == type;
    }

    public static boolean validate(Document doc, Schema schema)
    {
        Source source = new DOMSource(doc);
        return validate(source, schema);
    }

    public static boolean validate(String xml, Schema schema)
    {
        Source source = new StreamSource(new StringReader(xml));
        return validate(source, schema);
    }

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

    public static Element cloneElement(Element element)
    {
        return DomUtils.parse(DomUtils.asXml(element)).getDocumentElement();
    }

    public static Document cloneDocument(Document doc)
    {
        return DomUtils.parse(DomUtils.asXml(doc));
    }

    public interface NodeProcessor<T>
    {
        T process(Node node);
    }

    public interface NodeProcessorParameterized<T>
    {
        void process(Node node, T param);
    }

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

    public static Element copyChildren(Element src, Element dest)
    {
        for (Element child : DomUtils.selectChildren(src))
        {
            DomUtils.appendElement(dest, child);
        }
        return dest;
    }

    public static Element appendTextNode(Element parent, String text)
    {
        parent.appendChild(parent.getOwnerDocument().createTextNode(text));
        return parent;
    }

}
