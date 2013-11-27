package org.jvoicexml.voicexmlunit.demo;

import java.io.File;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.voicexmlunit.Call;

public class TestDtmfDemo {
    private Call call;
    /** URI of the application to call. */
    private URI uri;

    @Before
    public void setUp() throws Exception {
        uri = new File("etc/dtmf.vxml").toURI();
        call = new Call();
    }

    @Test
    public void testInputYes() {
        createConversation('1');
//        supervisor.process(uri);
    }

    @Test(expected = AssertionError.class)
    public void testInputNoFail() {
        createConversation('2');
//        supervisor.process(uri);
    }

    private void createConversation(char answer) {
//        Conversation conversation = supervisor.init(call);
//        conversation
//                .add(new Output(
//                        "Do you like this example? Please enter 1 for yes or 2 for no"));
//        conversation.add(new Dtmf(answer));
//        conversation.add(new Output("You like this example."));
    }
}
