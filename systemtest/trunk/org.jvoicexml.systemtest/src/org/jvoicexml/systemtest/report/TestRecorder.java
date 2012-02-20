/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.systemtest.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.jvoicexml.systemtest.Result;
import org.jvoicexml.systemtest.TestCase;
import org.jvoicexml.systemtest.TestCaseListener;
import org.jvoicexml.systemtest.TestResult;

/**
 * trace test result and create report XML file.
 *
 * @author lancer
 * @author Dirk Schnelle-Walka
 *
 */
public class TestRecorder implements TestCaseListener {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(TestRecorder.class);

    /** The appender to collect all log messages. */
    private final SystemTestAppender remoteAppender;

    /** The appender to collect all log messages. */
    private final SystemTestAppender localAppender;

    /**
     * default report file name.
     */
    private String reportName = "ir-report.xml";

    /**
     * report XML document.
     */
    private IRXMLDocument reportDoc;

    /**
     * testing case now.
     */
    private TestCase currentTestCase;

    /**
     * test start time.
     */
    private long currentTestStartTime;

    /**
     * directory of report.
     */
    private File reportDir;

    /**
     * Constructs a new object.
     */
    public TestRecorder() {
        remoteAppender = new SystemTestAppender();
        final LoggerNameFilter filter = new LoggerNameFilter();
        filter.setName("org.jvoicexml.systemtest");
        remoteAppender.addFilter(filter);
        final Logger jvxmlLogger = Logger.getLogger("org.jvoicexml");
        jvxmlLogger.addAppender(remoteAppender);
        localAppender = new SystemTestAppender();
        final Logger systemtestLogger =
                Logger.getLogger("org.jvoicexml.systemtest");
        systemtestLogger.addAppender(localAppender);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void testStopped(final Result result) {
        LOGGER.info("result is : ---- " + result.getAssert() + " ----");
        long cost = System.currentTimeMillis() - currentTestStartTime;

        ResultItem item = new ResultItem();
        item.id = currentTestCase.getId();
        item.res = result.getAssert();
        item.notes = result.getReason();
        item.costInMS = cost;
        item.spec = currentTestCase.getSpecSection();
        item.desc = currentTestCase.getDescription();

        String prefix = "" + currentTestCase.getId() + ".";
//        Map<String, File> map = moveFileTo(reportDir, prefix);

        if (result.getAssert() != TestResult.SKIP) {
            final Layout layout = new SimpleLayout();
            final File remoteFile = new File(reportDir, prefix + "remote.log");
            final File localFile = new File(reportDir, prefix + "local.log");
            try {
                remoteAppender.writeToFile(layout, remoteFile);
                localAppender.writeToFile(layout, localFile);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//            try {
//                item.logTag = LogUtil.getContent(
//                    map.get(LogRoller.LOG_TAG_LOG_NAME)).toString();
//            } catch (IOException e) {
//                LOGGER.warn(e.getMessage(), e);
//                item.logTag = e.getMessage();
//            }
//            item.localLogURI = LogUtil.getURI(reportDir,
//                    map.get(LogRoller.LOCAL_LOG_NAME)).toString();
//            item.remoteLogURI = LogUtil.getURI(reportDir,
//                    map.get(LogRoller.REMOTE_LOG_NAME)).toString();
//            item.hasErrorLevelLog = LogUtil.exists(
//                    map.get(LogRoller.ERROR_LEVEL_LOG_NAME)).toString();
//            try {
//                item.resourceLog = LogUtil.getContent(
//                    map.get(LogRoller.RESOURCE_LOG_NAME)).toString();
//            } catch (IOException e) {
//                LOGGER.warn(e.getMessage(), e);
//                item.resourceLog = e.getMessage();
//            }
        }

        reportDoc.add(item);
        currentTestCase = null;

        // write to file.
        final File report = new File(reportDir, reportName);
        LOGGER.info("writing report to: '" + report.getAbsolutePath() + "'");
        writeReport(report);
    }

    /**
     * Writes the report to the given file.
     * @param report the file where to write the report.
     * @since 0.7.6
     */
    private void writeReport(final File report) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(report);
            reportDoc.writeXML(os);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void testStarted(final TestCase tc) {
        if (reportDoc == null) {
            reportDoc = new IRXMLDocument();
//            if (logRoller != null) {
//                logRoller.roll();
//            }
        }

        // Check if we received a result from the previous test.
        if (currentTestCase != null) {
            final Result result = new MyFailResult("no report result");
            testStopped(result);
        }

        remoteAppender.clear();
        localAppender.clear();
        currentTestCase = tc;
        currentTestStartTime = System.currentTimeMillis();
    }

    /**
     * @param name
     *            report file name.
     */
    public final void setReportName(final String name) {
        reportName = name;
    }

    /**
     * Sets the directory, where to store the reports. If the specified
     * directory does not exist, it will be created.
     * @param dir directory of report.
     * @exception IOException
     *            error creating the report directory.
     */
    public final void setReportDir(final String dir) throws IOException {
        reportDir = new File(dir);
        if (!reportDir.exists()) {
            LOGGER.info("creating report directory '"
                    + reportDir.getCanonicalPath() + "'");
            reportDir.mkdirs();
        }
    }


    /**
     * implement of Result for this class.
     * @author lancer
     *
     */
    private class MyFailResult implements Result {

        /**
         * reason of failed.
         */
        private final String reason;

        /**
         * Construct a new object.
         *
         * @param arg0 reason of failed.
         */
        MyFailResult(final String arg0) {
            reason = arg0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TestResult getAssert() {
            return TestResult.FAIL;
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
