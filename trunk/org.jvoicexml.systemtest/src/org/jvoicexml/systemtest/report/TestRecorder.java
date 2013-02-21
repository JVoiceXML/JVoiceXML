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
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
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
public final class TestRecorder implements TestCaseListener {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(TestRecorder.class);

    /** The appender to collect all log messages from JVoiceXML. */
    private final SystemTestAppender remoteAppender;

    /** The appender to collect all log messages from the system test. */
    private final SystemTestAppender localAppender;

    /** The appender to collect all log messages of the resources. */
    private final SystemTestAppender resourceAppender;

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
        resourceAppender = new SystemTestAppender();
        final Logger resourceLogger =
                Logger.getLogger("org.jvoicexml.implementation.pool");
        resourceLogger.addAppender(resourceAppender);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void testStopped(final Result result) {
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

        if (result.getAssert() != TestResult.SKIP) {
            final PatternLayout layout = new PatternLayout();
            layout.setConversionPattern(
                    "%6r [%-20.20t] %-5p %30.30c (%6L) %x %m%n");
            final File remoteFile = new File(reportDir, prefix + "remote.log");
            final File localFile = new File(reportDir, prefix + "local.log");
            final File resourceFile = new File(reportDir,
                    prefix + "resource.log");
            try {
                remoteAppender.writeToFile(layout, remoteFile);
                item.remoteLogURI = "file:" + remoteFile.getName();
                boolean hasError = remoteAppender.hasErrorLevelEvent();
                item.hasErrorLevelLog = Boolean.toString(hasError);
                localAppender.writeToFile(layout, localFile);
                item.localLogURI = "file:" + localFile.getName();
                resourceAppender.writeToFile(layout, resourceFile);
                item.resourceLog = filterReturn(resourceAppender);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
                return;
            }
        }

        reportDoc.add(item);
        currentTestCase = null;

        // write to file.
        final File report = new File(reportDir, reportName);
        LOGGER.info("writing report to: '" + report.getAbsolutePath() + "'");
        writeReport(report);
    }

    /**
     * Filters the resource pool info from the message of the given appender.
     * @param appender resource appender
     * @return filtered pool size info
     * @since 0.7.4
     */
    private String filterReturn(final SystemTestAppender appender) {
        List<LoggingEvent> events = appender.getEvents();
        final StringBuilder str = new StringBuilder();
        final String lf = System.getProperty("line.separator");
        for (LoggingEvent event : events) {
            String message = (String) event.getMessage();
            int end = message.indexOf(" after return");
            if (end < 0) {
                continue;
            }
            message = message.substring("pool has now ".length(),
                    end);
            message = message.replaceAll(
                    "for key 'text' .org.jvoicexml.implementation.text.", "(");
            str.append(message);
            str.append(lf);
        }
        return str.toString();
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
    public void testStarted(final TestCase tc) {
        if (reportDoc == null) {
            reportDoc = new IRXMLDocument();
//            if (logRoller != null) {
//                logRoller.roll();
//            }
        }

        // Check if we received a result from the previous test.
        if (currentTestCase != null) {
            final Result result = new FailResult("no report result");
            testStopped(result);
        }

        remoteAppender.clear();
        localAppender.clear();
        resourceAppender.clear();
        currentTestCase = tc;
        currentTestStartTime = System.currentTimeMillis();
    }

    /**
     * Sets the base name of the report.
     * @param name
     *            report file name.
     */
    public void setReportName(final String name) {
        reportName = name;
    }

    /**
     * Sets the directory, where to store the reports. If the specified
     * directory does not exist, it will be created.
     * @param dir directory of report.
     * @exception IOException
     *            error creating the report directory.
     */
    public void setReportDir(final String dir) throws IOException {
        reportDir = new File(dir);
        if (!reportDir.exists()) {
            LOGGER.info("creating report directory '"
                    + reportDir.getCanonicalPath() + "'");
            reportDir.mkdirs();
        }
    }
}
