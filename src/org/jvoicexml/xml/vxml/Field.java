/*
 * File:    $RCSfile: Field.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group
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
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.ssml.Audio;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A <code>&lt;field&gt;</code> specifies an form input item to be gathered
 * from the user.
 *
 * @see org.jvoicexml.xml.vxml.Form
 *
 * @author Steve Doyle
 * @author Dirk Schnele
 *
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public class Field
        extends AbstractVoiceXmlNode {

    /** Name of the field tag. */
    public static final String TAG_NAME = "field";

    /**
     * The form item variable in the dialog scope that will hold the result.
     */
    public static final String ATTRIBUTE_NAME = "name";

    /**
     * The initial value of the form item variable; default is ECMAScript
     * undefined. If initialized to a value, then the form item will not be
     * visited unless the form item variable is cleared.
     */
    public static final String ATTRIBUTE_EXPR = "expr";

    /**
     * An expression that must evaluate to true after conversion to boolean
     * in order for the form item to be visited. The form item can also be
     * visited if the attribute is not specified.
     */
    public static final String ATTRIBUTE_COND = "cond";

    /**
     * The type of field, i.e., the name of a builtin grammar type. Platform
     * support for builtin grammar types is optional. If the specified builtin
     * type is not supported by the platform, an error.unsupported.builtin event
     * is thrown.
     */
    public static final String ATTRIBUTE_TYPE = "type";

    /**
     * The name of the grammar slot used to populate the variable (if it is
     * absent, it defaults to the variable name). This attribute is useful in
     * the case where the grammar format being used has a mechanism for
     * returning sets of slot/value pairs and the slot names differ from the
     * form item variable names.
     */
    public static final String ATTRIBUTE_SLOT = "slot";

    /**
     * If this is false (the default) all active grammars are turned on while
     * collecting this field. If this is true, then only the field's grammars
     * are enabled: all others are temporarily disabled.
     */
    public static final String ATTRIBUTE_MODAL = "modal";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_COND);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_EXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MODAL);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAME);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SLOT);
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

        CHILD_TAGS.add(Audio.TAG_NAME);
        CHILD_TAGS.add(Enumerate.TAG_NAME);
        CHILD_TAGS.add(Value.TAG_NAME);
        CHILD_TAGS.add(Catch.TAG_NAME);
        CHILD_TAGS.add(Help.TAG_NAME);
        CHILD_TAGS.add(Noinput.TAG_NAME);
        CHILD_TAGS.add(Nomatch.TAG_NAME);
        CHILD_TAGS.add(Error.TAG_NAME);
        CHILD_TAGS.add(Filled.TAG_NAME);
        CHILD_TAGS.add(Prompt.TAG_NAME);
        CHILD_TAGS.add(Grammar.TAG_NAME);
        CHILD_TAGS.add(Link.TAG_NAME);
        CHILD_TAGS.add(Option.TAG_NAME);
        CHILD_TAGS.add(Property.TAG_NAME);
    }

    /**
     * Construct a new field object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Field() {
        super(null);
    }

    /**
     * Construct a new field object.
     * @param node The encapsulated node.
     */
    protected Field(final Node node) {
        super(node);
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
     * Retrieve the cond attribute.
     *
     * @return Value of the cond attribute.
     * @see #ATTRIBUTE_COND
     */
    public String getCond() {
        return getAttribute(ATTRIBUTE_COND);
    }

    /**
     * Set the cond attribute.
     *
     * @param cond Value of the cond attribute.
     * @see #ATTRIBUTE_COND
     */
    public void setCond(final String cond) {
        setAttribute(ATTRIBUTE_COND, cond);
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
     *
     * @param type Value of the type attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public void setType(final String type) {
        setAttribute(ATTRIBUTE_TYPE, type);
    }

    /**
     * Retrieve the slot attribute.
     *
     * @return Value of the slot attribute.
     * @see #ATTRIBUTE_SLOT
     */
    public String getSlot() {
        return getAttribute(ATTRIBUTE_SLOT);
    }

    /**
     * Set the slot attribute.
     *
     * @param slot
     *            Value of the slot attribute.
     * @see #ATTRIBUTE_SLOT
     */
    public void setSlot(final String slot) {
        setAttribute(ATTRIBUTE_SLOT, slot);
    }

    /**
     * Retrieve the modal attribute.
     * @return Value of the modal attribute.
     * @see #ATTRIBUTE_MODAL
     */
    public String getModal() {
        final String modal = getAttribute(ATTRIBUTE_MODAL);
        if (modal != null) {
            return modal;
        }
        return Boolean.toString(false);
    }

    /**
     * Set the modal attribute.
     * @param modal Value of the modal attribute.
     * @see #ATTRIBUTE_MODAL
     */
    public void setModal(final String modal) {
        setAttribute(ATTRIBUTE_MODAL, modal);
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
        return new Field(n);
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
     * Returns a collection of permitted attribute names for the node.
     *
     * @return A collection of attribute names that are allowed for the node
     */
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
