/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Monitoring thread for the configuration folder.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class ConfigurationFolderMonitor extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(ConfigurationFolderMonitor.class);

    /** Delay between two scans of the configuration folder. */
    private static final int DEFAULT_DELAY = 60 * 1000;

    /** Known files. */
    private final Map<File, Long> files;

    /** Stop request. */
    private boolean stop;

    /** Location of the config folder. */
    private final File configFolder;

    /** Know listeners for configuration file change events. */
    private final Collection<ConfigurationFileChangedListener> listeners;

    /** A filter for files to look for. */
    private final FileFilter filter;

    /** The delay between two polls. */
    private long delay;

    /** Notification of the completion of the first scan. */
    private final Object lock;

    /**
     * Constructs a new object.
     * @param folder the configuration folder
     */
    public ConfigurationFolderMonitor(final File folder) {
        files = new java.util.HashMap<File, Long>();
        configFolder = folder;
        listeners = new java.util.ArrayList<ConfigurationFileChangedListener>();
        filter = new XMLFileFilter();
        delay = DEFAULT_DELAY;
        setDaemon(true);
        lock = new Object();
    }

    /**
     * Sets the delay between two polls of the configuration folder.
     * @param msec msec to wait
     */
    public void setDelay(final long msec) {
        delay = msec;
    }

    /**
     * Adds the given listener to the list of known listeners.
     * @param listener the listener to add.
     */
    public void addListener(final ConfigurationFileChangedListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the given listener from the list of known listeners.
     * @param listener the listener to remove.
     */
    public void removeListener(
            final ConfigurationFileChangedListener listener) {
        listeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        while (!stop) {
            if (LOGGER.isDebugEnabled()) {
                try {
                    LOGGER.debug("scanning configuration folder '"
                            + configFolder.getCanonicalPath() + "'");
                } catch (IOException e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
            final File[] configurationFiles = configFolder.listFiles(filter);
            checkAddedUpdatedFiles(configurationFiles);
            checkRemovedFiles(configurationFiles);
            synchronized (lock) {
                lock.notifyAll();
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    /**
     * Delays until a complete scan of the configuration folder is completed.
     * TODO Should be removed. Currently, this is necessary to ensure that
     * all configuration files have been read.
     * @throws InterruptedException
     *         error waiting for the completion
     */
    public void waitScanCompleted() throws InterruptedException {
        synchronized (lock) {
            lock.wait();
        }
    }

    /**
     * Checks, if any files have been added or updates.
     * @param configurationFiles the current files in the directory
     */
    private void checkAddedUpdatedFiles(final File[] configurationFiles) {
        if (configurationFiles == null) {
            LOGGER.warn("unable to check configuration files: no files given");
        }
        for (File file : configurationFiles) {
            try {
                processFile(file);
            } catch (IOException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }

    /**
     * Checks, if any files have been removed.
     * @param configurationFiles the current files in the directory
     */
    private void checkRemovedFiles(final File[] configurationFiles) {
        final Collection<File> toRemove = new java.util.ArrayList<File>();
        for (File file : files.keySet()) {
            boolean found = false;
            for (File configurationFile : configurationFiles) {
                if (file.equals(configurationFile)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                toRemove.add(file);
                for (ConfigurationFileChangedListener listener
                        : listeners) {
                    listener.fileRemoved(file);
                }
            }
        }
        files.remove(toRemove);
    }

    /**
     * Processes the given file.
     * @param file the file to process
     * @throws IOException
     *         error processing the files
     */
    private void processFile(final File file) throws IOException {
        final long lastModified = file.lastModified();
        final Long modified = files.get(file);
        if (modified == null) {
            files.put(file, lastModified);
            for (ConfigurationFileChangedListener listener : listeners) {
                listener.fileAdded(file);
            }
        } else if (modified.longValue() < lastModified) {
            files.put(file, lastModified);
            for (ConfigurationFileChangedListener listener : listeners) {
                listener.fileUpdated(file);
            }
        }
    }

    /**
     * Asks this thread to stop monitoring.
     */
    public void stopMonitoring() {
        stop = true;
    }
}
