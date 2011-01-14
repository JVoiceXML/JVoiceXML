/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/config/JVoiceXmlConfiguration.java $
 * Version: $LastChangedRevision: 2495 $
 * Date:    $LastChangedDate: 2011-01-11 09:35:51 +0100 (Di, 11 Jan 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * The <code>JVoiceXMLConfiguration</code> is the base class to retrieve
 * custom configuration settings for each component.
 *
 * <p>
 * This is the central for all configuration settings. It offers
 * access to custom configurations for each component using spring:
 * <ul>
 * <li>implementation</li>
 * <li>interpreter</li>
 * <li>document server</li>
 * </ul>
 *
 * The configuration is structured as spring beans to enable
 * configuration by injection.
 * </p>
 * <p>
 * Schema files for the configuration are located in the
 * <code>config</code> folder or can be obtained from
 * <a href="http://www.jvoicexml.org/xsd">http://www.jvoicexml.org/xsd</a>.
 * </p>
 *
 * <p>
 * The <code>config</code> can be used to load the
 * beans via:<br>
 * <code>
 * T object = config.loadObject(T.class, &lt;key&gt;);
 * </code>
 * </p>
 *
 * <p>
 * The configuration files are expected to be a in the folder
 * <code>${JVOICEXML_HOME}/config</code>. This default value can be changed via
 * the system property <code>jvoicexml.config</code>.
 * </p>
 *
 * @author Arindam Das
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision: 2495 $
 */
public final class JVoiceXmlConfiguration implements Configuration {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(JVoiceXmlConfiguration.class);;

    /** The factory to retrieve configured objects. */
    private XmlBeanFactory factory;

    /** Known class loader repositories. */
    private final Map<String, JVoiceXmlClassLoader> loaderRepositories;

    /** Location of the config folder. */
    private final File configFolder;

    /**
     * Constructs a new object.
     */
    public JVoiceXmlConfiguration() {
        final String filename =
            System.getProperty("jvoicexml.config", "config");
        configFolder = new File(filename);
        loaderRepositories =
            new java.util.HashMap<String, JVoiceXmlClassLoader>();
        final File resource = new File(configFolder, "jvoicexml.xml");
        try {
            LOGGER.info("loading configurations from '"
                    + configFolder.getCanonicalPath() + "'");
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }
        if (resource.exists()) {
            final Resource res = new FileSystemResource(resource); 
            try {
                factory = new XmlBeanFactory(res);
            } catch (BeansException e) {
                LOGGER.error("unable to load configuration", e);
                factory = null;
            }
        }
    }


    /**
     * Retrieves the class loader to use for the given loader repository.
     * @param repository name of the loader repository
     * @return class loader to use.
     */
    private JVoiceXmlClassLoader getClassLoader(final String repository) {
        if (repository == null) {
            final Thread thread = Thread.currentThread();
            final ClassLoader parent = thread.getContextClassLoader();
            return new JVoiceXmlClassLoader(parent);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("using loader repository '" + repository + "'");
        }
        JVoiceXmlClassLoader loader = loaderRepositories.get(repository);
        if (loader == null) {
            final Thread thread = Thread.currentThread();
            final ClassLoader parent = thread.getContextClassLoader();
            loader = new JVoiceXmlClassLoader(parent);
            loaderRepositories.put(repository, loader);
        }
        return loader;
    }

    /**
     * Retrieves a resource that can be used as a configuration input.
     * @param file configuration file to load.
     * @return resource to use.
     * @throws IOException
     *         if an error occurs while reading the resource
     * @since 0.7
     */
    private Resource getResource(final File file)
        throws IOException {
        final TransformerFactory tf = TransformerFactory.newInstance();
        try {
            final TransformerHandler th = ((SAXTransformerFactory) tf)
            .newTransformerHandler();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final Result result = new StreamResult(out);
            th.setResult(result);
            final SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(false);
            spf.setNamespaceAware(true);
            spf.setFeature("http://xml.org/sax/features/namespace-prefixes",
                    true);
            final SAXParser parser = spf.newSAXParser();
            final XMLFilterImpl filter = new BeansFilter(parser.getXMLReader());
            filter.setContentHandler(th);
            final EntityResolver resolver = new IgnoringEntityResolver();
            filter.setEntityResolver(resolver);
            final InputStream in = new FileInputStream(file);
            final InputSource input = new InputSource(in);
            filter.parse(input);
            final byte[] bytes = out.toByteArray();
            return new ByteArrayResource(bytes);
        } catch (TransformerConfigurationException e) {
            throw new IOException(e.getMessage());
        } catch (SAXException e) {
            throw new IOException(e.getMessage());
        } catch (ParserConfigurationException e) {
            throw new IOException(e.getMessage());
        }
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
        final XPathFactory xpathFactory = XPathFactory.newInstance();
        final XPath xpath = xpathFactory.newXPath();
        final FileFilter filter = new XMLFileFilter();
        final File[] children = configFolder.listFiles(filter);
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

    /**
     * Retrieves a class path extractor for the given file.
     * @param file the file to be parsed.
     * @return class path extractor with values from the given file.
     * @throws IOException
     *         error parsing the file
     */
    private ClasspathExtractor getClassPathExtractor(final File file)
            throws IOException {
        final TransformerFactory transformerFactory =
            TransformerFactory.newInstance();
        final Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new IOException(e.getMessage());
        }
        final Source source = new StreamSource(file);
        final ClasspathExtractor extractor = new ClasspathExtractor();
        final Result result = new SAXResult(extractor);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new IOException(e.getMessage());
        }
        return extractor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Object> Collection<T> loadObjects(
            final Class<T> baseClass, final String root)
            throws ConfigurationException {
        final Collection<T> beans = new java.util.ArrayList<T>();
        final Collection<File> files;
        try {
            files = getConfigurationFiles(root);
        } catch (IOException e) {
            throw new ConfigurationException(e.getMessage(), e);
        }
        for (File file : files) {
            try {
                LOGGER.info("loading configuration '" + file.getCanonicalPath()
                        + "'...");
                final Resource resource = getResource(file);
                final XmlBeanFactory beanFactory = new XmlBeanFactory(resource);
                final ClasspathExtractor extractor =
                    getClassPathExtractor(file);
                final String repository = extractor.getLoaderRepostory();
                final JVoiceXmlClassLoader loader = getClassLoader(repository);
                final URL[] urls = extractor.getClasspathEntries();
                loader.addURLs(urls);
                if (LOGGER.isDebugEnabled()) {
                    for (URL url : urls) {
                        LOGGER.debug("using classpath entry '" + url + "'");
                    }
                }
                beanFactory.setBeanClassLoader(loader);
                final String[] names =
                    beanFactory.getBeanNamesForType(baseClass);
                if (names.length == 0) {
                    LOGGER.info("no loadable objects of type '" + baseClass 
                            + "' in file '"
                            + file.getCanonicalPath() + "'");
                } else {
                    for (String name : names) {
                        LOGGER.info("loading '" + name + "'");
                        final Object o = beanFactory.getBean(name, baseClass);
                        final T bean = baseClass.cast(o);
                        beans.add(bean);
                        if (bean instanceof ExtendedConfiguration) {
                            final ExtendedConfiguration config =
                                (ExtendedConfiguration) bean;
                            config.setConfigurationFile(file);
                        }
                    }
                }
            } catch (IOException e) {
                throw new ConfigurationException(e.getMessage(), e);
            }
        }
        return beans;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Object> T loadObject(final Class<T> baseClass,
                                           final String key)
        throws ConfigurationException {
        if (factory == null) {
            LOGGER.warn("configuration error. unable to load object: key '"
                    + key + "' from a null configuration");
            return null;
        }
        if (!factory.containsBean(key)) {
            LOGGER.warn("unable to load object: key '" + key + "' not found");
            return null;
        }
        final Object object;
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("loading bean with id '" + key + "'");
            }
            object = factory.getBean(key, baseClass);
        } catch (org.springframework.beans.BeansException e) {
            throw new ConfigurationException(e.getMessage(), e);
        }

        return baseClass.cast(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Object> T loadObject(final Class<T> baseClass)
        throws ConfigurationException {
        final String key = baseClass.getCanonicalName();
        return loadObject(baseClass, key);
    }
}
