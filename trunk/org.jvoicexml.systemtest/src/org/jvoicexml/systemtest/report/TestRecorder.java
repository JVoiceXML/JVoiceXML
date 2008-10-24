package org.jvoicexml.systemtest.report;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.jvoicexml.systemtest.testcase.IRTestCase;

public class TestRecorder {
    /** Logger for this class. */
    final private static Logger LOGGER = Logger.getLogger(TestRecorder.class);

    final private static String STYLE_SHEET = "<?xml-stylesheet type=\"text/xsl\" href=\"@STYLE_SHEET@\" ?>";

    private final static String PASS = "pass";

    private final static String FAIL = "fail";

    private final static String SKIP = "skip";

    private final static Layout layout = new PatternLayout("%6r [%-20.20t] %-5p %30.30c (%6L) %x %m%n");

    private String LOCAL_LOG_SUFFIX = "systemtest.log.txt";

    private String REMOTE_LOG_SUFFIX = "interpreter.log.txt";

    private String reportName = "ir-report.xml";

    public IRXMLDocument reportDoc = null;

    private IRTestCase currentTestCase = null;

    private String reportLocation = null;

    private LogCollector localLogCollector = null;

    private LogCollector remoteLogCollector = null;

    // private LogCollector logTagLogCollector = null;
    //
    // private LogCollector ioLogCollector = null;

    private long currentTestStartTime = 0;

    public TestRecorder() {
        reportDoc = new IRXMLDocument();

        // logTagLogCollector = new LogCollector();
        // logTagLogCollector
        // .addAcceptName("org.jvoicexml.interpreter.tagstrategy.LogStrategy");
        // CharArrayWriter logTagLog = new CharArrayWriter();
        // logTagLogCollector.setId(logTagLog);
        //
        // ioLogCollector = new LogCollector();
        // ioLogCollector.addAcceptName("org.jvoicexml.client.text.TextServer");
        // CharArrayWriter ioLog = new CharArrayWriter();
        // ioLogCollector.setId(ioLog);

    }

    public void pass() {
        LOGGER.info("The test result is : ---- PASSED ----");
        result(PASS, PASS);
    }

    public void fail(String reason) {
        LOGGER.info("The test result is : ---- FAILED ----");
        result(FAIL, reason);
    }

    public void skip(IRTestCase tc, String reason) {
        TestResult item = new TestResult(tc.getId(), SKIP, reason, 0);

        item.logURIs.add("-");
        item.logURIs.add("-");
        // item.logURIs.add("-");
        // item.logURIs.add("-");

        reportDoc.add(item);

    }

    public void write(OutputStream os) {
        try {
            reportDoc.writeXML(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write() {
        File report = new File(reportLocation, reportName);
        try {
            OutputStream os = new FileOutputStream(report);
            write(os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void result(String result, String reason) {
        long cost = new Date().getTime() - currentTestStartTime;
        TestResult item = new TestResult(currentTestCase.getId(), result, reason, cost);

        stopCollectLog(item, remoteLogCollector);
        stopCollectLog(item, localLogCollector);
        // stopCollectLog(item, logTagLogCollector);
        // stopCollectLog(item, ioLogCollector);

        reportDoc.add(item);

        currentTestCase = null;

        write();

    }

    public void setCurrentTestCase(IRTestCase tc) {

        if (currentTestCase != null) {
            result(FAIL, "no report result");
        }

        currentTestCase = tc;
        currentTestStartTime = new Date().getTime();
        Appender appender;

        // appender = createBuffAppender(tc.getId()
        // + "logTagLogCollector", logTagLogCollector);
        // startColloctLog(logTagLogCollector, appender);
        //
        // appender = createBuffAppender(tc.getId() + "ioLogCollector",
        // ioLogCollector);
        // startColloctLog(ioLogCollector, appender);

        String name = currentTestCase.getId() + "." + LOCAL_LOG_SUFFIX;
        appender = createFileAppender(name);
        localLogCollector.setId(name);
        startColloctLog(localLogCollector, appender);

        name = currentTestCase.getId() + "." + REMOTE_LOG_SUFFIX;
        appender = createFileAppender(name);
        remoteLogCollector.setId(name);
        startColloctLog(remoteLogCollector, appender);
    }

    private void startColloctLog(LogCollector lc, Appender appender) {
        if (lc == null && appender != null) {
            return;
        }

        lc.setAppender(appender);
        lc.start();
    }

    private Appender createBuffAppender(String name, LogCollector lc) {
        CharArrayWriter writer = new CharArrayWriter();
        lc.setId(writer);
        Appender appender = new WriterAppender(new PatternLayout("%m"), writer);
        appender.setName(name);
        return appender;
    }

    private Appender createFileAppender(String fileName) {
        File docRoot = new File(reportLocation);
        File logFile = new File(docRoot, fileName);
        Appender appender = null;
        try {
            appender = new FileAppender(layout, logFile.getAbsolutePath(), false);
            appender.setName(fileName);
        } catch (IOException e) {
            LOGGER.error("FileAppender create error. ", e);
        }
        return appender;
    }

    private void stopCollectLog(TestResult item, LogCollector lc) {
        if (lc != null) {
            lc.stop();
            item.logURIs.add(lc.getId());
        } else {
            // for junit test, output html format
            item.logURIs.add("no collect log.");
        }
    }

    public void setReportLocation(String location) {

        reportLocation = location;
        File dir = new File(reportLocation);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.out.println(dir.getAbsolutePath() + " not exists, and can not create dirs.");
            }
            return;
        }
        if (!dir.isDirectory()) {
            if (!dir.delete()) {
                System.out.println(dir.getAbsolutePath() + " is not directory, and can not delete.");
                return;
            }
            dir.mkdirs();
            return;
        }
        if (!dir.canWrite()) {
            System.out.println(dir.getAbsolutePath() + " can not have write permission.");
        }

    }

    public void setReportName(String name) {
        reportName = name;
    }

    public IRTestCase currentTestcase() {
        return currentTestCase;
    }

    public void setXsltName(String xsltName) {
        reportDoc.addProcessingInstruction(STYLE_SHEET.replace("@STYLE_SHEET@", xsltName));
    }

    public void setLocalLogCollector(LogCollector localLogCollector) {
        this.localLogCollector = localLogCollector;
    }

    public void setRemoteLogCollector(LogCollector remoteLogCollector) {
        this.remoteLogCollector = remoteLogCollector;
    }

    public IRXMLDocument getReportDoc() {
        return reportDoc;
    }
}
