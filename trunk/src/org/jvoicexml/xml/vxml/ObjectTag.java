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

import java.net.URI;
import java.net.URISyntaxException;
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
 * Interact with a custom extension.
 * <p>
 * A VoiceXML implementation platform may expose platform-specific functionality
 * for use by a VoiceXML application via the <code>&lt;object&gt;</code>
 * element. The <code>&lt;object&gt;</code> element makes direct use of its
 * own content during initialization (e.g. <code>&lt;param&gt;</code> child
 * element) and execution. As a result, <code>&lt;object&gt;</code> content
 * cannot be treated as alternative content. Notice that like other input items,
 * <code>&lt;object&gt;</code> has prompts and catch elements. It may also
 * have <code>&lt;filled&gt;</code> actions.
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Filled
 * @see org.jvoicexml.xml.vxml.Param
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
public final class ObjectTag
        extends AbstractVoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "object";

    /**
     * When the object is evaluated, it sets this variable to an ECMAScript
     * value whose type is defined by the object.
     */
    public static final String ATTRIBUTE_NAME = "name";

    /**
     * The initial value of the form item variable; default is ECMAScript
     * undefined. If initialized to a value, then the form item will not be
     * visited unless the form item variable is cleared.
     */
    public static final String ATTRIBUTE_EXPR = "expr";

    /**
     * An expression that must evaluate to true after conversion to boolean in
     * order for the form item to be visited.
     */
    public static final String ATTRIBUTE_COND = "cond";

    /**
     * The URI specifying the location of the object's implementation. The URI
     * conventions are platform-dependent.
     */
    public static final String ATTRIBUTE_CLASSID = "classid";

    /**
     * The base path used to resolve relative URIs specified by classid, data,
     * and archive. It defaults to the base URI of the current document.
     */
    public static final String ATTRIBUTE_CODEBASE = "codebase";

    /**
     * The content type of data expected when downloading the object specified
     * by classid. When absent it defaults to the value of the type attribute.
     */
    public static final String ATTRIBUTE_CODETYPE = "codetype";

    /**
     * The URI specifying the location of the object's data. If it is a relative
     * URI, it is interpreted relative to the codebase attribute.
     */
    public static final String ATTRIBUTE_DATA = "data";

    /**
     * The content type of the data specified by the data attribute.
     */
    public static final String ATTRIBUTE_TYPE = "type";

    /**
     * A space-separated list of URIs for archives containing resources
     * relevant to the object, which may include the resources specified by
     * the classid and data attributes. URIs which are relative are interpreted
     * relative to the codebase attribute.
     */
    public static final String ATTRIBUTE_ARCHIVE = "archive";

    /**
     * The interval to wait for the content to be returned before throwing an
     * error.badfetch event. This defaults to the fetchtimeout property.
     */
    public static final String ATTRIBUTE_FETCHTIMEOUT = "fetchtimeout";

    /**
     * Defines when the interpreter context should retrieve content from the
     * server. prefetch indicates a file may be downloaded when the page is
     * loaded, whereas safe indicates a file that should only be downloaded when
     * actually needed. This defaults to the audiofetchhint property.
     */
    public static final String ATTRIBUTE_FETCHHINT = "fetchhint";

    /**
     * Indicates that the document is willing to use content whose age is no
     * greater than the specified time in seconds. The document is not willing
     * to use stale content, unless maxstale is also provided. This defaults to
     * the audiomaxage property.
     */
    public static final String ATTRIBUTE_MAXAGE = "maxage";

    /**
     * Indicates that the document is willing to use content that has exceeded
     * its expiration time. If maxstale is assigned a value, then the document
     * is willing to accept content that has exceeded its expiration time by no
     * more than the specified number of seconds. This defaults to the
     * audiomaxstale property.
     */
    public static final String ATTRIBUTE_MAXSTALE = "maxstale";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_ARCHIVE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_CLASSID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_CODEBASE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_CODETYPE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_COND);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_DATA);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_EXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHHINT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHTIMEOUT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXAGE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXSTALE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAME);
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

        CHILD_TAGS.add(Enumerate.TAG_NAME);
        CHILD_TAGS.add(Value.TAG_NAME);
        CHILD_TAGS.add(Audio.TAG_NAME);
        CHILD_TAGS.add(Catch.TAG_NAME);
        CHILD_TAGS.add(Help.TAG_NAME);
        CHILD_TAGS.add(Noinput.TAG_NAME);
        CHILD_TAGS.add(Nomatch.TAG_NAME);
        CHILD_TAGS.add(Error.TAG_NAME);
        CHILD_TAGS.add(Filled.TAG_NAME);
        CHILD_TAGS.add(Param.TAG_NAME);
        CHILD_TAGS.add(Prompt.TAG_NAME);
        CHILD_TAGS.add(Property.TAG_NAME);
    }

    /**
     * Construct a new object object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public ObjectTag() {
        super(null);
    }

    /**
     * Construct a new object object.
     * @param node The encapsulated node.
     */
    ObjectTag(final Node node) {
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
    private ObjectTag(final Node n,
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
        return new ObjectTag(n, factory);
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
     * @return Value of the cond attribute.
     * @see #ATTRIBUTE_COND
     */
    public String getCond() {
        return getAttribute(ATTRIBUTE_COND);
    }

    /**
     * Set the cond attribute.
     * @param cond Value of the cond attribute.
     * @see #ATTRIBUTE_COND
     */
    public void setCond(final String cond) {
        setAttribute(ATTRIBUTE_COND, cond);
    }

    /**
     * Retrieve the classid attribute.
     * @return Value of the classid attribute.
     * @see #ATTRIBUTE_CLASSID
     */
    public String getClassid() {
        return getAttribute(ATTRIBUTE_CLASSID);
    }

    /**
     * Retrieves the classid attribute as an URI.
     *
     * @return Value of the classi attribute.
     * @throws URISyntaxException
     *         Value is not a valid URI.
     * @see #ATTRIBUTE_CLASSID
     *
     * @since 0.6
     */
    public URI getClassidUri() throws URISyntaxException {
        String classid = getClassid();
        if (classid == null) {
            return null;
        }

        return new URI(classid);
    }

    /**
     * Set the classid attribute.
     * @param classid Value of the classid attribute.
     * @see #ATTRIBUTE_CLASSID
     */
    public void setClassid(final String classid) {
        setAttribute(ATTRIBUTE_CLASSID, classid);
    }

    /**
     * Set the classid attribute to
     * <code>method://" + classid.getName()</code>.
     * @param classid Class to call.
     * @see #ATTRIBUTE_CLASSID
     * @since 0.6
     */
    public void setClassid(final Class<?> classid) {
        setClassid(classid, null);
    }

    /**
     * Set the classid attribute to
     * <code>method://" + classid.getName()</code> if the method name is
     * <code>null</code> and to
     * <code>method://" + classid.getName() + "#" + method</code> if a
     * method is specified.
     * @param classid Class to call.
     * @param method name of the method to call.
     * @see #ATTRIBUTE_CLASSID
     * @since 0.6
     */
    public void setClassid(final Class<?> classid, final String method) {
        final StringBuilder str = new StringBuilder();
        str.append("method://");
        str.append(classid.getName());
        if (method != null) {
            str.append('#');
            str.append(method);
        }
        setClassid(str.toString());
    }

    /**
     * Set the classid attribute.
     * @param classid Value of the classid attribute.
     * @see #ATTRIBUTE_CLASSID
     * @since 0.6
     */
    public void setClassid(final URI classid) {
        final String id;
        if (classid == null) {
            id = null;
        } else {
            id = classid.toString();
        }

        setClassid(id);
    }

    /**
     * Retrieve the codebase attribute.
     * @return Value of the codebase attribute.
     * @see #ATTRIBUTE_CODEBASE
     */
    public String getCodebase() {
        return getAttribute(ATTRIBUTE_CODEBASE);
    }

    /**
     * Set the codebase attribute.
     * @param codebase Value of the codebase attribute.
     * @see #ATTRIBUTE_CODEBASE
     */
    public void setCodebase(final String codebase) {
        setAttribute(ATTRIBUTE_CODEBASE, codebase);
    }

    /**
     * Retrieve the codetype attribute.
     * @return Value of the codetype attribute.
     * @see #ATTRIBUTE_CODETYPE
     */
    public String getCodetype() {
        return getAttribute(ATTRIBUTE_CODETYPE);
    }

    /**
     * Set the codetype attribute.
     * @param codetype Value of the codetype attribute.
     * @see #ATTRIBUTE_CODETYPE
     */
    public void setCodetype(final String codetype) {
        setAttribute(ATTRIBUTE_CODETYPE, codetype);
    }

    /**
     * Retrieve the data attribute.
     * @return Value of the data attribute.
     * @see #ATTRIBUTE_DATA
     */
    public String getData() {
        return getAttribute(ATTRIBUTE_DATA);
    }

    /**
     * Set the data attribute.
     * @param data Value of the data attribute.
     * @see #ATTRIBUTE_DATA
     */
    public void setData(final String data) {
        setAttribute(ATTRIBUTE_DATA, data);
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
     * @param type Value of the type attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public void setType(final String type) {
        setAttribute(ATTRIBUTE_TYPE, type);
    }

    /**
     * Retrieve the archive attribute.
     * @return Value of the archive attribute.
     * @see #ATTRIBUTE_ARCHIVE
     */
    public String getArchive() {
        return getAttribute(ATTRIBUTE_ARCHIVE);
    }

    /**
     * Set the archive attribute.
     * @param archive Value of the archive attribute.
     * @see #ATTRIBUTE_ARCHIVE
     */
    public void setArchive(final String archive) {
        setAttribute(ATTRIBUTE_ARCHIVE, archive);
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
