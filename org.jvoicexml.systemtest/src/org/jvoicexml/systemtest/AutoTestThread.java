package org.jvoicexml.systemtest;

import java.net.UnknownHostException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.systemtest.report.TestRecorder;
import org.jvoicexml.systemtest.testcase.IRTestCase;

/**
 * AutoTestThread as the name
 * 
 * @author lancer
 */
class AutoTestThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(AutoTestThread.class);

    TextServer textServer;
    List<IRTestCase> testcaseList;
    JVoiceXml jvxml = null;

    int textServerPort = 0;

    TestRecorder report = null;

    final AnswerGenerator answerGenerator;

    public AutoTestThread(JVoiceXml interpreter, int port, List<IRTestCase> tests) {
        jvxml = interpreter;

        testcaseList = tests;

        textServerPort = port;
        textServer = new TextServer(textServerPort);

        answerGenerator = new AnswerGenerator(textServer);
        textServer.addTextListener(answerGenerator);

        textServer.start();
    }

    @Override
    public void run() {

        for (int i = 0; i < testcaseList.size(); i++) {

            IRTestCase testcase = testcaseList.get(i);

//            // do less
//            if (testcase.hasDeps()) {
//                if (report != null) {
//                    report.skip(testcase, "Test application not handle multi documents now.");
//                }
//                continue;
//            }

            prepairReport(testcase);

            TestExecutor executor = new TestExecutor(answerGenerator);

            try {
                executor.execute(jvxml, testcase, textServer.getRemoteClient());
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            precessResult(executor.result, executor.noExpectEvents);
        }

        LOGGER.info("no more test uri, exit.");
        textServer.stopServer();

        System.exit(0);
    }

    public void setReport(TestRecorder report) {
        this.report = report;
    }

    private void prepairReport(IRTestCase testcase) {
        if (report != null) {
            LOGGER.debug("report =" + report);
            report.setCurrentTestCase(testcase);
        }
    }

    private void precessResult(boolean result, List<Throwable> noExpectEvents) {

        for (Throwable t : noExpectEvents) {
            LOGGER.debug("Throwable: ", t);
        }
        if (report != null) {
            if (result && noExpectEvents.isEmpty()) {
                report.pass();
            } else if (result && !noExpectEvents.isEmpty()) {
                report.fail("received pass, session throw ErrorEvent");
            } else if (!result && noExpectEvents.isEmpty()) {
                report.fail("fail, no exception, reason not collected, see log.");
            } else {
                report.fail("fail and session throw ErrorEvent.");
            }
        }
    }

}
