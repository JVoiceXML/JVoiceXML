package org.jvoicexml.systemtest;


import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;

import org.jvoicexml.systemtest.log4j.Log4JSnoop;
import org.jvoicexml.systemtest.report.TestRecorder;
import org.jvoicexml.systemtest.response.Script;
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
        
        textServerPort = port;
    }

    @Override
    public void run() {

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

            Script script = scriptFactory.create("" + testcase.getId());
            if(script.isIgnored()){
                report.testEndWith(createSkipResult(script.getIgnoredReason()));
                continue;
            }

            for (LogSnoop collector : logCollectors) {
                collector.start("" + testcase.getId());
            }


            executor = new TestExecutor(script, textServerPort);

            result = executor.execute(jvxml, testcase);

            for (LogSnoop collector1 : logCollectors) {
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
    
    
    public void setScriptFactory(ScriptFactory factory) {
        this.scriptFactory = factory;
    }

}
