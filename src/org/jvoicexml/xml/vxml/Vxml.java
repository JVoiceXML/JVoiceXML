/*
 * File:    $RCSfile: Vxml.java,v $
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
import java.util.Map;
import java.util.Set;

import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A VoiceXML document is primiarily composed of top-level elements called
 * <em>dialogs</em>. There are two types of dialogs: <em>forms</em> and
 * <em>menus</em>. A document may also have <code>&lt;meta&gt;</code> and
 * <code>&lt;metadata&gt;</code> elements, <code>&lt;var&gt;</code> and
 * <code>&lt;script&gt;</code> elements, <code>&lt;catch&gt;</code>
 * elements, <code>&lt;property&gt;</code> elements and
 * <code>&lt;link&gt;</code> elements.
 *
 * @see org.jvoicexml.xml.vxml.VoiceXmlDocument
 * @see org.jvoicexml.xml.vxml.Form
 *
 * @author Dirk Schnelle
 * @author Steve Doyle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public class Vxml
        extends AbstractVoiceXmlNode {
    /** Name of the vxml tag. */
    public static final String TAG_NAME = "vxml";

    /**
     * Default voice xml version number.
     * @see #ATTRIBUTE_VERSION
     */
    public static final String DEFAULT_VERSION = "2.0";

    /**
     * The version of VoiceXML of this document (required). The current
     * version number is <code>DEFAULT_VERSION</code>.
     * @see #DEFAULT_VERSION
     */
    public static final String ATTRIBUTE_VERSION = "version";

    /**
     * Default namespace.
     * @see #ATTRIBUTE_XMLNS
     */
    public static final String DEFAULT_XMLNS = "http://www.w3.org/2001/vxml";

    /**
     * The designated namespace for VoiceXXML (required). The namespace for
     * VoiceXML is defined to be <code>DEFAULT_XMLNS</code>
     */
    public static final String ATTRIBUTE_XMLNS = "xmlns";

    /**
     * The base URI for this document as defined in
     * <a href="http://www.w3.org/TR/2001/REC-xml-20001006"><em>XML Base</em>,
     * J. Marsh, editor, W3C Recommendation, June 2001</a>. As in
     * <a href="http://www.w3.org/TR/1999/REC-html401-19991224">
     * <em>HTML 4.01 Specification</em>, Dave Ragget et. al. W3C
     * Recommendation, December 1999</a>, a URI which all relative references
     * within the docment take as their base.
     */
    public static final String ATTRIBUTE_XML_BASE = "xml:base";

    /**
     * The language identifier for this document. If omitted the value is
     * a platform-specific default.
     */
    public static final String ATTRIBUTE_XML_LANG = "xml:lang";

    /**
     * The URI of this document's application root document, if any.
     */
    public static final String ATTRIBUTE_APPLICATION = "application";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_APPLICATION);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_VERSION);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_XML_BASE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_XML_LANG);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_XMLNS);
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

        CHILD_TAGS.add(Catch.TAG_NAME);
        CHILD_TAGS.add(Help.TAG_NAME);
        CHILD_TAGS.add(Noinput.TAG_NAME);
        CHILD_TAGS.add(Nomatch.TAG_NAME);
        CHILD_TAGS.add(Error.TAG_NAME);
        CHILD_TAGS.add(Link.TAG_NAME);
        CHILD_TAGS.add(Menu.TAG_NAME);
        CHILD_TAGS.add(Meta.TAG_NAME);
        CHILD_TAGS.add(Metadata.TAG_NAME);
        CHILD_TAGS.add(Property.TAG_NAME);
        CHILD_TAGS.add(Script.TAG_NAME);
        CHILD_TAGS.add(Var.TAG_NAME);
        CHILD_TAGS.add(Form.TAG_NAME);
    }

    /**
     * Construct a new vxml object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Vxml() {
        super(null);
    }

    /**
     * Construct a new vxml object.
     * @param node The encapsulated node.
     */
    public Vxml(final Node node) {
        super(node);

        // Set the default attributes.
        setAttribute(ATTRIBUTE_XMLNS, DEFAULT_XMLNS);
        setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        setAttribute("xsi:schematicLocation",
                     DEFAULT_XMLNS
                     + " http://www.w3.org/TR/voicexml20/vxml.xsd");

        setAttribute(ATTRIBUTE_VERSION, DEFAULT_VERSION);
    }

    /**
     * Get all forms in this document as a map with the name of the form
     * beeing key.
     * @return Map with all named forms.
     */
    public Map<String, Form> getFormMap() {
        final Map<String, Form> map =
                new java.util.HashMap<String, Form>();

        final Document document = new VoiceXmlDocument(getOwnerDocument());
        final NodeList list = document.getElementsByTagName(Form.TAG_NAME);
        for (int i = 0; i < list.getLength(); i++) {
            final Form form = (Form) list.item(i);
            final String id = form.getId();
            if (id != null) {
                map.put(id, form);
            }
        }

        return map;
    }

    /**
     * Get all var in this document as a map with the name of the var
     * being key.
     * @return Map with all named var.
     */
    public Map<String, Var> getVarMap() {
        final Map<String, Var> map =
                new java.util.HashMap<String, Var>();

        final Document document = new VoiceXmlDocument(getOwnerDocument());
        final NodeList list = document.getElementsByTagName(Var.TAG_NAME);
        for (int i = 0; i < list.getLength(); i++) {
            final Var var = (Var) list.item(i);
            final String name = var.getName();
            if (name != null) {
                map.put(name, var);
            }
        }

        return map;
    }

    /**
     * Retrieve the version attribute. The version attribute is read only.
     *
     * @return Value of the version attribute.
     * @see #ATTRIBUTE_VERSION
     */
    public String getVersion() {
        return getAttribute(ATTRIBUTE_VERSION);
    }

    /**
     * Retrieve the xmlns attribute.
     *
     * @return Value of the xmlns attribute.
     * @see #ATTRIBUTE_XMLNS
     */
    public String getXmlns() {
        return getAttribute(ATTRIBUTE_XMLNS);
    }

    /**
     * Set the xmlns attribute.
     * @param xmlns Value of the xmlns attribute.
     * @see #ATTRIBUTE_XMLNS
     */
    public void setXmlns(final String xmlns) {
        setAttribute(DEFAULT_XMLNS, xmlns);
    }

    /**
     * Retrieve the xml:base attribute.
     *
     * @return Value of the xml:base attribute.
     * @see #ATTRIBUTE_XML_BASE
     */
    public String getXmlBase() {
        return getAttribute(ATTRIBUTE_XML_BASE);
    }

    /**
     * Set the xml:base attribute.
     * @param xmlBase Value of the xml:base attribute.
     * @see #ATTRIBUTE_XML_BASE
     */
    public void setXmlBase(final String xmlBase) {
        setAttribute(ATTRIBUTE_XML_BASE, xmlBase);
    }

    /**
     * Retrieve the xml:lang attribute.
     *
     * @return Value of the xml:lang attribute.
     * @see #ATTRIBUTE_XML_LANG
     */
    public String getXmlLang() {
        return getAttribute(ATTRIBUTE_XML_LANG);
    }

    /**
     * Set the xml:lang attribute.
     * @param xmlLang Value of the xml:lang attribute.
     * @see #ATTRIBUTE_XML_LANG
     */
    public void setXmlLang(final String xmlLang) {
        setAttribute(ATTRIBUTE_XML_LANG, xmlLang);
    }

    /**
     * Retrieve the application attribute.
     *
     * @return Value of the application attribute.
     * @see #ATTRIBUTE_APPLICATION
     */
    public String getApplication() {
        return getAttribute(ATTRIBUTE_APPLICATION);
    }

    /**
     * Set the application attribute.
     * @param application Value of the application attribute.
     * @see #ATTRIBUTE_XML_LANG
     */
    public void setApplication(final String application) {
        setAttribute(ATTRIBUTE_APPLICATION, application);
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
        return new Vxml(n);
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
