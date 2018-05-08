package org.jvoicexml.config;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test cases for {@link ConfigurationFolderMonitor}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public class TestConfigurationFolderMonitorForExistingFiles implements ConfigurationFileChangedListener {

    @Rule public Timeout globalTimeout= new Timeout(10, TimeUnit.SECONDS);

    /** The reported file. */
    private File reportedFile;

    /** The reported action. */
    private String action;

    /**
     * CountDownLatch for test setup steps. Begin assertions after:
     * 1. File action (create / update / delete) and;
     * 2. ConfigurationFolderMonitor has been notified
     */
    private CountDownLatch readyForAssertions = new CountDownLatch(2);

    /**
     * Tests the configuration folder monitor is aware of existing files
     * @throws Exception
     *         test failed
     */
    @Test
    public void testExistingFileIsNotified() throws Exception {

        // Monitor the (existing) config folder
        final File configFolder = new File("../org.jvoicexml.config/src/test/resources/config");
        final ConfigurationFolderMonitor monitor = new ConfigurationFolderMonitor(configFolder);
        monitor.addListener(this);
        monitor.start();

        // Wait for notification that existing files were 'added'
        readyForAssertions.countDown();
        readyForAssertions.await();

        // Check the notification
        Assert.assertEquals(new File(
                        "../org.jvoicexml.config/src/test/resources/config/test-implementation.xml").getCanonicalPath(),
                reportedFile.getCanonicalPath());
        Assert.assertEquals("added", action);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fileAdded(final File file) {
        reportedFile = file;
        action = "added";
        readyForAssertions.countDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fileUpdated(final File file) {
        reportedFile = file;
        action = "updated";
        readyForAssertions.countDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fileRemoved(final File file) {
        reportedFile = file;
        action = "deleted";
        readyForAssertions.countDown();
    }

}
