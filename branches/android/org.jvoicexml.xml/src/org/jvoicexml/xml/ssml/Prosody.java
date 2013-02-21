/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/ssml/Prosody.java $
 * Version: $LastChangedRevision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Set;

import org.jvoicexml.xml.NodeHelper;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.TextContainer;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.jvoicexml.xml.vxml.Enumerate;
import org.jvoicexml.xml.vxml.Value;
import org.w3c.dom.Node;

/**
 * The prosody element permits control of the pitch, speaking rate and volume
 * of the speech output.
 *
 * @author Steve Doyle
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2325 $
 */
public final class Prosody
        extends AbstractSsmlNode implements VoiceXmlNode, TextContainer {

    /** Name of the tag. */
    public static final String TAG_NAME = "prosody";

    /**
     * The baseline pitch for the contained text. Although the exact meaning
     * of "baseline pitch" will vary across synthesis processors,
     * increasing/decreasing this value will typically increase/decrease
     * the approximate pitch of the output. Legal values are: a number
     * followed by "Hz", a relative change or "x-low", "low", "medium",
     * "high", "x-high", or "default". Labels "x-low" through "x-high"
     * represent a sequence of monotonically non-decreasing pitch levels.
     */
    public static final String ATTRIBUTE_PITCH = "pitch";

    /** Pitch level <code>x-low</code>. */
    public static final String PITCH_X_LOW = "x-low";

    /** Pitch level <code>low</code>. */
    public static final String PITCH_LOW = "low";

    /** Pitch level <code>medium</code>. */
    public static final String PITCH_MEDIUM = "medium";

    /** Pitch level <code>high</code>. */
    public static final String PITCH_HIGH = "high";

    /** Pitch level <code>x-high</code>. */
    public static final String PITCH_X_HIGH = "x-high";

    /** Pitch level <code>default</code>. */
    public static final String PITCH_DEFAULT = "default";

    /**
     * Sets the actual pitch contour for the contained text.
     */
    public static final String ATTRIBUTE_CONTOUR = "contour";

    /**
     * The pitch range (variability) for the contained text. Although the
     * exact meaning of "pitch range" will vary across synthesis processors,
     * increasing/decreasing this value will typically increase/decrease the
     * dynamic range of the output pitch. Legal values are: a number followed
     * by "Hz", a relative change or "x-low", "low", "medium", "high",
     * "x-high", or "default". Labels "x-low" through "x-high" represent a
     * sequence of monotonically non-decreasing pitch ranges.
     */
    public static final String ATTRIBUTE_RANGE = "range";

    /** Range level <code>x-low</code>. */
    public static final String RANGE_X_LOW = "x-low";

    /** Range level <code>low</code>. */
    public static final String RANGE_LOW = "low";

    /** Range level <code>medium</code>. */
    public static final String RANGE_MEDIUM = "medium";

    /** Range level <code>high</code>. */
    public static final String RANGE_HIGH = "high";

    /** Range level <code>x-high</code>. */
    public static final String RANGE_X_HIGH = "x-high";

    /** Range level <code>default</code>. */
    public static final String RANGE_DEFAULT = "default";

    /**
     * A change in the speaking rate for the contained text. Legal values
     * are: a relative change or "x-slow", "slow", "medium", "fast",
     * "x-fast", or "default". Labels "x-slow" through "x-fast" represent
     * a sequence of monotonically non-decreasing speaking rates. When a
     * number is used to specify a relative change it acts as a multiplier
     * of the default rate. For example, a value of 1 means no change in
     * speaking rate, a value of 2 means a speaking rate twice the default
     * rate, and a value of 0.5 means a speaking rate of half the default
     * rate. The default rate for a voice depends on the language and dialect
     * and on the personality of the voice. The default rate for a voice should
     * be such that it is experienced as a normal speaking rate for the voice
     * when reading aloud text. Since voices are processor-specific, the
     * default rate will be as well.
     */
    public static final String ATTRIBUTE_RATE = "rate";

    /** Rate level <code>x-slow</code>. */
    public static final String RATE_X_SLOW = "x-slow";

    /** Rate level <code>slow</code>. */
    public static final String RATE_SLOW = "slow";

    /** Rate level <code>medium</code>. */
    public static final String RATE_MEDIUM = "medium";

    /** Rate level <code>fast</code>. */
    public static final String RATE_FAST = "fast";

    /** Rate level <code>x-fast</code>. */
    public static final String RATE_X_FAST = "x-fast";

    /** Rate level <code>default</code>. */
    public static final String RATE_DEFAULT = "default";

    /**
     * A value in seconds or milliseconds for the desired time to take to
     * read the element contents. Follows the time value format from the
     * Cascading Style Sheet Level 2 Recommendation, e.g. "250ms", "3s".
     */
    public static final String ATTRIBUTE_DURATION = "duration";

    /**
     * The volume for the contained text in the range 0.0 to 100.0
     * (higher values are louder and specifying a value of zero is
     * equivalent to specifying "silent"). Legal values are: number,
     * a relative change or "silent", "x-soft", "soft", "medium",
     * "loud", "x-loud", or "default". The volume scale is linear
     * amplitude. The default is 100.0. Labels "silent" through "x-loud"
     * represent a sequence of monotonically non-decreasing volume levels.
     */
    public static final String ATTRIBUTE_VOLUME = "volume";

    /** Volume level <code>silent</code>. */
    public static final String VOLUME_SILENT = "silent";

    /** Volume level <code>x-soft</code>. */
    public static final String VOLUME_X_SOFT = "x-soft";

    /** Volume level <code>soft</code>. */
    public static final String VOLUME_SOFT = "soft";

    /** Volume level <code>medium</code>. */
    public static final String VOLUME_MEDIUM = "medium";

    /** Volume level <code>loud</code>. */
    public static final String VOLUME_LOUD = "loud";

    /** Volume level <code>x-loud</code>. */
    public static final String VOLUME_X_LOUD = "x-loud";

    /** Volume level <code>default</code>. */
    public static final String VOLUME_DEFAULT = "default";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_CONTOUR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_DURATION);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_PITCH);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_RANGE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_RATE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_VOLUME);
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

        CHILD_TAGS.add(Audio.TAG_NAME);
        CHILD_TAGS.add(Enumerate.TAG_NAME);
        CHILD_TAGS.add(Value.TAG_NAME);
        CHILD_TAGS.add(Break.TAG_NAME);
        CHILD_TAGS.add(Emphasis.TAG_NAME);
        CHILD_TAGS.add(Mark.TAG_NAME);
        CHILD_TAGS.add(Phoneme.TAG_NAME);
        CHILD_TAGS.add(Prosody.TAG_NAME);
        CHILD_TAGS.add(SayAs.TAG_NAME);
        CHILD_TAGS.add(Voice.TAG_NAME);
        CHILD_TAGS.add(Sub.TAG_NAME);
        CHILD_TAGS.add(P.TAG_NAME);
        CHILD_TAGS.add(S.TAG_NAME);
    }

    /**
     * Construct a new prosody object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Prosody() {
        super(null);
    }

    /**
     * Construct a new prosody object.
     * @param node The encapsulated node.
     */
    Prosody(final Node node) {
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
    private Prosody(final Node n,
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
        return new Prosody(n, factory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Text addText(final String text) {
        return NodeHelper.addText(this, text);
    }

    /**
     * Retrieve the pitch attribute.
     * @return Value of the pitch attribute.
     * @see #ATTRIBUTE_PITCH
     */
    public String getPitch() {
        return getAttribute(ATTRIBUTE_PITCH);
    }

    /**
     * Retrieves the rate attribute as a float.
     * @return Value of the rate attribute, 100 if the rate is
     * not configured.
     * @see #ATTRIBUTE_RATE
     * @since 0.7.2
     */
    public float getPitchFloat() {
        final String value = getAttribute(ATTRIBUTE_PITCH);
        if (value == null) {
            // TODO handle this case
            throw new IllegalArgumentException("Pitch is not defined!");
        }
        return HertzParser.parse(value);
    }

    /**
     * Set the pitch attribute.
     * @param pitch Value of the pitch attribute.
     * @see #ATTRIBUTE_PITCH
     */
    public void setPitch(final String pitch) {
        setAttribute(ATTRIBUTE_PITCH, pitch);
    }

    /**
     * Set the pitch attribute.
     * @param pitch Value of the pitch attribute in Hertz.
     * @see #ATTRIBUTE_PITCH
     * @since 0.7.2
     */
    public void setPitch(final float pitch) {
        final String value = pitch + "Hz";
        setAttribute(ATTRIBUTE_PITCH, value);
    }

    /**
     * Retrieve the contour attribute.
     * @return Value of the contour attribute.
     * @see #ATTRIBUTE_CONTOUR
     */
    public String getContour() {
        return getAttribute(ATTRIBUTE_CONTOUR);
    }

    /**
     * Set the contour attribute.
     * @param contour Value of the contour attribute.
     * @see #ATTRIBUTE_CONTOUR
     */
    public void setContour(final String contour) {
        setAttribute(ATTRIBUTE_CONTOUR, contour);
    }

    /**
     * Retrieve the range attribute.
     * @return Value of the range attribute.
     * @see #ATTRIBUTE_RANGE
     */
    public String getRange() {
        return getAttribute(ATTRIBUTE_RANGE);
    }

    /**
     * Set the range attribute.
     * @param range Value of the range attribute.
     * @see #ATTRIBUTE_RANGE
     */
    public void setRange(final String range) {
        setAttribute(ATTRIBUTE_RANGE, range);
    }

    /**
     * Retrieve the rate attribute.
     * @return Value of the rate attribute.
     * @see #ATTRIBUTE_RATE
     */
    public String getRate() {
        return getAttribute(ATTRIBUTE_RATE);
    }

    /**
     * Retrieves the rate attribute as a float.
     * @return Value of the rate attribute, 100 if the rate is
     * not configured.
     * @see #ATTRIBUTE_RATE
     * @since 0.7.2
     */
    public float getRateFloat() {
        final String value = getAttribute(ATTRIBUTE_RATE);
        if (value == null) {
            return 100.0f;
        }
        return PercentageParser.parse(value);
    }

    /**
     * Set the rate attribute.
     * @param rate Value of the rate attribute.
     * @see #ATTRIBUTE_RATE
     */
    public void setRate(final String rate) {
        setAttribute(ATTRIBUTE_RATE, rate);
    }

    /**
     * Set the rate attribute.
     * @param rate Value of the rate attribute.
     * @see #ATTRIBUTE_RATE
     * @since 0.7.2
     */
    public void setRate(final float rate) {
        final String value = rate + "%";
        setAttribute(ATTRIBUTE_RATE, value);
    }

    /**
     * Retrieve the duration attribute.
     * @return Value of the duration attribute.
     * @see #ATTRIBUTE_DURATION
     */
    public String getDuration() {
        return getAttribute(ATTRIBUTE_DURATION);
    }

    /**
     * Set the duration attribute.
     * @param duration Value of the duration attribute.
     * @see #ATTRIBUTE_DURATION
     */
    public void setDuration(final String duration) {
        setAttribute(ATTRIBUTE_DURATION, duration);
    }

    /**
     * Retrieve the volume attribute.
     * @return Value of the volume attribute.
     * @see #ATTRIBUTE_VOLUME
     */
    public String getVolume() {
        return getAttribute(ATTRIBUTE_VOLUME);
    }

    /**
     * Set the volume attribute.
     * @param volume Value of the volume attribute.
     * @see #ATTRIBUTE_VOLUME
     */
    public void setVolume(final String volume) {
        setAttribute(ATTRIBUTE_VOLUME, volume);
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
