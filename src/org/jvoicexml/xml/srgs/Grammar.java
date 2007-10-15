/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package org.jvoicexml.xml.srgs;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.jvoicexml.xml.ssml.Lexicon;
import org.jvoicexml.xml.vxml.Meta;
import org.jvoicexml.xml.vxml.Metadata;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * The &lt;grammar&gt; element is used to provide a speech grammar that
 * <ul>
 * <li>
 * specifies a set of utterances that a user may speak to perform an action or
 * supply information, and
 * </li>
 * <li>
 * for a matching utterance, returns a corresponding semantic interpretation.
 * This may be a simple value (such as a string), a flat set of attribute-value
 * pairs (such as day, month, and year), or a nested object (for a complex
 * request).
 * </li>
 * </ul>
 *
 * @author Steve Doyle
 * @author Dirk Schnelle
 *
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */

public final class Grammar
        extends AbstractSrgsNode implements VoiceXmlNode {

    /** Name of the grammar tag. */
    public static final String TAG_NAME = "grammar";

    /**
     * Defines the version of the grammar.
     */
    public static final String ATTRIBUTE_VERSION = "version";

    /**
     * The language identifier for the grammar. If omitted, the value is
     * inherited down from the document hierarchy.
     */
    public static final String ATTRIBUTE_XML_LANG = "xml:lang";

    /**
     * Defines the mode of the grammar following the modes of the W3C Speech
     * Recognition Grammar Specification.
     */
    public static final String ATTRIBUTE_MODE = "mode";

    /**
     * Defines the rule which acts as the root rule of the grammar.
     */
    public static final String ATTRIBUTE_ROOT = "root";

    /**
     * Defines the tag content format for all tags within the grammar.
     */
    public static final String ATTRIBUTE_TAG_FORMAT = "tag-format";

    /**
     * Declares the base URI from which relative URIs in the grammar are
     * resolved. This base declaration has precedence over the
     * <code>&lt;vxml&gt;</code> base URI declaration. If a local declaration
     * is omitted, the value is inherited down the document hierarchy.
     */
    public static final String ATTRIBUTE_XML_BASE = "xml:base";

    /**
     * The URI specifying the location of the grammar and optionally a rulename
     * within that grammar, if it is external. The URI is interpreted as a rule
     * reference as defined in Section 2.2 of the Speech Recognition Grammar
     * Specification <A
     * href="http://www.w3.org/TR/voicexml20/#ref_SRGS"shape=rect>[SRGS] </A>
     * but not all forms of rule reference are permitted from within VoiceXML.
     * The rule reference capabilities are described in detail below this table.
     */
    public static final String ATTRIBUTE_SRC = "src";

    /**
     * Either "document", which makes the grammar active in all dialogs of the
     * current document (and relevant application leaf documents), or "dialog",
     * to make the grammar active throughout the current form. If omitted, the
     * grammar scoping is resolved by looking at the parent element.
     *
     * @see #SCOPE_DOCUMENT
     * @see #SCOPE_DIALOG
     */
    public static final String ATTRIBUTE_SCOPE = "scope";

    /**
     * Scope <em>document</em>, which makes the grammar active in all dialogs
     * of the current document (and relevant application leaf documents).
     * @see #ATTRIBUTE_SCOPE
     * @see #SCOPE_DIALOG
     */
    public static final String SCOPE_DOCUMENT = "document";

    /**
     * Scope <em>dialog</em>, which makes the grammar active throughout the
     * current form..
     *
     * @see #ATTRIBUTE_SCOPE
     * @see #SCOPE_DOCUMENT
     */
    public static final String SCOPE_DIALOG = "dialog";

    /**
     * The preferred media type of the grammar. A resource indicated by the URI
     * reference in the src attribute may be available in one or more media
     * types. The author may specify the preferred media-type via the type
     * attribute. When the content represented by a URI is available in many
     * data formats, a VoiceXML platform may use the preferred media-type to
     * influence which of the multiple formats is used. For instance, on a
     * server implementing HTTP content negotiation, the processor may use the
     * preferred media-type to order the preferences in the negotiation.
     */
    public static final String ATTRIBUTE_TYPE = "type";

    /**
     * Specifies the weight of the grammar.
     */
    public static final String ATTRIBUTE_WEIGHT = "weight";

    /**
     * This defaults to the grammarfetchhint property.
     */
    public static final String ATTRIBUTE_FETCHHINT = "fetchhint";

    /**
     * This defaults to the fetchtimeout property.
     */
    public static final String ATTRIBUTE_FETCHTIMEOUT = "fetchtimeout";

    /**
     * This defaults to the grammarmaxage property.
     */
    public static final String ATTRIBUTE_MAXAGE = "maxage";

    /**
     * This defaults to the grammarmaxstale property.
     */
    public static final String ATTRIBUTE_MAXSTALE = "maxstale";

    /**
     * Equivalent to src, except that the URI is dynamically determined by
     * evaluating the given ECMAScript expression in the current scope (e.g. the
     * current form item). The expression must be evaluated each time the
     * grammar needs to be activated. If srcexpr cannot be evaluated, an
     * error.semantic event is thrown.
     */
    public static final String ATTRIBUTE_SRCEXPR = "srcexpr";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHHINT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHTIMEOUT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXAGE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXSTALE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MODE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_ROOT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SCOPE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SRC);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SRCEXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TAG_FORMAT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TYPE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_VERSION);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_WEIGHT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_XML_BASE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_XML_LANG);
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

        CHILD_TAGS.add(Meta.TAG_NAME);
        CHILD_TAGS.add(Metadata.TAG_NAME);
        CHILD_TAGS.add(Lexicon.TAG_NAME);
        CHILD_TAGS.add(Rule.TAG_NAME);
        CHILD_TAGS.add(Tag.TAG_NAME);
    }

    /**
     * Construct a new grammar object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Grammar() {
        super(null);
    }

    /**
     * Construct a new grammar object.
     *
     * @param node The encapsulated node.
     */
    Grammar(final Node node) {
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
    private Grammar(final Node n,
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
        return new Grammar(n, factory);
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
     * Set the version attribute.
     *
     * @param version Value of the version attribute.
     * @see #ATTRIBUTE_VERSION
     */
    public void setVersion(final String version) {
        setAttribute(ATTRIBUTE_VERSION, version);
    }

    /**
     * Retrieves the mode attribute.
     *
     * @return Value of the mode attribute.
     * @see #ATTRIBUTE_MODE
     */
    public String getModename() {
        return getAttribute(ATTRIBUTE_MODE);
    }

    /**
     * Retrieves the mode attribute.
     *
     * @return Value of the mode attribute.
     * @see #ATTRIBUTE_MODE
     */
    public ModeType getMode() {
        final String mode = getModename();

        if (mode == null) {
            return null;
        }

        return ModeType.valueOfAttribute(mode);
    }

    /**
     * Set the mode attribute.
     *
     * @param mode Value of the mode attribute.
     * @see #ATTRIBUTE_MODE
     */
    public void setMode(final String mode) {
        setAttribute(ATTRIBUTE_MODE, mode);
    }

    /**
     * Sets the mode attribute.
     *
     * @param mode Value of the mode attribute.
     * @see #ATTRIBUTE_MODE
     */
    public void setMode(final ModeType mode) {
        final String modename = mode.getMode();

        setAttribute(ATTRIBUTE_MODE, modename);
    }

    /**
     * Retrieve sthe root attribute.
     *
     * @return Value of the root attribute.
     * @see #ATTRIBUTE_ROOT
     */
    public String getRoot() {
        return getAttribute(ATTRIBUTE_ROOT);
    }

    /**
     * Sets the root attribute.
     *
     * @param root Value of the root attribute.
     * @see #ATTRIBUTE_ROOT
     */
    public void setRoot(final String root) {
        setAttribute(ATTRIBUTE_ROOT, root);
    }

    /**
     * Retrieve the tag-format attribute.
     *
     * @return Value of the tag-format attribute.
     * @see #ATTRIBUTE_TAG_FORMAT
     */
    public String getTagFormat() {
        return getAttribute(ATTRIBUTE_TAG_FORMAT);
    }

    /**
     * Set the tag-format attribute.
     *
     * @param tagFormat Value of the tag-format attribute.
     * @see #ATTRIBUTE_TAG_FORMAT
     */
    public void setTagFormat(final String tagFormat) {
        setAttribute(ATTRIBUTE_TAG_FORMAT, tagFormat);
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
     *
     * @param xmlLang Value of the xml:lang attribute.
     * @see #ATTRIBUTE_XML_LANG
     */
    public void setXmlLang(final String xmlLang) {
        setAttribute(ATTRIBUTE_XML_LANG, xmlLang);
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
     *
     * @param xmlBase Value of the xml:base attribute.
     * @see #ATTRIBUTE_XML_BASE
     */
    public void setXmlBase(final String xmlBase) {
        setAttribute(ATTRIBUTE_XML_BASE, xmlBase);
    }

    /**
     * Retrieve the src attribute.
     *
     * @return Value of the src attribute.
     * @see #ATTRIBUTE_SRC
     */
    public String getSrc() {
        return getAttribute(ATTRIBUTE_SRC);
    }

    /**
     * Set the src attribute.
     *
     * @param src Value of the src attribute.
     * @see #ATTRIBUTE_SRC
     */
    public void setSrc(final String src) {
        setAttribute(ATTRIBUTE_SRC, src);
    }

    /**
     * Set the src attribute to the given URL.
     *
     * @param url URL of the src attribute.
     * @see #ATTRIBUTE_SRC
     */
    public void setSrc(final URL url) {
        final String src = url.toString();

        setSrc(src);
    }

    /**
     * Retrieve the scope attribute.
     *
     * @return Value of the scope attribute.
     * @see #ATTRIBUTE_SCOPE
     */
    public String getScope() {
        return getAttribute(ATTRIBUTE_SCOPE);
    }

    /**
     * Set the scope attribute.
     *
     * @param scope Value of the scope attribute.
     * @see #ATTRIBUTE_SCOPE
     */
    public void setScope(final String scope) {
        setAttribute(ATTRIBUTE_SCOPE, scope);
    }

    /**
     * Retrieves the type attribute.
     *
     * @return Value of the type attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public String getTypename() {
        return getAttribute(ATTRIBUTE_TYPE);
    }

    /**
     * Retrieves the type attribute.
     *
     * @return Value of the type attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public GrammarType getType() {
        final String type = getTypename();

        if (type == null) {
            return null;
        }

        return GrammarType.valueOfAttribute(type);
    }

    /**
     * Sets the type attribute.
     *
     * @param type Value of the type attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public void setType(final String type) {
        setAttribute(ATTRIBUTE_TYPE, type);
    }

    /**
     * Sets the type attribute.
     *
     * @param type Value of the type attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public void setType(final GrammarType type) {
        final String str = type.getType();

        setType(str);
    }

    /**
     * Retrieve the weight attribute.
     *
     * @return Value of the weight attribute.
     * @see #ATTRIBUTE_WEIGHT
     */
    public String getWeight() {
        return getAttribute(ATTRIBUTE_TYPE);
    }

    /**
     * Set the weight attribute.
     *
     * @param weight Value of the weight attribute.
     * @see #ATTRIBUTE_WEIGHT
     */
    public void setWeight(final String weight) {
        setAttribute(ATTRIBUTE_WEIGHT, weight);
    }

    /**
     * Retrieve the fetchhint attribute.
     * @return Value of the fetchhint attribute.
     * @see #ATTRIBUTE_FETCHHINT
     */
    public String getFetchhint() {
        return getAttribute(ATTRIBUTE_FETCHHINT);
    }

    /**
     * Set the fetchhint attribute.
     * @param fetchhint Value of the fetchhint attribute.
     * @see #ATTRIBUTE_FETCHHINT
     */
    public void setFetchhint(final String fetchhint) {
        setAttribute(ATTRIBUTE_FETCHHINT, fetchhint);
    }

    /**
     * Retrieve the fetchtimeout attribute.
     * @return Value of the fetchtimeout attribute.
     * @see #ATTRIBUTE_FETCHTIMEOUT
     */
    public String getFetchtimeout() {
        return getAttribute(ATTRIBUTE_FETCHTIMEOUT);
    }

    /**
     * Set the fetchtimeout attribute.
     * @param fetchtimeout Value of the fetchtimeout attribute.
     * @see #ATTRIBUTE_FETCHTIMEOUT
     */
    public void setFetchtimeout(final String fetchtimeout) {
        setAttribute(ATTRIBUTE_FETCHTIMEOUT, fetchtimeout);
    }

    /**
     * Retrieve the maxage attribute.
     * @return Value of the maxage attribute.
     * @see #ATTRIBUTE_MAXAGE
     */
    public String getMaxage() {
        return getAttribute(ATTRIBUTE_MAXAGE);
    }

    /**
     * Set the maxage attribute.
     * @param maxage Value of the maxage attribute.
     * @see #ATTRIBUTE_MAXAGE
     */
    public void setMaxage(final String maxage) {
        setAttribute(ATTRIBUTE_MAXAGE, maxage);
    }

    /**
     * Retrieve the maxstale attribute.
     * @return Value of the maxstale attribute.
     * @see #ATTRIBUTE_MAXSTALE
     */
    public String getMaxstale() {
        return getAttribute(ATTRIBUTE_MAXSTALE);
    }

    /**
     * Set the maxstale attribute.
     * @param maxstale Value of the maxstale attribute.
     * @see #ATTRIBUTE_MAXSTALE
     */
    public void setMaxstale(final String maxstale) {
        setAttribute(ATTRIBUTE_MAXSTALE, maxstale);
    }

    /**
     * Retrieve the srcexpr attribute.
     * @return Value of the maxssrcexprtale attribute.
     * @see #ATTRIBUTE_SRCEXPR
     */
    public String getSrcexpr() {
        return getAttribute(ATTRIBUTE_SRCEXPR);
    }

    /**
     * Set the srcexpr attribute.
     * @param srcexpr Value of the srcexpr attribute.
     * @see #ATTRIBUTE_SRCEXPR
     */
    public void setSrcexpr(final String srcexpr) {
        setAttribute(ATTRIBUTE_SRCEXPR, srcexpr);
    }

    /**
     * Create a new text within this grammar.
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
     * {@inheritDoc}
     */
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
