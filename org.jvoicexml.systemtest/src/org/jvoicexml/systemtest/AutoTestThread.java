package org.jvoicexml.systemtest;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.systemtest.log4j.Log4JSnoop;
import org.jvoicexml.systemtest.report.TestRecorder;
import org.jvoicexml.systemtest.script.Script;
import org.jvoicexml.systemtest.script.ScriptFactory;
import org.jvoicexml.systemtest.testcase.IRTestCase;

/**
 * AutoTestThread as the name
 * 
 * @author lancer
 */
class AutoTestThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(AutoTestThread.class);

    int textServerPort;

    Collection<IRTestCase> testcaseList;

    JVoiceXml jvxml = null;

    TestRecorder report = null;

    TestExecutor executor;

    ScriptFactory scriptFactory;

    List<Log4JSnoop> logSnoops = null;

    public AutoTestThread(JVoiceXml interpreter, int port,
            Collection<IRTestCase> tests, List<Log4JSnoop> snoops) {
        jvxml = interpreter;

        testcaseList = tests;

        logSnoops = snoops;

        textServerPort = port;
    }

    @Override
    public void run() {

        for (IRTestCase testcase : testcaseList) {

            TestResult result = null;

            report.add(testcase);

            // do less
            // if (testcase.hasDeps()) {
            // report.testEndWith(createSkipResult(
            // "Test application not handle multi documents now."));
            // continue;
            // }
            if (testcase.getIgnoreReason() != null) {
                report
                        .testEndWith(createSkipResult(testcase
                                .getIgnoreReason()));
                continue;
            }

            Script script = scriptFactory.create("" + testcase.getId());
            if (script == null) {
                report
                        .testEndWith(createSkipResult("not found suitable script."));
                continue;
            }

            for (LogSnoop snoop : logSnoops) {
                snoop.start("" + testcase.getId());
            }

            executor = new TestExecutor(script, textServerPort);

            result = executor.execute(jvxml, testcase);

            for (LogSnoop collector1 : logSnoops) {
                collector1.stop();
                result.addLogMessage(collector1.getTrove().toString());
            }

            report.testEndWith(result);

            LOGGER.info("The test result is : " + result.toString());
            LOGGER.info("testcase " + testcase.getId() + " finished");

        }

        LOGGER.info("no more test uri, exit.");

        jvxml.shutdown();

        System.exit(0);
    }

    public TestResult createSkipResult(String reason) {
        TestResult result = new TestResult("Skip", reason);
        for (int i = 0; i < logSnoops.size(); i++) {
            result.addLogMessage("-");
        }
        return result;
    }

    public void setReport(TestRecorder report) {
        this.report = report;
    }

    public void setScriptFactory(ScriptFactory factory) {
        this.scriptFactory = factory;
    }

}
