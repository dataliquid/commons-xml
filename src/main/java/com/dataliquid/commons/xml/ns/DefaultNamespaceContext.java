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
package com.dataliquid.commons.xml.ns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

/**
 * A default implementation of the NamespaceContext interface.
 */
public class DefaultNamespaceContext implements javax.xml.namespace.NamespaceContext
{
    public static final String NAMESPACE_XS = "http://www.w3.org/2001/XMLSchema";
    public static final String NAMESPACE_XML = "http://www.w3.org/XML/1998/namespace";
    public static final String NAMESPACE_XMLNS = "http://www.w3.org/2000/xmlns/";
    public static final String NAMESPACE_HTML = "http://www.w3.org/1999/xhtml";

    public static final String NAMESPACE_ALIAS_XS = "xs";
    public static final String NAMESPACE_ALIAS_XML = "xml";
    public static final String NAMESPACE_ALIAS_XMLNS = "xmlns";
    public static final String NAMESPACE_ALIAS_HTML = "html";

    public static final String DEFAULT_NS = NAMESPACE_XMLNS;

    private static final Map<String, String> alias = new ConcurrentHashMap<>();
    private static final Map<String, Collection<String>> uri = new ConcurrentHashMap<>();

    static
    {
        alias.put(NAMESPACE_ALIAS_HTML, NAMESPACE_HTML);
        alias.put(NAMESPACE_ALIAS_XML, NAMESPACE_XML);
        alias.put(NAMESPACE_ALIAS_XMLNS, NAMESPACE_XMLNS);
        alias.put(NAMESPACE_ALIAS_XS, NAMESPACE_XS);

        // Initialize the reverse mapping without instantiating objects in loops
        Collection<String> htmlPrefixes = new ArrayList<>();
        htmlPrefixes.add(NAMESPACE_ALIAS_HTML);
        uri.put(NAMESPACE_HTML, htmlPrefixes);

        Collection<String> xmlPrefixes = new ArrayList<>();
        xmlPrefixes.add(NAMESPACE_ALIAS_XML);
        uri.put(NAMESPACE_XML, xmlPrefixes);

        Collection<String> xmlnsPrefixes = new ArrayList<>();
        xmlnsPrefixes.add(NAMESPACE_ALIAS_XMLNS);
        uri.put(NAMESPACE_XMLNS, xmlnsPrefixes);

        Collection<String> xsPrefixes = new ArrayList<>();
        xsPrefixes.add(NAMESPACE_ALIAS_XS);
        uri.put(NAMESPACE_XS, xsPrefixes);
    }

    /**
     * Get the namespace URI bound to the given prefix in the current scope.
     *
     * @param prefix
     *            the prefix for which to retrieve the namespace URI
     * @return the namespace URI bound to the given prefix, or the DEFAULT_NS if not
     *         found
     */
    @Override
    public String getNamespaceURI(String prefix)
    {
        return StringUtils.isEmpty(prefix) ? DEFAULT_NS : alias.get(prefix);
    }

    /**
     * Get the prefix bound to the given namespace URI in the current scope.
     *
     * @param namespaceURI
     *            the namespace URI for which to retrieve the prefix
     * @return the prefix bound to the given namespace URI, or null if not found
     */
    @Override
    public String getPrefix(String namespaceURI)
    {
        Iterator<String> prefixes = getPrefixes(namespaceURI);
        return prefixes.hasNext() ? prefixes.next() : null;
    }

    /**
     * Get all prefixes bound to a namespace URI in the current scope.
     *
     * @param namespaceURI
     *            the namespace URI for which to retrieve the prefixes
     * @return an Iterator over all prefixes bound to the namespace URI
     */
    @Override
    public Iterator<String> getPrefixes(String namespaceURI)
    {
        Collection<String> prefixes = uri.get(namespaceURI);
        return prefixes != null ? prefixes.iterator() : new ArrayList<String>().iterator();
    }
}
