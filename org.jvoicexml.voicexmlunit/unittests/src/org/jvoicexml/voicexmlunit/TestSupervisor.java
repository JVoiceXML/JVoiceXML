package org.jvoicexml.voicexmlunit;


import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.voicexmlunit.io.Input;
import org.jvoicexml.voicexmlunit.io.Output;
import org.jvoicexml.voicexmlunit.processor.Assert;
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

        //Assert.assertStatements(0, conversation);
        Assert.assertNull(conversation.begin());
        
        final String prompt = "ping";
        final String reply = "pong";
        conversation.addOutput(prompt); // must have an Output before
        conversation.addInput(reply);

        //Assert.assertStatements(2, conversation);
        Assert.assertEquals(new Output(prompt).toString(), 
                conversation.next().toString());
        Assert.assertEquals(new Input(reply).toString(), 
                conversation.next().toString());
    }

    @Test
    public void testOuput() throws Exception {
        Conversation conversation = initMock();

        final String message = "bla";
        conversation.addOutput(message);

        Assert.assertEquals(message, conversation.begin().toString());

        simulateCall();
        assertOutputSimple(message);
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

        assertOutputSimple(message);
        supervisor.expectingInput();
    }

    @Test
    public void testDisconnect() {
        Conversation conversation = supervisor.init(null);

        conversation.addOutput("hello");

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
        Conversation conversation = supervisor.init(null);

        conversation.addOutput("input");

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
        Conversation conversation = supervisor.init(null);

        String message = "output";
        conversation.addInput(message);

        simulateCall();

        boolean failed = false;
        try {
            assertOutputSimple(message);
        } catch (AssertionError e) {
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
