package org.jvoicexml.systemtest.report;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.systemtest.TestResult;
import org.jvoicexml.systemtest.testcase.IRTestCase;
import org.jvoicexml.systemtest.testcase.IRTestCaseLibrary;

public class ReportTest {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(ReportTest.class);

    IRTestCaseLibrary lib = null;
    String docBase = "http://localhost:8080/irtest/irtests/";

    String docURI = docBase + "manifest.xml";

    @Before
    public void setUp() throws Exception {
        boolean remote = true;
        URL url = null;
        if (remote) {
            url = new URL(docURI);
        } else {
            File f = new File("irtest/irtests/manifest.xml");
            Assert.assertTrue(f.exists());
            url = f.toURI().toURL();
        }
        lib = new IRTestCaseLibrary();
        lib.setTestManifest(docURI);
    }

    @Test
    public void testOne() {
        TestRecorder report = new TestRecorder();
        report.setReportName("irtest/results/ir-report.xml");

        IRTestCase tc = lib.fetch(1);
        Assert.assertNotNull(tc);
        report.add(tc);
        Assert.assertEquals(tc, report.currentTestcase());
        report.testEndWith(new TestResult("pass", ""));
        Assert.assertNull(report.currentTestcase());
        LOGGER.debug("aaaa");
        try {
            report.write(System.out);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testAll() {
        String reportLocation = "./irtest/results/";

        String reportName = "ir-report.xml";
        String xsltName = "../xslt/ir.xslt";
        File f = new File(reportLocation, reportName);
        f.delete();
        Assert.assertFalse(f.exists());

        TestRecorder report = new TestRecorder();
        report.setReportName(reportLocation + reportName);
        report.setXsltName(xsltName);

        for (IRTestCase tc : lib.fetchAll()) {
            if (tc.getId() > 100) {
                continue;
            }
            Assert.assertNotNull(tc);
            report.add(tc);
            Assert.assertEquals(tc, report.currentTestcase());
            report.testEndWith(new TestResult("pass", ""));
            Assert.assertNull(report.currentTestcase());
        }
    }

    @Test
    public void testSummary() {
        TestRecorder report = new TestRecorder();
        report.add(lib.fetch(1));
        report.testEndWith(new TestResult("pass", ""));
        report.add(lib.fetch(2));
        report.testEndWith(new TestResult("pass", ""));

        report.add(lib.fetch(7));
        report.testEndWith(new TestResult("fail", ""));
        report.add(lib.fetch(8));
        report.testEndWith(new TestResult("fail", ""));
        report.add(lib.fetch(11));
        report.testEndWith(new TestResult("fail", ""));

        report.add(lib.fetch(12));
        report.testEndWith(new TestResult("skip", ""));
        report.add(lib.fetch(18));
        report.testEndWith(new TestResult("skip", ""));
        report.add(lib.fetch(19));
        report.testEndWith(new TestResult("skip", ""));
        report.add(lib.fetch(20));
        report.testEndWith(new TestResult("skip", ""));

        report.write(System.out);
        System.out.flush();

        Summary summary = report.getReportDoc().summary;

        Assert.assertEquals(3 + 1, summary.types.size());
        int i = 0;
        Assert.assertEquals("pass", summary.types.get(i));
        Assert.assertEquals(2, summary.countOfTypes.get(i));
        i++;
        Assert.assertEquals("fail", summary.types.get(i));
        Assert.assertEquals(3, summary.countOfTypes.get(i));
        i++;
        Assert.assertEquals("skip", summary.types.get(i));
        Assert.assertEquals(4, summary.countOfTypes.get(i));
        i++;
        Assert.assertEquals("total", summary.types.get(i));
        Assert.assertEquals(9, summary.countOfTypes.get(i));
    }

    @Test
    public void testHashMapWithStringKey() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("key", 1);
        Assert.assertNotNull(map.get("key"));
        Assert.assertEquals(1, map.get("key"));
    }
}
