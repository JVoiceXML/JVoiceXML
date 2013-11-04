/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/ccxml/Fetch.java $
 * Version: $LastChangedRevision: 3829 $
 * Date:    $Date: 2013-07-16 13:01:00 +0200 (Tue, 16 Jul 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.ccxml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * <code>&lt;fetch&gt;</code> is used to asynchronously fetch content
 * identified by the attributes of the <code>&lt;fetch&gt;</code>. The
 * fetched content may either be a CCXML document, or script content. Content
 * that has been acquired using <code>&lt;fetch&gt;</code> is accessible
 * through other elements defined by CCXML. Execution returns from the element
 * immediately, and the CCXML application can continue on while the platform
 * works to fetch the identified resource. When the fetch request has been
 * completed, an event is generated against the session that initiated the
 * fetch. The event is one of fetch.done, which indicates that the identified
 * content was fetched successfully, or error.fetch, indicative of a failure to
 * fetch the requested content. Note that even if content is successfully
 * fetched, errors in processing fetched content (for instance, a CCXML document
 * with a syntax error) may result in an error.fetch being thrown.
 *
 * The fetch request is local to the session that initiated the
 * <code>&lt;fetch&gt;</code>, and is referenced through a unique identifier
 * generated by the CCXML platform. The application may obtain the unique
 * identifier for a fetch request by providing an ECMAScript left-hand-side
 * expression in the fetchid attribute when the fetch is performed. The fetch
 * identifier can also be obtained as a property of the fetch.done event. The
 * application uses the fetch identifier in any CCXML elements that reference
 * fetched content, currently <code>&lt;goto&gt;</code> and
 * <code>&lt;script&gt;</code>.
 *
 * Fetched content has a lifetime that is limited to that of the document in
 * which it is fetched. Therefore, following a transition to a new CCXML
 * document using <code>&lt;goto&gt;</code>, content fetched in the scope of
 * the current document is no longer accessible. Note that this should not be
 * taken to preclude platform-level optimizations or caching of resources that
 * are fetched multiple times.
 *
 * The use of <code>&lt;fetch&gt;</code> to obtain content does not compel the
 * application to make use of that content. However, it is wasteful of system
 * resources to fetch resources that are not used. Platforms are responsible for
 * clearing out unused fetch resources, and may impose limits on the resources
 * that can be fetched by a single session.
 *
 *
 * @author Steve Doyle
 * @version $Revision: 3829 $
 */
public final class Fetch
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "fetch";

    /**
     * An ECMAScript expression which returns a character string that indicates
     * the media encoding type of the submitted document (when the value of the
     * method is "post").
     */
    public static final String ATTRIBUTE_ENCTYPE = "enctype";

    /**
     * An ECMAScript left hand side expression evaluating to a previously
     * defined variable. The value of the attribute will receive an internally
     * generated unique string identifier to be associated with the completion
     * event. This identifier can be tested by the fetch completion event
     * handler to distinguish among several outstanding fetch requests. If this
     * attribute is not specified, the fetch identifier can be acquired from the
     * fetch completion event. Every fetch request will receive a unique fetch
     * identifier, even if the request if for the same URL.
     */
    public static final String ATTRIBUTE_FETCHID = "fetchid";

    /**
     * The character string returned is interpreted as a time interval. This
     * indicates that the document is willing to use content whose age is no
     * greater than the specified time in seconds (cf. 'max-age' in HTTP 1.1
     * [RFC2616]). The document is not willing to use stale content, unless
     * maxstale is also provided.
     */
    public static final String ATTRIBUTE_MAXAGE = "maxage";

    /**
     * The character string returned is interpreted as a time interval. This
     * indicates that the document is willing to use content that has exceeded
     * its expiration time (cf. 'max-age' in HTTP 1.1 [RFC2616]). If maxstale is
     * assigned a value, then the document is willing to accept content that has
     * exceeded its expiration time by no more than the specified number of
     * seconds.
     */
    public static final String ATTRIBUTE_MAXSTALE = "maxstale";

    /**
     * An ECMAScript expression which returns a character string that indicates
     * the HTTP method to use.
     */
    public static final String ATTRIBUTE_METHOD = "method";

    /**
     * A list of zero or more whitespace separated CCXML variable names. These
     * variables will be submitted to the server, with the same qualification as
     * used in the namelist. When an ECMAscript variable is submitted to the
     * server, its value is first converted into a string before being
     * submitted. If the variable is an ECMAScript Object, the mechanism by
     * which it is submitted is not currently defined. Instead of submitting
     * ECMAScript Objects directly, the application developer may explicitly
     * submit the properties of an Object. e.g. "date.month date.year".
     */
    public static final String ATTRIBUTE_NAMELIST = "namelist";

    /**
     * An ECMAScript expression which returns the URI of the resource to be
     * fetched.
     */
    public static final String ATTRIBUTE_NEXT = "next";

    /**
     * The character string returned is interpreted as a time interval. This
     * interval begins when the fetch is executed. The fetch will fail if not
     * completed at the end of this interval. A failed fetch will return the
     * error.fetch event.
     */
    public static final String ATTRIBUTE_TIMEOUT = "timeout";

    /**
     * An ECMAScript expression which returns a character string that specifies
     * the MIME type of the fetched content.
     */
    public static final String ATTRIBUTE_TYPE = "type";

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
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXAGE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXSTALE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_METHOD);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAMELIST);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NEXT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TIMEOUT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TYPE);
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
     * Construct a new fetch object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Fetch() {
        super(null);
    }

    /**
     * Construct a new fetch object.
     * @param node The encapsulated node.
     */
    Fetch(final Node node) {
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
    private Fetch(final Node n,
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
        return new Fetch(n, factory);
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
     * Retrieve the fetchid attribute.
     * @return Value of the fetchid attribute.
     * @see #ATTRIBUTE_FETCHID
     */
    public String getFetchid() {
        return getAttribute(ATTRIBUTE_FETCHID);
    }

    /**
     * Set the fetchid attribute.
     * @param fetchid Value of the fetchid attribute.
     * @see #ATTRIBUTE_FETCHID
     */
    public void setFetchid(final String fetchid) {
        setAttribute(ATTRIBUTE_FETCHID, fetchid);
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
     * Retrieve the method attribute.
     * @return Value of the method attribute.
     * @see #ATTRIBUTE_METHOD
     */
    public String getMethod() {
        return getAttribute(ATTRIBUTE_METHOD);
    }

    /**
     * Set the method attribute.
     * @param method Value of the method attribute.
     * @see #ATTRIBUTE_METHOD
     */
    public void setMethod(final String method) {
        setAttribute(ATTRIBUTE_METHOD, method);
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
     * Retrieve the next attribute.
     * @return Value of the next attribute.
     * @see #ATTRIBUTE_NEXT
     */
    public String getNext() {
        return getAttribute(ATTRIBUTE_NEXT);
    }

    /**
     * Set the next attribute.
     * @param next Value of the next attribute.
     * @see #ATTRIBUTE_NEXT
     */
    public void setNext(final String next) {
        setAttribute(ATTRIBUTE_NEXT, next);
    }

    /**
     * Retrieve the timeout attribute.
     * @return Value of the timeout attribute.
     * @see #ATTRIBUTE_TIMEOUT
     */
    public String getTimeout() {
        return getAttribute(ATTRIBUTE_TIMEOUT);
    }

    /**
     * Set the timeout attribute.
     * @param timeout Value of the timeout attribute.
     * @see #ATTRIBUTE_TIMEOUT
     */
    public void setTimeout(final String timeout) {
        setAttribute(ATTRIBUTE_TIMEOUT, timeout);
    }

    /**
     * Retrieve the type attribute.
     * @return Value of the type attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public String getType() {
        return getAttribute(ATTRIBUTE_TYPE);
    }

    /**
     * Set the type attribute.
     * @param type Value of the type attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public void setType(final String type) {
        setAttribute(ATTRIBUTE_TYPE, type);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected boolean canContainChild(final String tagName) {
        return CHILD_TAGS.contains(tagName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
