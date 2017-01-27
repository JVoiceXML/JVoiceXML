/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.File;
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
public final class TestInputDemo {
    /** Timeout in msec for each test method. */
    private static final int TIMEOUT = 10000;
    /** The call to JVoiceXML. */
    private Call call;
    /** URI of the application to call. */
    private URI uri;

    /**
     * Set up the test environment.
     *
     * @throws Exception
     *             error setting up the test environment
     */
    @Before
    public void setUp() throws Exception {
        uri = new File("etc/input.vxml").toURI();
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
     * Runs a test with the option 'yes'.
     *
     * @throws Exception
     *             test failed
     */
    @Test(timeout = TIMEOUT)
    public void testInputYes() throws Exception {
        call.call(uri);
        call.hears("Do you like this example?");
        final URI chime = uri.resolve("chime.wav");
        call.hearsAudio(chime);
        call.say("yes");
        call.hears("You like this example.");
    }

    /**
     * Runs a test with the option 'no'.
     */
    @Test(timeout = TIMEOUT)
    public void testInputNo() {
        call.call(uri);
        call.hears("Do you like this example?");
        final URI chime = uri.resolve("chime.wav");
        call.hearsAudio(chime);
        call.say("no");
        call.hears("You do not like this example.");
    }

    /**
     * Runs a test with the option that does not match the input.
     */
    @Test(timeout = TIMEOUT)
    public void testInputNomatch() {
        call.call(uri);
        call.hears("Do you like this example?");
        final URI chime = uri.resolve("chime.wav");
        call.hearsAudio(chime);
        final String notmatchingUtterance = "blabla";
        call.say(notmatchingUtterance);
        call.hears(notmatchingUtterance + " is not a valid input.");
    }
}
