/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
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
 * Marker for the end of an audio stream to be delivered to the client's
 * audio device.
 *
 * <p>
 * When the client has played all audio, it informs the VoiceXML interpreter
 * that all audio has been delivered, by sending this message back to the
 * interpreter.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.6
 */
public class AudioEndMessage
        implements Serializable {
    /** The serial version UID. */
    private static final long serialVersionUID = 300710910117395780L;

    /**
     * Constructs a new object.
     */
    public AudioEndMessage() {
    }
}
