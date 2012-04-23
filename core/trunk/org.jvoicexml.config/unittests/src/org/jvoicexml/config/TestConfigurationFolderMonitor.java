package org.jvoicexml.config;

import java.io.File;
import java.io.FileWriter;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link ConfigurationFolderMonitor}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class TestConfigurationFolderMonitor
    implements ConfigurationFileChangedListener {
    /** Notifications about messages from the monitor. */
    private Object lock;

    /** The reported file. */
    private File reportedFile;

    /** The reported action. */
    private String action;

    /**
     * Setup the test environment.
     */
    @Before
    public void setUp() {
        lock = new Object();
    }

    /**
     * Tests the configuration folder monitor.
     * @throws Exception 
     *         test failed
     */
    @Test(timeout = 10000)
    public void test() throws Exception {
        final File configFolder = new File("unittests/config");
        final ConfigurationFolderMonitor monitor =
                new ConfigurationFolderMonitor(configFolder);
        monitor.setDelay(500);
        monitor.addListener(this);
        monitor.start();
        synchronized (lock) {
            lock.wait();
        }
        Assert.assertEquals(
                new File("unittests/config/test-implementation.xml"),
                reportedFile);
        Assert.assertEquals("added", action);
        reportedFile = null;
        action = null;
        final File added = new File("unittests/config/test.xml");
        added.deleteOnExit();
        added.createNewFile();
        synchronized (lock) {
            lock.wait();
        }
        Assert.assertEquals(added, reportedFile);
        Assert.assertEquals("added", action);
        reportedFile = null;
        action = null;
        Thread.sleep(100);
        final FileWriter writer = new FileWriter(added);
        writer.write("test");
        writer.close();
        synchronized (lock) {
            lock.wait();
        }
        Assert.assertEquals(added, reportedFile);
        Assert.assertEquals("updated", action);
        reportedFile = null;
        action = null;
        added.delete();
        synchronized (lock) {
            lock.wait();
        }
        Assert.assertEquals(added, reportedFile);
        Assert.assertEquals("deleted", action);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fileAdded(final File file) {
        reportedFile = file;
        action = "added";
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fileUpdated(final File file) {
        reportedFile = file;
        action = "updated";
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fileRemoved(final File file) {
        reportedFile = file;
        action = "deleted";
        synchronized (lock) {
            lock.notifyAll();
        }
    }

}
