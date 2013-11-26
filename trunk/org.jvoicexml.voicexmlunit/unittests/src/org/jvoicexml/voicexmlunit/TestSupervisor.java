/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit;

import java.io.File;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.voicexmlunit.io.Input;
import org.jvoicexml.voicexmlunit.io.Output;
import org.jvoicexml.voicexmlunit.processor.Assert;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Test cases for {@link Supervisor}.
 * @author Raphael Groner
 *
 */
public class TestSupervisor {

    private Supervisor supervisor;
    private Conversation conversation;
    /** URI of the application to call. */
    private URI uri;

    /**
     * Set up the test environment.
     * @throws Exception
     *         set up failed
     */
    @Before
    public void setUp() throws Exception {
        supervisor = new Supervisor();
        conversation = initMock();
        uri = new File("unittests/etc/mock.vxml").toURI();
    }

    @Test
    public void testStatements() {
        Assert.assertStatements(0, conversation);
        Assert.assertNull(conversation.begin());

        final String prompt = "ping";
        final String reply = "pong";
        hears(prompt); // must have an Output before
        says(reply);

        Assert.assertStatements(2, conversation);
        Assert.assertEquals(new Output(prompt).toString(),
                conversation.next().toString());
        Assert.assertEquals(new Input(reply).toString(),
                conversation.next().toString());
    }

    @Test
    public void testOuput() throws Exception {
        final String message = "bla";
        hears(message);

        Assert.assertEquals(message, conversation.begin().toString());

        simulateCall();
        assertOutputSimple(message);
    }

    @Test
    public void testInput() throws Exception {
        final String message = "blub";
        hears(message); // must have an Output before
        says(message);

        Assert.assertEquals(message, conversation.begin().toString());
        Assert.assertEquals(message, conversation.next().toString());

        simulateCall();

        assertOutputSimple(message);
        supervisor.expectingInput();
    }

    @Test
    public void testDisconnect() {
        hears("hello");

        simulateCall();

        boolean failed = false;
        try {
            supervisor.disconnected();
        } catch (AssertionError e) {
            failed = true;
        }
        Assert.assertTrue(failed);
    }

    @Test
    public void testInputIsOutput() {
        hears("input");

        simulateCall();

        boolean failed = false;
        try {
            supervisor.expectingInput();
        } catch (AssertionError e) {
            failed = true;
        }
        Assert.assertTrue(failed);
    }

    @Test
    public void testOutputIsinput() throws Exception {
        String message = "output";
        says(message);

        simulateCall();

        boolean failed = false;
        try {
            assertOutputSimple(message);
        } catch (AssertionError e) {
            failed = true;
        }
        Assert.assertTrue(failed);
    }

    @Test(timeout = 5000)
    public void testCall() {
        final Call call = new Call();

        conversation = supervisor.init(call);
        hears("test");

        supervisor.process(uri);
        Assert.assertNull(supervisor.getFailCause());
    }

    private Conversation initMock() {
        return supervisor.init(null);
    }

    private void hears(final String assumption) {
        conversation.add(new Output(assumption));
    }

    private void says(final String assumption) {
        conversation.add(new Input(assumption));
    }

    private void simulateCall() {
        // no session data, but begin conversation
        supervisor.connected(null);
    }

    /**
     * Asserts a simple message as an Output statement.
     * @param statement
     * @param message
     */
    private void assertOutputSimple(final String message) {
        try {
            final SsmlDocument document = new SsmlDocument();
            document.getSpeak().addText(message);
            supervisor.outputSsml(document);
        } catch (ParserConfigurationException e) {
            Assert.fail(e.getMessage());
        }
    }


}
