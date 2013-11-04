/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/ssml/GenderType.java $
 * Version: $LastChangedRevision: 3829 $
 * Date:    $Date: 2013-07-16 13:01:00 +0200 (Tue, 16 Jul 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.ssml;

/**
 * Gender of a {@link Voice}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3829 $
 * @since 0.6
 */

public enum GenderType {
    /**
     * Male voice.
     */
    MALE("male"),

    /**
     * Female voice.
     */
    FEMALE("female"),

    /**
     * Neutral voice that is neither male or female (for example, artificial
     * voices, robotic voices).
     */
    NEUTRAL("neutral");

    /** Name of the gender type. */
    private final String type;

    /**
     * Do not create from outside.
     * @param name name of the gender type.
     */
    private GenderType(final String name) {
        type = name;
    }

    /**
     * Retrieves the name of this gender type.
     * @return Name of this type.
     */
    public String getType() {
        return type;
    }
}
