/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/client/AudioEndMessage.java $
 * Version: $LastChangedRevision: 102 $
 * Date:    $Date: $
 * Author:  $java.LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.implementation.client;

import java.io.Serializable;

/**
 * Marker for a marker within an SSML document.
 *
 * <p>
 * When the client receives this message, it informs the VoiceXML interpreter
 * that all previous audio has been delivered, sending this marker back.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 102 $
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.6
 */
public final class MarkerMessage
        implements Serializable {
    /** Name of the mark. */
    private String marker;

    /** The serial version UID. */
    private static final long serialVersionUID = -2028438121654400662L;

    /**
     * Constructs a new object.
     */
    public MarkerMessage() {
    }

    /**
     * Constructs a new object with the given marker name.
     * @param name Name of the mark.
     */
    public MarkerMessage(final String name) {
        marker = name;
    }

    /**
     * Sets the marker to the given name.
     * @param name Name of the marker.
     */
    public void setMarker(final String name) {
        marker = name;
    }

    /**
     * Retrieves the name of the marker.
     * @return Name of the marker.
     */
    public String getMarker() {
        return marker;
    }
}
