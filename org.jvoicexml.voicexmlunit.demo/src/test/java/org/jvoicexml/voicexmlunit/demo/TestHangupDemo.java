/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit.demo;

import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.voicexmlunit.Call;
import org.jvoicexml.voicexmlunit.TextCall;

/**
 * A demo that tests a small VoiceXML script to enter a either 'yes' or 'no'.
 * <p>
 * Must be run with the system property
 * <code>-Djava.security.policy=${config}/jvoicexml.policy</code> and the
 * <code>config</code> folder added to the classpath.
 * </p>
 * <p>
 * This demo requires that JVoiceXML is configured with the text implementation
 * platform.
 * </p>
 *
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 *
 */
public final class TestHangupDemo {
    /** Timeout in msec for each test method. */
    private static final int TIMEOUT = 10000;
    /** The call to JVoiceXML. */
    private Call call;

    /**
     * Set up the test environment.
     *
     * @throws Exception
     *             error setting up the test environment
     */
    @Before
    public void setUp() throws Exception {
        call = new TextCall();
    }

    /**
     * Tear down the test environment.
     *
     * @exception JVoiceXMLEvent
     *                error tearing down
     */
    @After
    public void tearDown() throws JVoiceXMLEvent {
        call.hangup();
    }

    /**
     * Runs a test with a hangup in a field.
     *
     * @throws Exception
     *             test failed
     */
    @Test(timeout = TIMEOUT)
    public void testHangupInput() throws Exception {
        final URI uri = TestHangupDemo.class.getResource("/input.vxml").toURI();
        call.call(uri);
        call.hears("Do you like this example?");
        final URI chime = uri.resolve("chime.wav");
        call.hearsAudio(chime);
        call.hangup();
        call.waitForInterpreterLog("User hung up");
    }

    /**
     * Runs a test with a hangup in script.
     *
     * @throws Exception
     *             test failed
     */
    @Test(timeout = TIMEOUT)
    public void testHangupScript() throws Exception {
        final URI uri = TestHangupDemo.class.getResource("/scripthangup.vxml").toURI();
        call.call(uri);
        call.hears("You should not hear this!");
        call.hangup();
        call.waitForInterpreterLog("User hung up");
    }
}
