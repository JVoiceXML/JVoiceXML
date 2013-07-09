package org.jvoicexml.voicexmlunit.demo.input;

import junit.framework.AssertionFailedError;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.voicexmlunit.Call;
import org.jvoicexml.voicexmlunit.Conversation;
import org.jvoicexml.voicexmlunit.Supervisor;
import org.jvoicexml.voicexmlunit.Voice;

/**
 * DTMF demo for JVoiceXMLUnit.
 * <p>
 * Must be run with the system property
 * <code>-Djava.security.policy=${config}/jvoicexml.policy</code> and
 * the <code>etc</code> folder added to the classpath.
 * </p>
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3745 $
 * @since 0.7.6
 */
public class DtmfDemo {
    private Call call;
    private Supervisor supervisor;

    @Before
    public void setUp() throws Exception {
        call = new Call("etc/dtmf.vxml");

        Voice voice = call.getVoice();

        supervisor = new Supervisor();
    }

    @Test
    public void testInputYes() {
        createConversation('1');
        supervisor.process();
    }

    @Test
    public void testInputNoFail() {
        createConversation('2');
        boolean failed = false;
        try {
            supervisor.process();
        } catch (AssertionFailedError e) {
            failed = true;
        }
        Assert.assertEquals(true, failed);
    }

    private void createConversation(char answer) {
        Conversation conversation = supervisor.init(call);
        conversation
                .addOutput("Do you like this example? Please enter 1 for yes or 2 for no");
        conversation.addDtmf(answer);
        conversation.addOutput("You like this example.");
    }
}
