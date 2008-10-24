package org.jvoicexml.systemtest;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketNode;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.callmanager.CallManager;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.systemtest.report.TestRecorder;
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
    private static final Logger LOGGER = Logger.getLogger(SystemTestCallManager.class);

    private int port = 5900;

    private JVoiceXml _jvxml = null;

    private boolean autoTest = true;

    private String testcases = null;
    
    private String ignores = null;

    private int log4jSocketServerPort = 0;

    private String log4jSocketServer = null;

    private TestRecorder report;

    private String runMode;
    
    private IRTestCaseLibrary lib = null;

    @Override
    public void start() throws NoresourceError {
        LOGGER.debug("start()");

        if (runMode != null && runMode.equalsIgnoreCase("standalone")) {
            startReceiveRemoteLog();
        }

        List<IRTestCase> jobs = getJobs(testcases, ignores);
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
                Runnable r = new SocketNode(socket, LogManager.getLoggerRepository());

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

    private Thread selectRunningThread(boolean auto, List<IRTestCase> jobs) {
        AutoTestThread testThread;
        if (auto) {
            testThread = new AutoTestThread(_jvxml, port, jobs);
            testThread.setReport(report);
            return testThread;
        } else {
            LOGGER.info("not implemetns yet.");
            return null;
        }
    }

    List<IRTestCase> getJobs(String testcases, String ignoreTestCase) {

        Set<IRTestCase> testCaseExpectToTest = getJobs(testcases);
        Set<IRTestCase> testCaseExpectIgnore = getJobs(ignoreTestCase);
        List<IRTestCase> result = new ArrayList<IRTestCase>();
        for(IRTestCase tc : testCaseExpectToTest){
            if(!testCaseExpectIgnore.contains(tc)){
                result.add(tc);
            }
        }
        
        return result;
    }

    Set<IRTestCase> getJobs(String testcases) {

        if (testcases.equalsIgnoreCase("ALL")) {
            Set<IRTestCase> fetched = new LinkedHashSet<IRTestCase>();
            fetched.addAll(lib.fetchAll());
            return fetched;
        }

        Set<IRTestCase> fetched = new LinkedHashSet<IRTestCase>();
        String[] words = testcases.split(",");
        for (String s : words) {
            String cleanedString = s.trim().toUpperCase();
            if (cleanedString.matches("[0-9]+")) {
                int id = Integer.parseInt(s.trim());
                IRTestCase tc = lib.fetch(id);
                if (tc != null) {
                    fetched.add(lib.fetch(id));
                }
                continue;
            }
            if (cleanedString.matches("[0-9]+ *- *[ 0-9]+")) {
                String[] seq = cleanedString.split("-");
                int first = Integer.parseInt(seq[0].trim());
                int last = Integer.parseInt(seq[1].trim());
                for (int id = first; id <= last; id++) {
                    IRTestCase tc = lib.fetch(id);
                    if (tc != null) {
                        fetched.add(lib.fetch(id));
                    }
                }
                continue;
            }
            if (cleanedString.startsWith("SPEC=")) {
                String section = cleanedString.substring("SPEC=".length()).trim();
                fetched.addAll(lib.fetch(section));
                continue;
            }
            LOGGER.debug("unknown testcases '" + testcases + "' at '" + s + "'");

        }
        return fetched;

    }

    @Override
    public void stop() {
        LOGGER.debug("stop()");
    }

    @Override
    public void setJVoiceXml(JVoiceXml jvxml) {
        _jvxml = jvxml;
    }

    public void setTestManifest(String manifest) {
        LOGGER.debug("manifest = " + manifest);
        try {
            URL url = new URL(manifest);
            lib = new IRTestCaseLibrary(url);
        } catch (Exception e) {
            LOGGER.info("can not load test case library.", e);
        }
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

    public void setIgnores(String ignores) {
        this.ignores = ignores;
    }
}
