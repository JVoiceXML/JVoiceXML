package org.jvoicexml.voicexmlunit;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.AssertionFailedError;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

public class TestSupervisor {

    private Supervisor supervisor;

    @Before
    public void setUp() throws Exception {
        supervisor = new Supervisor();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testStatements() {
        Conversation conversation = initMock();

        supervisor.assertStatements(0);

        conversation.addOutput("ping"); // must have an Output before
        conversation.addInput("pong");

        supervisor.assertStatements(2);
    }

    /**
     * Creates a simple SSML document that only contains the given message.
     * @param message the message in the SSML document
     * @return created SSML document
     * @throws ParserConfigurationException
     *          error creating the SSML document
     */
    private SsmlDocument createSsmlDocument(final String message)
            throws ParserConfigurationException {
        final SsmlDocument document = new SsmlDocument();
        final Speak speak = document.getSpeak();
        speak.addText(message);
        return document;
    }

    @Test
    public void testOuput() throws Exception {
        Conversation conversation = initMock();

        final String message = "bla";
        conversation.addOutput(message);

        Assert.assertEquals(message, conversation.begin().toString());

        simulateCall();
        final SsmlDocument document = createSsmlDocument(message);
        supervisor.assertOutput(document);
    }

    @Test
    public void testInput() throws Exception {
        Conversation conversation = initMock();

        final String message = "blub";
        conversation.addOutput(message); // must have an Output before
        conversation.addInput(message);

        Assert.assertEquals(message, conversation.begin().toString());
        Assert.assertEquals(message, conversation.next().toString());

        simulateCall();

        final SsmlDocument document = createSsmlDocument(message);
        supervisor.assertOutput(document);
        supervisor.assertInput();
    }

    @Test
    public void testDisconnect() {
        Conversation conversation = supervisor.init(null);

        conversation.addOutput("hello");

        simulateCall();

        boolean failed = false;
        try {
            supervisor.disconnected();
        } catch (AssertionFailedError e) {
            failed = true;
        }
        Assert.assertTrue(failed);
    }

    @Test
    public void testInputIsOutput() {
        Conversation conversation = supervisor.init(null);

        conversation.addOutput("input");

        simulateCall();

        boolean failed = false;
        try {
            supervisor.assertInput();
        } catch (AssertionFailedError e) {
            failed = true;
        }
        Assert.assertTrue(failed);
    }

    @Test
    public void testOutputIsinput() throws Exception {
        Conversation conversation = supervisor.init(null);

        String message = "output";
        conversation.addInput(message);

        simulateCall();

        boolean failed = false;
        try {
            final SsmlDocument document = createSsmlDocument(message);
            supervisor.assertOutput(document);
        } catch (AssertionFailedError e) {
            failed = true;
        }
        Assert.assertTrue(failed);
    }

    private Conversation initMock() {
        return supervisor.init(null);
    }

    private void simulateCall() {
        // no session data, but begin conversation
        supervisor.connected(null);
    }
}
