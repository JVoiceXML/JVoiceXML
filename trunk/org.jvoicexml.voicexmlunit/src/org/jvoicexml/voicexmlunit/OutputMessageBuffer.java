/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.voicexmlunit/src/org/jvoicexml/voicexmlunit/Conversation.java $
 * Version: $LastChangedRevision: 3991 $
 * Date:    $Date: 2013-11-26 16:07:42 +0100 (Tue, 26 Nov 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;

import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.voicexmlunit.io.Output;
import org.jvoicexml.voicexmlunit.io.OutputMessage;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Buffer of messages received so far from JVoiceXml. The buffer gets filled
 * by calls to the implemented {@link TextListener}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
class OutputMessageBuffer implements TextListener {
    /** All queued messages that have been received. */
    private final BlockingQueue<OutputMessage> messages;

    /**
     * Constructs a new object.
     */
    public OutputMessageBuffer() {
        messages =
                new java.util.concurrent.LinkedBlockingQueue<OutputMessage>();
    }

    /**
     * Retrieves the next message.
     * @return next message, <code>null</code> if the call was interrupted.
     */
    public OutputMessage nextMessage() {
        try {
            return messages.take();
        } catch (InterruptedException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void started() {
        messages.clear();
    }

    @Override
    public void connected(final InetSocketAddress remote) {
        messages.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputSsml(final SsmlDocument document) {
        final OutputMessage message = new Output(document);
        messages.offer(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expectingInput() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void inputClosed() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnected() {
    }
}
