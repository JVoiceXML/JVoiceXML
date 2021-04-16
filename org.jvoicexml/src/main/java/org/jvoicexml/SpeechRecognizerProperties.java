/*7 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011-2021 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.xml.vxml.Prompt;

/**
 * Generic speech recognizer properties as described in
 * <a href="http://www.w3.org/TR/voicexml20#dml6.3.2">
 * http://www.w3.org/TR/voicexml20#dml6.3.2</a>. See there for a more detailed
 * description of the parameters.
 * <p>
 * This class must be extended to specify platform specific values.
 * </p>
 * @author Dirk Schnelle-Walka
 * @since 0.7.5
 */
public class SpeechRecognizerProperties {
    /** Name of the <code>timeout</code> property from the last prompt. */
    public static final String PROPERTY_TIMEOUT = Prompt.ATTRIBUTE_TIMEOUT;

    /** Name of the <code>confidencelevel</code> property. */
    public static final String PROPERTY_CONFIDENCE_LEVEL = "confidencelevel";

    /** Name of the <code>sensitivity</code> property. */
    public static final String PROPERTY_SENSITIVITY = "sensitivity";

    /** Name of the <code>speedvsaccuracy</code> property. */
    public static final String PROPERTY_SPEED_VS_ACCURACY = "speedvsaccuracy";

    /** Name of the <code>completetimeout</code> property. */
    public static final String PROPERTY_COMPLETE_TIMEOUT = "completetimeout";

    /** Name of the <code>incompletetimeout</code> property. */
    public static final String PROPERTY_INCOMPLETE_TIMEOUT
        = "incompletetimeout";

    /** Name of the <code>maxspeechtimeout</code> property. */
    public static final String PROPERTY_MAX_SPEECH_TIMEOUT
        = "maxspeechtimeout";

    /** The default confidence level. */
    public static final float DEFAULT_CONFIDENCE_LEVEL = 0.5f;

    /** The default sensitivity. */
    public static final float DEFAULT_SENSITIVITY = 0.5f;

    /** The default balance between speed vs. accuracy. */
    public static final float DEFAULT_SPEED_VS_ACCURACY = 0.5f;

    /** The default no-input timeout in msec. */
    public static final int DEFAULT_NO_INPUT_TIMEOUT = 30000;

    /**
     * The speech recognition confidence level, a float value in the range of
     * 0.0 to 1.0.
     */
    private float confidencelevel;

    /** Sensitivity level. */
    private float sensitivity;

    /** A hint specifying the desired balance between speed vs. accuracy. */
    private float speedvsaccuracy;

    /**
     * The length of silence required following user speech before the speech
     * recognizer finalizes a result.
     */
    private long completetimeout;

    /**
     * The required length of silence following user speech after which a
     * recognizer finalizes a result. 
     */
    private long incompletetimeout;

    /** The maximum duration of user speech. */
    private long maxspeechtimeout;

    /** The no input timeout. */
    private long timeout;

    /**
     * Constructs a new object.
     */
    public SpeechRecognizerProperties() {
        confidencelevel = DEFAULT_CONFIDENCE_LEVEL;
        sensitivity = DEFAULT_SENSITIVITY;
        speedvsaccuracy = DEFAULT_SPEED_VS_ACCURACY;
        timeout = DEFAULT_NO_INPUT_TIMEOUT;
    }

    /**
     * Retrieves the speech recognizer properties from the given map.
     * @param props map with current properties
     */
    public final void setProperties(final Map<String, String> props) {
        final String propConfidenceLevel = props.get(PROPERTY_CONFIDENCE_LEVEL);
        if (propConfidenceLevel != null) {
            confidencelevel = Float.parseFloat(propConfidenceLevel);
        }
        final String propSensitivity = props.get(PROPERTY_SENSITIVITY);
        if (propSensitivity != null) {
            sensitivity = Float.parseFloat(propSensitivity);
        }
        final String propCompletetimeout = props.get(PROPERTY_COMPLETE_TIMEOUT);
        if (propCompletetimeout != null) {
            setCompletetimeout(propCompletetimeout);
        }
        final String propIncompletetimeout =
            props.get(PROPERTY_INCOMPLETE_TIMEOUT);
        if (propIncompletetimeout != null) {
            setIncompletetimeout(propIncompletetimeout);
        }
        final String propMaxspeechtimeout =
            props.get(PROPERTY_MAX_SPEECH_TIMEOUT);
        if (propMaxspeechtimeout != null) {
            setMaxspeechtimeout(propMaxspeechtimeout);
        }
        final String propSpeedvsaccuracy =
            props.get(PROPERTY_SPEED_VS_ACCURACY);
        if (propSpeedvsaccuracy != null) {
            speedvsaccuracy = Float.parseFloat(propSpeedvsaccuracy);
        }
        final String propTimeout =
                props.get(PROPERTY_TIMEOUT);
            if (propTimeout != null) {
                setTimeout(propTimeout);
            }

        
        setEnhancedProperties(props);
    }

    /**
     * May be used to set custom properties if this class is extended.
     * @param props map with current properties.
     */
    protected void setEnhancedProperties(final Map<String, String> props) {
    }

    /**
     * Retrieves the confidence level.
     * @return the confidence level
     */
    public final float getConfidencelevel() {
        return confidencelevel;
    }

    /**
     * Sets the confidence level.
     * @param value the confidence level to set
     */
    public final void setConfidencelevel(final float value) {
        confidencelevel = value;
    }

    /**
     * Retrieves the sensitivity.
     * @return the sensitivity
     */
    public final float getSensitivity() {
        return sensitivity;
    }

    /**
     * Sets the sensitivity.
     * @param value the sensitivity to set
     */
    public final void setSensitivity(final float value) {
        sensitivity = value;
    }

    /**
     * Retrieves a hint specifying the desired balance between speed vs.
     * accuracy.
     * @return the speedvsaccuracy
     */
    public final float getSpeedvsaccuracy() {
        return speedvsaccuracy;
    }

    /**
     * Sets the hint specifying the desired balance between speed vs.
     * accuracy.
     * @param value the speedvsaccuracy to set
     */
    public final void setSpeedvsaccuracy(final float value) {
        speedvsaccuracy = value;
    }

    /**
     * Retrieves the length of silence required following user speech before the
     * speech recognizer finalizes a result.
     * @return the completetimeout
     */
    public final long getCompletetimeoutAsMsec() {
        return completetimeout;
    }

    /**
     * Sets the length of silence required following user speech before the
     * speech recognizer finalizes a result.
     * @param value the complete timeout to set as a time designitation
     */
    public final void setCompletetimeout(final String value) {
        final TimeParser parser = new TimeParser(value);
        completetimeout = parser.parse();
    }

    /**
     * Retrieves the required length of silence following user speech after
     * which a recognizer finalizes a result. 
     * @return the incomplete timeout
     */
    public final long getIncompletetimeoutAsMsec() {
        return incompletetimeout;
    }

    /**
     * Sets the required length of silence following user speech after
     * which a recognizer finalizes a result. 
     * @param value the incomplete timeout to set as a time designation
     */
    public final void setIncompletetimeout(final String value) {
        final TimeParser parser = new TimeParser(value);
        incompletetimeout = parser.parse();
    }

    /**
    /* Retrieves the maximum duration of user speech.
     * @return the max speech timeout
     */
    public final long getMaxspeechtimeoutAsMsec() {
        return maxspeechtimeout;
    }

    /**
    /* Sets the maximum duration of user speech.
     * @param value the max speech timeout to set as a time designation
     */
    public final void setMaxspeechtimeout(final String value) {
        final TimeParser parser = new TimeParser(value);
        maxspeechtimeout = parser.parse();
    }
    
    /**
    /* Retrieves the duration when recognition is started and there is no speech
     * detected.
     * @return the no input timeout
     */
    public final long getTimeoutAsMsec() {
        return timeout;
    }

    /**
    /* Sets the the duration when recognition is started and there is no speech
     * detected.
     * @param value the no input timeout to set as a time designation
     */
    public final void setTimeout(final String value) {
        final TimeParser parser = new TimeParser(value);
        timeout = parser.parse();
    }

    /**
    /* Sets the the duration when recognition is started and there is no speech
     * detected.
     * @param value the no input timeout to set as a time designation
     */
    public final void setTimeout(final long value) {
        timeout = value;
    }

    /**
     * {@inheritDoc}
     * 
     * Subclasses are requested to override
     * {@link SpeechRecognizerProperties#appendToStringEnhancedProperties(StringBuilder)}
     * to add their values.
     */
    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getName());
        builder.append(" [");
        builder.append(PROPERTY_CONFIDENCE_LEVEL);
        builder.append("=");
        builder.append(confidencelevel);
        builder.append(", ");
        builder.append(PROPERTY_SENSITIVITY);
        builder.append("=");
        builder.append(sensitivity);
        builder.append(", ");
        builder.append(PROPERTY_SPEED_VS_ACCURACY);
        builder.append("=");
        builder.append(speedvsaccuracy);
        builder.append(", ");
        builder.append(PROPERTY_COMPLETE_TIMEOUT);
        builder.append("=");
        builder.append(completetimeout);
        builder.append(", ");
        builder.append(PROPERTY_INCOMPLETE_TIMEOUT);
        builder.append("=");
        builder.append(incompletetimeout);
        builder.append(", ");
        builder.append(PROPERTY_MAX_SPEECH_TIMEOUT);
        builder.append("=");
        builder.append(maxspeechtimeout);
        builder.append(", ");
        builder.append(PROPERTY_TIMEOUT);
        builder.append("=");
        builder.append(timeout);
        appendToStringEnhancedProperties(builder);
        builder.append("]");
        return builder.toString();
    }
    
    /**
     * Add custom properties added by custom implementation. Preferred format is
     * {@code , <ClassName> [<property1>=..., <property2>=..., ...]}
     * @param str the builder to append to.
     * @since 0.7.9
     */
    protected void appendToStringEnhancedProperties(final StringBuilder str) {
    }
}
