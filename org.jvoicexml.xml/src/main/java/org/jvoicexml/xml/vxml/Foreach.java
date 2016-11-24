/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.xml.NodeHelper;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.TextContainer;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.ssml.Break;
import org.jvoicexml.xml.ssml.Emphasis;
import org.jvoicexml.xml.ssml.Mark;
import org.jvoicexml.xml.ssml.Phoneme;
import org.jvoicexml.xml.ssml.Prosody;
import org.jvoicexml.xml.ssml.SayAs;
import org.jvoicexml.xml.ssml.Sub;
import org.jvoicexml.xml.ssml.Voice;
import org.w3c.dom.Node;

/**
 * A foreach node.
 * @author Steve Doyle
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class Foreach
        extends AbstractVoiceXmlNode
        implements TextContainer {

    /** Name of the tag. */
    public static final String TAG_NAME = "foreach";

    /**
     * The variable that stores each array item upon each iteration of the loop.
     * A new variable will be declared if it is not already defined within the
     * parent's scope.
     */
    public static final String ATTRIBUTE_ITEM = "item";

    /**
     * An ECMAScript expression that must evaluate to an array; otherwise, an
     * error.semantic event is thrown.
     */
    public static final String ATTRIBUTE_ARRAY = "array";

    /**
     * Valid child tags for this node.
     */
    private static final Set<String> CHILD_TAGS;

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_ARRAY);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_ITEM);
    }

    /**
     * Set the valid child tags for this node.
     */
    static {
        CHILD_TAGS = new java.util.HashSet<String>();

        CHILD_TAGS.add(Audio.TAG_NAME);
        CHILD_TAGS.add(Enumerate.TAG_NAME);
        CHILD_TAGS.add(Value.TAG_NAME);
        CHILD_TAGS.add(Assign.TAG_NAME);
        CHILD_TAGS.add(Clear.TAG_NAME);
        CHILD_TAGS.add(Data.TAG_NAME);
        CHILD_TAGS.add(Disconnect.TAG_NAME);
        CHILD_TAGS.add(Exit.TAG_NAME);
        CHILD_TAGS.add(Foreach.TAG_NAME);
        CHILD_TAGS.add(Goto.TAG_NAME);
        CHILD_TAGS.add(If.TAG_NAME);
        CHILD_TAGS.add(Log.TAG_NAME);
        CHILD_TAGS.add(Prompt.TAG_NAME);
        CHILD_TAGS.add(Reprompt.TAG_NAME);
        CHILD_TAGS.add(Return.TAG_NAME);
        CHILD_TAGS.add(Script.TAG_NAME);
        CHILD_TAGS.add(Submit.TAG_NAME);
        CHILD_TAGS.add(Throw.TAG_NAME);
        CHILD_TAGS.add(Var.TAG_NAME);
        CHILD_TAGS.add(Break.TAG_NAME);
        CHILD_TAGS.add(Break.TAG_NAME);
        CHILD_TAGS.add(Emphasis.TAG_NAME);
        CHILD_TAGS.add(Mark.TAG_NAME);
        CHILD_TAGS.add(Phoneme.TAG_NAME);
        CHILD_TAGS.add(Prosody.TAG_NAME);
        CHILD_TAGS.add(SayAs.TAG_NAME);
        CHILD_TAGS.add(Voice.TAG_NAME);
        CHILD_TAGS.add(Sub.TAG_NAME);
        CHILD_TAGS.add(Metadata.TAG_NAME);
    }

    /**
     * Construct a new foreach object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Foreach() {
        super(null);
    }

    /**
     * Construct a new foreach object.
     * @param node The encapsulated node.
     */
    Foreach(final Node node) {
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
    private Foreach(final Node n,
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
        return new Foreach(n, factory);
    }

    /**
     * Retrieve the item attribute.
     * @return Value of the item attribute.
     * @see #ATTRIBUTE_ITEM
     */
    public String getItem() {
        return getAttribute(ATTRIBUTE_ITEM);
    }

    /**
     * Set the item attribute.
     * @param item Value of the item attribute.
     * @see #ATTRIBUTE_ITEM
     */
    public void setItem(final String item) {
        setAttribute(ATTRIBUTE_ITEM, item);
    }

    /**
     * Retrieve the array attribute.
     * @return Value of the array attribute.
     * @see #ATTRIBUTE_ARRAY
     */
    public String getArray() {
        return getAttribute(ATTRIBUTE_ARRAY);
    }

    /**
     * Set the array attribute.
     * @param array Value of the array attribute.
     * @see #ATTRIBUTE_ARRAY
     */
    public void setArray(final String array) {
        setAttribute(ATTRIBUTE_ARRAY, array);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canContainChild(final String tagName) {
        return CHILD_TAGS.contains(tagName);
    }

    /**
     * Returns a collection of permitted attribute names for the node.
     *
     * @return A collection of attribute names that are allowed for the node
     */
    @Override
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }

    /**
     * Creates a new text within this prompt. If the last child node already is
     * a text node the given trimmed text is appended to that node.
     * @param text The text to be added.
     * @return The new created text.
     */
    public Text addText(final String text) {
        return NodeHelper.addText(this, text);
    }
}
