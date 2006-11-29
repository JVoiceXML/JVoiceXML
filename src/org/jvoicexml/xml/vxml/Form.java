/*
 * File:    $RCSfile: Form.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 Dirk Schnelle (dirk.schnelle@web.de)
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

import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.srgs.Grammar;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Forms are the key concept of VoiceXML documents. A form contains
 * <ul>
 * <li>
 * A set of <em>form items</em>, elements that are visited in the main
 * loop of the form interpretation algorithm. Form items are dubdevided into
 * <em>input items</em> thatcan be 'filled' by user input and
 * <em>control items</em> that cannot.
 * </li>
 * <li>Declaration of non-form item variables.</li>
 * <li>Event handlers.</li>
 * <li>
 * "Filled" actions, block of procedural logic that execute when certain
 * combinations of input item variables are assigned.
 * </li>
 * </ul>
 *
 * @see org.jvoicexml.xml.vxml.VoiceXmlDocument
 *
 * @author Dirk Schnelle
 * @author Steve Doyle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Form
        extends AbstractVoiceXmlNode {
    /** Name of the form tag. */
    public static final String TAG_NAME = "form";

    /**
     * Name of the id attribute. The id is the name of the form. If specified,
     * the form can be referenced within the document or from another
     * document. For instance
     * <code>&lt;form id="weather"&gt;, &lt;goto next="#weather"&gt;</code>.
     */
    public static final String ATTRIBUTE_ID = "id";

    /**
     * Name of the scope attribute. The default scope of the form's
     * grammars are active only in the form. If the scope is document, then
     * the form grammars are active during any dialog in the same document.
     * If the scope is document and the document is an application root
     * document then the form grammars are active during any dialog in any
     * document of this application. Note that the scope of individual form
     * grammars takes precedence over the default scope; for example, in
     * non-root documents a form with the default scope <em>dialog</em>, and a
     * form grammar with the scope <em>document</em>, then that grammar is
     * active in any dialog in the document.
     */
    public static final String ATTRIBUTE_SCOPE = "scope";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

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

        CHILD_TAGS.add(Block.TAG_NAME);
        CHILD_TAGS.add(Grammar.TAG_NAME);
        CHILD_TAGS.add(Var.TAG_NAME);
        CHILD_TAGS.add(Field.TAG_NAME);
        CHILD_TAGS.add(Catch.TAG_NAME);
        CHILD_TAGS.add(Help.TAG_NAME);
        CHILD_TAGS.add(Noinput.TAG_NAME);
        CHILD_TAGS.add(Nomatch.TAG_NAME);
        CHILD_TAGS.add(Error.TAG_NAME);
        CHILD_TAGS.add(Filled.TAG_NAME);
        CHILD_TAGS.add(Initial.TAG_NAME);
        CHILD_TAGS.add(ObjectTag.TAG_NAME);
        CHILD_TAGS.add(Link.TAG_NAME);
        CHILD_TAGS.add(Property.TAG_NAME);
        CHILD_TAGS.add(Record.TAG_NAME);
        CHILD_TAGS.add(Script.TAG_NAME);
        CHILD_TAGS.add(Subdialog.TAG_NAME);
        CHILD_TAGS.add(Transfer.TAG_NAME);
    }

    /**
     * Construct a new form object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Form() {
        super(null);
    }

    /**
     * Construct a new form object.
     * @param node The encapsulated node.
     */
    Form(final Node node) {
        super(node);
    }

    /**
     * Create a new var.
     * @return The newly created var
     */
    public Var createVar() {
        final Document document = getOwnerDocument();
        final Node node = document.createElement(Var.TAG_NAME);

        return new Var(node);
    }

    /**
     * Create a new field.
     * @return The newly created field
     */
    public Field createField() {
        final Document document = getOwnerDocument();
        final Node node = document.createElement(Field.TAG_NAME);

        return new Field(node);
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
     * @param scope Value of the id attribute.
     * @see #ATTRIBUTE_SCOPE
     */
    public void setScope(final String scope) {
        setAttribute(ATTRIBUTE_SCOPE, scope);
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
        return new Form(n);
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
