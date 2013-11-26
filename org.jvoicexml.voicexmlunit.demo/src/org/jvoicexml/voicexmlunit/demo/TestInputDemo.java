package org.jvoicexml.voicexmlunit.demo;

import java.io.File;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.voicexmlunit.Call;
import org.jvoicexml.voicexmlunit.Conversation;
import org.jvoicexml.voicexmlunit.Supervisor;
import org.jvoicexml.voicexmlunit.io.Input;
import org.jvoicexml.voicexmlunit.io.Output;

public class TestInputDemo {
    private Call call;
    private Supervisor supervisor;
    /** URI of the application to call. */
    private URI uri;

    @Before
    public void setUp() throws Exception {
        uri = new File("etc/input.vxml").toURI();
        call = new Call();
        supervisor = new Supervisor();
    }

    @Test
    public void testInputYes() {
        createConversation("yes");
        supervisor.process(uri);
    }

    @Test(expected = AssertionError.class)
    public void testInputNoFail() {
        createConversation("no");
        boolean failed = false;
        supervisor.process(uri);
    }

    private void createConversation(String answer) {
        Conversation conversation = supervisor.init(call);
        conversation.add(new Output("Do you like this example?"));
        conversation.add(new Input(answer));
        conversation.add(new Output("You like this example."));
    }
}
