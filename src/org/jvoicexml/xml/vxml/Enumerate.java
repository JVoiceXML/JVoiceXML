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

import java.util.Set;

import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.ssml.Break;
import org.jvoicexml.xml.ssml.Emphasis;
import org.jvoicexml.xml.ssml.Mark;
import org.jvoicexml.xml.ssml.P;
import org.jvoicexml.xml.ssml.Phoneme;
import org.jvoicexml.xml.ssml.Prosody;
import org.jvoicexml.xml.ssml.S;
import org.jvoicexml.xml.ssml.SayAs;
import org.jvoicexml.xml.ssml.Sub;
import org.jvoicexml.xml.ssml.Voice;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Shorthand for enumerating the choices in a menu.
 * <p>
 * The <code>&lt;enumerate&gt;</code> element is an automatically generated
 * description of the choices available to the user. It specifies a template
 * that is applied to each choice in the order they appear in the menu. If it is
 * used with no content, a default template that lists all the choices is used,
 * determined by the interpreter context. If it has content, the content is the
 * template specifier. This specifier may refer to two special variables:
 * _prompt is the choice's prompt, and _dtmf is a normalized representation
 * (i.e. a single whitespace between DTMF tokens) of the choice's assigned DTMF
 * sequence (note that if no DTMF sequence is assigned to the choice element, or
 * if a <code>&lt;grammar&gt;</code> element is specified in
 * <code>&lt;choice&gt;</code>, then the _dtmf variable is assigned the
 * ECMAScript undefined value ).
 * </p>
 * <p>
 * The <code>&lt;enumerate&gt;</code> element may be used within the prompts
 * and the catch elements associated with <code>&lt;menu&gt;</code> elements
 * and with <code>&lt;field&gt;</code> elements that contain
 * <code>&lt;option&gt;</code> elements.
 * </p>
 *
 * @see org.jvoicexml.xml.srgs.Grammar
 * @see org.jvoicexml.xml.vxml.Choice
 * @see org.jvoicexml.xml.vxml.Menu
 * @see org.jvoicexml.xml.vxml.Field
 * @see org.jvoicexml.xml.vxml.Option
 *
 * @author Steve Doyle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Enumerate
        extends AbstractVoiceXmlNode {

    /** Name of the varaible that holds the DTMF value. */
    public static final String DTMF_VARIABLE = "_dtmf";

    /** Name of the varaible that holds the prompt value. */
    public static final String PROMPT_VARIABLE = "_prompt";

    /** Name of the tag. */
    public static final String TAG_NAME = "enumerate";

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
        CHILD_TAGS.add(Break.TAG_NAME);
        CHILD_TAGS.add(Emphasis.TAG_NAME);
        CHILD_TAGS.add(Mark.TAG_NAME);
        CHILD_TAGS.add(Phoneme.TAG_NAME);
        CHILD_TAGS.add(Prosody.TAG_NAME);
        CHILD_TAGS.add(SayAs.TAG_NAME);
        CHILD_TAGS.add(Voice.TAG_NAME);
        CHILD_TAGS.add(Sub.TAG_NAME);
        CHILD_TAGS.add(P.TAG_NAME);
        CHILD_TAGS.add(S.TAG_NAME);
    }

    /**
     * Construct a new enumerate object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Enumerate() {
        super(null);
    }

    /**
     * Construct a new enumerate object.
     * @param node The encapsulated node.
     */
    Enumerate(final Node node) {
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
    private Enumerate(final Node n,
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
        return new Enumerate(n, factory);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean canContainChild(final String tagName) {
        return CHILD_TAGS.contains(tagName);
    }

    /**
     * Create a new text within this node.
     * @param text The text to be added.
     * @return The new created text.
     * @since 0.6
     */
    public Text addText(final String text) {
        final Document document = getOwnerDocument();
        final Node node = document.createTextNode(text);
        final Text textNode = new Text(node, getNodeFactory());
        appendChild(textNode);
        return textNode;
    }

    /**
     * Adds a <code>_prompt</code> variable to this enumerat tag.
     * @since 0.6
     */
    public void addPromptVariable() {
        final Value value = appendChild(Value.class);
        value.setExpr(PROMPT_VARIABLE);
    }

    /**
     * Adds a <code>_dtmf</code> variable to this enumerat tag.
     * @since 0.6
     */
    public void addDtmfVariable() {
        final Value value = appendChild(Value.class);
        value.setExpr(DTMF_VARIABLE);
    }

    /**
     * Retrieves the <code>&lt;field&gt;</code> containing this node.
     * @return the parent field, <code>null</code> if there is none.
     * @since 0.6
     */
    public Field getField() {
        Node node = this;
        do {
            node = node.getParentNode();
            if (node instanceof Field) {
                return (Field) node;
            }
        } while (node != null);

        return null;
    }
}
