package org.jvoicexml.systemtest.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.apache.log4j.Logger;
import org.jvoicexml.systemtest.TestResult;
import org.jvoicexml.systemtest.testcase.IRTestCase;

public class TestRecorder {
    /** Logger for this class. */
    final private static Logger LOGGER = Logger.getLogger(TestRecorder.class);

    final private static String STYLE_SHEET = "<?xml-stylesheet type=\"text/xsl\" href=\"@STYLE_SHEET@\" ?>";

    private String reportName = "ir-report.xml";

    private IRXMLDocument reportDoc = null;

    private IRTestCase currentTestCase = null;

    private String reportLocation = null;

    private long currentTestStartTime = 0;

    public TestRecorder() {
        reportDoc = new IRXMLDocument();
    }

    public void write(final OutputStream os) {
        try {
            reportDoc.writeXML(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write() {
        File report = new File(reportLocation, reportName);
        LOGGER.debug("The report :" + report.getAbsolutePath());
        try {
            OutputStream os = new FileOutputStream(report);
            write(os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testEndWith(TestResult result) {
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

        write();

    }

    public void add(IRTestCase tc) {

        if (currentTestCase != null) {
            testEndWith(new TestResult(TestResult.FAIL, "no report result"));
        }

        currentTestCase = tc;
        currentTestStartTime = new Date().getTime();

    }

    public void setReportName(String name) {
        reportName = name;
    }

    IRTestCase currentTestcase() {
        return currentTestCase;
    }

    public void setXsltName(String xsltName) {
        reportDoc.addProcessingInstruction(STYLE_SHEET.replace("@STYLE_SHEET@",
                xsltName));
    }

    public IRXMLDocument getReportDoc() {
        return reportDoc;
    }
}
