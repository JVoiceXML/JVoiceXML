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
     * Clear all prompts in the queue and append this prompt.
     */
    CLEAR("clear");

    /** The priority value. */
    private final String priority;

    /**
     * Do not create from outside.
     * @param name name of the priority.
     */
    PriorityType(final String name) {
        priority = name;
    }

    /**
     * Retrieves the name of priority.
     * @return Name of this priority.
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Retrieves the type from the provided attribute irrespective of
     * upper and lower case.
     * @param attribute
     * @return resolved type, or {@code null} in case {@code attribute} is
     *          {@code null}
     * @throws IllegalArgumentException
     *          if the provided attribute cannot be resolved to a type.
     * @since 0.7.9
     */
    public static PriorityType valueOfAttribute(final String attribute) {
        if (attribute == null) {
            return null;
        }
        if (attribute.equalsIgnoreCase(APPEND.getPriority())) {
            return PriorityType.APPEND;
        } else if (attribute.equalsIgnoreCase(PREPEND.getPriority())) {
            return PriorityType.PREPEND;
        } else if (attribute.equalsIgnoreCase(CLEAR.getPriority())) {
            return PriorityType.CLEAR;
        } else {
            throw new IllegalArgumentException("'" + attribute 
                    + "' cannot be resolved to an param value type");
        }
    }

}