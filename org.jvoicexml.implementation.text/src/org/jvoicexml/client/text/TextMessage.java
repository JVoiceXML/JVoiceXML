/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

package org.jvoicexml.client.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * A message that is sent over the network.
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
*/
public final class TextMessage implements Serializable {
    /** The serial version UID. */
    private static final long serialVersionUID = 7832004614102610277L;

    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(TextMessage.class);
    
    /**
     * Code indicating that this message contains data. Usually this is used
     * to send prompts as text or SSML 
     */
    public static final int DATA = 1;

    /** Code indicating an acknowledge. */
    public static final int ACK = 2;

    /** Code indicating an acknowledge. */
    public static final int BYE = 3;

    /** Code indicating user input. */
    public static final int USER = 4;

    /** Code indicating that clients may send input. */
    public static final int EXPECTING_INPUT = 5;

    /** Code indicating that clients may no loger send input. */
    public static final int INPUT_CLOSED = 6;

    /** The message code. */
    private final int code;

    /** Current sequence number. */
    private final int seqNo;

    /** The message data. */
    private final Serializable data;
    
    /** size of a serialized object. */
    private static final int SIZE_SERIALIZED_MIN = 114;
    
    /** Debug flag, see {@link #isStreamed(InputStream, boolean)}. */
    private static int availableLog = -1;
    
    /**
     * Constructs a new object.
     */
    public TextMessage() {
        this(0, 0, null);
    }

    /**
     * Constructs a new object.
     * @param messageCode the message code.
     */
    public TextMessage(final int messageCode) {
        this(messageCode, 0, null);
    }

    /**
     * Constructs a new object.
     * @param messageCode the message code.
     * @param seq sequence number.
     */
    public TextMessage(final int messageCode, final int seq) {
        this(messageCode, seq, null);
    }

    /**
     * Constructs a new object.
     * @param messageCode the message code.
     * @param seq sequence number.
     * @param messageData the message data.
     */
    public TextMessage(final int messageCode, final int seq,
            final Serializable messageData) {
        code = messageCode;
        seqNo = seq;
        data = messageData;
    }

    /**
     * Retrieves the message code.
     * @return the message code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Retrieves the sequence number.
     * @return the sequence number
     */
    public int getSequenceNumber() {
        return seqNo;
    }

    /**
     * Retrieves the message data.
     * @return the message data.
     */
    public Object getData() {
        return data;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + code;
        if (data == null) {
            result = prime * result;
        } else {
            result = prime * result + data.hashCode();
        }
        result = prime * result + seqNo;
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TextMessage other = (TextMessage) obj;
        if (code != other.code) {
            return false;
        }
        if (data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!data.equals(other.data)) {
            return false;
        }
        if (seqNo != other.seqNo) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append("TextMessage[");
        switch(code) {
        case DATA:
            str.append("DATA");
            break;
        case BYE:
            str.append("BYE");
            break;
        case ACK:
            str.append("ACK");
            break;
        case USER:
            str.append("USER");
            break;
        case EXPECTING_INPUT:
            str.append("EXPECTING_INPUT");
            break;
        case INPUT_CLOSED:
            str.append("INPUT_CLOSED");
            break;
        default:
            str.append(code);
        }
        str.append(", ");
        str.append(seqNo);
        str.append(", ");
        if (data instanceof String) {
            str.append('\'');
        }
        str.append(data);
        if (data instanceof String) {
            str.append('\'');
        }
        str.append("]");
        return str.toString();
    }
    
    /**
     * Checks available and completed object in a stream.
     * @param stream the stream to check
     * @param firstAvailable indicate the first check
     * @return <code>true</code> if a valid object is in the stream buffer
     * @throws IOException stream error
     * @since 0.7.6
     */
    public static boolean isStreamAvailable(final InputStream stream, 
            final boolean firstAvailable) throws IOException {
        int available = stream.available();
        if (LOGGER.isDebugEnabled()) {
            if (firstAvailable || (available != availableLog)) {
                LOGGER.debug("stream available: " + available);
                availableLog = available;
            }
        }
        // should find at least one complete object
        return (available >= SIZE_SERIALIZED_MIN);
     }
}
