/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
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

import junit.framework.Assert;

import org.apache.log4j.Logger;
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
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(TestConfigurationFolderMonitor.class);

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
        final long delay = 500;
        final File configFolder = new File(
                "../org.jvoicexml.config/unittests/config");
        final ConfigurationFolderMonitor monitor =
                new ConfigurationFolderMonitor(configFolder);
        monitor.addListener(this);
        monitor.start();
        synchronized (lock) {
            lock.wait();
        }
        LOGGER.info(action + " on " + reportedFile);
        Assert.assertEquals(new File(
            "../org.jvoicexml.config/unittests/config/test-implementation.xml").getCanonicalPath(),
            reportedFile.getCanonicalPath());
        Assert.assertEquals("added", action);
        reportedFile = null;
        action = null;
        final File file = new File(
                "../org.jvoicexml.config/unittests/config/test.xml");
        file.deleteOnExit();

        // Added
        Thread.sleep(100);
        file.createNewFile();
        synchronized (lock) {
            lock.wait();
        }
        LOGGER.info("observed '" + action + "' on " + reportedFile);
        Assert.assertEquals(file.getCanonicalPath(), reportedFile.getCanonicalPath());
        Assert.assertEquals("added", action);
        reportedFile = null;
        action = null;
        Thread.sleep(delay);

        // Updated
        Thread.sleep(100);
        final FileWriter writer = new FileWriter(file);
        writer.write("test");
        writer.close();
        synchronized (lock) {
            lock.wait();
        }
        LOGGER.info("observed '" + action + "' on " + reportedFile);
        Assert.assertEquals(file, reportedFile);
        Assert.assertEquals("updated", action);
        reportedFile = null;
        action = null;
        Thread.sleep(delay);

        // removed
        Thread.sleep(100);
        file.delete();
        synchronized (lock) {
            lock.wait();
        }
        LOGGER.info("observed '" + action + "' on " + reportedFile);
        Assert.assertEquals(file, reportedFile);
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
