/*
 * File:    $RCSfile: Record.java,v $
 * Version: $Revision: 1.7 $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.ssml.Audio;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * The <code>&lt;record&gt;</code> element is an input item that collects a
 * recording from the user.
 *
 * @see org.jvoicexml.xml.ssml.Audio
 * @see org.jvoicexml.xml.vxml.Form
 *
 * @author Steve Doyle
 * @version $Revision: 1.7 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class Record
        extends AbstractVoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "record";

    /**
     * The input item variable that will hold the recording.
     */
    public static final String ATTRIBUTE_NAME = "name";

    /**
     * The initial value of the form item variable; default is ECMAScript
     * undefined. If initialized to a value, then the form item will not
     * be visited unless the form item variable is cleared.
     */
    public static final String ATTRIBUTE_EXPR = "expr";

    /**
     * An expression that must evaluate to true after conversion to boolean
     * in order for the form item to be visited.
     */
    public static final String ATTRIBUTE_COND = "cond";

    /**
     * If this is true (the default) all non-local speech and DTMF grammars
     * are not active while making the recording. If this is false, non-local
     * speech and DTMF grammars are active.
     */
    public static final String ATTRIBUTE_MODAL = "modal";

    /**
     * If true, a tone is emitted just prior to recording. Defaults to false.
     */
    public static final String ATTRIBUTE_BEEP = "beep";

    /**
     * The maximum duration to record. The value is a Time Designation.
     * Defaults to a platform-specific value.
     */
    public static final String ATTRIBUTE_MAXTIME = "maxtime";

    /**
     * The interval of silence that indicates end of speech. The value is a
     * Time Designation. Defaults to a platform-specific value.
     */
    public static final String ATTRIBUTE_FINALSILENCE = "finalsilence";

    /**
     * If true, any DTMF keypress not matched by an active grammar will be
     * treated as a match of an active (anonymous) local DTMF grammar.
     * Defaults to true.
     */
    public static final String ATTRIBUTE_DTMFTERM = "dtmfterm";

    /**
     * The media format of the resulting recording.
     */
    public static final String ATTRIBUTE_TYPE = "type";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_BEEP);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_COND);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_DTMFTERM);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_EXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FINALSILENCE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXTIME);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MODAL);
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
        CHILD_TAGS.add(Grammar.TAG_NAME);
        CHILD_TAGS.add(Property.TAG_NAME);
        CHILD_TAGS.add(Prompt.TAG_NAME);
    }

    /**
     * Construct a new record object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Record() {
        super(null);
    }

    /**
     * Construct a new record object.
     * @param node The encapsulated node.
     */
    Record(final Node node) {
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
        return new Record(n);
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
     * Retrieve the modal attribute.
     * @return Value of the modal attribute.
     * @see #ATTRIBUTE_MODAL
     */
    public String getModal() {
        final String modal = getAttribute(ATTRIBUTE_MODAL);
        if (modal != null) {
            return modal;
        }
        return Boolean.toString(true);
    }

    /**
     * Set the modal attribute.
     * @param modal Value of the modal attribute.
     * @see #ATTRIBUTE_MODAL
     */
    public void setModal(final String modal) {
        setAttribute(ATTRIBUTE_MODAL, modal);
    }

    /**
     * Retrieve the beep attribute.
     * @return Value of the beep attribute.
     * @see #ATTRIBUTE_BEEP
     */
    public String getBeep() {
        return getAttribute(ATTRIBUTE_BEEP);
    }

    /**
     * Set the beep attribute.
     * @param beep Value of the beep attribute.
     * @see #ATTRIBUTE_BEEP
     */
    public void setBeep(final String beep) {
        setAttribute(ATTRIBUTE_BEEP, beep);
    }

    /**
     * Retrieve the maxtime attribute.
     * @return Value of the maxtime attribute.
     * @see #ATTRIBUTE_MAXTIME
     */
    public String getMaxtime() {
        return getAttribute(ATTRIBUTE_MAXTIME);
    }

    /**
     * Set the maxtime attribute.
     * @param maxtime Value of the maxtime attribute.
     * @see #ATTRIBUTE_MAXTIME
     */
    public void setMaxtime(final String maxtime) {
        setAttribute(ATTRIBUTE_MAXTIME, maxtime);
    }

    /**
     * Retrieve the finalsilence attribute.
     * @return Value of the finalsilence attribute.
     * @see #ATTRIBUTE_FINALSILENCE
     */
    public String getFinalsilence() {
        return getAttribute(ATTRIBUTE_FINALSILENCE);
    }

    /**
     * Set the finalsilence attribute.
     * @param finalsilence Value of the finalsilence attribute.
     * @see #ATTRIBUTE_FINALSILENCE
     */
    public void setFinalsilence(final String finalsilence) {
        setAttribute(ATTRIBUTE_FINALSILENCE, finalsilence);
    }

    /**
     * Retrieve the dtmfterm attribute.
     * @return Value of the dtmfterm attribute.
     * @see #ATTRIBUTE_DTMFTERM
     */
    public String getDtmfterm() {
        return getAttribute(ATTRIBUTE_DTMFTERM);
    }

    /**
     * Set the dtmfterm attribute.
     * @param dtmfterm Value of the dtmfterm attribute.
     * @see #ATTRIBUTE_DTMFTERM
     */
    public void setDtmfterm(final String dtmfterm) {
        setAttribute(ATTRIBUTE_DTMFTERM, dtmfterm);
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
