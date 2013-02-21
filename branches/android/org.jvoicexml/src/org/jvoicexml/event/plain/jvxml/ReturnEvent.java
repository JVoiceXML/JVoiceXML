/*
 * File:    $RCSfile: ReturnEvent.java,v $
 * Version: $Revision: 2476 $
 * Date:    $Date: 2010-12-23 05:36:01 -0600 (jue, 23 dic 2010) $
 * Author:  $Author: schnelle $
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.event.plain.jvxml;

import java.util.Map;

import org.jvoicexml.event.PlainEvent;

/**
 * The FIA processed a <code>&lt;return&gt;</code> event.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2476 $
 *
 * @since 0.3
 */
public final class ReturnEvent
        extends PlainEvent {
    /** The serial version UID. */
    private static final long serialVersionUID = 3041489879498627754L;

    /** The detail message. */
    public static final String EVENT_TYPE = ReturnEvent.class.getName();

    /** Variable names and their values to be returned to the interpreter. */
    private final Map<String, Object> variables;

    /**
     * Constructs a new object with the given namelist as return value.
     * @param mappings
     *        Variable names to be returned to the interpreter.
     */
    public ReturnEvent(final Map<String, Object> mappings) {
        variables = mappings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }

    /**
     * Retrieves the variable names to be passed to the interpreter context.
     * @return Variable names and their values.
     */
    public Map<String, Object> getVariables() {
        return variables;
    }
}
