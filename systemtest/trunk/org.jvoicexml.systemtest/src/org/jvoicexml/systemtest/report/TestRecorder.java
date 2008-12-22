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
package org.jvoicexml.systemtest.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.systemtest.Report;
import org.jvoicexml.systemtest.Result;
import org.jvoicexml.systemtest.TestCase;
import org.jvoicexml.systemtest.Result;

/**
 * trace test result and create report XML file.
 *
 * @author lancer
 *
 */
public class TestRecorder implements Report {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(TestRecorder.class);

    /**
     * log roller.
     */
    private LogRoller logRoller = null;

    /**
     * log file names.
     */
    private Map<String, String> logFiles = new HashMap<String, String>();

    /**
     * default report file name.
     */
    private String reportName = "ir-report.xml";

    /**
     * report XML document.
     */
    private IRXMLDocument reportDoc = null;

    /**
     * testing case now.
     */
    private TestCase currentTestCase = null;

    /**
     * test start time.
     */
    private long currentTestStartTime = 0;

    /**
     * directory of report.
     */
    private File reportDir = null;

    /**
     * write the report to output Stream.
     *
     * @param os OutputStream which report write to.
     */
    public final void write(final OutputStream os) {
        try {
            reportDoc.writeXML(os);
        } catch (IOException e) {
            LOGGER.error("write report doc error.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void markStop(final Result result) {
        LOGGER.info("result is : ---- " + result.getAssert() + " ----");

        if (logRoller != null) {
            logRoller.roll();
        }

        long cost = new Date().getTime() - currentTestStartTime;

        ResultItem item = new ResultItem();
        item.id = currentTestCase.getId();
        item.res = result.getAssert();
        item.notes = result.getReason();
        item.costInMS = cost;
        item.spec = currentTestCase.getSpecSection();
        item.desc = currentTestCase.getDescription();

        String prefix = "" + currentTestCase.getId() + ".";
        Map<String, File> map = moveFileTo(reportDir, prefix);

        if (map.size() > 0 && !"skip".equalsIgnoreCase(item.res)) {
            item.logTag = LogUtil.getContent(
                    map.get(LogRoller.LOG_TAG_LOG_NAME)).toString();
            item.localLogURI = LogUtil.getURI(reportDir,
                    map.get(LogRoller.LOCAL_LOG_NAME)).toString();
            item.remoteLogURI = LogUtil.getURI(reportDir,
                    map.get(LogRoller.REMOTE_LOG_NAME)).toString();
            item.hasErrorLevelLog = LogUtil.isExists(
                    map.get(LogRoller.ERROR_LEVEL_LOG_NAME)).toString();
        }

        reportDoc.add(item);

        currentTestCase = null;

        // write to file.
        File report = new File(reportDir, reportName);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("The report :" + report.getAbsolutePath());
        }
        try {
            OutputStream os = new FileOutputStream(report);
            write(os);
            os.close();
        } catch (IOException e) {
            LOGGER.error("IOException", e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void markStart(final TestCase tc) {
        if (reportDoc == null) {
            reportDoc = new IRXMLDocument();
            if (logRoller != null) {
                logRoller.roll();
            }
        }

        if (currentTestCase != null) {
            markStop(new MyFailResult("no report result"));
        }

        currentTestCase = tc;
        currentTestStartTime = new Date().getTime();

    }

    /**
     * @param dir the directory the log move to.
     * @param prefix log file name prefix.
     * @return log map with new file.
     */
    private Map<String, File> moveFileTo(final File dir, final String prefix) {
        Map<String, File> newFiles = new HashMap<String, File>();
        for (String key : logFiles.keySet()) {
            String filename = logFiles.get(key);
            String from = filename + LogRoller.LAST_LOG_SUFFIX;
            String to = prefix + new File(filename).getName();
            File writrTo = new File(dir, to);
            if (writrTo.exists()) {
                writrTo.delete();
            }
            new File(from).renameTo(writrTo);
            newFiles.put(key, writrTo);
        }
        return newFiles;
    }

    /**
     * @param name
     *            report file name.
     */
    public final void setReportName(final String name) {
        reportName = name;
    }

    /**
     *
     * @param arg0 log Roller.
     */
    public final void setLogRoller(final LogRoller arg0) {
        logRoller = arg0;
    }

    /**
     * @param locations of log.
     */
    public final void setLogLocations(final Map<String, String> locations) {
        logFiles = locations;
    }

    /**
     * @param dir directory of report.
     */
    public final void setReportDir(final String dir) {
        reportDir = new File(dir);
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
        public String getAssert() {
            return Result.FAIL;
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
