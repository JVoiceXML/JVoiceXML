/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011-2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * Accept mode of a choice.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.5
 */
public enum AcceptType {
    /**
     * If the accept attribute is "exact" then the user must say the entire
     * phrase in the same order in which they occur in the choice phrase.
     */
    EXACT("exact"),

    /**
     * If the accept attribute is "approximate", then the choice may be matched
     * when a user says a subphrase of the expression. For example, in response
     * to the prompt "Stargazer astrophysics news" a user could say "Stargazer",
     * "astrophysics", "Stargazer news", "astrophysics news", and so on.
     */
    APPROXIMATE("approximate");

    /** Name of the barge-in type. */
    private final String type;

    /**
     * Do not create from outside.
     * @param name name of the barge-in type.
     */
    private AcceptType(final String name) {
        type = name;
    }

    /**
     * Retrieves the name of this accept type.
     * @return Name of this type.
     */
    public String getType() {
        return type;
    }
}
