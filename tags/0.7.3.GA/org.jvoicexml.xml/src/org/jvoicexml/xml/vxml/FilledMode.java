/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * Filled mode of the <code>&lt;filled&gt;</code> tag.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.3
 */
public enum FilledMode {
    /**
     * The action is executed when all of the mentioned input items are filled
     * by the last user input.
     */
    ALL("all"),
    /**
     * The action is executed when any of the specified input items is filled by
     * the last user input.
     */
    ANY("any");


    /** Name of the mode. */
    private final String mode;

    /**
     * Do not create from outside.
     * @param name name of the barge-in type.
     */
    private FilledMode(final String name) {
        mode = name;
    }

    /**
     * Retrieves the name of this filled mode.
     * @return Name of this mode.
     */
    public String getMode() {
        return mode;
    }

    /**
     * Converts the given value of the attribute into a
     * <code>FilledMode</code> object. If the attribute can not be
     * resolved, an {@link IllegalArgumentException} is thrown.
     *
     * @param attribute Value of the attribute as it is specified in
     *        a {@link Filled} type.
     * @return corresponding <code>FilledMode</code> object.
     * @since 0.6
     */
    public static FilledMode valueOfAttribute(final String attribute) {
        if (ALL.getMode().equalsIgnoreCase(attribute)) {
            return FilledMode.ALL;
        }
        if (ANY.getMode().equalsIgnoreCase(attribute)) {
            return FilledMode.ANY;
        }
        throw new IllegalArgumentException("Unsupported mode '"
                + attribute + "'");
    }
}
