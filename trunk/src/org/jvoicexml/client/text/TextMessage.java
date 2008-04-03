/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.Serializable;

/**
 * A message that is sent over the network.
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
*/
@SuppressWarnings("serial")
public final class TextMessage implements Serializable {
    /** Code indicating that this message contains data. */
    public static final int DATA = 1;

    /** Code indicating an acknowledge. */
    public static final int ACK = 2;

    /** Code indicating an acknowledge. */
    public static final int BYE = 3;

    /** Code indicating user input. */
    public static final int USER = 4;

    /** The message code. */
    private final int code;

    /** Current sequence number. */
    private final int seqNo;

    /** The message data. */
    private final Serializable data;

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
}
