package org.jvoicexml.voicexmlunit.demo;

import java.io.File;
import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.voicexmlunit.Call;
import org.jvoicexml.voicexmlunit.TextCall;

/**
 * A demo that tests the venerable hello world.
 * <p>
 * Must be run with the system property
 * <code>-Djava.security.policy=${config}/jvoicexml.policy</code> and
 * the <code>config</code> folder added to the classpath.
 * </p>
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 *
 */
public final class TestInputDemo {
    /** The call to JVoiceXML. */
    private Call call;
    /** URI of the application to call. */
    private URI uri;


    /**
     * Set up the test environment.
     * @throws Exception
     *        error setting up the test environment
     */
    @Before
    public void setUp() throws Exception {
        uri = new File("etc/input.vxml").toURI();
        call = new TextCall();
    }

    /**
     * Tear down the test environment.
     * @exception JVoiceXMLEvent
     *            error tearing down
     */
    @After
    public void tearDown() throws JVoiceXMLEvent {
        call.hangup();
    }

    /**
     * Runs a test with the option '1'.
     */
    @Test(timeout = 5000)
    public void testInputYes() {
        call.call(uri);
        call.hears("Do you like this example?");
        call.say("yes");
        call.hears("You like this example.");
    }

    /**
     * Runs a test with the option '2'.
     */
    @Test(timeout = 5000)
    public void testInputNo() {
        call.call(uri);
        call.hears("Do you like this example?");
        call.say("no");
        call.hears("You do not like this example.");
    }

}
