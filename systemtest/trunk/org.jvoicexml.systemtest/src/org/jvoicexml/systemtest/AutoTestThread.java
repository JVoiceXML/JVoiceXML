/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.client.text.TextServer;

/**
 * AutoTestThread as the name. It will run all of test case in testcaseList.
 *
 * @author Zhang Nan
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
class AutoTestThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(AutoTestThread.class);

    /**
     *
     */
    private final int textServerPort;

    /**
     *
     */
    private final Collection<TestCase> testcaseList;

    /**
     * jvoicexml interpreter.
     */
    private final JVoiceXml jvxml;

    /**
     * XML report document.
     */
    private TestCaseListener listener;


    /**
     *
     */
    private ScriptFactory scriptFactory;

    /**
     * Construct a new object.
     *
     * @param interpreter
     *            the JVoiceXML interpreter.
     * @param port
     *            TextServer port.
     * @param tests
     *            test cases for test.
     */
    public AutoTestThread(final JVoiceXml interpreter,
            final int port,
            final Collection<TestCase> tests) {
        jvxml = interpreter;
        testcaseList = tests;
        textServerPort = port;
        setName("AutoTestThread");
        setDaemon(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        for (TestCase testcase : testcaseList) {
            LOGGER.info("running " + testcase.getId() + "...");
            Result result = null;

            listener.testStarted(testcase);

            LOGGER.info("check if all resources can be accessed...");
            boolean complete = testcase.completenessCheck();
            if (!complete) {
                final String reason = testcase.getIgnoreReason();
                final Result skip = new Skip(reason);
                listener.testStopped(skip);
                continue;
            }
            LOGGER.info("...all resources can be accessed");
            final Script script = scriptFactory.create(
                    Integer.toString(testcase.getId()));
            if (script == null) {
                final Result skip = new Skip("not found suitable script.");
                listener.testStopped(skip);
                continue;
            }

            LOGGER.info("\n\n");
            LOGGER.info("###########################################");
            LOGGER.info("start testcase: '" + testcase.toString() + "'");
            LOGGER.info("start uri     : '" + testcase.getStartURI() + "'");
            LOGGER.info("start TextServer at port " + textServerPort);
            final TextServer textServer = new TextServer(textServerPort);

            final Executor executor =
                new Executor(testcase, script, textServer);
            final ConnectionTimeoutMonitor timeoutMonitor =
                new ConnectionTimeoutMonitor(executor, 5 * 60 * 1000);
            executor.addStatusListener(timeoutMonitor);

            textServer.addTextListener(executor);
            try {
                timeoutMonitor.start();
                textServer.start();
                executor.execute(jvxml);
                result = executor.getResult();
            } finally {
                timeoutMonitor.stopMonitor();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("stop text server");
                }
                textServer.stopServer();

                LOGGER.info("testcase " + testcase.getId() + " finished");
                LOGGER.info(result.toString());
                listener.testStopped(result);
            }
        }

        LOGGER.info("no more test uri, exit.");

        jvxml.shutdown();
    }

    /**
     * Sets the listener for test case notifications.
     * @param testCaseListener
     *            listener for test case notifications
     */
    public void setTestCaseListenr(final TestCaseListener testCaseListener) {
        listener = testCaseListener;
    }

    /**
     * @param factory
     *            create script.
     */
    public void setScriptFactory(final ScriptFactory factory) {
        this.scriptFactory = factory;
    }

    /**
     * the result of test case be skipped.
     * @author lancer
     *
     */
    public class Skip implements Result {

        /**
         * reason of skip.
         */
        private final String reason;

        /**
         * @param arg0 reason of skip.
         */
        public Skip(final String arg0) {
            reason = arg0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TestResult getAssert() {
            return TestResult.SKIP;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getReason() {
            return reason;
        }
    }
}

