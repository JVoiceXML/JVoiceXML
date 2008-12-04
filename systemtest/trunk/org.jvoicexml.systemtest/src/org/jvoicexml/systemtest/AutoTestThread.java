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

/**
 * AutoTestThread as the name. It will run all of test case in testcaseList.
 * 
 * @author Zhang Nan
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
    private Report report;

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
    public AutoTestThread(final JVoiceXml interpreter, final int port,
            final Collection<TestCase> tests) {
        jvxml = interpreter;

        testcaseList = tests;

        textServerPort = port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        TestExecutor executor;

        for (TestCase testcase : testcaseList) {

            TestResult result = null;

            report.markStart(testcase);

            LOGGER.info("check completeness...");
            testcase.completenessCheck();

            if (testcase.getIgnoreReason() != null) {
                String reason = testcase.getIgnoreReason();
                report.markStop(new TestResult("skip", reason));
                continue;
            }

            Script script = scriptFactory.create("" + testcase.getId());
            if (script == null) {
                String reason = "not found suitable script.";
                report.markStop(new TestResult("skip", reason));
                continue;
            }

            executor = new TestExecutor(script, textServerPort);

            result = executor.execute(jvxml, testcase);

            report.markStop(result);

            LOGGER.info("The test result is : " + result.toString());
            LOGGER.info("testcase " + testcase.getId() + " finished");

        }

        LOGGER.info("no more test uri, exit.");

        jvxml.shutdown();

        System.exit(0);
    }





    /**
     * @param recorder
     *            result recorder for test.
     */
    public void setReport(final Report recorder) {
        this.report = recorder;
    }

    /**
     * @param factory
     *            create script.
     */
    public void setScriptFactory(final ScriptFactory factory) {
        this.scriptFactory = factory;
    }

}
