/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.xml.vxml;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * Submit values to a document server.
 * <p>
 * The <code>&lt;submit&gt;</code> element is used to submit information to
 * the origin Web server and then transition to the document sent back in the
 * response. Unlike <code>&lt;goto&gt;</code>, it lets you submit a list of
 * variables to the document server via an HTTP GET or POST request.
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Goto
 *
 * @author Steve Doyle
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Submit
        extends AbstractVoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "submit";

    /**
     * The URI reference.
     */
    public static final String ATTRIBUTE_NEXT = "next";

    /**
     * Like next, except that the URI reference is dynamically determined by
     * evaluating the given ECMAScript expression.
     */
    public static final String ATTRIBUTE_EXPR = "expr";

    /**
     * The list of variables to submit. By default, all the named input item
     * variables are submitted. If a namelist is supplied, it may contain
     * individual variable references which are submitted with the same
     * qualification used in the namelist. Declared VoiceXML and ECMAScript
     * variables can be referenced. If an undeclared variable is referenced in
     * the namelist, then an error.semantic is thrown.
     */
    public static final String ATTRIBUTE_NAMELIST = "namelist";

    /**
     * The request method: get (the default) or post.
     */
    public static final String ATTRIBUTE_METHOD = "method";

    /**
     * The media encoding type of the submitted document (when the value of
     * method is "post"). The default is application/x-www-form-urlencoded.
     * Interpreters must also support multipart/form-data and may support
     * additional encoding types.
     */
    public static final String ATTRIBUTE_ENCTYPE = "enctype";

    /**
     * The URI of the audio clip to play while the fetch is being done. If not
     * specified, the fetchaudio property is used, and if that property is not
     * set, no audio is played during the fetch. The fetching of the audio clip
     * is governed by the audiofetchhint, audiomaxage, audiomaxstale, and
     * fetchtimeout properties in effect at the time of the fetch. The playing
     * of the audio clip is governed by the fetchaudiodelay, and
     * fetchaudiominimum properties in effect at the time of the fetch.
     */
    public static final String ATTRIBUTE_FETCHAUDIO = "fetchaudio";

    /**
     * The interval to wait for the content to be returned before throwing an
     * error.badfetch event. This defaults to the fetchtimeout property.
     */
    public static final String ATTRIBUTE_FETCHTIMEOUT = "fetchtimeout";

    /**
     * Defines when the interpreter context should retrieve content from the
     * server. prefetch indicates a file may be downloaded when the page is
     * loaded, whereas safe indicates a file that should only be downloaded when
     * actually needed. This defaults to the audiofetchhint property.
     */
    public static final String ATTRIBUTE_FETCHHINT = "fetchhint";

    /**
     * Indicates that the document is willing to use content whose age is no
     * greater than the specified time in seconds. The document is not willing
     * to use stale content, unless maxstale is also provided. This defaults to
     * the audiomaxage property.
     */
    public static final String ATTRIBUTE_MAXAGE = "maxage";

    /**
     * Indicates that the document is willing to use content that has exceeded
     * its expiration time. If maxstale is assigned a value, then the document
     * is willing to accept content that has exceeded its expiration time by no
     * more than the specified number of seconds. This defaults to the
     * audiomaxstale property.
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
        ATTRIBUTE_NAMES.add(ATTRIBUTE_EXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHAUDIO);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHHINT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHTIMEOUT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXAGE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXSTALE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_METHOD);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAMELIST);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NEXT);
    }

    /**
     * Construct a new submit object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Submit() {
        super(null);
    }

    /**
     * Construct a new submit object.
     *
     * @param node
     *        The encapsulated node.
     */
    Submit(final Node node) {
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
    private Submit(final Node n,
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
        return new Submit(n, factory);
    }

    /**
     * Retrieve the next attribute.
     *
     * @return Value of the next attribute.
     * @see #ATTRIBUTE_NEXT
     */
    public String getNext() {
        return getAttribute(ATTRIBUTE_NEXT);
    }

    /**
     * Retrieve the next attribute.
     *
     * @return Value of the next attribute.
     * @throws URISyntaxException
     *         Value is not a valid URI.
     * @see #ATTRIBUTE_NEXT
     * @since 0.7
     */
    public URI getNextUri() throws URISyntaxException {
        final String value = getNext();
        if (value == null) {
            return null;
        }
        return new URI(value);
    }

    /**
     * Set the next attribute.
     *
     * @param next
     *        Value of the next attribute.
     * @see #ATTRIBUTE_NEXT
     */
    public void setNext(final String next) {
        setAttribute(ATTRIBUTE_NEXT, next);
    }

    /**
     * Set the next attribute.
     *
     * @param next
     *        Value of the next attribute.
     * @see #ATTRIBUTE_NEXT
     * @since 0.7
     */
    public void setNextUri(final URI next) {
        final String value;
        if (next == null) {
            value = null;
        } else {
            value = next.toString();
        }
        setAttribute(ATTRIBUTE_NEXT, value);
    }

    /**
     * Retrieve the expr attribute.
     *
     * @return Value of the expr attribute.
     * @see #ATTRIBUTE_EXPR
     */
    public String getExpr() {
        return getAttribute(ATTRIBUTE_EXPR);
    }

    /**
     * Set the expr attribute.
     *
     * @param expr
     *        Value of the expr attribute.
     * @see #ATTRIBUTE_EXPR
     */
    public void setExpr(final String expr) {
        setAttribute(ATTRIBUTE_EXPR, expr);
    }

    /**
     * Retrieve the namelist attribute.
     *
     * @return Value of the namelist attribute.
     * @see #ATTRIBUTE_NAMELIST
     * @see #getNameListObject()
     */
    public String getNamelist() {
        return getAttribute(ATTRIBUTE_NAMELIST);
    }

    /**
     * Set the namelist attribute.
     *
     * @param namelist
     *        Value of the namelist attribute.
     * @see #ATTRIBUTE_NAMELIST
     * @see #setNameListObject(TokenList)
     */
    public void setNamelist(final String namelist) {
        setAttribute(ATTRIBUTE_NAMELIST, namelist);
    }

    /**
     * Retrieve the namelist attribute as a list object.
     *
     * @return Value of the namelist attribute as a list.
     *
     * @see #getNamelist()
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
     */
    public void setNameListObject(final TokenList list) {
        if (list == null) {
            return;
        }

        final String namelist = list.toString();

        setNamelist(namelist);
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

        throw new IllegalArgumentException("Unsupported method '"
                + method + "'");
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
     *
     * @return Value of the enctype attribute.
     * @see #ATTRIBUTE_ENCTYPE
     */
    public String getEnctype() {
        return getAttribute(ATTRIBUTE_ENCTYPE);
    }

    /**
     * Set the enctype attribute.
     *
     * @param enctype
     *        Value of the enctype attribute.
     * @see #ATTRIBUTE_ENCTYPE
     */
    public void setEnctype(final String enctype) {
        setAttribute(ATTRIBUTE_ENCTYPE, enctype);
    }

    /**
     * Retrieve the fetchaudio attribute.
     *
     * @return Value of the fetchaudio attribute.
     * @see #ATTRIBUTE_FETCHAUDIO
     */
    public String getFetchaudio() {
        return getAttribute(ATTRIBUTE_FETCHAUDIO);
    }

    /**
     * Set the fetchaudio attribute.
     *
     * @param fetchaudio
     *        Value of the fetchaudio attribute.
     * @see #ATTRIBUTE_FETCHAUDIO
     */
    public void setFetchaudio(final String fetchaudio) {
        setAttribute(ATTRIBUTE_FETCHAUDIO, fetchaudio);
    }

    /**
     * Retrieve the fetchhint attribute.
     *
     * @return Value of the fetchhint attribute.
     * @see #ATTRIBUTE_FETCHHINT
     */
    public String getFetchhint() {
        return getAttribute(ATTRIBUTE_FETCHHINT);
    }

    /**
     * Set the fetchhint attribute.
     *
     * @param fetchhint
     *        Value of the fetchhint attribute.
     * @see #ATTRIBUTE_FETCHHINT
     */
    public void setFetchhint(final String fetchhint) {
        setAttribute(ATTRIBUTE_FETCHHINT, fetchhint);
    }

    /**
     * Retrieve the fetchtimeout attribute.
     *
     * @return Value of the fetchtimeout attribute.
     * @see #ATTRIBUTE_FETCHTIMEOUT
     */
    public String getFetchtimeout() {
        return getAttribute(ATTRIBUTE_FETCHTIMEOUT);
    }

    /**
     * Set the fetchtimeout attribute.
     *
     * @param fetchtimeout
     *        Value of the fetchtimeout attribute.
     * @see #ATTRIBUTE_FETCHTIMEOUT
     */
    public void setFetchtimeout(final String fetchtimeout) {
        setAttribute(ATTRIBUTE_FETCHTIMEOUT, fetchtimeout);
    }

    /**
     * Retrieve the maxage attribute.
     *
     * @return Value of the maxage attribute.
     * @see #ATTRIBUTE_MAXAGE
     */
    public String getMaxage() {
        return getAttribute(ATTRIBUTE_MAXAGE);
    }

    /**
     * Set the maxage attribute.
     *
     * @param maxage
     *        Value of the maxage attribute.
     * @see #ATTRIBUTE_MAXAGE
     */
    public void setMaxage(final String maxage) {
        setAttribute(ATTRIBUTE_MAXAGE, maxage);
    }

    /**
     * Retrieve the maxstale attribute.
     *
     * @return Value of the maxstale attribute.
     * @see #ATTRIBUTE_MAXSTALE
     */
    public String getMaxstale() {
        return getAttribute(ATTRIBUTE_MAXSTALE);
    }

    /**
     * Set the maxstale attribute.
     *
     * @param maxstale
     *        Value of the maxstale attribute.
     * @see #ATTRIBUTE_MAXSTALE
     */
    public void setMaxstale(final String maxstale) {
        setAttribute(ATTRIBUTE_MAXSTALE, maxstale);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean canContainChild(final String tagName) {
        return false;
    }

    /**
     * Returns a collection of permitted attribute names for the node.
     *
     * @return A collection of attribute names that are allowed for the node
     */
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
