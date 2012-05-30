/**
 * 
 */
package org.jvoicexml.systemtest.mmi;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.JVoiceXmlMain;
import org.jvoicexml.JVoiceXmlMainListener;
import org.jvoicexml.config.JVoiceXmlConfiguration;
import org.jvoicexml.systemtest.mmi.mcspecific.Assert169;
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

    /**
     * Construct a new object. never used.
     */
    private MMISystemTest() {
        lock = new Object();
    }

    /**
     * The main method.
     *
     * @param args Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        LOGGER.info("Starting SystemTest for JVoiceXML...");

        final MMISystemTest test = new MMISystemTest();
        final ImplementationReport report =
                new ImplementationReport("JVoiceXML");
        try {
            final SystemTestETLSocketServer server = test.startSocketServer();
            final JVoiceXml interpreter = test.startInterpreter();
            final TestCaseReport result = new TestCaseReport(169);
            Assert169 testcase = new Assert169();
            testcase.setSource(server.getUri());
            server.setListener(testcase);
            try {
                testcase.test();
                result.setResult(TestResult.PASS);
            } catch (Exception e) {
                result.setResult(TestResult.FAIL);
                result.setNotes(e.getMessage());
            }
            LOGGER.info("all test terminated");
            interpreter.shutdown();
            LOGGER.info("...JVoiceXML shutdown");
            final JAXBContext ctx =
                    JAXBContext.newInstance(ImplementationReport.class);
            final Marshaller marshaller = ctx.createMarshaller();
            result.setResult(TestResult.PASS);
            result.setNotes("Test entry");
            report.addReport(result);
            final FileOutputStream out = new FileOutputStream("mmi-report.xml");
            marshaller.marshal(report, out);
            out.close();
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
