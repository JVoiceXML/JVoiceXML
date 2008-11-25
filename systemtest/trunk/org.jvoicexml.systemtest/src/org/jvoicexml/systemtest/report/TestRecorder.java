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

import org.apache.log4j.Logger;
import org.jvoicexml.systemtest.Report;
import org.jvoicexml.systemtest.TestCase;
import org.jvoicexml.systemtest.TestResult;

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
     * style sheet template.
     */
    private static final String STYLE_SHEET = "<?xml-stylesheet "
            + "type=\"text/xsl\" "
            + "href=\"@STYLE_SHEET@\" ?>";

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
     * Construct a new object.
     */
    public TestRecorder() {
        reportDoc = new IRXMLDocument();
    }

    /**
     * write the report to output Stream.
     * @param os the report write to.
     */
    public final void write(final OutputStream os) {
        try {
            reportDoc.writeXML(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void markStop(final TestResult result) {
        LOGGER
                .info("The test result is : ---- " + result.getAssert()
                        + " ----");

        long cost = new Date().getTime() - currentTestStartTime;
        ResultItem item = new ResultItem(currentTestCase.getId(), result
                .getAssert(), result.getReason(), cost);

        for (String log : result.getLogMessages()) {
            String m = log.toString().trim();
            if (m.length() == 0) {
                item.logURIs.add("-");
            } else {
                item.logURIs.add(log.toString());
            }

        }

        reportDoc.add(item);

        currentTestCase = null;

        // write to file.
        File report = new File(reportName);
        LOGGER.debug("The report :" + report.getAbsolutePath());
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
    public final void markStart(final TestCase tc) {

        if (currentTestCase != null) {
            markStop(new TestResult(TestResult.FAIL, "no report result"));
        }

        currentTestCase = tc;
        currentTestStartTime = new Date().getTime();

    }

    /**
     * @param name report file name.
     */
    public final void setReportName(final String name) {
        reportName = name;
    }


    /**
     * @param xsltName style file name for XML Processing.
     */
    public final void setXsltName(final String xsltName) {
        reportDoc.addProcessingInstruction(STYLE_SHEET.replace("@STYLE_SHEET@",
                xsltName));
    }
}
