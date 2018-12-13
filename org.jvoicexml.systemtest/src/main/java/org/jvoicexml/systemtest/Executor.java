/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2018 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jvoicexml.systemtest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.voicexmlunit.Call;
import org.jvoicexml.voicexmlunit.TextCall;

/**
 * Executer for one test case.
 *
 * @author lancer
 * @author Dirk Schnelle-Walka
 *
 */
public final class Executor {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(Executor.class);

    /** Max wait time. */
    public static final long MAX_WAIT_TIME = 5000L;

    /** Delay answer time. */
    public static final long DELAY_ANSWER_TIME = 500L;

    /** The current test case. */
    private final TestCase testcase;

    /** The script used with this test case. */
    private final Script script;

    /** Possible exception that was caught while the test was executed. */
    private Throwable lastError;

    /** Status change listeners. */
    private final List<StatusListener> listeners
        = new java.util.ArrayList<StatusListener>();

    /** The current status. */
    private final ClientConnectionStatus status = ClientConnectionStatus.INITIAL;

    /** Wait lock. */
    private final Object waitLock;

    /**
     * Constructs a new object.
     * @param test the test case.
     * @param answerScript the answer script of this test case.
     */
    public Executor(final TestCase test,
            final Script answerScript) {
        testcase = test;
        script = answerScript;
        waitLock = new Object();
    }

    /**
     * Executes the test.
     * @param jvxml the interpreter.
     * @param port the port to use for the text server
     * @return result of this test
     * @throws URISyntaxException
     *          error determining the start URI of a test
     */
    public TestResult execute(final JVoiceXml jvxml, final int port) throws URISyntaxException {
        final URI testURI = testcase.getStartURI();

        LOGGER.info("create session and call '" + testURI + "'");
        Call call = null;
        try {
            call = new TextCall("127.0.0.1", port);
            call.call(testURI);
            script.perform(call);
            return TestResult.PASS;
        } catch (Throwable t) {
            LOGGER.error("Error calling the interpreter", t);
            lastError = t;
            return TestResult.FAIL;
        } finally {
            if (call != null) {
                call.hangup();
            }
        }
    }

    /**
     * Retrieves the last received error while executing the test.
     * @return last captured error, maybe <code>null</code>
     */
    public Throwable getLastError() {
        return lastError;
    }
}
