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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.ssml.Audio;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Transfer the caller to another destination.
 * <p>
 * The <code>&lt;transfer&gt;</code> element directs the interpreter to connect
 * the caller to another entity (e.g. telephone line or another voice
 * application). During the transfer operation, the current interpreter session
 * is suspended.
 * </p>
 *
 * @author Steve Doyle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class Transfer
        extends AbstractVoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "transfer";

    /**
     * Stores the outcome of a bridge transfer attempt. In the case of a blind
     * transfer, this variable is undefined.
     */
    public static final String ATTRIBUTE_NAME = "name";

    /**
     * The initial value of the form item variable; default is ECMAScript
     * undefined. If initialized to a value, then the form item will not be
     * visited unless the form item variable is cleared.
     */
    public static final String ATTRIBUTE_EXPR = "expr";

    /**
     * An expression that must evaluate to true in order for the form item to
     * be visited.
     */
    public static final String ATTRIBUTE_COND = "cond";

    /**
     * The URI of the destination (telephone, IP telephony address). Platforms
     * must support the tel: URL syntax described in RFC2806 and may support
     * other URI-based addressing schemes.
     */
    public static final String ATTRIBUTE_DEST = "dest";

    /**
     * An ECMAScript expression yielding the URI of the destination.
     */
    public static final String ATTRIBUTE_DESTEXPR = "destexpr";

    /**
     * The type of transfer. The value can be "bridge", "blind", or
     * "consultation".
     */
    public static final String ATTRIBUTE_TYPE = "type";

    /**
     * Determines whether the platform remains in the connection with the caller
     * and callee.
     * <p>
     * <dl>
     * <dt>bridge="true"</dt>
     *
     * <dd>
     * <p>
     * <i>Bridge transfer. </i> The platform adds the callee to the connection.
     * Document interpretation suspends until the transferred call terminates.
     * The platform remains in the connection for the duration of the
     * transferred call; listening during transfer is controlled by any included
     * &lt;grammar&gt;s.
     * </p>
     *
     * <p>
     * If the caller disconnects by going onhook or if the network disconnects
     * the caller, the platform throws a connection.disconnect.hangup event.
     * </p>
     *
     * <p>
     * If the connection is released for any other reason, that outcome is
     * reported in the name attribute (see the following table).
     * </p>
     * </dd>
     *
     * <dt>bridge="false"</dt>
     *
     * <dd>
     * <p>
     * <i>Blind transfer (default). </i> The platform redirects the caller to
     * the callee without remaining in the connection, and does not monitor the
     * outcome.
     * </p>
     *
     * <p>
     * The platform throws a connection.disconnect.transfer immediately,
     * regardless of whether the transfer was successful or not.
     * </p>
     * </dd>
     * </dl>
     * </p>
     */
    public static final String ATTRIBUTE_BRIDGE = "bridge";

    /**
     * The time to wait while trying to connect the call before returning the
     * noanswer condition. The value is a Time Designation. Only applies if
     * bridge is true. Default is platform specific.
     */
    public static final String ATTRIBUTE_CONNECTTIMEOUT = "connecttimeout";

    /**
     * The time that the call is allowed to last, or 0s if no limit is imposed.
     * The value is a Time Designation. Only applies if bridge is true.
     * Default is 0s.
     */
    public static final String ATTRIBUTE_MAXTIME = "maxtime";

    /**
     * The URI of audio source to play while the transfer attempt is in progress
     * (before far-end answer).
     */
    public static final String ATTRIBUTE_TRANSFERAUDIO = "transferaudio";

    /**
     * Application-to-application information. A string containing data sent to
     * an application on the far-end, available in the session variable
     * session.connection.aai.
     */
    public static final String ATTRIBUTE_AAI = "aai";

    /**
     * An ECMAScript expression yielding the AAI data.
     */
    public static final String ATTRIBUTE_AAIEXPR = "aaiexpr";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_AAI);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_AAIEXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_BRIDGE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_COND);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_CONNECTTIMEOUT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_DEST);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_DESTEXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_EXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXTIME);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAME);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TRANSFERAUDIO);
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
     * Construct a new transfer object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Transfer() {
        super(null);
    }

    /**
     * Construct a new transfer object.
     * @param node The encapsulated node.
     */
    Transfer(final Node node) {
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
        return new Transfer(n);
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
     * Retrieve the dest attribute.
     * @return Value of the dest attribute.
     * @see #ATTRIBUTE_DEST
     */
    public String getDest() {
        return getAttribute(ATTRIBUTE_DEST);
    }

    /**
     * Set the dest attribute.
     * @param dest Value of the dest attribute.
     * @see #ATTRIBUTE_DEST
     */
    public void setDest(final String dest) {
        setAttribute(ATTRIBUTE_DEST, dest);
    }

    /**
     * Retrieve the destexpr attribute.
     * @return Value of the destexpr attribute.
     * @see #ATTRIBUTE_DESTEXPR
     */
    public String getDestexpr() {
        return getAttribute(ATTRIBUTE_DESTEXPR);
    }

    /**
     * Set the destexpr attribute.
     * @param destexpr Value of the destexpr attribute.
     * @see #ATTRIBUTE_DESTEXPR
     */
    public void setDestexpr(final String destexpr) {
        setAttribute(ATTRIBUTE_DESTEXPR, destexpr);
    }

    /**
     * Retrieve the bridge attribute.
     * @return Value of the bridge attribute.
     * @see #ATTRIBUTE_BRIDGE
     */
    public String getBridge() {
        return getAttribute(ATTRIBUTE_BRIDGE);
    }

    /**
     * Set the bridge attribute.
     * @param bridge Value of the bridge attribute.
     * @see #ATTRIBUTE_BRIDGE
     */
    public void setBridge(final String bridge) {
        setAttribute(ATTRIBUTE_BRIDGE, bridge);
    }

    /**
     * Retrieve the connecttimeout attribute.
     * @return Value of the connecttimeout attribute.
     * @see #ATTRIBUTE_CONNECTTIMEOUT
     */
    public String getConnecttimeout() {
        return getAttribute(ATTRIBUTE_CONNECTTIMEOUT);
    }

    /**
     * Set the connecttimeout attribute.
     * @param connecttimeout Value of the connecttimeout attribute.
     * @see #ATTRIBUTE_CONNECTTIMEOUT
     */
    public void setConnecttimeout(final String connecttimeout) {
        setAttribute(ATTRIBUTE_CONNECTTIMEOUT, connecttimeout);
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
     * Retrieve the transferaudio attribute.
     * @return Value of the transferaudio attribute.
     * @see #ATTRIBUTE_TRANSFERAUDIO
     */
    public String getTransferaudio() {
        return getAttribute(ATTRIBUTE_TRANSFERAUDIO);
    }

    /**
     * Set the transferaudio attribute.
     * @param transferaudio Value of the transferaudio attribute.
     * @see #ATTRIBUTE_TRANSFERAUDIO
     */
    public void setTransferaudio(final String transferaudio) {
        setAttribute(ATTRIBUTE_TRANSFERAUDIO, transferaudio);
    }

    /**
     * Retrieve the aai attribute.
     * @return Value of the aai attribute.
     * @see #ATTRIBUTE_AAI
     */
    public String getAai() {
        return getAttribute(ATTRIBUTE_AAI);
    }

    /**
     * Set the aai attribute.
     * @param aai Value of the aai attribute.
     * @see #ATTRIBUTE_AAI
     */
    public void setAai(final String aai) {
        setAttribute(ATTRIBUTE_AAI, aai);
    }

    /**
     * Retrieve the aaiexpr attribute.
     * @return Value of the aaiexpr attribute.
     * @see #ATTRIBUTE_AAIEXPR
     */
    public String getAaiexpr() {
        return getAttribute(ATTRIBUTE_AAIEXPR);
    }

    /**
     * Set the aaiexpr attribute.
     * @param aaiexpr Value of the aaiexpr attribute.
     * @see #ATTRIBUTE_AAIEXPR
     */
    public void setAaiexpr(final String aaiexpr) {
        setAttribute(ATTRIBUTE_AAIEXPR, aaiexpr);
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
