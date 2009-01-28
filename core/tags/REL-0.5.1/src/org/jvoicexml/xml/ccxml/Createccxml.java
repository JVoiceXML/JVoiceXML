/*
 * File:    $RCSfile: Createccxml.java,v $
 * Version: $Revision: 1.5 $
 * Date:    $Date: 2006/05/16 07:26:21 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 *
 */

package org.jvoicexml.xml.ccxml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.XmlNode;
import org.w3c.dom.Node;

/**
 * <code>&lt;createccxml&gt;</code> is used to create another CCXML session,
 * which begins execution with the document identified by this element. The term
 * "session" is not meant to imply a particular form of implementation. A CCXML
 * session exists for each concurrently executing CCXML document. A session
 * provides independent execution and a separate variable space for the CCXML
 * documents it executes. A session is associated with one or more event sources
 * and will receive events only from those endpoints. The execution of a CCXML
 * document MAY add or subtract event sources from a session. The new CCXML
 * session has no relation to its creator once spawned, and has a wholly
 * separate lifetime and address space.
 *
 * Execution returns from the <code>&lt;createccxml&gt;</code> element
 * immediately, and the CCXML interpreter can continue on while the new CCXML
 * session is established and loads its initial document. If the new session is
 * successfully established or a failure occurs an event is generated and is
 * delivered to the session that executed the <code>&lt;createccxml&gt;</code>
 * element.
 *
 *
 * @author Steve Doyle
 * @version $Revision: 1.5 $
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Createccxml
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "createccxml";

    /**
     * An ECMAScript expression which returns a character string that indicates
     * the media encoding type of the submitted document (when the value of the
     * method is "post").
     */
    public static final String ATTRIBUTE_ENCTYPE = "enctype";

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
     * The character string returned is interpreted as a time interval. This
     * indicates that the document is willing to use content that has exceeded
     * its expiration time (cf. 'max-age' in HTTP 1.1 [RFC2616]). If maxstale is
     * assigned a value, then the document is willing to accept content that has
     * exceeded its expiration time by no more than the specified number of
     * seconds.
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
     * An ECMAScript left hand side expression evaluating to a previously
     * defined variable. The value of the attribute will receive an internally
     * generated unique string identifier which identifies the newly created
     * session.
     */
    public static final String ATTRIBUTE_SESSIONID = "sessionid";

    /**
     * An ECMAScript left hand side expression evaluating to a previously
     * defined variable. The value of the attribute will receive an internally
     * generated unique string identifier which identifies the newly created
     * session.
     */
    public static final String ATTRIBUTE_TIMEOUT = "timeout";

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
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXAGE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXSTALE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_METHOD);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAMELIST);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NEXT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SESSIONID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TIMEOUT);
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
     * Construct a new createccxml object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Createccxml() {
        super(null);
    }

    /**
     * Construct a new createccxml object.
     * @param node The encapsulated node.
     */
    Createccxml(final Node node) {
        super(node);
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
     * Create a new instance for the given node.
     * @param n The node to encapsulate.
     * @return The new instance.
     */
    public XmlNode newInstance(final Node n) {
        return new Createccxml(n);
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
     * Retrieve the sessionid attribute.
     * @return Value of the sessionid attribute.
     * @see #ATTRIBUTE_SESSIONID
     */
    public String getSessionid() {
        return getAttribute(ATTRIBUTE_SESSIONID);
    }

    /**
     * Set the sessionid attribute.
     * @param sessionid Value of the sessionid attribute.
     * @see #ATTRIBUTE_SESSIONID
     */
    public void setSessionid(final String sessionid) {
        setAttribute(ATTRIBUTE_SESSIONID, sessionid);
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
     * {@inheritDoc}
     */
    protected boolean canContainChild(final String tagName) {
        return CHILD_TAGS.contains(tagName);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
