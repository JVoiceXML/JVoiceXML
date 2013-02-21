/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
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


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jvoicexml.xml.LanguageIdentifierConverter;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A VoiceXML document is primarily composed of top-level elements called
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
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Vxml
        extends AbstractVoiceXmlNode {
    /** Name of the VXML tag. */
    public static final String TAG_NAME = "vxml";

    /**
     * Default Voice XML version number.
     * @see #ATTRIBUTE_VERSION
     */
    public static final String DEFAULT_VERSION = "2.1";

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
     * within the document take as their base.
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
    }

    /**
     * Constructs a new node.
     *
     * @param n
     *            The encapsulated node.
     * @param factory
     *            The node factory to use.
     */
    private Vxml(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        super(n, factory);
    }

    /**
     * Adds the default attributes.
     */
    void addDefaultAttributes() {
        setAttribute(ATTRIBUTE_XMLNS, DEFAULT_XMLNS);
        setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        setAttribute("xsi:schematicLocation",
                     DEFAULT_XMLNS
                     + " http://www.w3.org/TR/voicexml20/vxml.xsd");
        String version = System.getProperty(VoiceXmlDocument.VXML_VERSION);
        if (version == null) {
            version = Vxml.DEFAULT_VERSION;
        }
        setVersion(version);
    }

    /**
     * Get all forms in this document as a map with the name of the form
     * being key.
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
     * Get all VAR tags in this document as a map with the name of the var
     * being key.
     * @return Map with all named VAR tags.
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
     * Retrieve the version attribute.
     *
     * @return Value of the version attribute.
     * @see #ATTRIBUTE_VERSION
     */
    public String getVersion() {
        return getAttribute(ATTRIBUTE_VERSION);
    }

    /**
     * Sets the version attribute.
     *
     * @param version Value of the version attribute.
     * @see #ATTRIBUTE_VERSION
     * @since 0.7
     */
    public void setVersion(final String version) {
        setAttribute(ATTRIBUTE_VERSION, version);
    }

    /**
     * Retrieves the xmlns attribute.
     *
     * @return Value of the xmlns attribute.
     * @see #ATTRIBUTE_XMLNS
     */
    public String getXmlns() {
        return getAttribute(ATTRIBUTE_XMLNS);
    }

    /**
     * Sets the xmlns attribute.
     * @param xmlns Value of the xmlns attribute.
     * @see #ATTRIBUTE_XMLNS
     */
    public void setXmlns(final String xmlns) {
        setAttribute(DEFAULT_XMLNS, xmlns);
    }

    /**
     * Retrieves the xml:base attribute.
     *
     * @return Value of the xml:base attribute.
     * @see #ATTRIBUTE_XML_BASE
     */
    public String getXmlBase() {
        return getAttribute(ATTRIBUTE_XML_BASE);
    }

    /**
     * Retrieves the xml:base attribute as an URI.
     *
     * @return Value of the xml:base attribute.
     * @throws URISyntaxException
     *         Value is not a valid URI.
     * @see #ATTRIBUTE_XML_BASE
     *
     * @since 0.6
     */
    public URI getXmlBaseUri()
        throws URISyntaxException {
        final String base = getXmlBase();
        if (base == null) {
            return null;
        }

        return new URI(base);
    }

    /**
     * Sets the xml:base attribute.
     * @param xmlBase Value of the xml:base attribute.
     * @see #ATTRIBUTE_XML_BASE
     */
    public void setXmlBase(final String xmlBase) {
        setAttribute(ATTRIBUTE_XML_BASE, xmlBase);
    }

    /**
     * Sets the xml:base attribute.
     * @param xmlBase Value of the xml:base attribute.
     * @see #ATTRIBUTE_XML_BASE
     *
     * @since 0.6
     */
    public void setXmlBase(final URI xmlBase) {
        final String base;
        if (xmlBase == null) {
            base = null;
        } else {
            base = xmlBase.toString();
        }

        setXmlBase(base);
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
     * Retrieve the xml:lang attribute.
     *
     * @return Value of the xml:lang attribute.
     * @see #ATTRIBUTE_XML_LANG
     * @since 0.7.1
     */
    public Locale getXmlLangObject() {
        final String xmlLang = getXmlLang();
        return LanguageIdentifierConverter.toLocale(xmlLang);
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
     * Set the xml:lang attribute.
     * @param locale Value of the xml:lang attribute.
     * @see #ATTRIBUTE_XML_LANG
     * @since 0.7.1
     */
    public void setXmlLang(final Locale locale) {
        final String xmlLang =
            LanguageIdentifierConverter.toLanguageIdentifier(locale);
        setAttribute(ATTRIBUTE_XML_LANG, xmlLang);
    }

    /**
     * Retrieves the application attribute.
     *
     * @return Value of the application attribute.
     * @see #ATTRIBUTE_APPLICATION
     */
    public String getApplication() {
        return getAttribute(ATTRIBUTE_APPLICATION);
    }

    /**
     * Retrieves the application attribute.
     *
     * @return Value of the application attribute.
     * @see #ATTRIBUTE_APPLICATION
     * @exception URISyntaxException
     *            value is not a valid URI.
     *
     * @since 0.6
     */
    public URI getApplicationUri()
        throws URISyntaxException {
            final String application = getApplication();
            if (application == null) {
                return null;
            }

            return new URI(application);
    }

    /**
     * Sets the application attribute.
     * @param application Value of the application attribute.
     * @see #ATTRIBUTE_XML_LANG
     */
    public void setApplication(final String application) {
        setAttribute(ATTRIBUTE_APPLICATION, application);
    }

    /**
     * Sets the application attribute.
     * @param application Value of the application attribute.
     * @see #ATTRIBUTE_XML_LANG
     *
     * @since 0.6
     */
    public void setApplication(final URI application) {
        if (application == null) {
            return;
        }

        final String app = application.toString();

        setApplication(app);
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
        return new Vxml(n, factory);
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
}
