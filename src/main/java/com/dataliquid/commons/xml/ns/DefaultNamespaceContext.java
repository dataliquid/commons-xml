package com.dataliquid.commons.xml.ns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

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

    private static final Map<String, String> alias = new HashMap<>();
    private static final Map<String, Collection<String>> uri = new HashMap<>();

    static
    {
        alias.put(NAMESPACE_ALIAS_HTML, NAMESPACE_HTML);
        alias.put(NAMESPACE_ALIAS_XML, NAMESPACE_XML);
        alias.put(NAMESPACE_ALIAS_XMLNS, NAMESPACE_XMLNS);
        alias.put(NAMESPACE_ALIAS_XS, NAMESPACE_XS);

        for (Map.Entry<String, String> entry : alias.entrySet())
        {
            if (!uri.containsKey(entry.getValue()))
            {
                uri.put(entry.getValue(), new ArrayList<String>());
            }
            uri.get(entry.getValue()).add(entry.getKey());
        }
    }

    @Override
    public String getNamespaceURI(String prefix)
    {
        return StringUtils.isEmpty(prefix) ? DEFAULT_NS : alias.get(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI)
    {
        Iterator<?> prefixes = getPrefixes(namespaceURI);
        return prefixes.hasNext() ? (String) prefixes.next() : null;
    }

    @Override
    public Iterator<?> getPrefixes(String namespaceURI)
    {
        return uri.get(namespaceURI).iterator();
    }
}
