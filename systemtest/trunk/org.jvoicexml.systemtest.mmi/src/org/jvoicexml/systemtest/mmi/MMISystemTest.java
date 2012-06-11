/**
 * 
 */
package org.jvoicexml.systemtest.mmi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.JVoiceXmlMain;
import org.jvoicexml.JVoiceXmlMainListener;
import org.jvoicexml.config.JVoiceXmlConfiguration;
import org.jvoicexml.systemtest.mmi.mcspecific.AbstractAssert;
import org.jvoicexml.systemtest.mmi.mcspecific.Assert156;
import org.jvoicexml.systemtest.mmi.report.ImplementationReport;
import org.jvoicexml.systemtest.mmi.report.TestCaseReport;
import org.jvoicexml.systemtest.mmi.report.TestResult;

/**
 * Main class of the MMI system test.
 * @author Dirk Schnelle-Walka
 */
public class MMISystemTest implements JVoiceXmlMainListener {
    /** The logger instance. */
    private static final Logger LOGGER = Logger.getLogger(MMISystemTest.class);

    /** Semaphore. */
    private final Object lock;

    /** Tests to perform. */
    private final List<AbstractAssert> tests;

    /**
     * Construct a new object. never used.
     */
    private MMISystemTest() {
        lock = new Object();
        tests = new java.util.ArrayList<AbstractAssert>();
//        tests.add(new Assert94());
//        tests.add(new Assert155());
        tests.add(new Assert156());
//        tests.add(new Assert159());
//        tests.add(new Assert165());
//        tests.add(new Assert207());
//        tests.add(new Assert167());
//        tests.add(new Assert169());
//        tests.add(new Assert170());
//        tests.add(new Assert176());
//        tests.add(new Assert177());
//        tests.add(new Assert8());
//        tests.add(new Assert178());
//        tests.add(new Assert184());
//        tests.add(new Assert24());
//        tests.add(new Assert27());
//        tests.add(new Assert181());
//        tests.add(new Assert187());
//        tests.add(new Assert5());
//        tests.add(new Assert69());
    }

    /**
     * Run all tests.
     * @param server the receiving server
     * @return the created report over the test runs
     */
    private ImplementationReport runTests(
            final SystemTestETLSocketServer server) {
        final ImplementationReport report =
                new ImplementationReport("JVoiceXML");
        for (AbstractAssert testcase : tests) {
            final int id = testcase.getId();
            LOGGER.info("runnig test case " + id);
            final TestCaseReport result = new TestCaseReport(id);
            testcase.setSource(server.getUri());
            server.setListener(testcase);
            try {
                testcase.test();
                result.setResult(TestResult.PASS);
                final String notes = testcase.getNotes();
                result.setNotes(notes);
            } catch (NotImplementedException e) {
                result.setResult(TestResult.NOT_IMPLEMENTED);
            } catch (Exception e) {
                result.setResult(TestResult.FAIL);
                final String message = e.getMessage();
                result.setNotes(message);
            } finally {
                try {
                    testcase.clearContext();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JAXBException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (TestFailedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            report.addReport(result);
            final TestResult testResult = result.getResult();
            if (testResult == TestResult.PASS) {
                LOGGER.info("result: " + result.getResult());
            } else {
                LOGGER.info("result: " + result.getResult() + " ("
                        + result.getNotes() + ")");
                
            }
        }
        return report;
    }

    /**
     * Writes the report.
     * @param report the report to writer
     * @throws IOException
     *         if the report could not be written
     * @throws JAXBException
     *         if the report could not be serialized
     */
    private void writeReport(final ImplementationReport report)
            throws IOException, JAXBException {
        final JAXBContext ctx =
                JAXBContext.newInstance(ImplementationReport.class);
        final Marshaller marshaller = ctx.createMarshaller();
        final FileOutputStream out =
                new FileOutputStream("jvoicexml-mmi-report.xml");
        marshaller.marshal(report, out);
        out.close();
    }

    /**
     * The main method.
     *
     * @param args Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        LOGGER.info("Starting SystemTest for JVoiceXML...");

        final MMISystemTest test = new MMISystemTest();
        ImplementationReport report = null;
        try {
            final SystemTestETLSocketServer server = test.startSocketServer();
            final JVoiceXml interpreter = test.startInterpreter();
            report = test.runTests(server);
            LOGGER.info("all test terminated");
            interpreter.shutdown();
            LOGGER.info("...JVoiceXML shutdown");
            test.writeReport(report);
        } catch (InterruptedException e) {
            LOGGER.fatal(e.getMessage(), e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Starts the MMI socket server.
     * @return the started server.
     * @throws IOException
     *         error starting the server
     */
    private SystemTestETLSocketServer startSocketServer() throws IOException {
        final SystemTestETLSocketServer server =
                new SystemTestETLSocketServer(4545);
        server.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return server;
    }

    /**
     * Starts JVoiceXML.
     * @return reference to the JVoiceXML interpreter
     * @throws InterruptedException
     *         error waiting until JVoiceXML started
     */
    private JVoiceXml startInterpreter() throws InterruptedException {
        System.setProperty("jvoicexml.config", "../org.jvoicexml/config");
        final JVoiceXmlConfiguration config = new JVoiceXmlConfiguration();
        final JVoiceXmlMain jvxml = new JVoiceXmlMain(config);
        jvxml.addListener(this);
        jvxml.start();
        LOGGER.info("Waiting for JVoiceXML startup complete...");
        synchronized (lock) {
            lock.wait();
        }
        LOGGER.info("...JVoiceXML started");
        return jvxml;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void jvxmlStarted() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void jvxmlTerminated() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void jvxmlStartupError(final Throwable exception) {
        LOGGER.fatal("JVoiceXML did not start up", exception);
        System.exit(-1);
    }
}
