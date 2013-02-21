/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.mmi.events/src/org/jvoicexml/mmi/events/CancelRequest.java $
 * Version: $LastChangedRevision: 3485 $
 * Date:    $Date: 2013-01-23 12:45:54 +0100 (Wed, 23 Jan 2013) $
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

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.voicexmlunit.io.Assertion;
import org.jvoicexml.voicexmlunit.io.Input;
import org.jvoicexml.voicexmlunit.io.Output;
import org.jvoicexml.voicexmlunit.io.Recording;
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
public final class Supervisor implements TextListener {

    private Call call = null;
    private Conversation conversation = null;
    private Assertion statement = null;

    /**
     * Initialize a new server conversation.
     * 
     * @param call
     *            the call object, maybe <code>null</code>
     * @return Conversation to be used and initialized by the caller
     */
    public Conversation init(Call call) {
        this.call = call;
        if (call != null) { // null means a mock object
            call.setListener(this);
        }
        conversation = new Conversation();
        return conversation;
    }

    /**
     * Process a VoiceXML file and generate test log
     */
    public void process() {
        if (call == null) {
            return;
        }

        statement = conversation.begin();
        call.run();

        AssertionFailedError error = call.getFailure();
        if (error != null) {
            error.printStackTrace();
            Assert.fail(error.toString());
        }
    }

    /**
     * Assert the expected count of conversation statements
     * 
     * @param expectedCount
     *            How many statements should we have?
     */
    public void assertStatements(int expectedCount) throws AssertionFailedError {
        Assert.assertEquals("Statements", expectedCount,
                conversation.countStatements());
    }

    /**
     * Assert that the current statement is an Output instance with the given
     * message
     * 
     * @param message
     *            Message to expect in the call
     */
    public void assertOutput(SsmlDocument message) throws AssertionFailedError {
        if (statement == null) {
            Assert.assertEquals(Output.class.getSimpleName(), message,
                    "## nothing ##");
        } else {
            statement.receive(message);
            statement = conversation.next();
        }
    }

    /**
     * Assert that the current statement is an Input instance and the actual
     * message can be send.´
     */
    public void assertInput() throws AssertionFailedError {
        if (statement == null) {
            Assert.fail(Input.class.getSimpleName() + " expected");
        } else {
            Recording record;
            if (call == null) {
                record = new Recording(null, null); // mock
            } else {
                record = call.record();
            }
            statement.send(record);
            statement = conversation.next();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jvoicexml.client.text.TextListener#started()
     */
    public void started() {
        call.startDialog();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jvoicexml.client.text.TextListener#connected(java.net.InetSocketAddress
     * )
     */
    public void connected(final InetSocketAddress remote) {
        statement = conversation.begin();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jvoicexml.client.text.TextListener#outputSsml(org.jvoicexml.xml.ssml
     * .SsmlDocument)
     */
    public void outputSsml(final SsmlDocument document) {
        // TODO better handling of the XML structure inside (xpath?)
        if (document != null) {
            try {
                assertOutput(document);
            } catch (AssertionFailedError e) {
                if (call == null) {
                    Assert.fail(e.getMessage());
                } else {
                    call.fail(e);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jvoicexml.client.text.TextListener#expectingInput()
     */
    public void expectingInput() {
        try {
            assertInput();
        } catch (AssertionFailedError e) {
            call.fail(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jvoicexml.client.text.TextListener#inputClosed()
     */
    public void inputClosed() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jvoicexml.client.text.TextListener#disconnected()
     */
    public void disconnected() {
        if (statement != null) {
            final AssertionFailedError error = new AssertionFailedError(
                    "## disconnected ##");
            if (call == null) {
                Assert.fail(error.getMessage());
            } else {
                call.fail(error);
            }
        }
    }
}
