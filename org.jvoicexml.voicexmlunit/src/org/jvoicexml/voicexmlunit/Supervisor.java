/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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
import java.net.URI;

import org.jvoicexml.voicexmlunit.io.Message;
import org.jvoicexml.voicexmlunit.io.Nothing;
import org.jvoicexml.voicexmlunit.processor.Assert;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Supervisor can help you to write unit tests for VoiceXML documents. Use case
 * scenario:
 * <ol>
 * <li>Create an instance of Call with your wished VoiceXML resource.</li>
 * <li>Initialize a new conversation with your Call object.</li>
 * <li>Process the given VoiceXML file.</lI>
 * </ol>
 *
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 *
 */
public final class Supervisor
    implements org.jvoicexml.client.text.TextListener, org.jvoicexml.voicexmlunit.processor.Facade {

    private Call call;
    private Conversation conversation;
    private Message statement;

    /**
     * Initialize a new server conversation.
     *
     * @param call
     *            the call object, maybe <code>null</code>
     * @return Conversation to be used and initialized by the caller
     */
    public Conversation init(final Call call) {
        this.call = call;
        if (call != null) { // null means a mock object
            call.addTextListener(this);
        }
        conversation = new Conversation();
        return conversation;
    }

    /**
     * Process a VoiceXML file and generate test log.
     * @param uri URI of the application to call
     */
    public void process(final URI uri) {
        if (call == null) {
            return;
        }

        statement = conversation.begin();
        call.call(uri);

        AssertionError error = call.getFailure();
        if (error != null) {
            throw error;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void started() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connected(final InetSocketAddress remote) {
        statement = conversation.begin();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputSsml(final SsmlDocument document) {
        // TODO better handling of the XML structure inside (xpath?)
        if (document != null) {
            try {
                assertOutput(document);
            } catch (AssertionError e) {
                handleError(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expectingInput() {
        try {
            assertInput();
        } catch (AssertionError e) {
            handleError(e);
        }
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
        try {
            assertHangup();
        } catch (AssertionError e) {
            handleError(e);
        }
    }

    /**
     * Assert that the current statement is an Output instance with the given
     * message.
     * @param message
     *             Message to expect in the call
     */
    @Override
    public void assertOutput(final SsmlDocument message)
            throws AssertionError {
        if (statement == null) {
            statement = new Nothing();
        }
        new Assert(statement).assertOutput(message);
        statement = conversation.next();
    }

    /**
     * Assert that the current statement is an Input instance and the actual
     * message can be send.
     */
    @Override
    public void assertInput() {
        if (statement == null) {
            statement = new Nothing();
        }
        new Assert(statement, call).assertInput();
        statement = conversation.next();
    }

    /**
     * Asserts a final hangup that should be always at end of the call.
     */
    @Override
    public void assertHangup() {
        if (statement != null) {
            new Assert(statement).assertHangup();
            statement = conversation.next();
        }
    }

    /**
     * Register an error with the given Exception.
     * @param error the exception for the error
     */
    private void handleError(final AssertionError error) {
        if (call == null) {
            throw error;
        } else {
            call.fail(error);
        }
    }

    /**
     * @return Assertion in trouble, null if no error has occured
     */
    public Message getFailCause() {
        return statement;
    }
}
