/*
 * Copyright (C) 2005-2007 JVoiceXML group
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
import java.util.Set;

import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.ssml.Audio;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A dialog for choosing amongst alternative destinations.
 * <p>
 * This identifies the menu, and determines the scope of its grammars.
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Goto
 * @see org.jvoicexml.xml.vxml.Submit
 * @see org.jvoicexml.xml.vxml.Choice
 *
 * @author Steve Doyle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class Menu
        extends AbstractVoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "menu";

    /**
     * The identifier of the menu. It allows the menu to be the target of a
     * <code>&lt;goto&gt;</code> or a <code>&lt;submit&gt;</code>.
     */
    public static final String ATTRIBUTE_ID = "id";

    /**
     * The menu's grammar scope. If it is dialog (the default), the menu's
     * grammars are only active when the user transitions into the menu. If
     * the scope is document, its grammars are active over the whole document
     * (or if the menu is in the application root document, any loaded document
     * in the application).
     */
    public static final String ATTRIBUTE_SCOPE = "scope";

    /**
     * When set to true, the first nine choices that have not explicitly
     * specified a value for the dtmf attribute are given the implicit ones "1",
     * "2", etc. Remaining choices that have not explicitly specified a value
     * for the dtmf attribute will not be assigned DTMF values (and thus cannot
     * be matched via a DTMF keypress). If there are choices which have
     * specified their own DTMF sequences to be something other than "*", "#",
     * or "0", an error.badfetch will be thrown. The default is false.
     */
    public static final String ATTRIBUTE_DTMF = "dtmf";

    /**
     * When set to "exact" (the default), the text of the choice elements in the
     * menu defines the exact phrase to be recognized. When set to
     * "approximate", the text of the choice elements defines an approximate
     * recognition phrase. Each <code>&lt;choice&gt;</code> can override this
     * setting.
     */
    public static final String ATTRIBUTE_ACCEPT = "accept";

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
        ATTRIBUTE_NAMES.add(ATTRIBUTE_ID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SCOPE);
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

        CHILD_TAGS.add(Prompt.TAG_NAME);
        CHILD_TAGS.add(Enumerate.TAG_NAME);
        CHILD_TAGS.add(Value.TAG_NAME);
        CHILD_TAGS.add(Audio.TAG_NAME);
        CHILD_TAGS.add(Choice.TAG_NAME);
        CHILD_TAGS.add(Catch.TAG_NAME);
        CHILD_TAGS.add(Help.TAG_NAME);
        CHILD_TAGS.add(Noinput.TAG_NAME);
        CHILD_TAGS.add(Nomatch.TAG_NAME);
        CHILD_TAGS.add(Error.TAG_NAME);
        CHILD_TAGS.add(Property.TAG_NAME);
    }

    /**
     * Construct a new menu object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Menu() {
        super(null);
    }

    /**
     * Construct a new menu object.
     * @param node The encapsulated node.
     */
    Menu(final Node node) {
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
        return new Menu(n);
    }

    /**
     * Retrieve the id attribute.
     * @return Value of the id attribute.
     * @see #ATTRIBUTE_ID
     */
    public String getId() {
        return getAttribute(ATTRIBUTE_ID);
    }

    /**
     * Set the id attribute.
     * @param id Value of the id attribute.
     * @see #ATTRIBUTE_ID
     */
    public void setId(final String id) {
        setAttribute(ATTRIBUTE_ID, id);
    }

    /**
     * Retrieve the scope attribute.
     * @return Value of the scope attribute.
     * @see #ATTRIBUTE_SCOPE
     */
    public String getScope() {
        return getAttribute(ATTRIBUTE_SCOPE);
    }

    /**
     * Set the scope attribute.
     * @param scope Value of the scope attribute.
     * @see #ATTRIBUTE_SCOPE
     */
    public void setScope(final String scope) {
        setAttribute(ATTRIBUTE_SCOPE, scope);
    }

    /**
     * Retrieve the dtmf attribute.
     * @return Value of the dtmf attribute.
     * @see #ATTRIBUTE_DTMF
     */
    public String getDtmf() {
        final String dtmf = getAttribute(ATTRIBUTE_DTMF);
        if (dtmf != null) {
            return dtmf;
        }
        return Boolean.toString(false);
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
