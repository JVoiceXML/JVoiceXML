/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.xml.vxml;

/**
 * The type of a priority.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.5
 */
public enum PriorityType {
    /**
     * Default for a prompt to be appended to the prompt queue.
     */
    APPEND("append"),

    /**
     * Set the value of this prompt to the top of the queue.
     */
    PREPEND("prepend"),
    
    /**
     * Clear all prompts in the queue and append this prompt
     */
    CLEAR("clear");

    /** The priority value. */
    private final String priority;

    /**
     * Do not create from outside.
     * @param name name of the priority.
     */
    private PriorityType(final String name) {
        priority = name;
    }

    /**
     * Retrieves the name of priority.
     * @return Name of this priority.
     */
    public String getPriority() {
        return priority;
    }
}