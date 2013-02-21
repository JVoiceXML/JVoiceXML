/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/srgs/ModeType.java $
 * Version: $LastChangedRevision: 2914 $
 * Date:    $Date: 2012-01-30 02:46:04 -0600 (lun, 30 ene 2012) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * @version $Revision: 2914 $
 * @since 0.6
 */
public enum ModeType {
    /**
     * Speech recognition grammar.
     */
    VOICE("voice"),

    /**
     * DTMF grammar.
     */
    DTMF("dtmf");

    /** Name of the mode. */
    private final String mode;

    /**
     * Do not create from outside.
     * @param name name of the mode.
     */
    private ModeType(final String name) {
        mode = name;
    }

    /**
     * Retrieves the name of this barge-in type.
     * @return Name of this type.
     */
    public String getMode() {
        return mode;
    }
}
