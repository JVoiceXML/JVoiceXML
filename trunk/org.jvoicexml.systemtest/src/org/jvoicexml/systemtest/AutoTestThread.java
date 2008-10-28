package org.jvoicexml.systemtest;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.systemtest.log4j.Log4JSnoop;
import org.jvoicexml.systemtest.report.TestRecorder;
import org.jvoicexml.systemtest.response.Script;
import org.jvoicexml.systemtest.testcase.IRTestCase;
import org.jvoicexml.xml.ssml.SsmlDocument;

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

    TestRecorder report = null;

    TestExecutor executor;

    ScriptFactory scriptFactory ;

    List<Log4JSnoop> logCollectors = null;

    public AutoTestThread(JVoiceXml interpreter, int port, List<IRTestCase> tests, List<Log4JSnoop> collectors) {
        jvxml = interpreter;

        testcaseList = tests;

        logCollectors = collectors;

        textServer = new TextServer(port);

        textServer.addTextListener(new OutputListener());

        textServer.start();
    }

    @Override
    public void run() {
        RemoteClient client = null;
        try {
            client = textServer.getRemoteClient();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            LOGGER.debug("UnknownHostException, check config, exit.", e);
            return;
        }

        for (int i = 0; i < testcaseList.size(); i++) {

            IRTestCase testcase = testcaseList.get(i);
            TestResult result = null;
            
            report.add(testcase);

            // do less
//            if (testcase.hasDeps()) {
//                report.testEndWith(createSkipResult("Test application not handle multi documents now."));
//                continue;
//            }
            if (testcase.getIgnoreReason() != null) {
                report.testEndWith(createSkipResult(testcase.getIgnoreReason()));
                continue;
            }

           

            for (LogSnoop collector : logCollectors) {
                collector.start("" + testcase.getId());
            }

            Script script = scriptFactory.create("" + testcase.getId());

            executor = new TestExecutor(script, textServer);

            result = executor.execute(jvxml, testcase, client);

            for (LogSnoop collector1 : logCollectors) {
                collector1.stop();
                result.addLogMessage(collector1.getTrove().toString());
            }
            
            
            report.testEndWith(result);
            
            LOGGER.info("The test result is : " + result.toString());
            LOGGER.info("testcase " + testcase.getId() + " finished");


        }

        LOGGER.info("no more test uri, exit.");
        textServer.stopServer();

        jvxml.shutdown();

        System.exit(0);
    }
    
    public TestResult createSkipResult(String reason){
        TestResult result = new TestResult("Skip", reason);
        for (int i = 0; i < logCollectors.size(); i++) {
            result.addLogMessage("-");
        }
        return result;
    }

    public void setReport(TestRecorder report) {
        this.report = report;
    }
    
    
    public void setScriptsDirectory(String directory) {
        this.scriptFactory = new ScriptFactory(directory);
    }

    /**
     * Bridge of TextListener and TextExcutor. Because TextServer can not remove
     * added listener, so use a bright fit the change of TextExcutor instance.
     * 
     * @author lancer
     */
    private class OutputListener implements TextListener {

        @Override
        public void outputSsml(final SsmlDocument arg0) {
            if (executor != null) {
                executor.outputSsml(arg0);
            }
        }

        @Override
        public void outputText(final String arg0) {
            if (executor != null) {
                executor.outputText(arg0);
            }
        }

        @Override
        public void connected(final InetSocketAddress remote) {
            if (executor != null) {
                executor.connected(remote);
            }
        }

        @Override
        public void disconnected() {
            if (executor != null) {
                executor.disconnected();
            }
        }
    }
}
