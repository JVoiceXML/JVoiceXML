/*
 * File:    $RCSfile: Createcall.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 *
 */

package org.jvoicexml.xml.ccxml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.XmlNode;
import org.w3c.dom.Node;

/**
 * A CCXML document can attempt to place an outgoing call with
 * <code>&lt;createcall&gt;</code>. This element will instruct the
 * platform to allocate a Connection and attempt to place an
 * outgoing call to a specified address. The element is non-blocking,
 * and the CCXML document is immediately free to perform other tasks,
 * such as initiating dialog interaction with another caller. The CCXML
 * interpreter will receive an asynchronous event when the call attempt
 * is completed. An <code>&lt;eventprocessor&gt;</code>
 * <code>&lt;transition&gt;</code> block can handle this event and
 * perform further call control, such as conferencing. If the call
 * was successfully placed, the transition block can also initiate
 * a dialog interaction with the called party.
 *
 * @author Steve Doyle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Createcall
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "createcall";

    /**
     * An ECMAScript expression which returns a string of
     * application-to-application information to be passed to the
     * destination endpoint when establishing the connection.
     * Note: Even if an implementation platform accepts the aai
     * data, certain protocols and network elements may prevent
     * the transmission to the target endpoint. If the platform
     * does not support the transmission of aai data it should
     * raise a connection.progressing event and indicate that the
     * use of aai is not supported.
     */
    public static final String ATTRIBUTE_AAI = "aai";

    /**
     * An ECMAScript expression which returns a string defining the
     * caller identity to be used when making the outbound connection.
     * The format of this information is protocol and platform specific
     * but might consist of a telephone URI, as described in [RFC2806]
     * or a SIP URI as described in [RFC3261].
     * Note: An implementation platform is not required to use the
     * specified data and certain protocols and network elements may
     * prevent its use. If the platform does not support the specification
     * of callerid it should raise a connection.progressing event and
     * indicate that the use of callerid is not supported.
     */
    public static final String ATTRIBUTE_CALLERID = "callerid";

    /**
     * An ECMAScript expression which returns a string defining the caller
     * identity to be used when making the outbound connection. The format
     * of this information is protocol and platform specific but might
     * consist of a telephone URI, as described in [RFC2806] or a SIP
     * URI as described in [RFC3261].
     * Note: An implementation platform is not required to use the specified
     * data and certain protocols and network elements may prevent its use.
     * If the platform does not support the specification of callerid it
     * should raise a connection.progressing event and indicate that the
     * use of callerid is not supported.
     */
    public static final String ATTRIBUTE_CONNECTIONID = "connectionid";

    /**
     * An ECMAScript expression which returns a string that is the target
     * of the outbound telephone call. A platform must support a telephone
     * URI, as described in [RFC2806] or a SIP URI as described in [RFC3261].
     */
    public static final String ATTRIBUTE_DEST = "dest";

    /**
     * ECMAScript expression that returns an ECMAScript object  The
     * ECMAScript object returned contains information which may be
     * used by the implementing platform when establishing the outbound
     * connection. This information may consist of protocol-specific
     * parameters, protocol selection guidelines, or routing hints.
     * Note: The meaning of these hints is specific to the implementing
     * platform.
     */
    public static final String ATTRIBUTE_HINTS = "hints";

    /**
     * An ECMAScript expression that defines the direction of the media flow
     * between the newly created connection, and the existing
     * connection/conference/dialog referenced by joinid.
     */
    public static final String ATTRIBUTE_JOINDIRECTION = "joindirection";

    /**
     * An ECMAScript expression that identifies a connection, conference, or
     * dialog ID that the new call will be joined to. This is equivalent, from
     * the perspective of the CCXML application, to performing a
     * <code>&lt;join&gt;</code> immediately following the
     * <code>&lt;createcall&gt;</code> except that no events specific to
     * the join will be generated. However, platforms may use knowledge about
     * the connection/conference/dialog to which the new call will be connected
     * to optimize the call creation process.
     */
    public static final String ATTRIBUTE_JOINID = "joinid";

    /**
     * The character string returned is interpreted as a time interval. This
     * interval begins when createcall is executed. The createcall will fail if
     * not completed by the end of this interval. A completion is defined as the
     * call getting to a CONNECTED state as signaled by a connection.connected
     * event. A failed createcall will return the connection.failed event.
     */
    public static final String ATTRIBUTE_TIMEOUT = "timeout";

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
        ATTRIBUTE_NAMES.add(ATTRIBUTE_CALLERID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_CONNECTIONID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_DEST);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_HINTS);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_JOINDIRECTION);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_JOINID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TIMEOUT);
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

    }

    /**
     * Construct a new createcall object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Createcall() {
        super(null);
    }

    /**
     * Construct a new createcall object.
     * @param node The encapsulated node.
     */
    Createcall(final Node node) {
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
    public XmlNode newInstance(final Node n) {
        return new Createcall(n);
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
     * Retrieve the callerid attribute.
     * @return Value of the callerid attribute.
     * @see #ATTRIBUTE_CALLERID
     */
    public String getCallerid() {
        return getAttribute(ATTRIBUTE_CALLERID);
    }

    /**
     * Set the callerid attribute.
     * @param callerid Value of the callerid attribute.
     * @see #ATTRIBUTE_CALLERID
     */
    public void setCallerid(final String callerid) {
        setAttribute(ATTRIBUTE_CALLERID, callerid);
    }

    /**
     * Retrieve the connectionid attribute.
     * @return Value of the connectionid attribute.
     * @see #ATTRIBUTE_CONNECTIONID
     */
    public String getConnectionid() {
        return getAttribute(ATTRIBUTE_CONNECTIONID);
    }

    /**
     * Set the connectionid attribute.
     * @param connectionid Value of the connectionid attribute.
     * @see #ATTRIBUTE_CONNECTIONID
     */
    public void setConnectionid(final String connectionid) {
        setAttribute(ATTRIBUTE_CONNECTIONID, connectionid);
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
     * Retrieve the hints attribute.
     * @return Value of the hints attribute.
     * @see #ATTRIBUTE_HINTS
     */
    public String getHints() {
        return getAttribute(ATTRIBUTE_HINTS);
    }

    /**
     * Set the hints attribute.
     * @param hints Value of the hints attribute.
     * @see #ATTRIBUTE_HINTS
     */
    public void setHints(final String hints) {
        setAttribute(ATTRIBUTE_HINTS, hints);
    }

    /**
     * Retrieve the joindirection attribute.
     * @return Value of the joindirection attribute.
     * @see #ATTRIBUTE_JOINDIRECTION
     */
    public String getJoindirection() {
        return getAttribute(ATTRIBUTE_JOINDIRECTION);
    }

    /**
     * Set the joindirection attribute.
     * @param joindirection Value of the joindirection attribute.
     * @see #ATTRIBUTE_JOINDIRECTION
     */
    public void setJoindirection(final String joindirection) {
        setAttribute(ATTRIBUTE_JOINDIRECTION, joindirection);
    }

    /**
     * Retrieve the joinid attribute.
     * @return Value of the joinid attribute.
     * @see #ATTRIBUTE_JOINID
     */
    public String getJoinid() {
        return getAttribute(ATTRIBUTE_JOINID);
    }

    /**
     * Set the joinid attribute.
     * @param joinid Value of the joinid attribute.
     * @see #ATTRIBUTE_JOINID
     */
    public void setJoinid(final String joinid) {
        setAttribute(ATTRIBUTE_JOINID, joinid);
    }

    /**
     * Retrieve the timeout attribute.
     * @return Value of the timeout attribute.
     * @see #ATTRIBUTE_TIMEOUT
     */
    public String getTimeout() {
        return getAttribute(ATTRIBUTE_TIMEOUT);
    }

    /**
     * Set the timeout attribute.
     * @param timeout Value of the timeout attribute.
     * @see #ATTRIBUTE_TIMEOUT
     */
    public void setTimeout(final String timeout) {
        setAttribute(ATTRIBUTE_TIMEOUT, timeout);
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
