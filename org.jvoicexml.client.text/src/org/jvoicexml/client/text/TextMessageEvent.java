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
package org.jvoicexml.client.text;

import java.util.EventObject;

import org.jvoicexml.client.text.protobuf.TextMessageOuterClass.TextMessage;
import org.jvoicexml.client.text.protobuf.TextMessageOuterClass.TextMessage.TextMessageType;

/**
 * An event raised to indicate message arrival.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.8
 */
@SuppressWarnings("serial")
public class TextMessageEvent extends EventObject {
    private TextMessage message;

    public TextMessageEvent(final Object source, final TextMessage msg) {
        super(source);
        message = msg;
    }

    public TextMessageType getType() {
        return message.getType();
    }

    public TextMessage getMessage() {
        return message;
    }
}
