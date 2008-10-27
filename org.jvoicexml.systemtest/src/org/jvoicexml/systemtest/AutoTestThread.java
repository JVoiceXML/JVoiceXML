package org.jvoicexml.systemtest;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
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

    int textServerPort = 0;

    TestRecorder report = null;

    TestExecutor executor;
    
    ScriptFactory scriptFactory = new ScriptFactory();

    public AutoTestThread(JVoiceXml interpreter, int port, List<IRTestCase> tests) {
        jvxml = interpreter;

        testcaseList = tests;

        textServerPort = port;
        textServer = new TextServer(textServerPort);

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
            Result result = null;

            // do less
//            if (testcase.hasDeps()) {
//                if (report != null) {
//                    report.skip(testcase, "Test application not handle multi documents now.");
//                }
//                continue;
//            }
            if(testcase.getIgnoreReason() != null){
                if (report != null) {
                    report.skip(testcase, testcase.getIgnoreReason());
                }
                continue;
            }

            prepairReport(testcase);
            
            Script script = scriptFactory.create(testcase);

            executor = new TestExecutor(script, textServer);

            result = executor.execute(jvxml, testcase, client);

            precessResult(result);
            
            if(executor.stopTest == true){
                break;
            }
        }

        LOGGER.info("no more test uri, exit.");
        textServer.stopServer();

        jvxml.shutdown();
        
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

    private void precessResult(Result result) {

        if (report != null) {
            if (result.isSuccess()) {
                report.pass();
            } else {
                report.fail(result.getReason());
            }
        }
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
