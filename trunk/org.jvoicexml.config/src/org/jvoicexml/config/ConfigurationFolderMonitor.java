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
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collection;
import java.util.List;

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

    /** Stop request. */
    private boolean stop;

    /** Location of the config folder. */
    private final File configFolder;

    /** Know listeners for configuration file change events. */
    private final Collection<ConfigurationFileChangedListener> listeners;
    
    /** Notification of the completion of the first scan. */
    private final Object lock;

    /** The watcher of the configuration folder. */
    private final WatchService watcher;
    
    /**
     * Constructs a new object.
     * @param folder the configuration folder
     * @exception IOException
     *            error creating the watcher
     */
    public ConfigurationFolderMonitor(final File folder) throws IOException {
        configFolder = folder;
        listeners = new java.util.ArrayList<ConfigurationFileChangedListener>();
        setDaemon(true);
        lock = new Object();
        final FileSystem filesystem = FileSystems.getDefault();
        watcher = filesystem.newWatchService();
        final String canonicalPath = folder.getCanonicalPath();
        final Path path = filesystem.getPath(canonicalPath);
        path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);
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
        if (LOGGER.isDebugEnabled()) {
            try {
                LOGGER.debug("scanning configuration folder '"
                      + configFolder.getCanonicalPath() + "'");
            } catch (IOException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        final FileFilter filter = new XMLFileFilter();
        final File[] configurationFiles = configFolder.listFiles(filter);
        try {
            final FileSystem filesystem = FileSystems.getDefault();
            for (File file : configurationFiles) {
                final String name = file.getCanonicalPath();
                final Path path = filesystem.getPath(name);
                notifyFileAdded(path);
            }
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        synchronized (lock) {
            lock.notifyAll();
        }
        while (!stop) {
            try {
                final WatchKey key = watcher.take();
                final List<WatchEvent<?>> events = key.pollEvents();
                for (WatchEvent<?> event : events) {
                    final Kind<?> kind = event.kind();
                    try {
                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            final Path path = (Path) event.context();
                                notifyFileAdded(path);
                        } else if (kind
                                == StandardWatchEventKinds.ENTRY_DELETE) {
                            final Path path = (Path) event.context();
                            notifyFileRemoved(path);
                        } else if (kind
                                == StandardWatchEventKinds.ENTRY_MODIFY) {
                            final Path path = (Path) event.context();
                            notifyFileUpdated(path);
                        }
                    } catch (IOException e) {
                        LOGGER.warn(e.getMessage(), e);
                    }
                }
            } catch (ClosedWatchServiceException | InterruptedException e1) {
                return;
            }
        }
    }

    /**
     * Resolves this path against the config folder.
     * @param path the observed path
     * @return a file object relative to the config folder.
     * @throws IOException error resolving the path
     */
    private File resolve(final Path path) throws IOException {
        final FileSystem filesystem = FileSystems.getDefault();
        final String canonicalPath = configFolder.getCanonicalPath();
        final Path configFolderPath = filesystem.getPath(canonicalPath);
        final Path resolvedPath = configFolderPath.resolve(path);
        return resolvedPath.toFile();
    }

    /**
     * Notifies all registered listeners that a file has been added.
     * @param path path of the added file
     * @throws IOException error resolving the path
     */
    private void notifyFileAdded(final Path path) throws IOException {
        final File file = resolve(path);
        if (LOGGER.isDebugEnabled()) {
            try {
                LOGGER.debug("added '" + file.getCanonicalPath()
                        + "'");
            } catch (IOException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        for (ConfigurationFileChangedListener listener
                : listeners) {
            listener.fileAdded(file);
        }
    }

    /**
     * Notifies all registered listeners that a file has been removed.
     * @param path path of the added file
     * @throws IOException error resolving the path
     */
    private void notifyFileRemoved(final Path path) throws IOException {
        final File file = resolve(path);
        if (LOGGER.isDebugEnabled()) {
            try {
                LOGGER.debug("removed '" + file.getCanonicalPath()
                        + "'");
            } catch (IOException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        for (ConfigurationFileChangedListener listener
                : listeners) {
            listener.fileRemoved(file);
        }
    }

    /**
     * Notifies all registered listeners that a file has been modified.
     * @param path path of the added file
     * @throws IOException error resolving the path
     */
    private void notifyFileUpdated(final Path path) throws IOException {
        final File file = resolve(path);
        if (LOGGER.isDebugEnabled()) {
            try {
                LOGGER.debug("updated '" + file.getCanonicalPath()
                        + "'");
            } catch (IOException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        for (ConfigurationFileChangedListener listener
                : listeners) {
            listener.fileUpdated(file);
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
     * Asks this thread to stop monitoring.
     * @exception IOException
     *            error closing the folder monitor
     */
    public void stopMonitoring() throws IOException {
        stop = true;
        watcher.close();
    }
}
