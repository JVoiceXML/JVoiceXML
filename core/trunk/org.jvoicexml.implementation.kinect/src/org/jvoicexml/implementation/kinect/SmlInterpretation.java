/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.implementation.kinect;

/**
 * Part of an interpretation result, consisting of a tag, an associated value
 * and a confidence value.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
final class SmlInterpretation {
    /** The tag. */
    private final String tag;
    /** The value for the tag. */
    private String value;
    /** Confidence in the result. */
    private final float confidence;

    /**
     * Constructs a new object.
     * @param tag the tag
     * @param conf confidence n the result
     */
    SmlInterpretation(final String tag, final float conf) {
        this.tag = tag;
        confidence = conf;
    }

    /**
     * Constructs a new object.
     * @param tag the tag
     * @param value the associated value
     * @param conf confidence n the result
     */
    SmlInterpretation(final String tag, final String value, final float conf) {
        this.tag = tag;
        this.value = value;
        confidence = conf;
    }

    /**
     * Retrieves the tag.
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets the associated value.
     * @param val the associated value.
     */
    public void setValue(final String val) {
        value = val;
    }

    /**
     * Retrieves the associated value.
     * @return the associated value
     */
    public String getValue() {
        if (value == null) {
            return null;
        }
        return value.trim();
    }

    /**
     * Appends the given string to the value.
     * @param str the string to append
     */
    public void appendValue(final String str) {
        if (value == null) {
            value = str;
        } else {
            value += str;
        }
    }

    /**
     * Retrieves the confidence value.
     * @return confidence value.
     */
    public float getConfidence() {
        return confidence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append(getClass().getCanonicalName());
        str.append('[');
        str.append(tag);
        str.append(',');
        str.append(getValue());
        str.append(',');
        str.append(confidence);
        str.append(']');
        return str.toString();
    }
}
