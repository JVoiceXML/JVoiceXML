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

/**
 * System Test configuration. For fit two scenery, it can be call as
 * CallManager in JVoiceXML, or start as stand alone from
 * SystemTestMain.
 *
 * @author Zhang Nan
 * @version $Revision$
 * @since 0.7
 */
public class SystemTestCallManager implements CallManager {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(SystemTestCallManager.class);

    /**
     * the port of text server.
     */
    private int textServerport;

    /**
     * interpreter.
     */
    private JVoiceXml jvxml = null;

    /**
     * is auto execute all test cases. now, always true.
     */
    private boolean autoTest = true;

    /**
     * test cases expression.
     */
    private String testcases = null;

    /**
     * log4j Socket server port.
     */
    private int log4jSocketServerPort = 0;

    /**
     * log4j socket server name or address.
     */
    private String log4jSocketServer = null;

    /**
     * test report instance.
     */
    private Report testRecorder;

    /**
     * run mode, "stand_alone" or "with_jvoicexml".
     */
    private String runMode;

    /**
     * test case library instance.
     */
    private TestCaseLibrary testcaseLibrary;

    /**
     * log snoop list.
     */
    private List<LogSnoop> logCollectors = null;

    /**
     * script factory.
     */
    private ScriptFactory scriptFactory = null;

    /**
     * start this callManager.
     */
    @Override
    public final void start() {
        LOGGER.debug("start()");

        if (runMode != null && runMode.equalsIgnoreCase("standalone")) {
            startReceiveRemoteLog();
        }

        Collection<TestCase> jobs = testcaseLibrary.fetch(testcases);
        LOGGER.info("There have " + jobs.size() + " test case(s).");

        Thread testThread = selectRunningThread(autoTest, jobs);
        if (testThread != null) {
            testThread.start();
        }
    }

    /**
     * start a thread for receive remote logs.
     */
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
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param arg0 if true, create AutoTestThread, else InteractiveTestThread.
     * @param jobs test cases.
     * @return the thread will be start().
     */
    private Thread selectRunningThread(final boolean arg0,
            final Collection<TestCase> jobs) {
        AutoTestThread testThread;
        if (arg0) {
            testThread = new AutoTestThread(jvxml,
                    textServerport, jobs, logCollectors);
            testThread.setReport(testRecorder);
            testThread.setScriptFactory(scriptFactory);
            return testThread;
        } else {
            LOGGER.info("not implemetns yet.");
            return null;
        }
    }

    /**
     * @see org.jvoicexml.callmanager.CallManager#stop()
     */
    @Override
    public final void stop() {
        LOGGER.debug("stop()");
    }

    /**
     * @see org.jvoicexml.callmanager.CallManager#setJVoiceXml(org.jvoicexml.JVoiceXml)
     * @param interpreter the JVoiceXML interpreter.
     */
    @Override
    public final void setJVoiceXml(final JVoiceXml interpreter) {
        this.jvxml = interpreter;
    }

    /**
     * @param lib of test cases.
     */
    public final void setTestcaseLibrary(final TestCaseLibrary lib) {
        this.testcaseLibrary = lib;
    }

    /**
     * @param arg0 if true, application should all tests by test case
     * expressions.
     */
    public final void setAutoTest(final boolean arg0) {
        this.autoTest = arg0;
    }

    /**
     * @param cases expressions.
     */
    public final void setTestcases(final String cases) {
        testcases = cases;
    }

    /**
     * @param port of Text Server.
     */
    public final void setTextServerPort(final int port) {
        this.textServerport = port;
    }

    /**
     * @param port log4j Socket port.
     */
    public final void setLog4jSocketHubServerPort(final int port) {
        this.log4jSocketServerPort = port;
    }

    /**
     * @param host log4j Socket server name or IP address.
     */
    public final void setLog4jSocketHubServer(final String host) {
        this.log4jSocketServer = host;
    }

    /**
     * @param report test recorder.
     */
    public final void setReport(final Report report) {
        this.testRecorder = report;
    }

    /**
     * @param mode run mode 'stand_alone' or 'with_jvoicexml'.
     */
    public final void setRunMode(final String mode) {
        this.runMode = mode;
    }

    /**
     *
     * @param list of log snoop.
     */
    public final void setLogCollectors(final List<LogSnoop> list) {
        this.logCollectors = list;
    }

    /**
     * @param factory which create script.
     */
    public final void setScriptFactory(final ScriptFactory factory) {
        this.scriptFactory = factory;
    }
}
