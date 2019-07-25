/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.xml.IgnoringEntityResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

/**
 * Cached configuration file repository.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
final class ConfigurationRepository
    implements ConfigurationFileChangedListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
        LogManager.getLogger(ConfigurationRepository.class);

    /** Size of the read buffer when reading objects. */
    private static final int READ_BUFFER_SIZE = 1024;

    /** Location of the config folder. */
    private final File configFolder;

    /** Known configuration files. */
    private final Map<File, byte[]> configurationFiles;

    /**
     * Constructs a new object.
     * @param config the configuration folder
     * @exception IOException
     *            error reading from the configuration folder
     */
    ConfigurationRepository(final File config) throws IOException {
        configFolder = config;
        configurationFiles = new java.util.HashMap<File, byte[]>();
        final ConfigurationFolderMonitor monitor =
                new ConfigurationFolderMonitor(config);
        monitor.addListener(this);
        monitor.start();
        try {
            monitor.waitScanCompleted();
        } catch (InterruptedException e) {
            return;
        }
    }

    /**
     * Retrieves the contents of the given configuration file.
     * @param file the file
     * @return the contents of the file.
     * @since 0.7.6
     */
    public byte[] getConfigurationFile(final File file) {
        return configurationFiles.get(file);
    }

    /**
     * Retrieves the files within the configuration folder. 
     * @return configuration files
     * @throws IOException
     *         error listing the configuration files
     */
    private Collection<File> getConfigFiles() throws IOException {
        final Collection<File> files = new java.util.ArrayList<File>();
        for (File file : configurationFiles.keySet()) {
            final String name = file.getName();
            if (name.endsWith(".xml")) {
                files.add(file);
            }
        }
        return files;
    }

    /**
     * Retrieves all files for the given configuration base.
     *
     * <p>
     * All XML files in the config folder are scanned if they have the given
     * document root element.
     * </p>
     *
     * @param root name of the root element.
     * @return list of configuration files with the given root.
     * @exception IOException
     *            error reading the configuration files.
     * @since 0.7
     */
    public Collection<File> getConfigurationFiles(final String root)
        throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("looking for configurations '" + root + "'");
        }
        final Collection<File> children = getConfigFiles();
        if (children == null) {
            LOGGER.warn("no configuration files found at '"
                    + configFolder.getCanonicalPath() + "'");
            return null;
        }
        final Collection<File> files = new java.util.ArrayList<File>();
        final DocumentBuilderFactory dbfactory =
            DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder;
        try {
            builder = dbfactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IOException(e.getMessage());
        }
        final EntityResolver resolver = new IgnoringEntityResolver();
        builder.setEntityResolver(resolver);

        // reset class loader
        final ClassLoader loader =
                Thread.currentThread().getContextClassLoader();
        final URLClassLoader urlloader = new URLClassLoader(new URL[0], loader);
        Thread.currentThread().setContextClassLoader(urlloader);

        // inspect the files
        final XPathFactory xpathFactory = XPathFactory.newInstance();
        final XPath xpath = xpathFactory.newXPath();
        for (File current : children) {
            final Node node;
            try {
                final byte[] buffer;
                synchronized (configurationFiles) {
                    buffer = configurationFiles.get(current);
                    if (buffer == null) {
                        continue;
                    }
                }
                final InputStream in = new ByteArrayInputStream(buffer);
                final Document document = builder.parse(in);
                final Element element = document.getDocumentElement();
                node = (Node) xpath.evaluate("/" + root, element,
                        XPathConstants.NODE);
                if (node != null) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("inspecting file '"
                                + current.getCanonicalPath() + "'");
                    }
                    files.add(current);
                }
            } catch (XPathExpressionException | SAXException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("error inspecting configuration file '"
                            + current.getCanonicalPath() + "'", e);
                }
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("found " + files.size() +
                    " matching configurations for '" + root + "'");
        }
        return files;
    }

    /**
     * Loads the given configuration file.
     * @param file the file to load
     * @throws IOException
     *         error loading the configuration file
     */
    private void loadConfigurationFile(final File file) throws IOException {
        final byte[] readBuffer = new byte[READ_BUFFER_SIZE];
        final InputStream input = new FileInputStream(file);
        final byte[] bytes;
        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int num;
            do {
                num = input.read(readBuffer);
                if (num >= 0) {
                    buffer.write(readBuffer, 0, num);
                }
            } while(num >= 0);
            bytes = buffer.toByteArray();
        } finally {
            input.close();
        }
        synchronized (configurationFiles) {
            configurationFiles.put(file, bytes);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fileAdded(final File file) {
        try {
            loadConfigurationFile(file);
            LOGGER.info("added config file '" + file.getCanonicalPath()
                    + "'");
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fileUpdated(final File file) {
        try {
            loadConfigurationFile(file);
            LOGGER.info("updated config file '" + file.getCanonicalPath()
                    + "'");
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fileRemoved(final File file) {
        synchronized (configurationFiles) {
            configurationFiles.remove(file);
        }
        try {
            LOGGER.info("removed config file '" + file.getCanonicalPath()
                    + "'");
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

}
