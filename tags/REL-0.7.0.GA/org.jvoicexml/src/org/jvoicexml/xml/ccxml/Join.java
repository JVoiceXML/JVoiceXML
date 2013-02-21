/*
 * File:    $RCSfile: Join.java,v $
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
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * A CCXML document can attempt to create a bridge between any two connections,
 * conferences, or dialogs using <code>&lt;join&gt;</code>. This element
 * instructs the implementation to bridge the connections, conferences, or
 * dialogs specified using the id1 and id2 attributes in accordance with media
 * options specified by the other attributes of <code>&lt;join&gt;</code>.
 * The successful execution of<code>&lt;join&gt;</code> will result in the
 * generation of a conference.joined event. If for any reason the implementation
 * is unable to create the bridge using the specified options it MUST fail with
 * a error.conference.join event.
 *
 * Joining two objects that are owned by separate CCXML sessions will result in
 * the generation of a conference.joined to each of the sessions. However if the
 * implementation is unable to join the objects an error.conference.join will
 * only be sent to the session issued the <code>&lt;join&gt;</code>.
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
public final class Join
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "join";

    /**
     * An ECMAScript Boolean expression that tells the conference mixer if it
     * should use AGC to determine the input gain for a leg. If a platform does
     * not support AGC, it should ignore this attribute.
     */
    public static final String ATTRIBUTE_AUTOINPUTGAIN = "autoinputgain";

    /**
     * An ECMAScript boolean expression that tells the conference mixer if it
     * should use AGC to determine the output gain for a leg. If a platform does
     * not support AGC, it should ignore this attribute.
     */
    public static final String ATTRIBUTE_AUTOOUTPUTGAIN = "autooutputgain";

    /**
     * An ECMAScript Boolean expression that tells the conference mixer if it
     * should attempt to remove detected DTMF tones. If a platform does not
     * support removal of DTMF tones, it should ignore this attribute.
     */
    public static final String ATTRIBUTE_DTMFCLAMP = "dtmfclamp";

    /**
     * An ECMAScript expression that returns a character string equal to "half"
     * or "full", which defines the direction of the media flow between id1
     * resource and id2 resource. Refer to the discussion of bridging in Section
     * 10.4 . The duplex attribute determines whether the join will establish a
     * half-duplex (unidirectional) or full-duplex (bi-directional) bridge.
     */
    public static final String ATTRIBUTE_DUPLEX = "duplex";

    /**
     * An ECMAScript expression that returns a character string that is used to
     * play a tone or a custom wav file to the conference participants when
     * another caller joins.
     */
    public static final String ATTRIBUTE_ENTERTONE = "entertone";

    /**
     * An ECMAScript expression that returns a character string that is used to
     * play a tone or a custom wav file to the conference participants when
     * another caller exits.
     */
    public static final String ATTRIBUTE_EXITTONE = "exittone";

    /**
     * The ECMAScript object returned contains information which may be used by
     * the implementing platform or passed to the network when the two specified
     * Connections, Dialogs or Conferences (id1 and id2) are joined. This
     * information may consist of protocol-specific parameters. Note: The
     * meaning of these hints is specific to the implementing platform and the
     * underlying protocol.
     */
    public static final String ATTRIBUTE_HINTS = "hints";

    /**
     * The ECMAScript object returned contains information which may be used by
     * the implementing platform or passed to the network when the two specified
     * Connections, Dialogs or Conferences (id1 and id2) are joined. This
     * information may consist of protocol-specific parameters. Note: The
     * meaning of these hints is specific to the implementing platform and the
     * underlying protocol.
     */
    public static final String ATTRIBUTE_ID1 = "id1";

    /**
     * The ECMAScript object returned contains information which may be used by
     * the implementing platform or passed to the network when the two specified
     * Connections, Dialogs or Conferences (id1 and id2) are joined. This
     * information may consist of protocol-specific parameters. Note: The
     * meaning of these hints is specific to the implementing platform and the
     * underlying protocol.
     */
    public static final String ATTRIBUTE_ID2 = "id2";

    /**
     * An ECMAScript Boolean expression that tells the conference mixer if it
     * should attempt to remove loud single-frequency tones from the audio
     * stream. If a platform does not support removal of tones, it should ignore
     * this attribute.
     */
    public static final String ATTRIBUTE_TONECLAMP = "toneclamp";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_AUTOINPUTGAIN);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_AUTOOUTPUTGAIN);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_DTMFCLAMP);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_DUPLEX);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_ENTERTONE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_EXITTONE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_HINTS);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_ID1);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_ID2);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TONECLAMP);
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
     * Construct a new join object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Join() {
        super(null);
    }

    /**
     * Construct a new join object.
     * @param node The encapsulated node.
     */
    Join(final Node node) {
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
    private Join(final Node n,
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
        return new Join(n, factory);
    }

    /**
     * Retrieve the autoinputgain attribute.
     * @return Value of the autoinputgain attribute.
     * @see #ATTRIBUTE_AUTOINPUTGAIN
     */
    public String getAutoinputgain() {
        return getAttribute(ATTRIBUTE_AUTOINPUTGAIN);
    }

    /**
     * Set the autoinputgain attribute.
     * @param autoinputgain Value of the autoinputgain attribute.
     * @see #ATTRIBUTE_AUTOINPUTGAIN
     */
    public void setAutoinputgain(final String autoinputgain) {
        setAttribute(ATTRIBUTE_AUTOINPUTGAIN, autoinputgain);
    }

    /**
     * Retrieve the autooutputgain attribute.
     * @return Value of the autooutputgain attribute.
     * @see #ATTRIBUTE_AUTOOUTPUTGAIN
     */
    public String getAutooutputgain() {
        return getAttribute(ATTRIBUTE_AUTOOUTPUTGAIN);
    }

    /**
     * Set the autooutputgain attribute.
     * @param autooutputgain Value of the autooutputgain attribute.
     * @see #ATTRIBUTE_AUTOOUTPUTGAIN
     */
    public void setAutooutputgain(final String autooutputgain) {
        setAttribute(ATTRIBUTE_AUTOOUTPUTGAIN, autooutputgain);
    }

    /**
     * Retrieve the dtmfclamp attribute.
     * @return Value of the dtmfclamp attribute.
     * @see #ATTRIBUTE_DTMFCLAMP
     */
    public String getDtmfclamp() {
        return getAttribute(ATTRIBUTE_DTMFCLAMP);
    }

    /**
     * Set the dtmfclamp attribute.
     * @param dtmfclamp Value of the dtmfclamp attribute.
     * @see #ATTRIBUTE_DTMFCLAMP
     */
    public void setDtmfclamp(final String dtmfclamp) {
        setAttribute(ATTRIBUTE_DTMFCLAMP, dtmfclamp);
    }

    /**
     * Retrieve the duplex attribute.
     * @return Value of the duplex attribute.
     * @see #ATTRIBUTE_DUPLEX
     */
    public String getDuplex() {
        return getAttribute(ATTRIBUTE_DUPLEX);
    }

    /**
     * Set the duplex attribute.
     * @param duplex Value of the duplex attribute.
     * @see #ATTRIBUTE_DUPLEX
     */
    public void setDuplex(final String duplex) {
        setAttribute(ATTRIBUTE_DUPLEX, duplex);
    }

    /**
     * Retrieve the entertone attribute.
     * @return Value of the entertone attribute.
     * @see #ATTRIBUTE_ENTERTONE
     */
    public String getEntertone() {
        return getAttribute(ATTRIBUTE_ENTERTONE);
    }

    /**
     * Set the entertone attribute.
     * @param entertone Value of the entertone attribute.
     * @see #ATTRIBUTE_ENTERTONE
     */
    public void setEntertone(final String entertone) {
        setAttribute(ATTRIBUTE_ENTERTONE, entertone);
    }

    /**
     * Retrieve the exittone attribute.
     * @return Value of the exittone attribute.
     * @see #ATTRIBUTE_EXITTONE
     */
    public String getExittone() {
        return getAttribute(ATTRIBUTE_EXITTONE);
    }

    /**
     * Set the exittone attribute.
     * @param exittone Value of the exittone attribute.
     * @see #ATTRIBUTE_EXITTONE
     */
    public void setExittone(final String exittone) {
        setAttribute(ATTRIBUTE_EXITTONE, exittone);
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
     * Retrieve the id1 attribute.
     * @return Value of the id1 attribute.
     * @see #ATTRIBUTE_ID1
     */
    public String getId1() {
        return getAttribute(ATTRIBUTE_ID1);
    }

    /**
     * Set the id1 attribute.
     * @param id1 Value of the id1 attribute.
     * @see #ATTRIBUTE_ID1
     */
    public void setId1(final String id1) {
        setAttribute(ATTRIBUTE_ID1, id1);
    }

    /**
     * Retrieve the id2 attribute.
     * @return Value of the id2 attribute.
     * @see #ATTRIBUTE_ID2
     */
    public String getId2() {
        return getAttribute(ATTRIBUTE_ID2);
    }

    /**
     * Set the id2 attribute.
     * @param id2 Value of the id2 attribute.
     * @see #ATTRIBUTE_ID2
     */
    public void setId2(final String id2) {
        setAttribute(ATTRIBUTE_ID2, id2);
    }

    /**
     * Retrieve the toneclamp attribute.
     * @return Value of the toneclamp attribute.
     * @see #ATTRIBUTE_TONECLAMP
     */
    public String getToneclamp() {
        return getAttribute(ATTRIBUTE_TONECLAMP);
    }

    /**
     * Set the toneclamp attribute.
     * @param toneclamp Value of the toneclamp attribute.
     * @see #ATTRIBUTE_TONECLAMP
     */
    public void setToneclamp(final String toneclamp) {
        setAttribute(ATTRIBUTE_TONECLAMP, toneclamp);
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
