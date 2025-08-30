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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.dataliquid.commons.xml.ns.DefaultNamespaceContext;

public class DomUtilsTest
{

    @Test
    public void testParse()
    {
        // Given
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><element>Value</element></root>";

        // When
        Document document = DomUtils.parse(xml);

        // Then
        assertThat(document, notNullValue());
        assertThat(document.getDocumentElement().getNodeName(), equalTo("root"));
        assertThat(document.getElementsByTagName("element").getLength(), equalTo(1));
        assertThat(document.getElementsByTagName("element").item(0).getTextContent(), equalTo("Value"));
    }

    @Test
    public void testParseInvalid()
    {
        // Given
        String xml = "no xml content";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> DomUtils.parse(xml));
    }

    @Test
    public void testParseWithNamespaceAware()
    {
        // Given
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root xmlns=\"http://example.com\"><element>Value</element></root>";

        // When
        Document document = DomUtils.parse(xml, true);

        // Then
        assertThat(document, notNullValue());
        assertThat(document.getDocumentElement().getNodeName(), equalTo("root"));
        assertThat(document.getElementsByTagName("element").getLength(), equalTo(1));
        assertThat(document.getElementsByTagName("element").item(0).getTextContent(), equalTo("Value"));
    }

    @Test
    public void testParseWithoutNamespaceAware()
    {
        // Given
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root xmlns=\"http://example.com\"><element>Value</element></root>";

        // When
        Document document = DomUtils.parse(xml, false);

        // Then
        assertThat(document, notNullValue());
        assertThat(document.getDocumentElement().getNodeName(), equalTo("root"));
        assertThat(document.getElementsByTagName("element").getLength(), equalTo(1));
        assertThat(document.getElementsByTagName("element").item(0).getTextContent(), equalTo("Value"));
    }

    @Test
    public void testParseFileNotFound()
    {
        // Given
        File nonExistentFile = new File("path/to/nonexistent.xml");

        // When & Then
        assertThrows(FileNotFoundException.class, () -> DomUtils.parse(nonExistentFile));
    }

    @Test
    public void testParseFile() throws FileNotFoundException
    {
        // Given
        File file = new File("src/test/resources/xml/test-parse-file.xml");

        // When
        Document document = DomUtils.parse(file);

        // Then
        assertThat(document, notNullValue());
        assertThat(document.getDocumentElement().getNodeName(), equalTo("root"));
        assertThat(document.getElementsByTagName("element").getLength(), equalTo(1));
        assertThat(document.getElementsByTagName("element").item(0).getTextContent(), equalTo("Value"));
    }

    @Test
    public void testParseFileWithNamespaceAwareNotFound()
    {
        // Given
        File nonExistentFile = new File("path/to/nonexistent.xml");

        // When & Then
        assertThrows(FileNotFoundException.class, () -> DomUtils.parse(nonExistentFile, true));
    }

    @Test
    public void testParseInputStream()
    {
        // Given
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><element>Value</element></root>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());

        // When
        Document document = DomUtils.parse(inputStream);

        // Then
        assertThat(document, notNullValue());
        assertThat(document.getDocumentElement().getNodeName(), equalTo("root"));
        assertThat(document.getElementsByTagName("element").getLength(), equalTo(1));
        assertThat(document.getElementsByTagName("element").item(0).getTextContent(), equalTo("Value"));
    }

    @Test
    public void testParseInputStreamWithNamespaceAware()
    {
        // Given
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root xmlns:ns=\"http://example.com\"><ns:element>Value</ns:element></root>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());

        // When
        Document document = DomUtils.parse(inputStream, true);

        // Then
        assertThat(document, notNullValue());
        assertThat(document.getDocumentElement().getNodeName(), equalTo("root"));
        assertThat(document.getElementsByTagName("ns:element").getLength(), equalTo(1));
        assertThat(document.getElementsByTagName("ns:element").item(0).getTextContent(), equalTo("Value"));
    }

    @Test
    public void testParseInputStreamWithoutNamespaceAware()
    {
        // Given
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root xmlns=\"http://example.com\"><element>Value</element></root>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());

        // When
        Document document = DomUtils.parse(inputStream, false);

        // Then
        assertThat(document, notNullValue());
        assertThat(document.getDocumentElement().getNodeName(), equalTo("root"));
        assertThat(document.getElementsByTagName("element").getLength(), equalTo(1));
        assertThat(document.getElementsByTagName("element").item(0).getTextContent(), equalTo("Value"));
    }

    @Test
    public void testParseResource()
    {
        // Given
        String resourceName = "xml/test-parse-resource.xml";

        // When
        Document document = DomUtils.parseResource(resourceName);

        // Then
        assertThat(document, notNullValue());
        assertThat(document.getDocumentElement().getNodeName(), equalTo("root"));
        assertThat(document.getElementsByTagName("element").getLength(), equalTo(1));
        assertThat(document.getElementsByTagName("element").item(0).getTextContent(), equalTo("Value"));
    }

    @Test
    public void testParseResourceWithNamespaceAware()
    {
        // Given
        String resourceName = "xml/test-parse-resource-with-namespace-aware.xml";

        // When
        Document document = DomUtils.parseResource(resourceName, true);

        // Then
        assertThat(document, notNullValue());
        assertThat(document.getDocumentElement().getNodeName(), equalTo("root"));
        assertThat(document.getElementsByTagName("ns:element").getLength(), equalTo(1));
        assertThat(document.getElementsByTagName("ns:element").item(0).getTextContent(), equalTo("Value"));
    }

    @Test
    public void testCreateDocument()
    {
        // Given
        String rootElementName = "root";

        // When
        Document document = DomUtils.createDocument(rootElementName);

        // Then
        assertThat(document, notNullValue());
        assertThat(document.getDocumentElement().getNodeName(), equalTo(rootElementName));
    }

    @Test
    public void testCreateDocumentWithNamespace()
    {
        // Given
        String rootElementName = "root";
        String namespaceUri = "http://example.com";

        // When
        Document document = DomUtils.createDocument(rootElementName, namespaceUri);

        // Then
        assertThat(document, notNullValue());
        assertThat(document.getDocumentElement().getNodeName(), equalTo(rootElementName));
        assertThat(document.getDocumentElement().getNamespaceURI(), equalTo(namespaceUri));
    }

    @Test
    public void testCreateDocumentWithoutRoot()
    {
        // When
        Document document = DomUtils.createDocument();

        // Then
        assertThat(document, notNullValue());
        assertThat(document.getDocumentElement(), nullValue());
    }

    @Test
    public void testCreateDocumentFromGivenNode() throws Exception
    {
        // Given
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document originalDocument = builder.newDocument();
        Element documentElement = originalDocument.createElement("root");

        // When
        Document newDocument = DomUtils.createDocument(documentElement);

        // Then
        assertThat(newDocument, is(notNullValue()));
        assertThat(newDocument.getDocumentElement().getNodeName(), is("root"));
    }

    @Test
    public void testCreateElement()
    {
        // Given
        Document document = DomUtils.createDocument();
        String elementName = "myElement";
        String namespaceURI = "http://example.com";

        // When
        Element element = DomUtils.createElement(document, elementName, namespaceURI);

        // Then
        assertThat(element, notNullValue());
        assertThat(element.getNodeName(), equalTo(elementName));
        assertThat(element.getNamespaceURI(), equalTo(namespaceURI));
    }

    @Test
    public void testAppendElement() throws Exception
    {
        // Given
        String xml = "<root></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Element newElement = document.createElement("child");

        // When
        DomUtils.appendElement(rootElement, newElement);

        // Then
        assertThat(rootElement.getLastChild().getNodeName(), is("child"));
    }

    @Test
    public void testInsertElement() throws Exception
    {
        // Given
        String xml = "<root><node1/><node3/></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Element newElement = document.createElement("node2");
        List<String> orderedNodeNames = Arrays.asList("node1", "node2", "node3");

        // When
        DomUtils.insertElement(rootElement, newElement, orderedNodeNames);

        // Then
        Node insertedNode = rootElement.getChildNodes().item(1);
        assertThat(insertedNode, instanceOf(Element.class));
        assertThat(((Element) insertedNode).getTagName(), is("node2"));
    }

    @Test
    public void testInsertElementAsFirst() throws Exception
    {
        // Given
        String xml = "<root><node1/><node3/></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Element newElement = document.createElement("node2");

        // When
        DomUtils.insertElementAsFirst(rootElement, newElement);

        // Then
        Node firstChild = rootElement.getFirstChild();
        assertThat(firstChild, instanceOf(Element.class));
        assertThat(((Element) firstChild).getTagName(), is("node2"));
    }

    @Test
    public void testInsertElementBefore() throws Exception
    {
        // Given
        String xml = "<root><node1/><node3/></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element node1 = (Element) document.getElementsByTagName("node3").item(0);
        Element newElement = document.createElement("node2");

        // When
        DomUtils.insertElementBefore(node1, newElement);

        // Then
        Node parentNode = node1.getParentNode();
        assertThat(parentNode, instanceOf(Element.class));

        NodeList childNodes = parentNode.getChildNodes();
        assertThat(childNodes.getLength(), is(3));
        assertThat(childNodes.item(1), is(instanceOf(Element.class)));
        assertThat(((Element) childNodes.item(1)).getTagName(), is("node2"));
    }

    @Test
    public void testInsertElementAfter() throws Exception
    {
        // Given
        String xml = "<root><node1/><node3/></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element node1 = (Element) document.getElementsByTagName("node1").item(0);
        Element newElement = document.createElement("node2");

        // When
        DomUtils.insertElementAfter(node1, newElement);

        // Then
        Node parentNode = node1.getParentNode();
        assertThat(parentNode, instanceOf(Element.class));

        NodeList childNodes = parentNode.getChildNodes();
        assertThat(childNodes.getLength(), is(3));
        assertThat(childNodes.item(1), is(instanceOf(Element.class)));
        assertThat(((Element) childNodes.item(1)).getTagName(), is("node2"));
    }

    @Test
    public void testSelectElementBefore()
    {
        // Given
        Document document = DomUtils.createDocument();
        Element parentElement = document.createElement("parent");
        Element existingElement1 = document.createElement("existing1");
        Element existingElement2 = document.createElement("existing2");
        parentElement.appendChild(existingElement1);
        parentElement.appendChild(existingElement2);

        // When
        Element selectedElement = DomUtils.selectElementBefore(existingElement2);

        // Then
        assertThat(selectedElement, equalTo(existingElement1));
    }

    @Test
    public void testSelectElementAfter()
    {
        // Given
        Document document = DomUtils.createDocument();
        Element parentElement = document.createElement("parent");
        Element existingElement1 = document.createElement("existing1");
        Element existingElement2 = document.createElement("existing2");
        parentElement.appendChild(existingElement1);
        parentElement.appendChild(existingElement2);

        // When
        Element selectedElement = DomUtils.selectElementAfter(existingElement1);

        // Then
        assertThat(selectedElement, equalTo(existingElement2));
    }

    @Test
    public void testDelete()
    {
        // Given
        Document document = DomUtils.createDocument();
        Element parentElement = document.createElement("parent");
        Element childElement = document.createElement("child");
        parentElement.appendChild(childElement);

        // When
        DomUtils.delete(childElement);

        // Then
        assertThat(childElement.getParentNode(), nullValue());
        assertThat(parentElement.getFirstChild(), nullValue());
    }

    @Test
    public void testDeleteNodeList()
    {
        // Given
        Document document = DomUtils.createDocument();
        Element parentElement = document.createElement("parent");
        Element childElement1 = document.createElement("child1");
        Element childElement2 = document.createElement("child2");
        parentElement.appendChild(childElement1);
        parentElement.appendChild(childElement2);

        List<Node> nodesToDelete = new ArrayList<>();
        nodesToDelete.add(childElement1);
        nodesToDelete.add(childElement2);

        // When
        DomUtils.delete(nodesToDelete);

        // Then
        assertThat(childElement1.getParentNode(), nullValue());
        assertThat(childElement2.getParentNode(), nullValue());
        assertThat(parentElement.getFirstChild(), nullValue());
    }

    @Test
    public void testDeleteWithXpath() throws Exception
    {
        // Given
        String xml = "<root><element1/><element2/><element3/></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        DomUtils.delete(rootElement, "//element2");

        // Then
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("//element2");
        NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        assertThat(nodeList.getLength(), is(0));
    }

    @Test
    public void testDeleteWithXpathAndNamespace() throws Exception
    {
        // Given
        String xml = "<root xmlns:ns='http://example.com'><ns:element1/><ns:element2/><ns:element3/></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        NamespaceContext namespaceContext = new NamespaceContext()
        {
            @Override
            public String getNamespaceURI(String prefix)
            {
                if ("ns".equals(prefix))
                {
                    return "http://example.com";
                }
                return null;
            }

            @Override
            public String getPrefix(String namespaceURI)
            {
                if ("http://example.com".equals(namespaceURI))
                {
                    return "ns";
                }
                return null;
            }

            @Override
            public Iterator<String> getPrefixes(String namespaceURI)
            {
                return null;
            }
        };

        DomUtils.delete(rootElement, "//ns:element2", namespaceContext);

        // Then
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(namespaceContext);
        XPathExpression expr = xpath.compile("//ns:element2");
        NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        assertThat(nodeList.getLength(), is(0));
    }

    @Test
    public void testExists() throws Exception
    {
        // Given
        String xml = "<root xmlns:ns='http://example.com'><ns:element1/><ns:element2/><ns:element3/></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        NamespaceContext namespaceContext = new NamespaceContext()
        {
            @Override
            public String getNamespaceURI(String prefix)
            {
                if ("ns".equals(prefix))
                {
                    return "http://example.com";
                }
                return null;
            }

            @Override
            public String getPrefix(String namespaceURI)
            {
                if ("http://example.com".equals(namespaceURI))
                {
                    return "ns";
                }
                return null;
            }

            @Override
            public Iterator<String> getPrefixes(String namespaceURI)
            {
                return null;
            }
        };

        boolean exists = DomUtils.exists(rootElement, "//ns:element2", namespaceContext);

        // Then
        assertThat(exists, is(true));
    }

    @Test
    public void testSelectNode() throws Exception
    {
        // Given
        String xml = "<root xmlns:ns='http://example.com'><ns:element1/><ns:element2/><ns:element3/></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        NamespaceContext namespaceContext = new NamespaceContext()
        {
            @Override
            public String getNamespaceURI(String prefix)
            {
                if ("ns".equals(prefix))
                {
                    return "http://example.com";
                }
                return null;
            }

            @Override
            public String getPrefix(String namespaceURI)
            {
                if ("http://example.com".equals(namespaceURI))
                {
                    return "ns";
                }
                return null;
            }

            @Override
            public Iterator<String> getPrefixes(String namespaceURI)
            {
                return null;
            }
        };

        Node selectedNode = DomUtils.selectNode(rootElement, "//ns:element2", namespaceContext);

        // Then
        assertThat(selectedNode, instanceOf(Element.class));
        assertThat(selectedNode.getNodeName(), equalTo("ns:element2"));
    }

    @Test
    public void testAddNamespace() throws Exception
    {
        // Given
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.newDocument();
        Element rootElement = document.createElement("root");

        // When
        NamespaceContext namespaceContext = new NamespaceContext()
        {
            @Override
            public String getNamespaceURI(String prefix)
            {
                if ("ns".equals(prefix))
                {
                    return "http://example.com";
                }
                return null;
            }

            @Override
            public String getPrefix(String namespaceURI)
            {
                if ("http://example.com".equals(namespaceURI))
                {
                    return "ns";
                }
                return null;
            }

            @Override
            public Iterator<String> getPrefixes(String namespaceURI)
            {
                return null;
            }
        };

        Element element = DomUtils.addNamespace(rootElement, "ns", namespaceContext);

        // Then
        String namespaceUri = element.lookupNamespaceURI("ns");
        assertThat(namespaceUri, equalTo("http://example.com"));
    }

    @Test
    public void testAddNamespaceWithUri() throws Exception
    {
        // Given
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.newDocument();
        Element rootElement = document.createElement("root");

        // When
        String alias = "ns";
        String uri = "http://example.com";
        Element element = DomUtils.addNamespace(rootElement, alias, uri);

        // Then
        String namespaceUri = element.lookupNamespaceURI(alias);
        assertThat(namespaceUri, equalTo(uri));
    }

    @Test
    public void testSelectNodeAsGeneric() throws Exception
    {
        // Given
        String xml = "<root><element1/><element2/><element3/></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        XPathExpression expr = xpath.compile("//element2");

        Node selectedNode = DomUtils.selectNode(rootElement, expr);

        // Then
        assertThat(selectedNode, instanceOf(Element.class));
        assertThat(selectedNode.getNodeName(), equalTo("element2"));
    }

    @Test
    public void testSelectNodesWithNamespace() throws Exception
    {
        // Given
        String xml = "<root xmlns:html=\"http://www.w3.org/1999/xhtml\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><html:element/><xs:element/><html:element/></root>";
        Document document = DomUtils.parse(xml);
        Element rootElement = document.getDocumentElement();

        // When
        List<Node> selectedNodes = DomUtils.selectNodes(rootElement, "//html:element", new DefaultNamespaceContext());

        // Then
        assertThat(selectedNodes.size(), is(2));
        assertThat(selectedNodes.get(0).getNodeName(), equalTo("html:element"));
        assertThat(selectedNodes.get(1).getNodeName(), equalTo("html:element"));
    }

    @Test
    public void testSelectNodes() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
    {
        // Given
        String xml = "<root><element/><item/><element/></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        XPathExpression expr = xpath.compile("//element");

        List<Node> selectedNodes = DomUtils.selectNodes(rootElement, expr);

        // Then
        assertThat(selectedNodes.size(), is(2));
        assertThat(selectedNodes.get(0).getNodeName(), equalTo("element"));
        assertThat(selectedNodes.get(1).getNodeName(), equalTo("element"));
    }

    @Test
    public void testSelectStrings() throws Exception
    {
        // Given
        String xml = "<root><element>Value 1</element><element>Value 2</element><element>Value 3</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        List<String> selectedStrings = DomUtils.selectStrings(rootElement, "//element", new DefaultNamespaceContext());

        // Then
        assertThat(selectedStrings.size(), is(3));
        assertThat(selectedStrings.get(0), equalTo("Value 1"));
        assertThat(selectedStrings.get(1), equalTo("Value 2"));
        assertThat(selectedStrings.get(2), equalTo("Value 3"));
    }

    @Test
    public void testSelectString() throws Exception
    {
        // Given
        String xml = "<root><element1>Value 1</element1><element2>Value 2</element2><element3>Value 3</element3></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        String selectedString = DomUtils.selectString(rootElement, "//element2");

        // Then
        assertThat(selectedString, equalTo("Value 2"));
    }

    @Test
    public void testSelectInteger() throws Exception
    {
        // Given
        String xml = "<root><count>10</count></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        Integer selectedInteger = DomUtils.selectInteger(rootElement, "//count");

        // Then
        assertThat(selectedInteger, equalTo(10));
    }

    @Test
    public void testSelectIntegerWithDefaultValue() throws Exception
    {
        // Given
        String xml = "<root></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        Integer selectedInteger = DomUtils.selectInteger(rootElement, "//count", -10);

        // Then
        assertThat(selectedInteger, equalTo(-10));
    }

    @Test
    public void testSelectBoolean() throws Exception
    {
        // Given
        String xml = "<root><enabled>true</enabled></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        boolean selectedBoolean = DomUtils.selectBoolean(rootElement, "//enabled");

        // Then
        assertThat(selectedBoolean, is(true));
    }

    @Test
    public void testEvaluateXpathWithQName() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException
    {
        // Given
        String xml = "<root><element>Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        XPathExpression expr = xpath.compile("//element");
        QName expectedType = XPathConstants.STRING;

        String result = DomUtils.evaluateXpath(rootElement, expr, expectedType);

        // Then
        assertThat(result, equalTo("Value"));
    }

    @Test
    public void testChildren() throws Exception
    {
        // Given
        String xml = "<root><element1/><element2/><element3/></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        short nodeType = Node.ELEMENT_NODE;
        List<Node> childNodes = DomUtils.children(rootElement, nodeType);

        // Then
        assertThat(childNodes.size(), is(3));
        assertThat(childNodes.get(0).getNodeName(), equalTo("element1"));
        assertThat(childNodes.get(1).getNodeName(), equalTo("element2"));
        assertThat(childNodes.get(2).getNodeName(), equalTo("element3"));
    }

    @Test
    public void testTranslateListOfNodes() throws Exception
    {
        // Given
        String xml = "<root><element1/><element2/><element3/></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        NodeList nodeList = rootElement.getChildNodes();

        // When
        List<Node> translatedList = DomUtils.translateListOfNodes(nodeList);

        // Then
        assertThat(translatedList.size(), is(3));
        assertThat(translatedList.get(0).getNodeName(), equalTo("element1"));
        assertThat(translatedList.get(1).getNodeName(), equalTo("element2"));
        assertThat(translatedList.get(2).getNodeName(), equalTo("element3"));
    }

    @Test
    public void testGetOwnerDocument() throws Exception
    {
        // Given
        String xml = "<root><element>Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Node childNode = rootElement.getFirstChild();

        // When
        Document ownerDocument = DomUtils.getOwnerDocument(childNode);

        // Then
        assertThat(ownerDocument, is(notNullValue()));
        assertThat(ownerDocument, equalTo(document));
    }

    @Test
    public void testImportNode() throws Exception
    {
        // Given
        String xml1 = "<root1><child>Value</child></root1>";
        String xml2 = "<root2></root2>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document1 = builder.parse(IOUtils.toInputStream(xml1, "utf-8"));
        Document document2 = builder.parse(IOUtils.toInputStream(xml2, "utf-8"));
        Element root1 = document1.getDocumentElement();
        Element root2 = document2.getDocumentElement();
        Node childNode = root1.getFirstChild();

        // When
        Node importedNode = DomUtils.importNode(root2, childNode);

        // Then
        assertThat(importedNode, is(notNullValue()));
        assertThat(importedNode.getNodeType(), is(Node.ELEMENT_NODE));
        assertThat(importedNode.getNodeName(), is(childNode.getNodeName()));
        assertThat(importedNode.getTextContent(), is(childNode.getTextContent()));
    }

    @Test
    public void testEnforceNodeName() throws Exception
    {
        // Given
        String xml = "<root><element>Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Node childNode = rootElement.getFirstChild();

        // When
        DomUtils.enforceNodeName(childNode, "element");

        // Then
        assertThat(childNode.getNodeName(), equalTo("element"));
    }

    @Test
    public void testEnforceNoNamespaceMixes() throws Exception
    {
        // Given
        String xml1 = "<root xmlns=\"http://example.com\"><element1>Value</element1></root>";
        String xml2 = "<root xmlns=\"http://example.com\"><element1>Value</element1></root>";
        Document document1 = DomUtils.parse(xml1);
        Document document2 = DomUtils.parse(xml2);
        Element rootElement1 = document1.getDocumentElement();
        Element rootElement2 = document2.getDocumentElement();

        // When
        DomUtils.enforceNoNamespaceMixes(rootElement1, rootElement2);

        // Then
        assertThat(rootElement1.getNamespaceURI(), is("http://example.com"));
        assertThat(rootElement2.getNamespaceURI(), is("http://example.com"));
    }

    @Test
    public void testEnforceNoNamespaceMixesFails() throws Exception
    {
        // Given
        String xml1 = "<root xmlns=\"http://example.com\"><element1>Value</element1></root>";
        String xml2 = "<root><element1>Value</element1></root>";
        Document document1 = DomUtils.parse(xml1);
        Document document2 = DomUtils.parse(xml2);
        Element rootElement1 = document1.getDocumentElement();
        Element rootElement2 = document2.getDocumentElement();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> DomUtils.enforceNoNamespaceMixes(rootElement1, rootElement2));
    }

    @Test
    public void testIsNodeName() throws Exception
    {
        // Given
        String xml = "<root><element>Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Node childNode = rootElement.getFirstChild();

        // When
        boolean isElementNode = DomUtils.isNodeName(childNode, "element");
        boolean isRootNode = DomUtils.isNodeName(rootElement, "root");

        // Then
        assertThat(isElementNode, is(true));
        assertThat(isRootNode, is(true));
    }

    @Test
    public void testNodeHasAttribute() throws Exception
    {
        // Given
        String xml = "<root><element attr=\"value\">Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Node childNode = rootElement.getFirstChild();

        // When
        boolean hasAttribute = DomUtils.nodeHasAttribute(childNode, "attr");

        // Then
        assertThat(hasAttribute, is(true));
    }

    @Test
    public void testGetAttributeNames() throws Exception
    {
        // Given
        String xml = "<root><element attr1=\"value1\" attr2=\"value2\">Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Node childNode = rootElement.getFirstChild();

        // When
        List<String> attributeNames = DomUtils.getAttributeNames(childNode);

        // Then
        assertThat(attributeNames.size(), is(2));
        assertThat(attributeNames, containsInAnyOrder("attr1", "attr2"));
    }

    @Test
    public void testGetAttribute() throws Exception
    {
        // Given
        String xml = "<root><element attr1=\"value1\" attr2=\"value2\">Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Node childNode = rootElement.getFirstChild();

        // When
        String attributeValue = DomUtils.getAttribute(childNode, "attr1");

        // Then
        assertThat(attributeValue, equalTo("value1"));
    }

    @Test
    public void testSetAttribute() throws Exception
    {
        // Given
        String xml = "<root><element>Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Node childNode = rootElement.getFirstChild();

        // When
        DomUtils.setAttribute(childNode, "attr", "attributeValue");

        // Then
        String attributeValue = ((Element) childNode).getAttribute("attr");
        assertThat(attributeValue, equalTo("attributeValue"));
    }

    @Test
    public void testAsXml() throws Exception
    {
        // Given
        String xml = "<root><element>Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Node rootNode = document.getDocumentElement();

        // When
        String xmlString = DomUtils.asXml(rootNode);

        // Then
        assertThat(xmlString, equalTo("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><element>Value</element></root>"));
    }

    @Test
    public void testAsXmlWithIndent() throws Exception
    {
        // Given
        String xml = "<root><element>Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Node rootNode = document.getDocumentElement();

        // When
        String xmlString = DomUtils.asXml(rootNode, true);

        // Then
        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>\n   <element>Value</element>\n</root>";
        assertThat(xmlString, equalTo(expectedXml));

    }

    @Test
    public void testAsXmlWithoutIndent() throws Exception
    {
        // Given
        String xml = "<root><element>Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Node rootNode = document.getDocumentElement();

        // When
        String xmlString = DomUtils.asXml(rootNode, false);

        // Then
        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><element>Value</element></root>";
        assertThat(xmlString, equalTo(expectedXml));
    }

    @Test
    public void testCreateXMLDeclarationString()
    {
        // Given
        Properties outputProperties = new Properties();
        outputProperties.setProperty("indent", "yes");
        outputProperties.setProperty("omit-xml-declaration", "yes");
        outputProperties.setProperty("encoding", "ISO8859-15");
        outputProperties.setProperty("version", "1.2");

        // When
        String xmlDeclaration = DomUtils.createXMLDeclarationString(outputProperties);

        // Then
        String expectedXmlDeclaration = "<?xml version=\"1.2\" encoding=\"ISO8859-15\"?>";
        assertThat(xmlDeclaration, equalTo(expectedXmlDeclaration));
    }

    @Test
    public void testWrite() throws Exception
    {
        // Given
        String xml = "<root><element>Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Node rootNode = document.getDocumentElement();

        StringWriter writer = new StringWriter();
        Properties outputProperties = new Properties();
        outputProperties.setProperty("encoding", "UTF-8");
        outputProperties.setProperty("version", "1.0");
        outputProperties.setProperty("standalone", "yes");

        // When
        DomUtils.write(rootNode, writer, outputProperties);

        // Then
        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><root><element>Value</element></root>";
        assertThat(writer.toString(), equalTo(expectedXml));
    }

    @Test
    public void testSelectChild() throws Exception
    {
        // Given
        String xml = "<root><element1>Value1</element1><element2>Value2</element2></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        Element childElement1 = DomUtils.selectChild(rootElement, "element1");
        Element childElement2 = DomUtils.selectChild(rootElement, "element2");
        Element nonExistentElement = DomUtils.selectChild(rootElement, "nonexistent");

        // Then
        assertThat(childElement1, is(notNullValue()));
        assertThat(childElement1.getNodeName(), equalTo("element1"));
        assertThat(childElement1.getTextContent(), equalTo("Value1"));

        assertThat(childElement2, is(notNullValue()));
        assertThat(childElement2.getNodeName(), equalTo("element2"));
        assertThat(childElement2.getTextContent(), equalTo("Value2"));

        assertThat(nonExistentElement, is(nullValue()));
    }

    @Test
    public void testSelectChildrenByName() throws Exception
    {
        // Given
        String xml = "<root><element>Value1</element><element>Value2</element><element>Value3</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        List<Element> children = DomUtils.selectChildren(rootElement, "element");

        // Then
        assertThat(children.size(), equalTo(3));

        Element child1 = children.get(0);
        assertThat(child1.getNodeName(), equalTo("element"));
        assertThat(child1.getTextContent(), equalTo("Value1"));

        Element child2 = children.get(1);
        assertThat(child2.getNodeName(), equalTo("element"));
        assertThat(child2.getTextContent(), equalTo("Value2"));

        Element child3 = children.get(2);
        assertThat(child3.getNodeName(), equalTo("element"));
        assertThat(child3.getTextContent(), equalTo("Value3"));
    }

    @Test
    public void testSelectChildren() throws Exception
    {
        // Given
        String xml = "<root><element1>Value1</element1><element2>Value2</element2><element3>Value3</element3></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        List<Element> children = DomUtils.selectChildren(rootElement);

        // Then
        assertThat(children.size(), equalTo(3));

        Element child1 = children.get(0);
        assertThat(child1.getNodeName(), equalTo("element1"));
        assertThat(child1.getTextContent(), equalTo("Value1"));

        Element child2 = children.get(1);
        assertThat(child2.getNodeName(), equalTo("element2"));
        assertThat(child2.getTextContent(), equalTo("Value2"));

        Element child3 = children.get(2);
        assertThat(child3.getNodeName(), equalTo("element3"));
        assertThat(child3.getTextContent(), equalTo("Value3"));
    }

    @Test
    public void testDump() throws Exception
    {
        // Given
        String xml = "<root><element1>Value1</element1><element2>Value2</element2></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Node rootNode = document.getDocumentElement();

        // When
        DomUtils.dump(rootNode);
    }

    @Test
    public void testAppendCDATA() throws Exception
    {
        // Given
        String xml = "<root><![CDATA[My Value]]></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        DomUtils.appendCDATA(rootElement, "CDATA Content");
        DomUtils.dump(document);

        // Then
        List<Node> selectChildNodes = DomUtils.selectChildNodes(rootElement);
        assertThat(selectChildNodes.get(1).getNodeValue(), equalTo("CDATA Content"));
    }

    @Test
    public void testAppendComment() throws Exception
    {
        // Given
        String xml = "<root><element/></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        DomUtils.appendComment(rootElement, "This is a comment");

        // Then
        List<Node> selectChildNodes = DomUtils.selectChildNodes(rootElement);
        assertThat(selectChildNodes.get(1).getNodeValue(), equalTo("This is a comment"));
    }

    @Test
    public void testRenameNode() throws Exception
    {
        // Given
        String xml = "<root><element>Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Element element = (Element) rootElement.getFirstChild();

        // When
        Node renamedNode = DomUtils.renameNode(element, "newElementName");

        // Then
        assertThat(renamedNode, is(notNullValue()));
        assertThat(renamedNode.getNodeName(), equalTo("newElementName"));
    }

    @Test
    public void testRenameNodeWithNamespace() throws Exception
    {
        // Given
        String xml = "<root><element>Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Element element = (Element) rootElement.getFirstChild();
        String namespaceUri = "http://example.com";
        String newName = "newElementName";

        // When
        Node renamedNode = DomUtils.renameNode(element, namespaceUri, newName);

        // Then
        assertThat(renamedNode, is(notNullValue()));
        assertThat(renamedNode.getNamespaceURI(), equalTo(namespaceUri));
        assertThat(renamedNode.getLocalName(), equalTo(newName));
    }

    @Test
    public void testRenameAll() throws Exception
    {
        // Given
        String xml = "<root><from>Value1</from><from>Value2</from></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        DomUtils.renameAll(rootElement, "from", "to");

        // Then
        Node renamedNode1 = rootElement.getElementsByTagName("to").item(0);
        assertThat(renamedNode1, is(notNullValue()));
        assertThat(renamedNode1.getTextContent(), equalTo("Value1"));

        Node renamedNode2 = rootElement.getElementsByTagName("to").item(1);
        assertThat(renamedNode2, is(notNullValue()));
        assertThat(renamedNode2.getTextContent(), equalTo("Value2"));
    }

    @Test
    public void testSelectChildNodes() throws Exception
    {
        // Given
        String xml = "<root><element1>Value1</element1><element2>Value2</element2><element3>Value3</element3></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        List<Node> childNodes = DomUtils.selectChildNodes(rootElement);

        // Then
        assertThat(childNodes.size(), equalTo(3));

        Node childNode1 = childNodes.get(0);
        assertThat(childNode1.getNodeType(), equalTo(Node.ELEMENT_NODE));
        assertThat(childNode1.getNodeName(), equalTo("element1"));

        Node childNode2 = childNodes.get(1);
        assertThat(childNode2.getNodeType(), equalTo(Node.ELEMENT_NODE));
        assertThat(childNode2.getNodeName(), equalTo("element2"));

        Node childNode3 = childNodes.get(2);
        assertThat(childNode3.getNodeType(), equalTo(Node.ELEMENT_NODE));
        assertThat(childNode3.getNodeName(), equalTo("element3"));
    }

    @Test
    public void testIsElement() throws Exception
    {
        // Given
        String xml = "<root><element attribute=\"value\">Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Node elementNode = rootElement.getFirstChild();
        Attr attr = rootElement.getAttributeNode("attribute");

        // When
        boolean element = DomUtils.isElement(elementNode);
        boolean attribute = DomUtils.isElement(attr);

        // Then
        assertThat(element, is(true));
        assertThat(attribute, is(false));
    }

    @Test
    public void testIsText() throws Exception
    {
        // Given
        String xml = "<root><element>Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Node textNode = rootElement.getFirstChild().getFirstChild();

        // When
        boolean element = DomUtils.isText(rootElement);
        boolean text = DomUtils.isText(textNode);

        // Then
        assertThat(element, is(false));
        assertThat(text, is(true));
    }

    @Test
    public void testIsAttribute() throws Exception
    {
        // Given
        String xml = "<root attribute=\"value\"></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Attr attr = rootElement.getAttributeNode("attribute");

        // When
        boolean element = DomUtils.isAttribute(rootElement);
        boolean attribute = DomUtils.isAttribute(attr);

        // Then
        assertThat(element, is(false));
        assertThat(attribute, is(true));
    }

    @Test
    public void testIsCData() throws Exception
    {
        // Given
        String xml = "<root><![CDATA[CDATA Content]]></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Node cdataNode = rootElement.getFirstChild();

        // When
        boolean element = DomUtils.isCData(rootElement);
        boolean cdata = DomUtils.isCData(cdataNode);

        // Then
        assertThat(element, is(false));
        assertThat(cdata, is(true));
    }

    @Test
    public void testIsComment() throws Exception
    {
        // Given
        String xml = "<root><!-- This is a comment --></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Node commentNode = rootElement.getFirstChild();

        // When
        boolean element = DomUtils.isComment(rootElement);
        boolean comment = DomUtils.isComment(commentNode);

        // Then
        assertThat(element, is(false));
        assertThat(comment, is(true));
    }

    @Test
    public void testIsDocument() throws Exception
    {
        // Given
        String xml = "<root><element>Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Node documentNode = document;

        // When
        boolean isRootElement = DomUtils.isDocument(rootElement);
        boolean isDocumentNode = DomUtils.isDocument(documentNode);

        // Then
        assertThat(isRootElement, is(false));
        assertThat(isDocumentNode, is(true));
    }

    @Test
    public void testIsType() throws Exception
    {
        // Given
        String xml = "<root><element>Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Node elementNode = rootElement.getFirstChild();

        // When
        boolean isElementNode = DomUtils.isType(elementNode, Node.ELEMENT_NODE);
        boolean isTextNode = DomUtils.isType(elementNode, Node.TEXT_NODE);

        // Then
        assertThat(isElementNode, is(true));
        assertThat(isTextNode, is(false));
    }

    @Test
    public void testValidateXmlFile() throws Exception
    {
        // Given
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new File("src/test/resources/xml/test-validate-xml-file.xml"));

        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = schemaFactory.newSchema(new File("src/test/resources/xsd/test-validate-xml-file.xsd"));

        // When
        boolean isValid = DomUtils.validate(document, schema);

        // Then
        assertThat(isValid, is(true));
    }

    @Test
    public void testValidateXml() throws IOException, SAXException
    {
        // Given
        String xml = "<root><element>Value</element></root>";

        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = schemaFactory.newSchema(new File("src/test/resources/xsd/test-validate-xml.xsd"));

        // When
        boolean isValid = DomUtils.validate(xml, schema);

        // Then
        assertThat(isValid, is(true));
    }

    @Test
    public void testValidateXmlInvalid() throws IOException, SAXException
    {
        // Given
        String xml = "<root><item>Value</item></root>";

        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = schemaFactory.newSchema(new File("src/test/resources/xsd/test-validate-xml.xsd"));

        // When
        boolean isValid = DomUtils.validate(xml, schema);

        // Then
        assertThat(isValid, is(false));
    }

    @Test
    public void testValidateSource() throws IOException, SAXException
    {
        // Given
        String xml = "<root><element>Value</element></root>";
        Source source = new StreamSource(new StringReader(xml));

        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = schemaFactory.newSchema(new File("src/test/resources/xsd/test-validate-source.xsd"));

        // When
        boolean isValid = DomUtils.validate(source, schema);

        // Then
        assertThat(isValid, is(true));
    }

    @Test
    public void testSelectSuccessorElementFromOrder() throws Exception
    {
        // Given
        String xml = "<root><element1/><element2/><element3/><element4/></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        List<String> orderedElementNames = Arrays.asList("element1", "element2", "element3", "element4");

        // When
        Element successorElement = DomUtils.selectSuccessorElementFromOrder(rootElement, orderedElementNames, "element2");

        // Then
        assertThat(successorElement, is(notNullValue()));
        assertThat(successorElement.getTagName(), is("element3"));
    }

    @Test
    public void testSelectPredecessors()
    {
        // Given
        List<String> orderedNodeNames = Arrays.asList("node1", "node2", "node3", "node4");
        String nodeName = "node3";

        // When
        Set<String> predecessors = DomUtils.selectPredecessors(orderedNodeNames, nodeName);

        // Then
        assertThat(predecessors, containsInAnyOrder("node1", "node2", "node3"));
    }

    @Test
    public void testCloneElement() throws Exception
    {
        // Given
        String xml = "<root><element>Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Element originalElement = (Element) rootElement.getFirstChild();

        // When
        Element clonedElement = DomUtils.cloneElement(originalElement);

        // Then
        assertThat(clonedElement, is(notNullValue()));
        assertThat(clonedElement.getTagName(), is(originalElement.getTagName()));
        assertThat(clonedElement.getTextContent(), is(originalElement.getTextContent()));
    }

    @Test
    public void testCloneDocument() throws Exception
    {
        // Given
        String xml = "<root><element>Value</element></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document originalDocument = builder.parse(IOUtils.toInputStream(xml, "utf-8"));

        // When
        Document clonedDocument = DomUtils.cloneDocument(originalDocument);

        // Then
        assertThat(clonedDocument, is(notNullValue()));
        assertThat(clonedDocument, not(sameInstance(originalDocument)));

        Element originalRootElement = originalDocument.getDocumentElement();
        Element clonedRootElement = clonedDocument.getDocumentElement();
        assertThat(clonedRootElement.getTagName(), is(originalRootElement.getTagName()));
        assertThat(clonedRootElement.getTextContent(), is(originalRootElement.getTextContent()));
    }

    @Test
    public void testCopyAttributes() throws Exception
    {
        // Given
        String xml = "<root><source attr1=\"value1\" attr2=\"value2\"></source><destination></destination></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Element sourceElement = (Element) rootElement.getElementsByTagName("source").item(0);
        Element destElement = (Element) rootElement.getElementsByTagName("destination").item(0);

        // When
        DomUtils.copyAttributes(sourceElement, destElement);

        // Then
        assertThat(destElement.getAttribute("attr1"), is("value1"));
        assertThat(destElement.getAttribute("attr2"), is("value2"));
    }

    @Test
    public void testCopyChildren() throws Exception
    {
        // Given
        String xml = "<root><source><child1>Value1</child1><child2>Value2</child2></source><destination></destination></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();
        Element sourceElement = (Element) rootElement.getElementsByTagName("source").item(0);
        Element destElement = (Element) rootElement.getElementsByTagName("destination").item(0);

        // When
        DomUtils.copyChildren(sourceElement, destElement);

        // Then
        NodeList copiedChildren = destElement.getChildNodes();
        assertThat(copiedChildren.getLength(), is(2));

        Element copiedChild1 = (Element) copiedChildren.item(0);
        assertThat(copiedChild1.getTagName(), is("child1"));
        assertThat(copiedChild1.getTextContent(), is("Value1"));

        Element copiedChild2 = (Element) copiedChildren.item(1);
        assertThat(copiedChild2.getTagName(), is("child2"));
        assertThat(copiedChild2.getTextContent(), is("Value2"));
    }

    @Test
    public void testAppendTextNode() throws Exception
    {
        // Given
        String xml = "<root></root>";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(IOUtils.toInputStream(xml, "utf-8"));
        Element rootElement = document.getDocumentElement();

        // When
        String textContent = "Sample Text";
        Element parentElement = DomUtils.appendTextNode(rootElement, textContent);

        // Then
        assertThat(parentElement, is(sameInstance(rootElement)));

        Node textNode = parentElement.getFirstChild();
        assertThat(textNode, instanceOf(Text.class));
        assertThat(textNode.getNodeValue(), is(textContent));
    }
}