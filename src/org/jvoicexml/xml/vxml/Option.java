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

import java.util.ArrayList;
import java.util.Collection;

import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Specify an option in a <code>&lt;field&gt;</code>.
 * <p>
 * When a simple set of alternatives is all that is needed to specify the legal
 * input values for a field, it may be more convenient to use an option list
 * than a grammar. An option list is represented by a set of
 * <code>&lt;option&gt;</code> elements contained in a
 * <code>&lt;field&gt;</code> element. Each <code>&lt;option&gt;</code>
 * element contains PCDATA that is used to generate a speech grammar.
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Field
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
public final class Option
        extends AbstractVoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "option";

    /**
     * An optional DTMF sequence for this option. It is equivalent to a simple
     * DTMF <code>&lt;grammar&gt;</code> and DTMF properties apply to
     * recognition of the sequence. Unlike DTMF grammars, whitespace is
     * optional: dtmf="123#" is equivalent to dtmf="1 2 3 #". If unspecified, no
     * DTMF sequence is associated with this option so it cannot be matched
     * using DTMF.
     */
    public static final String ATTRIBUTE_DTMF = "dtmf";

    /**
     * When set to "exact" (the default), the text of the option element defines
     * the exact phrase to be recognized. When set to "approximate", the text of
     * the option element defines an approximate recognition phrase.
     */
    public static final String ATTRIBUTE_ACCEPT = "accept";

    /**
     * The string to assign to the field's form item variable when a user
     * selects this option, whether by speech or DTMF. The default assignment is
     * the CDATA content of the <code>&lt;option&gt;</code> element with
     * leading and trailing white space removed. If this does not exist, then
     * the DTMF sequence is used instead. If neither CDATA content nor a dtmf
     * sequence is specified, then the default assignment is undefined and the
     * field's form item variable is not filled.
     */
    public static final String ATTRIBUTE_VALUE = "value";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_ACCEPT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_DTMF);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_VALUE);
    }

    /**
     * Construct a new option object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Option() {
        super(null);
    }

    /**
     * Construct a new option object.
     * @param node The encapsulated node.
     */
    Option(final Node node) {
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
    private Option(final Node n,
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
        return new Option(n, factory);
    }

    /**
     * Retrieve the dtmf attribute.
     * @return Value of the dtmf attribute.
     * @see #ATTRIBUTE_DTMF
     */
    public String getDtmf() {
        return getAttribute(ATTRIBUTE_DTMF);
    }

    /**
     * Set the dtmf attribute.
     * @param dtmf Value of the dtmf attribute.
     * @see #ATTRIBUTE_DTMF
     */
    public void setDtmf(final String dtmf) {
        setAttribute(ATTRIBUTE_DTMF, dtmf);
    }

    /**
     * Retrieve the accept attribute.
     * @return Value of the accept attribute.
     * @see #ATTRIBUTE_ACCEPT
     */
    public String getAccept() {
        final String accept = getAttribute(ATTRIBUTE_ACCEPT);
        if (accept != null) {
            return accept;
        }
        return new String("exact");
    }

    /**
     * Set the accept attribute.
     * @param accept Value of the accept attribute.
     * @see #ATTRIBUTE_ACCEPT
     */
    public void setAccept(final String accept) {
        setAttribute(ATTRIBUTE_ACCEPT, accept);
    }

    /**
     * Retrieve the value attribute.
     * @return Value of the value attribute.
     * @see #ATTRIBUTE_VALUE
     */
    public String getValue() {
        return getAttribute(ATTRIBUTE_VALUE);
    }

    /**
     * Set the value attribute.
     * @param value Value of the value attribute.
     * @see #ATTRIBUTE_VALUE
     */
    public void setValue(final String value) {
        setAttribute(ATTRIBUTE_VALUE, value);
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
