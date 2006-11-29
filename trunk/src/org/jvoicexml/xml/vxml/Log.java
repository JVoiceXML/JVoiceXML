/*
 * File:    $RCSfile: Log.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Generate a debug message.
 * <p>
 * The <code>&lt;log&gt;</code> element allows an application to generate a
 * logging or debug message which a developer can use to help in application
 * development or post-execution analysis of application performance.
 * </p>
 * <p>
 * The <code>&lt;log&gt;</code> element may contain any combination of text
 * (CDATA) and <code>&lt;value&gt;</code> elements. The generated message
 * consists of the concatenation of the text and the string form of the value of
 * the "expr" attribute of the <code>&lt;value&gt;</code> elements.
 * </p>
 * <p>
 * <code>
 * &lt;log&gt;The card number was <br>
 * &lt;value expr="card_num"/&gt; <br>
 * &lt;/log&gt;
 * </code>
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Value
 *
 * @author Steve Doyle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Log
        extends AbstractVoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "log";

    /**
     * An optional string which may be used, for example, to indicate the
     * purpose of the log.
     */
    public static final String ATTRIBUTE_LABEL = "label";

    /**
     * An optional ECMAScript expression evaluating to a string.
     */
    public static final String ATTRIBUTE_EXPR = "expr";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_EXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_LABEL);
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

        CHILD_TAGS.add(Value.TAG_NAME);
    }

    /**
     * Construct a new log object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Log() {
        super(null);
    }

    /**
     * Construct a new log object.
     * @param node The encapsulated node.
     */
    Log(final Node node) {
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
    public VoiceXmlNode newInstance(final Node n) {
        return new Log(n);
    }

    /**
     * Retrieve the label attribute.
     * @return Value of the label attribute.
     * @see #ATTRIBUTE_LABEL
     */
    public String getLabel() {
        return getAttribute(ATTRIBUTE_LABEL);
    }

    /**
     * Set the label attribute.
     * @param label Value of the label attribute.
     * @see #ATTRIBUTE_LABEL
     */
    public void setLabel(final String label) {
        setAttribute(ATTRIBUTE_LABEL, label);
    }

    /**
     * Retrieve the expr attribute.
     * @return Value of the expr attribute.
     * @see #ATTRIBUTE_EXPR
     */
    public String getExpr() {
        return getAttribute(ATTRIBUTE_EXPR);
    }

    /**
     * Set the expr attribute.
     * @param expr Value of the expr attribute.
     * @see #ATTRIBUTE_EXPR
     */
    public void setExpr(final String expr) {
        setAttribute(ATTRIBUTE_EXPR, expr);
    }

    /**
     * Creates a new text node.
     * @param textValue The inbound text
     * @return The newly created text-node
     */
    public Text addText(final String textValue) {
        final Document document = getOwnerDocument();
        final Node node = document.createTextNode(textValue);
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
     * Returns a collection of permitted attribute names for the node.
     *
     * @return A collection of attribute names that are allowed for the node
     */
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
