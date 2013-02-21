/*
 * File:    $RCSfile: Enumerate.java,v $
 * Version: $Revision: 1.6 $
 * Date:    $Date: 2006/05/16 07:26:22 $
 * Author:  $Author: schnelle $
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

import java.util.Set;

import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
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
 * @version $Revision: 1.6 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Enumerate
        extends AbstractVoiceXmlNode {

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
        return new Enumerate(n);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean canContainChild(final String tagName) {
        return CHILD_TAGS.contains(tagName);
    }
}
