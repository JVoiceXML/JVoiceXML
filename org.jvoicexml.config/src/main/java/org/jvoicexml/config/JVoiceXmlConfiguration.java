/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Map;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.xml.IgnoringEntityResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
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
 */
public final class JVoiceXmlConfiguration implements Configuration {
    /** Logger for this class. */
    private static final Logger LOGGER =
        LogManager.getLogger(JVoiceXmlConfiguration.class);;

    /** The factory to retrieve configured objects. */
    private ApplicationContext context;

    /** Known class loader repositories. */
    private final Map<String, JVoiceXmlClassLoader> loaderRepositories;

    /** Location of the config folder. */
    private final File configFolder;

    /** The configuration file storage. */
    private ConfigurationRepository configurationRepository;

    /** The cached SAX Parser Factory. */
    private SAXParserFactory parserFactory;

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
        final File canonicalFile;
        try {
            canonicalFile = resource.getCanonicalFile();
            LOGGER.info("loading configurations from '"
                    + configFolder.getCanonicalPath() + "'");
        } catch (IOException e) {
            configurationRepository = null;
            LOGGER.error(e.getMessage(), e);
            return;
        }
        if (resource.exists()) {
            try {
                final URI uri = canonicalFile.toURI();
                final String uriPath = uri.toString();
                context = new FileSystemXmlApplicationContext(uriPath);
            } catch (BeansException e) {
                LOGGER.error("unable to load configuration", e);
                context = null;
            }
        } else {
            LOGGER.error("main configruation file '" + resource 
                    + "' does not seam to exist. Cannot create xontext.");
        }
        try {
            configurationRepository = new ConfigurationRepository(configFolder);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }
    }

    /**
     * Retrieves the parent class loader to use.
     * @return parent class loader
     * @since 0.7.9
     */
    @SuppressWarnings("resource")
    private ClassLoader getParentClassLoader() {
        // Must use the classloader of this class as parent to ensure that all
        // all repos use the same basis regardless if called via
        // loadObjects directly or indirectly from an init method of a loaded
        // class
        // The class loader of this class is the app launcher class loader
        final ClassLoader parent = JVoiceXmlConfiguration.class.getClassLoader();
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("parent class loader '" + parent + "'");
            if (parent instanceof URLClassLoader) {
                final URLClassLoader urlLoader = (URLClassLoader) parent;
                final URL[] urls = urlLoader.getURLs();
                if (urls.length == 0) {
                    LOGGER.trace("parent class loader entry: none");
                } else {
                    for (URL url : urls) {
                        LOGGER.trace("parent class loader entry: '" + url
                                + "'");
                    }
                }
            }
        }
        return parent;
    }
    
    /**
     * Retrieves the class loader to use for the given loader repository.
     * @param repository name of the loader repository
     * @return class loader to use.
     */
    private JVoiceXmlClassLoader getClassLoader(final String repository) {
        if (repository == null) {
            final ClassLoader parent = getParentClassLoader();
            return new JVoiceXmlClassLoader(parent);
        }
        JVoiceXmlClassLoader loader = loaderRepositories.get(repository);
        if (loader == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("creating new loader repository '" + repository
                        + "'");
            }
            final ClassLoader parent = getParentClassLoader();
            loader = new JVoiceXmlClassLoader(parent, repository);
            loaderRepositories.put(repository, loader);
            // TODO resolve why the delegate principle does not work in RMI
            // As a workaround copy the path entries from the parent loader
            // to this instance
            // see https://stackoverflow.com/questions/58648325/delegation-of-custom-class-loader-in-rmi
            if (parent instanceof URLClassLoader) {
                final URLClassLoader parentLoader = (URLClassLoader) parent;
                for (URL url : parentLoader.getURLs()) {
                    loader.addURL(url);
                }
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("adding to loader repository '" + repository
                        + "'");
            }
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
        try {
            final TransformerFactory tf = TransformerFactory.newInstance();
            final TransformerHandler th = ((SAXTransformerFactory) tf)
                    .newTransformerHandler();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
                final Result result = new StreamResult(out);
            th.setResult(result);
            final SAXParser parser = getSAXParser();
            final XMLFilterImpl filter = new BeansFilter(parser.getXMLReader());
            filter.setContentHandler(th);
            final EntityResolver resolver = new IgnoringEntityResolver();
            filter.setEntityResolver(resolver);
            final byte[] buffer =
                    configurationRepository.getConfigurationFile(file);
            if (buffer == null) {
                return null;
            }
            final InputStream in = new ByteArrayInputStream(buffer);
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
     * Lazy instantiation of a SAX Parser.
     * @return SAX parser
     * @throws TransformerConfigurationException
     *         if the SAX parser could not be created
     * @throws SAXException
     *         if the SAX parser could not be created
     * @throws ParserConfigurationException
     *         if the SAX parser could not be created
     * @since 0.7.5
     */
    private SAXParser getSAXParser()
            throws TransformerConfigurationException, SAXException,
            ParserConfigurationException {
        if (parserFactory == null) {
            parserFactory = SAXParserFactory.newInstance();
            parserFactory.setValidating(false);
            parserFactory.setNamespaceAware(true);
            parserFactory.setFeature(
                    "http://xml.org/sax/features/namespace-prefixes", true);
        }
        return parserFactory.newSAXParser();
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
    Collection<File> getConfigurationFiles(final String root)
        throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("looking for configurations '" + root + "'");
        }
        return configurationRepository.getConfigurationFiles(root);
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
        final byte[] buffer =
                configurationRepository.getConfigurationFile(file);
        if (buffer == null) {
            return null;
        }
        final InputStream input = new ByteArrayInputStream(buffer);
        final Source source = new StreamSource(input);
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
    public synchronized <T extends Object> Collection<T> loadObjects(
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
                if (resource == null) {
                    continue;
                }
                final DefaultListableBeanFactory factory =
                        new DefaultListableBeanFactory();
                final XmlBeanDefinitionReader reader =
                        new XmlBeanDefinitionReader(factory);
                reader.loadBeanDefinitions(resource);
                final ClasspathExtractor extractor =
                    getClassPathExtractor(file);
                if (extractor == null) {
                    continue;
                }
                final String repository = extractor.getLoaderRepostory();
                final JVoiceXmlClassLoader loader = getClassLoader(repository);
                final URL[] urls = extractor.getClasspathEntries();
                loader.addURLs(urls);
                if (LOGGER.isDebugEnabled()) {
                    for (URL url : urls) {
                        LOGGER.debug("using classpath entry '" + url + "'");
                    }
                }
                factory.setBeanClassLoader(loader);
                final String[] names =
                        factory.getBeanNamesForType(baseClass);
                if (names.length == 0) {
                    LOGGER.info("no loadable objects of type '" + baseClass 
                            + "' in file '"
                            + file.getCanonicalPath() + "'");
                } else {
                    for (String name : names) {
                        LOGGER.info("loading '" + name + "'");
                        final Object o =
                                factory.getBean(name, baseClass);
                        final T bean = baseClass.cast(o);
                        beans.add(bean);
                        if (bean instanceof ExtendedConfiguration) {
                            final ExtendedConfiguration config =
                                (ExtendedConfiguration) bean;
                            config.setConfigurationFile(file);
                        }
                    }
                }
            } catch (IOException | BeansException e) {
                throw new ConfigurationException(e.getMessage(), e);
            }
        }
        return beans;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized <T extends Object> T loadObject(
            final Class<T> baseClass, final String key)
        throws ConfigurationException {
        if (context == null) {
            LOGGER.warn("configuration error. unable to load object: key '"
                    + key + "' from a null configuration");
            return null;
        }
        if (!context.containsBean(key)) {
            LOGGER.warn("unable to load object: key '" + key + "' not found");
            return null;
        }
        final Object object;
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("loading bean with id '" + key + "'");
            }
            object = context.getBean(key, baseClass);
        } catch (BeansException e) {
            throw new ConfigurationException(e.getMessage(), e);
        }

        return baseClass.cast(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized <T extends Object> T loadObject(
            final Class<T> baseClass)
        throws ConfigurationException {
        final String key = baseClass.getCanonicalName();
        return loadObject(baseClass, key);
    }
}
