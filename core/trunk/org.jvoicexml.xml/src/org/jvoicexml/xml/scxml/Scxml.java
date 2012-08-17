/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.scxml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * The top-level wrapper element, which carries version information. The actual
 * state machine consists of its children. 
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public final class Scxml
        extends AbstractScxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "scxml";

    /**
     * The id of the initial state(s) for the document. If not specified, the
     * default initial state is the first child state in document order.
     */
    private static final String ATTRIBUTE_INITIAL = "inital";

    /**
     * The name of this state machine. It is for purely informational purposes.
     */
    private static final String ATTRIBUTE_NAME = "name";

    /**
     * The namespace URI for SCXML is
     * <code>"http://www.w3.org/2005/07/scxm"</code>.
     */
    public static final String ATTRIBUTE_XMLNS = "xmlns";

    /**
     * The value MUST be <code>1.0</code>.
     */
    public static final String ATTRIBUTE_VERSION = "version";

    /**
     * The datamodel that this document requires. <code>null</code> denotes the
     * Null datamodel, <code>ecmascript</code> the ECMAScript datamodel, and
     * <code>xpath</code> the XPath datamodel.
     */
    public static final String ATTRIBUTE_DATAMODEL = "datamodel";

    /**
     * The data binding to use.
     */
    public static final String ATTRIBUTE_BINDING = "binding";

    /**
     * Determines whether the processor should silently ignore markup that it
     * does not support.
     */
    public static final String ATTRIBUTE_EXMODE = "exmode";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_INITIAL);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAME);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_XMLNS);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_VERSION);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_DATAMODEL);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_BINDING);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_EXMODE);
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
        
        CHILD_TAGS.add(Datamodel.TAG_NAME);
        CHILD_TAGS.add(Final.TAG_NAME);
        CHILD_TAGS.add(Parallel.TAG_NAME);
        CHILD_TAGS.add(Script.TAG_NAME);
        CHILD_TAGS.add(State.TAG_NAME);
    }

    /**
     * Construct a new scxml object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.scxml.ScxmlNodeFactory
     */
    public Scxml() {
        super(null);
    }

    /**
     * Construct a new scxml object.
     * @param node The encapsulated node.
     */
    Scxml(final Node node) {
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
    private Scxml(final Node n,
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
        return new Scxml(n, factory);
    }

    /**
     * Retrieve the initial attribute.
     *
     * @return value of the inital attribute.
     * @see #ATTRIBUTE_INITIAL
     */
    public String getInitial() {
        return getAttribute(ATTRIBUTE_INITIAL);
    }

    /**
     * Set the initial attribute.
     *
     * @param initial value of the initial attribute.
     * @see #ATTRIBUTE_ALPHABET
     */
    public void setInitial(final String initial) {
        setAttribute(ATTRIBUTE_INITIAL, initial);
    }

    /**
     * Retrieve the name attribute.
     *
     * @return value of the name attribute.
     * @see #ATTRIBUTE_NAME
     */
    public String getName() {
        return getAttribute(ATTRIBUTE_NAME);
    }

    /**
     * Sets the name attribute.
     *
     * @param name Value of the name attribute.
     * @see #ATTRIBUTE_NAME
     */
    public void setName(final String name) {
        setAttribute(ATTRIBUTE_NAME, name);
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
     *
     * @param xmlns Value of the xml:base attribute.
     * @see #ATTRIBUTE_XMLNS
     */
    public void setXmlns(final String xmlns) {
        setAttribute(ATTRIBUTE_XMLNS, xmlns);
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
     * Retrieves the datamodel attribute.
     *
     * @return value of the datamodel attribute.
     * @see #ATTRIBUTE_DATAMODEL
     */
    public String getDatamodel() {
        return getAttribute(ATTRIBUTE_DATAMODEL);
    }

    /**
     * Sets the datamodel attribute.
     *
     * @param datamodel Value of the datamodel attribute.
     * @see #ATTRIBUTE_DATAMODEL
     */
    public void setDatamodel(final String datamodel) {
        setAttribute(ATTRIBUTE_DATAMODEL, datamodel);
    }

    /**
     * Retrieves the binding attribute.
     *
     * @return value of the binding attribute.
     * @see #ATTRIBUTE_DATAMODEL
     */
    public String getBinding() {
        return getAttribute(ATTRIBUTE_BINDING);
    }

    /**
     * Sets the binding attribute.
     *
     * @param binding Value of the binding attribute.
     * @see #ATTRIBUTE_BINDING
     */
    public void setBinding(final String binding) {
        setAttribute(ATTRIBUTE_BINDING, binding);
    }

    /**
     * Retrieves the exmode attribute.
     *
     * @return value of the exmode attribute.
     * @see #ATTRIBUTE_EXMODE
     */
    public String getExmode() {
        return getAttribute(ATTRIBUTE_EXMODE);
    }

    /**
     * Sets the exmode attribute.
     *
     * @param exmode Value of the exmode attribute.
     * @see #ATTRIBUTE_EXMODE
     */
    public void setExmode(final String exmode) {
        setAttribute(ATTRIBUTE_EXMODE, exmode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canContainChild(final String tagName) {
        return CHILD_TAGS.contains(tagName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
