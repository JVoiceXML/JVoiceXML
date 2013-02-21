/*
 * File:    $RCSfile: Script.java,v $
 * Version: $Revision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * <code>&lt;script&gt;</code> encloses computations written in the ECMAScript
 * Compact Profile scripting language. The ECMAScript Compact Profile is a
 * strict subset of the third edition of ECMA-262. It has been designed to meet
 * the needs of resource-constrained environments. Special attention has been
 * paid to constraining ECMAScript features that require proportionately large
 * amounts of system memory, and continuous or proportionately large amounts of
 * processing power. In particular, it is designed to facilitate prior
 * compilation for execution in a lightweight environment.
 *
 * @author Steve Doyle
 * @version $Revision: 2325 $
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Script
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "script";

    /**
     * A character string that indicates the character encoding type of the
     * script. UTF-8 and UTF-16 encodings of ISO/IEC 10646 must be supported (as
     * in [XML] ) and other encodings, as defined in the [IANA] , may be
     * supported.
     */
    public static final String ATTRIBUTE_CHARSET = "charset";

    /**
     * An ECMAScript expression which returns the fetch identifier of a
     * completed fetch request, acquired either in a fetch with the fetchid
     * attribute, or from the fetchid attribute of a fetch.done event. If the
     * fetch identifier is invalid, has not completed, or the fetched content is
     * not valid ECMAScript, an error.semantic event is thrown.
     */
    public static final String ATTRIBUTE_FETCHID = "fetchid";

    /**
     * The character string is interpreted as a time interval. This indicates
     * that the document is willing to use content whose age is no greater than
     * the specified time in seconds (cf. 'max-age' in HTTP 1.1 [RFC2616]). The
     * document is not willing to use stale content, unless maxstale is also
     * provided.
     */
    public static final String ATTRIBUTE_MAXAGE = "maxage";

    /**
     * The character string is interpreted as a time interval. This indicates
     * that the document is willing to use content that has exceeded its
     * expiration time (cf. 'max-age' in HTTP 1.1 [RFC2616]). If maxstale is
     * assigned a value, then the document is willing to accept content that has
     * exceeded its expiration time by no more than the specified number of
     * seconds.
     */
    public static final String ATTRIBUTE_MAXSTALE = "maxstale";

    /**
     * A URI which references a resource which is the script content, and which
     * will be resolved when the CCXML document is compiled. If both the src and
     * fetchid are not present, the script element content provides the script
     * content. If both are present the implementation must throw an error.fetch
     * event. Note that the value of the src attribute is not an ECMAScript
     * expression in order to allow it to be resolved at compile-time. If the
     * script cannot be fetched the implementation must throw an error.fetch
     * event.
     */
    public static final String ATTRIBUTE_SRC = "src";

    /**
     * The character string is interpreted as a time interval. This interval
     * begins when the script is requested; If the script has not been fetched
     * at the end of this interval, an error.fetch event occurs.
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

        ATTRIBUTE_NAMES.add(ATTRIBUTE_CHARSET);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXAGE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXSTALE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SRC);
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
     * Construct a new script object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Script() {
        super(null);
    }

    /**
     * Construct a new script object.
     * @param node The encapsulated node.
     */
    Script(final Node node) {
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
    private Script(final Node n,
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
        return new Script(n, factory);
    }

    /**
     * Retrieve the charset attribute.
     * @return Value of the charset attribute.
     * @see #ATTRIBUTE_CHARSET
     */
    public String getCharset() {
        return getAttribute(ATTRIBUTE_CHARSET);
    }

    /**
     * Set the charset attribute.
     * @param charset Value of the charset attribute.
     * @see #ATTRIBUTE_CHARSET
     */
    public void setCharset(final String charset) {
        setAttribute(ATTRIBUTE_CHARSET, charset);
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
     * Retrieve the src attribute.
     * @return Value of the src attribute.
     * @see #ATTRIBUTE_SRC
     */
    public String getSrc() {
        return getAttribute(ATTRIBUTE_SRC);
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
     * Create a new text within this node.
     * @param text The text to be added.
     * @return The new created text.
     */
    public Text addText(final String text) {
        final Document document = getOwnerDocument();
        final Node node = document.createTextNode(text);
        final Text textNode = new Text(node, getNodeFactory());
        appendChild(textNode);
        return textNode;
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
