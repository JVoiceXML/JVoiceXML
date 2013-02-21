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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.systemtest.Result;
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
    public void testAll() {
        String reportLocation = "./irtest/results/";

        String reportName = "ir-report.xml";
        String xsltName = "../xslt/ir.xslt";
        File f = new File(reportLocation, reportName);
        f.delete();
        Assert.assertFalse(f.exists());

        TestRecorder report = new TestRecorder();
        report.setReportName(reportLocation + reportName);

        for (IRTestCase tc : lib.fetchAll()) {
            if (tc.getId() > 100) {
                continue;
            }
            Assert.assertNotNull(tc);
            report.testStarted(tc);
            report.testStopped(new DummyResult(TestResult.PASS, ""));
        }
    }

    @Test
    public void testSummary() {
        TestRecorder report = new TestRecorder();
        report.testStarted(lib.fetch(1));
        report.testStopped(new DummyResult(TestResult.PASS, ""));
        report.testStarted(lib.fetch(2));
        report.testStopped(new DummyResult(TestResult.PASS, ""));

        report.testStarted(lib.fetch(7));
        report.testStopped(new DummyResult(TestResult.FAIL, ""));
        report.testStarted(lib.fetch(8));
        report.testStopped(new DummyResult(TestResult.FAIL, ""));
        report.testStarted(lib.fetch(11));
        report.testStopped(new DummyResult(TestResult.FAIL, ""));

        report.testStarted(lib.fetch(12));
        report.testStopped(new DummyResult(TestResult.SKIP, ""));
        report.testStarted(lib.fetch(18));
        report.testStopped(new DummyResult(TestResult.SKIP, ""));
        report.testStarted(lib.fetch(19));
        report.testStopped(new DummyResult(TestResult.SKIP, ""));
        report.testStarted(lib.fetch(20));
        report.testStopped(new DummyResult(TestResult.SKIP, ""));

//        report.write(System.out);
        System.out.flush();

    }

    @Test
    public void testHashMapWithStringKey() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("key", 1);
        Assert.assertNotNull(map.get("key"));
        Assert.assertEquals(new Integer(1), map.get("key"));
    }
    
    class DummyResult implements Result {
        
        final TestResult ast;
        final String reason;
        
        DummyResult(TestResult arg0, String arg1){
            reason = arg1;
            ast = arg0;
        }

        @Override
        public TestResult getAssert() {
            return ast;
        }

        @Override
        public String getReason() {
            return reason;
        }
    }
}
