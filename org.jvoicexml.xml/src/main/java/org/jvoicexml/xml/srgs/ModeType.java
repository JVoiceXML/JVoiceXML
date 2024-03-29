/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.srgs;

/**
 * The mode of a grammar indicates the type of input that the user agent should
 * be detecting.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public enum ModeType {
    /**
     * Voice input.
     */
    VOICE("voice"),

    /**
     * DTMF input.
     */
    DTMF("dtmf"),

    /**
     * External input.
     */
    EXTERNAL("external");

    /** Name of the mode. */
    private final String mode;

    /**
     * Do not create from outside.
     * 
     * @param name
     *            name of the mode.
     */
    ModeType(final String name) {
        mode = name;
    }

    /**
     * Retrieves the name of this barge-in type.
     * 
     * @return Name of this type.
     */
    public String getMode() {
        return mode;
    }
}
