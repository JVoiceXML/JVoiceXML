/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

package org.jvoicexml.implementation.text;

import org.jvoicexml.SpeakableText;
import org.jvoicexml.client.text.TextMessage;

/**
 * A message that has not been acknowledged by the client.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.3
 */
class PendingMessage {
    /** The sent text message. */
    private final TextMessage message;

    /** The related speakable. */
    private final SpeakableText speakable;

    /**
     * Constructs a new object.
     * @param msg the text message
     * @param spk the speakable
     */
    public PendingMessage(final TextMessage msg, final SpeakableText spk) {
        message = msg;
        speakable = spk;
    }

    /**
     * Retrieves the message.
     * @return the message.
     */
    public TextMessage getMessage() {
        return message;
    }

    /**
     * Retrieves the speakable.
     * @return the speakable.
     */
    public SpeakableText getSpeakable() {
        return speakable;
    }
}
