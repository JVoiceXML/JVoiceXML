package org.jvoicexml.systemtest;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketNode;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.callmanager.CallManager;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.systemtest.log4j.Log4JSnoop;
import org.jvoicexml.systemtest.report.TestRecorder;
import org.jvoicexml.systemtest.script.ScriptFactory;
import org.jvoicexml.systemtest.testcase.IRTestCase;
import org.jvoicexml.systemtest.testcase.IRTestCaseLibrary;

/**
 * System Test config. For fit two scenery, it can be call as CallManager in
 * jovicexml, or start as stand alone from SystemTestMain.
 * 
 * @author Zhang Nan
 * @version $Revision$
 * @since 0.7
 */
public class SystemTestCallManager implements CallManager {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(SystemTestCallManager.class);

    private int port = 5900;

    private JVoiceXml _jvxml = null;

    private boolean autoTest = true;

    private String testcases = null;

    private int log4jSocketServerPort = 0;

    private String log4jSocketServer = null;

    private TestRecorder report;

    private String runMode;

    private IRTestCaseLibrary testcaseLibrary;

    private List<Log4JSnoop> logCollectors = null;

    private ScriptFactory scriptFactory = null;

    @Override
    public void start() throws NoresourceError {
        LOGGER.debug("start()");

        if (runMode != null && runMode.equalsIgnoreCase("standalone")) {
            startReceiveRemoteLog();
        }

        Collection<IRTestCase> jobs = testcaseLibrary.fetch(testcases);
        LOGGER.info("There have " + jobs.size() + " test case(s).");

        Thread testThread = selectRunningThread(autoTest, jobs);
        if (testThread != null) {
            testThread.start();
        }
    }

    private void startReceiveRemoteLog() {

        if (log4jSocketServerPort > 0 && log4jSocketServer != null) {
            Socket socket = null;
            try {
                socket = new Socket(log4jSocketServer, log4jSocketServerPort);
                Runnable r = new SocketNode(socket, LogManager
                        .getLoggerRepository());

                Thread t = new Thread(r);
                t.start();
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    private Thread selectRunningThread(boolean auto, Collection<IRTestCase> jobs) {
        AutoTestThread testThread;
        if (auto) {
            testThread = new AutoTestThread(_jvxml, port, jobs, logCollectors);
            testThread.setReport(report);
            testThread.setScriptFactory(scriptFactory);
            return testThread;
        } else {
            LOGGER.info("not implemetns yet.");
            return null;
        }
    }

    @Override
    public void stop() {
        LOGGER.debug("stop()");
    }

    @Override
    public void setJVoiceXml(JVoiceXml jvxml) {
        _jvxml = jvxml;
    }

    public void setTestcaseLibrary(IRTestCaseLibrary lib) {
        this.testcaseLibrary = lib;
    }

    public void setAutoTest(boolean autoTest) {
        this.autoTest = autoTest;
    }

    public void setTestcases(String cases) {
        testcases = cases;
    }

    public void setTextServerPort(int port) {
        this.port = port;
    }

    public void setLog4jSocketHubServerPort(int port) {
        this.log4jSocketServerPort = port;
    }

    public void setLog4jSocketHubServer(String host) {
        this.log4jSocketServer = host;
    }

    public void setReport(TestRecorder report) {
        this.report = report;
    }

    public void setRunMode(String runMode) {
        this.runMode = runMode;
    }

    public void setLogCollectors(List<Log4JSnoop> logCollectors) {
        this.logCollectors = logCollectors;
    }

    public void setScriptFactory(ScriptFactory factory) {
        this.scriptFactory = factory;
    }
}
