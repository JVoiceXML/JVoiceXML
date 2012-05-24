/**
 * 
 */
package org.jvoicexml.systemtest.mmi;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.JVoiceXmlMain;
import org.jvoicexml.JVoiceXmlMainListener;
import org.jvoicexml.config.JVoiceXmlConfiguration;
import org.jvoicexml.systemtest.mmi.mcspecific.Assert169;

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
        try {
            final JVoiceXml interpreter = test.startInterpreter();
            Assert169 testcase = new Assert169();
            testcase.test();
            
            LOGGER.info("Waiting for JVoiceXML shutdown...");
            synchronized (test.lock) {
                test.lock.wait();
            }
            LOGGER.info("...JVoiceXML shutdown");
        } catch (InterruptedException e) {
            LOGGER.fatal(e.getMessage(), e);
        }
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
