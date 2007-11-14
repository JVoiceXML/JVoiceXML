/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
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
import java.util.Set;

import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.XmlCDataSection;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Specify a block of ECMAScript client-side scripting logic.
 * <p>
 * A <code>&lt;script&gt;</code> element may occur in the
 * <code>&lt;vxml&gt;</code> and <code>&lt;form&gt;</code> elements, or in
 * executable content (in <code>&lt;filled&gt;</code>,
 * <code>&lt;if&gt;</code>,<code>&lt;block&gt;</code>,
 * <code>&lt;catch&gt;</code>, or the short forms of
 * <code>&lt;catch&gt;</code>). Scripts in the <code>&lt;vxml&gt;</code>
 * element are evaluated just after the document is loaded, along with the
 * <code>&lt;var&gt;</code> elements, in document order. Scripts in the
 * <code>&lt;form&gt;</code> element are evaluated in document order, along
 * with <code>&lt;var&gt;</code> elements and form item variables, each time
 * execution moves into the <code>&lt;form&gt;</code> element. A
 * <code>&lt;script&gt;</code> element in executable content is executed, like
 * other executable elements, as it is encountered.
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Form
 * @see org.jvoicexml.xml.vxml.Filled
 * @see org.jvoicexml.xml.vxml.Vxml
 * @see org.jvoicexml.xml.vxml.If
 * @see org.jvoicexml.xml.vxml.Block
 * @see org.jvoicexml.xml.vxml.Catch
 * @see org.jvoicexml.xml.vxml.Var
 *
 * @author Steve Doyle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Script
        extends AbstractVoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "script";

    /**
     * The URI specifying the location of the script, if it is external.
     */
    public static final String ATTRIBUTE_SRC = "src";

    /**
     * The character encoding of the script designated by src. UTF-8 and UTF-16
     * encodings of ISO/IEC 10646 must be supported (as in XML) and other
     * encodings may be supported. The default
     * value is UTF-8.
     */
    public static final String ATTRIBUTE_CHARSET = "charset";

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
     * Equivalent to src, except that the URI is dynamically determined by
     * evaluating the given ECMAScript expression. The expression must be
     * evaluated each time the script needs to be executed. If srcexpr cannot be
     * evaluated, an error.semantic event is thrown.
     */
    public static final String ATTRIBUTE_SRCEXPR = "srcexpr";

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
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHHINT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHTIMEOUT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXAGE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXSTALE);
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

        CHILD_TAGS.add(XmlCDataSection.TAG_NAME);
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
     * @param uri Value of the src attribute.
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
     * Retrieve the fetchhint attribute.
     * @return Value of the fetchhint attribute.
     * @see #ATTRIBUTE_FETCHHINT
     */
    public String getFetchhint() {
        return getAttribute(ATTRIBUTE_FETCHHINT);
    }

    /**
     * Set the fetchhint attribute.
     * @param fetchhint Value of the fetchhint attribute.
     * @see #ATTRIBUTE_FETCHHINT
     */
    public void setFetchhint(final String fetchhint) {
        setAttribute(ATTRIBUTE_FETCHHINT, fetchhint);
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
     * Retrieve the srcexpr attribute.
     * @return Value of the maxssrcexprtale attribute.
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
     * Create a new text within this node.
     * @param data The data to be added.
     * @return The new created CDATA section.
     * @since 0.6
     */
    public XmlCDataSection addCdata(final String data) {
        final Document document = getOwnerDocument();
        final Node node = document.createCDATASection(data);
        final XmlCDataSection dataNode =
            new XmlCDataSection(node, getNodeFactory());
        appendChild(dataNode);
        return dataNode;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean canContainChild(final String tagName) {
        return CHILD_TAGS.contains(tagName);
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
