package org.jvoicexml.config;

import java.io.File;

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
        monitor.addListener(this);
        monitor.start();
        synchronized (lock) {
            lock.wait();
        }
        Assert.assertEquals(
                new File("unittests/config/test-implementation.xml"),
                reportedFile);
        reportedFile = null;
        final File added = new File("unittests/config/test.xml");
        added.deleteOnExit();
        added.createNewFile();
        synchronized (lock) {
            lock.wait();
        }
        Assert.assertEquals(added, reportedFile);
        reportedFile = null;
        added.delete();
        synchronized (lock) {
            lock.wait();
        }
        Assert.assertEquals(added, reportedFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fileAdded(final File file) {
        reportedFile = file;
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
        synchronized (lock) {
            lock.notifyAll();
        }
    }

}
