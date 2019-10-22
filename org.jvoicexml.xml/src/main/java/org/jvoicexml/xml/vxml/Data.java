/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package org.jvoicexml.xml.vxml;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * The {@code <data>} element allows a VoiceXML application to fetch arbitrary
 * XML data from a document server without transitioning to a new VoiceXML
 * document. The XML data fetched by the {@code <data>} element is bound to
 * ECMAScript through the named variable that exposes a read-only subset of the
 * W3C Document Object Model.
 * @author Steve Doyle
 * @author Dirk Schnelle-Walka
 */
public class Data
        extends AbstractVoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "data";

    /**
     * The name of the variable that exposes the DOM.
     */
    public static final String ATTRIBUTE_NAME = "name";

    /**
     * The URI specifying the location of the XML data to retrieve.
     */
    public static final String ATTRIBUTE_SRC = "src";

    /**
     * Like src, except that the URI is dynamically determined by evaluating the
     * given ECMAScript expression when the data needs to be fetched. If srcexpr
     * cannot be evaluated, an error.semantic event is thrown.
     */
    public static final String ATTRIBUTE_SRCEXPR = "srcexpr";

    /**
     * This defaults to the fetchaudio property.
     */
    public static final String ATTRIBUTE_FETCHAUDIO = "fetchaudio";

    /**
     * The request method: get (the default) or post.
     */
    public static final String ATTRIBUTE_METHOD = "method";

    /**
     * The media encoding type of the submitted document. The default is
     * application/x-www-form-urlencoded. Interpreters must also support
     * multipart/form-data and may support additional encoding types.
     */
    public static final String ATTRIBUTE_ENCTYPE = "enctype";

    /**
     * The list of variables to submit. By default, no variables are submitted.
     * If a namelist is supplied, it may contain individual variable references
     * which are submitted with the same qualification used in the namelist.
     * Declared VoiceXML and ECMAScript variables can be referenced.
     */
    public static final String ATTRIBUTE_NAMELIST = "namelist";

    /**
     * This defaults to the datafetchhint property.
     */
    public static final String ATTRIBUTE_FETCHHINT = "fetchint";

    /**
     * This defaults to the fetchtimeout property.
     */
    public static final String ATTRIBUTE_FETCHTIMEOUT = "fetchtimeout";

    /**
     * This defaults to the datamaxage property.
     */
    public static final String ATTRIBUTE_MAXAGE = "maxage";

    /**
     * This defaults to the datamaxstale property.
     */
    public static final String ATTRIBUTE_MAXSTALE = "maxstale";
    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_ENCTYPE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHAUDIO);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHHINT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHTIMEOUT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXAGE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXSTALE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_METHOD);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAME);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAMELIST);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SRC);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SRCEXPR);
    }

    /**
     * Valid child tags for this node.
     */
    private static final Set<String> CHILD_TAGS;

    /**
     * Set the valid child tags for this node.
     */
    static {
        CHILD_TAGS = new java.util.HashSet<String>();
    }

    /**
     * Construct a new data object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Data() {
        super(null);
    }

    /**
     * Construct a new data object.
     * @param node The encapsulated node.
     */
    Data(final Node node) {
        super(node);
    }

    /**
     * Constructs a new node.
     *
     * @param n
     *            The encapsulated node.
     * @param factory
     *            The node factory to use.
     */
    protected Data(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        super(n, factory);
    }

    /**
     * Get the name of the tag for the derived node.
     *
     * @return name of the tag.
     */
    public String getTagName() {
        return TAG_NAME;
    }

    /**
     * {@inheritDoc}
     */
    public XmlNode newInstance(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        return new Data(n, factory);
    }

    /**
     * Retrieve the name attribute.
     * @return Value of the name attribute.
     * @see #ATTRIBUTE_NAME
     */
    public String getName() {
        return getAttribute(ATTRIBUTE_NAME);
    }

    /**
     * Set the name attribute.
     * @param name Value of the name attribute.
     * @see #ATTRIBUTE_NAME
     */
    public void setName(final String name) {
        setAttribute(ATTRIBUTE_NAME, name);
    }

    /**
     * Retrieve the src attribute.
     * @return Value of the src attribute.
     * @see #ATTRIBUTE_SRC
     */
    public String getSrc() {
        return getAttribute(ATTRIBUTE_SRC);
    }

    /**
     * Retrieve the src attribute.
     * @return Value of the src attribute.
     * @throws URISyntaxException
     *         Src attribute does not denote a valid URI.
     * @see #ATTRIBUTE_SRC
     * @since 0.6
     */
    public URI getSrcUri() throws URISyntaxException {
        final String src = getSrc();
        if (src == null) {
            return null;
        }

        return new URI(src);
    }

    /**
     * Set the src attribute.
     * @param src Value of the src attribute.
     * @see #ATTRIBUTE_SRC
     */
    public void setSrc(final String src) {
        setAttribute(ATTRIBUTE_SRC, src);
    }

    /**
     * Set the src attribute.
     * @param uri URI of the src attribute.
     * @see #ATTRIBUTE_SRC
     */
    public void setSrc(final URI uri) {
        final String src;
        if (uri == null) {
            src = null;
        } else {
            src = uri.toString();
        }
        setSrc(src);
    }

    /**
     * Retrieve the srcexpr attribute.
     * @return Value of the srcexpr attribute.
     * @see #ATTRIBUTE_SRCEXPR
     */
    public String getSrcexpr() {
        return getAttribute(ATTRIBUTE_SRCEXPR);
    }

    /**
     * Set the srcexpr attribute.
     * @param srcexpr Value of the srcexpr attribute.
     * @see #ATTRIBUTE_SRCEXPR
     */
    public void setSrcexpr(final String srcexpr) {
        setAttribute(ATTRIBUTE_SRCEXPR, srcexpr);
    }

    /**
     * Retrieve the fetchaudio attribute.
     * @return Value of the fetchaudio attribute.
     * @see #ATTRIBUTE_FETCHAUDIO
     */
    public String getFetchaudio() {
        return getAttribute(ATTRIBUTE_FETCHAUDIO);
    }

    /**
     * Set the fetchaudio attribute.
     * @param fetchaudio Value of the fetchaudio attribute.
     * @see #ATTRIBUTE_FETCHAUDIO
     */
    public void setFetchaudio(final String fetchaudio) {
        setAttribute(ATTRIBUTE_FETCHAUDIO, fetchaudio);
    }

    /**
     * Retrieve the method attribute.
     *
     * @return Value of the method attribute.
     * @see #ATTRIBUTE_METHOD
     */
    public String getMethodName() {
        final RequestMethod method = getMethod();
        if (method == null) {
            return null;
        }
        return method.getMethod();
    }

    /**
     * Retrieve the method attribute.
     *
     * @return Value of the method attribute.
     * @see #ATTRIBUTE_METHOD
     * @since 0.7
     */
    public RequestMethod getMethod() {
        final String method = getAttribute(ATTRIBUTE_METHOD);
        if (method == null) {
            return null;
        }

        if (RequestMethod.POST.getMethod().equalsIgnoreCase(method)) {
            return RequestMethod.POST;
        } else if (RequestMethod.GET.getMethod().equalsIgnoreCase(method)) {
            return RequestMethod.GET;
        }

        throw new IllegalArgumentException("Unsupported method '" + method
                + "'");
    }

    /**
     * Set the method attribute.
     *
     * @param method
     *        Value of the method attribute.
     * @see #ATTRIBUTE_METHOD
     */
    public void setMethodName(final String method) {
        setAttribute(ATTRIBUTE_METHOD, method);
    }

    /**
     * Set the method attribute.
     *
     * @param method
     *        Value of the method attribute.
     * @see #ATTRIBUTE_METHOD
     * @since 0.7
     */
    public void setMethod(final RequestMethod method) {
        final String value;
        if (method == null) {
            value = null;
        } else {
            value = method.getMethod();
        }
        setAttribute(ATTRIBUTE_METHOD, value);
    }

    /**
     * Retrieve the enctype attribute.
     * @return Value of the enctype attribute.
     * @see #ATTRIBUTE_ENCTYPE
     */
    public String getEnctype() {
        return getAttribute(ATTRIBUTE_ENCTYPE);
    }

    /**
     * Set the enctype attribute.
     * @param enctype Value of the enctype attribute.
     * @see #ATTRIBUTE_ENCTYPE
     */
    public void setEnctype(final String enctype) {
        setAttribute(ATTRIBUTE_ENCTYPE, enctype);
    }

    /**
     * Retrieve the namelist attribute.
     * @return Value of the namelist attribute.
     * @see #ATTRIBUTE_NAMELIST
     */
    public String getNamelist() {
        return getAttribute(ATTRIBUTE_NAMELIST);
    }

    /**
     * Set the namelist attribute.
     * @param namelist Value of the namelist attribute.
     * @see #ATTRIBUTE_NAMELIST
     */
    public void setNamelist(final String namelist) {
        setAttribute(ATTRIBUTE_NAMELIST, namelist);
    }

    /**
     * Retrieve the namelist attribute as a list object.
     *
     * @return Value of the namelist attribute as a list.
     * @see #getNamelist()
     * @since 0.7.1
     */
    public TokenList getNameListObject() {
        final String namelist = getNamelist();

        return new TokenList(namelist);
    }

    /**
     * Set the namelist attribute.
     *
     * @param list
     *        Value of the namelist attribute.
     * @see #ATTRIBUTE_NAMELIST
     * @see #setNamelist(String)
     * @since 0.7.1
     */
    public void setNameList(final TokenList list) {
        final String namelist;
        if (list == null) {
            namelist = null;
        } else {
            namelist = list.toString();
        }
        setNamelist(namelist);
    }

    /**
     * Retrieve the fetchint attribute.
     * @return Value of the fetchint attribute.
     * @see #ATTRIBUTE_FETCHHINT
     */
    public String getFetchint() {
        return getAttribute(ATTRIBUTE_FETCHHINT);
    }

    /**
     * Set the fetchint attribute.
     * @param fetchint Value of the fetchint attribute.
     * @see #ATTRIBUTE_FETCHHINT
     */
    public void setFetchint(final String fetchint) {
        setAttribute(ATTRIBUTE_FETCHHINT, fetchint);
    }

    /**
     * Retrieve the fetchtimeout attribute.
     * @return Value of the fetchtimeout attribute.
     * @see #ATTRIBUTE_FETCHTIMEOUT
     */
    public String getFetchtimeout() {
        return getAttribute(ATTRIBUTE_FETCHTIMEOUT);
    }

    /**
     * Set the fetchtimeout attribute.
     * @param fetchtimeout Value of the fetchtimeout attribute.
     * @see #ATTRIBUTE_FETCHTIMEOUT
     */
    public void setFetchtimeout(final String fetchtimeout) {
        setAttribute(ATTRIBUTE_FETCHTIMEOUT, fetchtimeout);
    }

    /**
     * Retrieve the maxage attribute.
     * @return Value of the maxage attribute.
     * @see #ATTRIBUTE_MAXAGE
     */
    public String getMaxage() {
        return getAttribute(ATTRIBUTE_MAXAGE);
    }

    /**
     * Set the maxage attribute.
     * @param maxage Value of the maxage attribute.
     * @see #ATTRIBUTE_MAXAGE
     */
    public void setMaxage(final String maxage) {
        setAttribute(ATTRIBUTE_MAXAGE, maxage);
    }

    /**
     * Retrieve the maxstale attribute.
     * @return Value of the maxstale attribute.
     * @see #ATTRIBUTE_MAXSTALE
     */
    public String getMaxstale() {
        return getAttribute(ATTRIBUTE_MAXSTALE);
    }

    /**
     * Set the maxstale attribute.
     * @param maxstale Value of the maxstale attribute.
     * @see #ATTRIBUTE_MAXSTALE
     */
    public void setMaxstale(final String maxstale) {
        setAttribute(ATTRIBUTE_MAXSTALE, maxstale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canContainChild(final String tagName) {
        return CHILD_TAGS.contains(tagName);
    }

    /**
     * Returns a collection of permitted attribute names for the node.
     *
     * @return A collection of attribute names that are allowed for the node
     */
    @Override
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
