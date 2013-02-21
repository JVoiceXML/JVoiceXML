/*
 * File:    $RCSfile: Break.java,v $
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

package org.jvoicexml.xml.ssml;

import java.util.ArrayList;
import java.util.Collection;

import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.w3c.dom.Node;

/**
 * The break element is an empty element that controls the pausing or other
 * prosodic boundaries between words. The use of the break element between
 * any pair of words is optional. If the element is not present between words,
 * the synthesis processor is expected to automatically determine a break
 * based on the linguistic context. In practice, the break element is most
 * often used to override the typical automatic behavior of a synthesis
 * processor.
 *
 * @author Steve Doyle
 * @version $Revision: 1.7 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Break
        extends AbstractSsmlNode implements VoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "break";

    /**
     * The time attribute is an optional attribute indicating the duration
     * of a pause to be inserted in the output in seconds or milliseconds.
     * It follows the time value format from the Cascading Style Sheets
     * Level 2 Recommendation, e.g. "250ms", "3s".
     */
    public static final String ATTRIBUTE_TIME = "time";

    /**
     *  The strength attribute is an optional attribute having one of the
     *  following values: "none", "x-weak", "weak", "medium" (default value),
     *  "strong", or "x-strong". This attribute is used to indicate the
     *  strength of the prosodic break in the speech output. The value
     *  "none" indicates that no prosodic break boundary should be outputted,
     *  which can be used to prevent a prosodic break which the processor
     *  would otherwise produce. The other values indicate monotonically
     *  non-decreasing (conceptually increasing) break strength between
     *  words. The stronger boundaries are typically accompanied by pauses.
     *  "x-weak" and "x-strong" are mnemonics for "extra weak" and
     *  "extra strong", respectively.
     */
    public static final String ATTRIBUTE_STRENGTH = "strength";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_STRENGTH);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TIME);
    }

    /**
     * Construct a new break object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Break() {
        super(null);
    }

    /**
     * Construct a new break object.
     * @param node The encapsulated node.
     */
    Break(final Node node) {
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
     * {@inheritDoc}
     */
    public XmlNode newInstance(final Node n) {
        return new Break(n);
    }

    /**
     * Retrieve the time attribute.
     * @return Value of the time attribute.
     * @see #ATTRIBUTE_TIME
     */
    public String getTime() {
        return getAttribute(ATTRIBUTE_TIME);
    }

    /**
     * Set the time attribute.
     * @param time Value of the time attribute.
     * @see #ATTRIBUTE_TIME
     */
    public void setTime(final String time) {
        setAttribute(ATTRIBUTE_TIME, time);
    }

    /**
     * Retrieve the strength attribute.
     * @return Value of the strength attribute.
     * @see #ATTRIBUTE_STRENGTH
     */
    public String getStrength() {
        return getAttribute(ATTRIBUTE_STRENGTH);
    }

    /**
     * Set the strength attribute.
     * @param strength Value of the strength attribute.
     * @see #ATTRIBUTE_STRENGTH
     */
    public void setStrength(final String strength) {
        setAttribute(ATTRIBUTE_STRENGTH, strength);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean canContainChild(final String tagName) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
