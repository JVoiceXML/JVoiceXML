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
 * The <code>&lt;invoke&gt;</code> element is used to create an instance of
 * an external service.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public final class Invoke
        extends AbstractScxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "invoke";

    /**
     * A URI specifying the type of the external service.
     */
    private static final String ATTRIBUTE_TYPE = "type";

    /**
     * A dynamic alternative to <code>type</code>. If this attribute is
     * present, the SCXML Processor MUST evaluate it when the parent
     * <code>&lt;invoke&gt;</code> element is evaluated and treat the result as
     * if it had been entered as the value of <code>type</code>.
     */
    private static final String ATTRIBUTE_TYPEEXPR = "typeexpr";

    /**
     * A URI to be passed to the external service.
     */
    private static final String ATTRIBUTE_SRC = "src";

    /**
     * A dynamic alternative to <code>src</code>. If this attribute is
     * present, the SCXML Processor MUST evaluate it when the parent
     * <code>&lt;invoke&gt;</code> element is evaluated and treat the result as
     * if it had been entered as the value of <code>src</code>.
     */
    private static final String ATTRIBUTE_SRCEXPR = "srcexpr";

    /**
     * A string literal to be used as the identifier for this instance of
     * <code>&lt;invoke&gt;</code>.
     */
    private static final String ATTRIBUTE_ID = "id";

    /**
     * Any data model expression evaluating to a data model location.
     */
    private static final String ATTRIBUTE_IDLOCATION = "idlocation";

    /**
     * A space-separated list of zero or more data model locations to be passed
     * to the invoked service. 
     */
    private static final String ATTRIBUTE_NAMELIST = "namelist";

    /**
     * A flag indicating whether to forward events to the invoked process.
     */
    private static final String ATTRIBUTE_AUTOFORWARD = "autoforward";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;


    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_TYPE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TYPEEXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SRC);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SRCEXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_ID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_IDLOCATION);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAMELIST);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_AUTOFORWARD);
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

        CHILD_TAGS.add(Content.TAG_NAME);
        CHILD_TAGS.add(Finalize.TAG_NAME);
        CHILD_TAGS.add(Param.TAG_NAME);
    }

    /**
     * Construct a new send object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.scxml.ScxmlNodeFactory
     */
    public Invoke() {
        super(null);
    }

    /**
     * Construct a new send object.
     * @param node The encapsulated node.
     */
    Invoke(final Node node) {
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
    private Invoke(final Node n,
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
        return new Invoke(n, factory);
    }

    /**
     * Retrieves the src attribute.
     *
     * @return value of the src attribute.
     * @see #ATTRIBUTE_SRC
     */
    public String getSrc() {
        return getAttribute(ATTRIBUTE_SRC);
    }

    /**
     * Sets the src attribute.
     *
     * @param src Value of the src attribute.
     * @see #ATTRIBUTE_SRC
     */
    public void setSrc(final String src) {
        setAttribute(ATTRIBUTE_SRC, src);
    }

    /**
     * Retrieves the srcexpr attribute.
     *
     * @return value of the srcexpr attribute.
     * @see #ATTRIBUTE_SRCEXPR
     */
    public String getSrcexpr() {
        return getAttribute(ATTRIBUTE_SRCEXPR);
    }

    /**
     * Sets the srcexpr attribute.
     *
     * @param srcexpr Value of the srcexpr attribute.
     * @see #ATTRIBUTE_SRCEXPR
     */
    public void setSrcexpr(final String srcexpr) {
        setAttribute(ATTRIBUTE_SRCEXPR, srcexpr);
    }

    /**
     * Retrieves the type attribute.
     *
     * @return value of the type attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public String getType() {
        return getAttribute(ATTRIBUTE_TYPE);
    }

    /**
     * Sets the type attribute.
     *
     * @param type Value of the target attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public void setType(final String type) {
        setAttribute(ATTRIBUTE_TYPE, type);
    }

    /**
     * Retrieves the typeexpr attribute.
     *
     * @return value of the typeexpr attribute.
     * @see #ATTRIBUTE_TYPEEXPR
     */
    public String getTypeexpr() {
        return getAttribute(ATTRIBUTE_TYPEEXPR);
    }

    /**
     * Sets the typeexpr attribute.
     *
     * @param typeexpr Value of the target attribute.
     * @see #ATTRIBUTE_TYPEEXPR
     */
    public void setTypeexpr(final String typeexpr) {
        setAttribute(ATTRIBUTE_TYPEEXPR, typeexpr);
    }

    /**
     * Retrieves the id attribute.
     *
     * @return value of the id attribute.
     * @see #ATTRIBUTE_ID
     */
    public String getId() {
        return getAttribute(ATTRIBUTE_ID);
    }

    /**
     * Sets the id attribute.
     *
     * @param id Value of the id attribute.
     * @see #ATTRIBUTE_ID
     */
    public void setId(final String id) {
        setAttribute(ATTRIBUTE_ID, id);
    }

    /**
     * Retrieves the idlocation attribute.
     *
     * @return value of the idlocation attribute.
     * @see #ATTRIBUTE_IDLOCATION
     */
    public String getIdlocation() {
        return getAttribute(ATTRIBUTE_IDLOCATION);
    }

    /**
     * Sets the idlocation attribute.
     *
     * @param idlocation Value of the idlocation attribute.
     * @see #ATTRIBUTE_IDLOCATION
     */
    public void setIdlocation(final String idlocation) {
        setAttribute(ATTRIBUTE_IDLOCATION, idlocation);
    }

    /**
     * Retrieves the namelist attribute.
     *
     * @return value of the namelist attribute.
     * @see #ATTRIBUTE_NAMELIST
     */
    public String getNamelist() {
        return getAttribute(ATTRIBUTE_NAMELIST);
    }

    /**
     * Sets the namelist attribute.
     *
     * @param namelist Value of the namelist attribute.
     * @see #ATTRIBUTE_NAMELIST
     */
    public void setNamelist(final String namelist) {
        setAttribute(ATTRIBUTE_NAMELIST, namelist);
    }

    /**
     * Retrieves the autoforward attribute.
     *
     * @return value of the autoforward attribute.
     * @see #ATTRIBUTE_AUTOFORWARD
     */
    public String getAutoforward() {
        return getAttribute(ATTRIBUTE_AUTOFORWARD);
    }

    /**
     * Sets the autoforward attribute.
     *
     * @param autoforward Value of the autoforward attribute.
     * @see #ATTRIBUTE_AUTOFORWARD
     */
    public void setAutoforward(final String autoforward) {
        setAttribute(ATTRIBUTE_AUTOFORWARD, autoforward);
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
