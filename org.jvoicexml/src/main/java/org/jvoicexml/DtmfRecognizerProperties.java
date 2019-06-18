/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

import java.util.Map;

import org.jvoicexml.xml.TimeParser;

/**
 * Generic DTMF recognizer properties as described in
 * <a href="http://www.w3.org/TR/voicexml20#dml6.3.3">
 * http://www.w3.org/TR/voicexml20#dml6.3.3</a>. See there for a more detailed
 * description of the parameters.
 * <p>
 * This class must be extended to specify platform specific values.
 * </p>
 * @author Dirk Schnelle-Walka
 * @since 0.7.5
 */
public class DtmfRecognizerProperties {
    /** Name of the <code>no-input-timeout</code> property. */
    public static final String NO_INPUT_TIMEOUT = "no-input-timeout";

    /** Name of the <code>interdigittimeout</code> property. */
    public static final String PROPERTY_INTERDIGIT_TIMEOUT
        = "interdigittimeout";

    /** Name of the <code>termtimeout</code> property. */
    public static final String PROPERTY_TERM_TIMEOUT = "termtimeout";

    /** Name of the <code>termchar</code> property. */
    public static final String PROPERTY_TERM_CHAR = "termchar";

    /** The default value for the terminating timeout. */
    public static final String DEFAULT_TERM_TIMEOUT = "0s";

    /** The default value for the terminating character. */
    public static final char DEFAULT_TERM_CHAR = '#';

    /** The default no-input timeout in msec. */
    public static final int DEFAULT_NO_INPUT_TIMEOUT = 30000;

    /**
     * The inter-digit timeout value to use when recognizing DTMF input.
     */
    private long interdigittimeout;

    /**
     * The terminating timeout to use when recognizing DTMF input.
     */
    private long termtimeout;

    /**
     * The terminating DTMF character for DTMF input recognition.
     */
    private char termchar;

    /** The no input timeout. */
    private long noInputTimeout;

    /**
     * Constructs a new object.
     */
    public DtmfRecognizerProperties() {
        setTermtimeout(DEFAULT_TERM_TIMEOUT);
        termchar = DEFAULT_TERM_CHAR;
    }

    /**
     * Retrieves the DTMF recognizer properties from the given map.
     * @param props map with current properties
     * @since 0.7.5
     */
    public final void setProperties(final Map<String, String> props) {
        final String propInterdigittimeout =
            props.get(PROPERTY_INTERDIGIT_TIMEOUT);
        if (propInterdigittimeout != null) {
            setInterdigittimeout(propInterdigittimeout);
        }
        final String propTermtimeout = props.get(PROPERTY_TERM_TIMEOUT);
        if (propTermtimeout != null) {
            setTermtimeout(propTermtimeout);
        }
        final String propTermchar = props.get(PROPERTY_TERM_CHAR);
        if ((propTermchar != null) && !propTermchar.isEmpty()) {
            termchar = propTermchar.charAt(0);
        }
        setEnhancedProperties(props);
    }

    /**
     * May be used to set custom properties if this class is extended.
     * @param props map with current properties.
     * @since 0.7.5
     */
    protected void setEnhancedProperties(final Map<String, String> props) {
    }
    
    /**
     * Retrieves the inter-digit timeout value to use when recognizing DTMF
     * input.
     * @return the inter-digit timeout
     */
    public final long getInterdigittimeoutAsMsec() {
        return interdigittimeout;
    }

    /**
     * Sets the inter-digit timeout value to use when recognizing DTMF
     * input.
     * @param value the inter-digit timeout to set as a time designation
     */
    public final void setInterdigittimeout(final String value) {
        final TimeParser parser = new TimeParser(value);
        interdigittimeout = parser.parse();
    }

    /**
     * Retrieves the terminating timeout to use when recognizing DTMF input.
     * @return the terminating timeout
     */
    public final long getTermtimeoutAsMsec() {
        return termtimeout;
    }

    /**
     * Sets the terminating timeout to use when recognizing DTMF input.
     * @param value the terminating timeout to set
     */
    public final void setTermtimeout(final String value) {
        final TimeParser parser = new TimeParser(value);
        termtimeout = parser.parse();
    }

    /**
     * Retrieves the terminating DTMF character for DTMF input recognition.
     * @return the terminating DTMF character, maybe 0, if no terminating
     *          character has been defined
     */
    public final char getTermchar() {
        return termchar;
    }

    /**
     * Sets the terminating DTMF character for DTMF input recognition.
     * @param value the terminating DTMF character to set
     */
    public final void setTermchar(final char value) {
        termchar = value;
    }

    /**
    /* Retrieves the duration when recognition is started and there is no speech
     * detected.
     * @return the no input timeout
     */
    public final long getNoInputTimeoutAsMsec() {
        return noInputTimeout;
    }

    /**
    /* Sets the the duration when recognition is started and there is no speech
     * detected.
     * @param value the no input timeout to set as a time designation
     */
    public final void setNoInputTimeout(final String value) {
        final TimeParser parser = new TimeParser(value);
        noInputTimeout = parser.parse();
    }

    /**
    /* Sets the the duration when recognition is started and there is no speech
     * detected.
     * @param value the no input timeout to set as a time designation
     */
    public final void setNoInputTimeout(final long value) {
        noInputTimeout = value;
    }
}
