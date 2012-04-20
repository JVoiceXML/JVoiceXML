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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.jvoicexml.xml.IgnoringEntityResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

/**
 * Cached configuration file repository.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
final class ConfigurationRepository {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(ConfigurationRepository.class);

    /** Location of the config folder. */
    private final File configFolder;

    /** Files in the config folder. */
    private File[] configFiles;

    /** The timestamp when the config files where retrieved the last time. */
    private long timestampRetrievedConfigFiles;

    /** 
     * The time-interval after which the configuration folder should
     * be reread.
     */
    private static final long REFRESH_INTERVAL = 1000 * 60 * 10;

    /**
     * Constructs a new object.
     * @param config the configuration folder
     */
    public ConfigurationRepository(final File config) {
        configFolder = config;
    }

    /**
     * Retrieves the files within the configuration folder. 
     * @return configuration files
     * @throws IOException
     *         error listing the configuration files
     */
    private File[] getConfigFiles() throws IOException {
        final long now = System.currentTimeMillis();
        if ((configFiles == null)
                || (now - timestampRetrievedConfigFiles > REFRESH_INTERVAL)) {
            final FileFilter filter = new XMLFileFilter();
            configFiles = configFolder.listFiles(filter);
            if (configFiles == null) {
                LOGGER.warn("no configuration files found at '"
                        + configFolder.getCanonicalPath() + "'");
            } else {
                timestampRetrievedConfigFiles = System.currentTimeMillis();
                LOGGER.info("(re-)reading configuration folder: "
                        + configFiles.length + " files");
            }
        }
        return configFiles;
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
        final File[] children = getConfigFiles();
        if (children == null) {
            LOGGER.warn("no configuration files found at '"
                    + configFolder.getCanonicalPath() + "'");
            return null;
        }
        final Collection<File> files = new java.util.ArrayList<File>();
        final DocumentBuilderFactory dbfactory =
            DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = dbfactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IOException(e.getMessage());
        }
        final EntityResolver resolver = new IgnoringEntityResolver();
        builder.setEntityResolver(resolver);
        final XPathFactory xpathFactory = XPathFactory.newInstance();
        final XPath xpath = xpathFactory.newXPath();
        for (File current : children) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("inspecting file '"
                        + current.getCanonicalPath() + "'");
            }
            final Node node;
            try {
                final Document document = builder.parse(current);
                final Element element = document.getDocumentElement();
                node = (Node) xpath.evaluate("/" + root, element,
                        XPathConstants.NODE);
                if (node != null) {
                    files.add(current);
                }
            } catch (XPathExpressionException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("error inspecting configuration files", e);
                }
            } catch (SAXException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("error inspecting configuration files", e);
                }
            }
        }
        return files;
    }

}
