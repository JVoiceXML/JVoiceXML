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
import java.util.Set;

import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.jvoicexml.xml.ssml.Audio;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * An action executed when fields are filled.
 *
 * <p>
 * The <code>&lt;filled&gt;</code> element specifies an action to perform when
 * some combination of input items are filled. It may occur in two places: as a
 * child of the <code>&lt;form&gt;</code> element, or as a child of an input
 * item.
 * </p>
 * <p>
 * As a child of a <code>&lt;form&gt;</code> element, the
 * <code>&lt;filled&gt;</code> element can be used to perform actions that
 * occur when a combination of one or more input items is filled.
 * </p>
 * <p>
 * If the <code>&lt;filled&gt;</code> element appears inside an input item, it
 * specifies an action to perform after that input item is filled in:
 * </p>
 * <p>
 * After each gathering of the user's input, all the input items mentioned in
 * the input are set, and then the interpreter looks at each
 * <code>&lt;filled&gt;</code> element in document order (no preference is
 * given to ones in input items vs. ones in the form). Those whose conditions
 * are matched by the utterance are then executed in order, until there are no
 * more, or until one transfers control or throws an event.
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Form
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
public final class Filled
        extends AbstractVoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "filled";

    /**
     * Either all (the default), or any. If any, this action is executed when
     * any of the specified input items is filled by the last user input. If
     * all, this action is executed when all of the mentioned input items are
     * filled, and at least one has been filled by the last user input. A
     * <code>&lt;filled&gt;</code> element in an input item cannot specify a
     * mode; if a mode is specified, then an error.badfetch is thrown by the
     * platform upon encountering the document.
     */
    public static final String ATTRIBUTE_MODE = "mode";

    /**
     * The input items to trigger on. For a <code>&lt;filled&gt;</code> in a
     * form, namelist defaults to the names (explicit and implicit) of the
     * form's input items. A <code>&lt;filled&gt;</code> element in an input
     * item cannot specify a namelist (the namelist in this case is the input
     * item name); if a namelist is specified, then an error.badfetch is thrown
     * by the platform upon encountering the document. Note that control items
     * are not permitted in this list; an error.badfetch is thrown when the
     * document contains a <code>&lt;filled&gt;</code> element with a namelist
     * attribute referencing a control item variable.
     */
    public static final String ATTRIBUTE_NAMELIST = "namelist";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAMELIST);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MODE);
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

        CHILD_TAGS.add(Assign.TAG_NAME);
        CHILD_TAGS.add(Prompt.TAG_NAME);
        CHILD_TAGS.add(Enumerate.TAG_NAME);
        CHILD_TAGS.add(Value.TAG_NAME);
        CHILD_TAGS.add(Audio.TAG_NAME);
        CHILD_TAGS.add(Clear.TAG_NAME);
        CHILD_TAGS.add(Data.TAG_NAME);
        CHILD_TAGS.add(Disconnect.TAG_NAME);
        CHILD_TAGS.add(Exit.TAG_NAME);
        CHILD_TAGS.add(Foreach.TAG_NAME);
        CHILD_TAGS.add(Goto.TAG_NAME);
        CHILD_TAGS.add(If.TAG_NAME);
        CHILD_TAGS.add(Log.TAG_NAME);
        CHILD_TAGS.add(Reprompt.TAG_NAME);
        CHILD_TAGS.add(Return.TAG_NAME);
        CHILD_TAGS.add(Script.TAG_NAME);
        CHILD_TAGS.add(Submit.TAG_NAME);
        CHILD_TAGS.add(Throw.TAG_NAME);
        CHILD_TAGS.add(Var.TAG_NAME);
    }

    /**
     * Construct a new filled object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Filled() {
        super(null);
    }

    /**
     * Construct a new filled object.
     * @param node The encapsulated node.
     */
    Filled(final Node node) {
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
    private Filled(final Node n,
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
        return new Filled(n, factory);
    }

    /**
     * Retrieve the mode attribute.
     * @return Value of the mode attribute.
     * @see #ATTRIBUTE_MODE
     */
    public String getMode() {
        final String mode = getAttribute(ATTRIBUTE_MODE);
        if (mode != null) {
            return mode;
        }
        return new String("all");
    }

    /**
     * Set the mode attribute.
     * @param mode Value of the mode attribute.
     * @see #ATTRIBUTE_MODE
     */
    public void setMode(final String mode) {
        setAttribute(ATTRIBUTE_MODE, mode);
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
