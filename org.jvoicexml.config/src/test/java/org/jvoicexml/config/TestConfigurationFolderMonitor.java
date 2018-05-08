/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.config;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;

/**
 * Test cases for {@link ConfigurationFolderMonitor}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public final class TestConfigurationFolderMonitor
    implements ConfigurationFileChangedListener {

    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(TestConfigurationFolderMonitor.class);

    private static final String EXISTING_FILE_NAME = "existing.xml";
    private static final String NEW_FILE_NAME = "new.xml";

    @Rule public Timeout globalTimeout= new Timeout(10, TimeUnit.SECONDS);
    @Rule public TemporaryFolder monitoredFolder = new TemporaryFolder();

    /**
     * CountDownLatch for test setup steps. Begin assertions after:
     * 1. File action (create / update / delete) and;
     * 2. ConfigurationFolderMonitor has been notified
     */
    private CountDownLatch readyForAssertions = new CountDownLatch(2);

    private File existingFile;

    /** The reported file. */
    private File reportedFile;

    /** The reported action. */
    private String action;

    /**
     * Setup the test environment.
     */
    @Before
    public void setUp() throws Exception {
        final ConfigurationFolderMonitor monitor = new ConfigurationFolderMonitor(monitoredFolder.getRoot());
        monitor.addListener(this);
        monitor.start();

        existingFile = monitoredFolder.newFile(EXISTING_FILE_NAME);
        Thread.sleep(500);

        // Ignore notifications so far - reset the latch
        readyForAssertions = new CountDownLatch(2);
    }

    /**
     * Tests the configuration folder monitor is aware of new file add
     * @throws Exception
     *         test failed
     */
    @Test
    public void testNewFileAdded() throws Exception {

        monitoredFolder.newFile(NEW_FILE_NAME);
        readyForAssertions.countDown();
        readyForAssertions.await();

        LOGGER.info("observed '" + action + "' on " + reportedFile);

        File expectedFile = new File(monitoredFolder.getRoot(), NEW_FILE_NAME);
        Assert.assertEquals(expectedFile, reportedFile);
        Assert.assertEquals("added", action);
    }


    /**
     * Tests the configuration folder monitor is aware of file updates
     * @throws Exception
     *         test failed
     */
    @Test
    public void testFileUpdated() throws Exception {

        final FileWriter writer = new FileWriter(existingFile);
        writer.write("test");
        writer.close();
        readyForAssertions.countDown();
        readyForAssertions.await();

        LOGGER.info("observed '" + action + "' on " + reportedFile);

        File expectedFile = new File(monitoredFolder.getRoot(), EXISTING_FILE_NAME);
        Assert.assertEquals(expectedFile, reportedFile);
        Assert.assertEquals("updated", action);
    }



    /**
     * Tests the configuration folder monitor is aware of file deletion
     * @throws Exception
     *         test failed
     */
    @Test
    public void testFileDeleted() throws Exception {

        existingFile.delete();
        readyForAssertions.countDown();
        readyForAssertions.await();

        LOGGER.info("observed '" + action + "' on " + reportedFile);

        File expectedFile = new File(monitoredFolder.getRoot(), EXISTING_FILE_NAME);
        Assert.assertEquals(expectedFile, reportedFile);
        Assert.assertEquals("deleted", action);
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
