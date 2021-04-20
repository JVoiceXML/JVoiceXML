/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2021 JVoiceXML group - http://jvoicexml.sourceforge.net
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

/**
 * Generic telephony properties.
 * <p>
 * This class must be extended to specify platform specific values.
 * </p>
 * @author Dirk Schnelle-Walka
 * @since 0.7.5
 */
public class CallControlProperties {
    /**
     * Retrieves the call control recognizer properties from the given map.
     * @param props map with current properties
     */
    public final void setProperties(final Map<String, String> props) {
        setEnhancedProperties(props);
    }

    /**
     * May be used to set custom properties if this class is extended.
     * @param props map with current properties.
     */
    protected void setEnhancedProperties(final Map<String, String> props) {
    }

    /**
     * {@inheritDoc}
     * Subclasses are requested to override
     * {@link SpeechRecognizerProperties#appendToStringEnhancedProperties(StringBuilder)}
     * to add their values.
     */
    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getName());
        builder.append(" [");
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
