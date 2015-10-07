/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.voicexmlunit;

import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * An SSML document that has been received from JVoiceXML and is waiting to be
 * processed.
 * @author Dirk Schnelle-Walka
 * @since 0.7.8
 */
class BufferedSsmlDocument {
    /** The received SSML document. */
    private final SsmlDocument document;
    /**
     * The sequence number of the message that was used when sending the
     * document.
     */
    private final int sequenceNumber;

    /**
     * Creates a new empty object.
     */
    public BufferedSsmlDocument() {
        document = null;
        sequenceNumber = -1;
    }

    /**
     * Creates a new object with the given document and sequence number.
     * @param doc the received document
     * @param num the used sequence number
     */
    public BufferedSsmlDocument(final SsmlDocument doc, final int num) {
        document = doc;
        sequenceNumber = num;
    }

    /**
     * Retrieves the SSML document.
     * @return the SSML document
     */
    public SsmlDocument getDocument() {
        return document;
    }

    /**
     * Receives the sequence number.
     * @return the sequence number
     */
    public int getSequenceNumber() {
        return sequenceNumber;
    }
}
