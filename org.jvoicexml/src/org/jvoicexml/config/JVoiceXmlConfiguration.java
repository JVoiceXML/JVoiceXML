/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;

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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * The <code>JVoiceXMLConfiguration</code> is the base class to retrieve
 * custom configuration settings for each component.
 *
 * <p>
 * This is the central for all configuration settings. It offers
 * access to custom configurations for each component:
 * <ul>
 * <li>implementation</li>
 * <li>interpreter</li>
 * <li>document server</li>
 * </ul>
 *
 * The configuration is structured as spring beans to enable
 * configuration by injection.
 * </p>
 *
 * <p>
 * Access to the configuration can be obtained via<br>
 * <br>
 * <code>
 * JVoiceXmlConfiguration config = JVoiceXmlConfiguration.getInstance()
 * </code><br>
 * <br>
 * Then, the references <code>config</code> can be used to load the
 * beans via:<br>
 * <code>
 * T object = config.loadObject(T.class, &lt;key&gt;);
 * </code>
 * </p>
 *
 * <p>
 * The main configuration file is expected to be a resource named
 * <code>/jvoicexml.xml</code>. This default value can be changed via the
 * system property <code>jvoicexml.config</code>.
 * </p>
 *
 * @author Arindam Das
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision$
 */
public final class JVoiceXmlConfiguration {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(JVoiceXmlConfiguration.class);;

    /** The singleton. */
    private static final JVoiceXmlConfiguration CONFIGURATION;

    /** The factory to retrieve configured objects. */
    private XmlBeanFactory factory;

    static {
        final String filename =
                System.getProperty("jvoicexml.config", "/jvoicexml.xml");

        CONFIGURATION = new JVoiceXmlConfiguration(filename);
    }

    /**
     * Do not create from outside.
     *
     * <p>
     * Loads the configuration.
     * </p>
     *
     * @param filename
     *        Location of the configuration file.
     */
    private JVoiceXmlConfiguration(final String filename) {
        final Resource res = new ClassPathResource(filename);
        try {
            factory = new XmlBeanFactory(res);
        } catch (BeansException e) {
            LOGGER.error("unable to load configuration", e);
            factory = null;
        }
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
        TransformerHandler th;
        try {
            th = ((SAXTransformerFactory) tf)
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
        final XPathFactory xpathFactory = XPathFactory.newInstance();
        final XPath xpath = xpathFactory.newXPath();
        final File config = new File("config");
        final FileFilter filter = new XMLFileFilter();
        final File[] children = config.listFiles(filter);
        final Collection<File> files = new java.util.ArrayList<File>();
        for (File current : children) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("inspecting file '" + current.getCanonicalPath()
                        + "'");
            }
            final Reader reader = new FileReader(current);
            final InputSource source = new InputSource(reader);
            final Node node;
            try {
                node = (Node) xpath.evaluate("/" + root, source,
                        XPathConstants.NODE);
                if (node != null) {
                    files.add(current);
                }
            } catch (XPathExpressionException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("error inspecting configuration files", e);
                }
            }
        }
        return files;
    }

    /**
     * Retrieves the class path entries from the given configuration file.
     * @param file the configuration file
     * @return detected class path entries.
     * @exception IOException
     *            error reading the class path entries
     */
    private URL[] getClasspathEntries(final File file) throws IOException {
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
        return extractor.getClasspathEntries();
    }

    /**
     * Loads all objects extending the <code>baseClass</code> from all
     * files with the given configuration base.
     * @param <T> type of class to loads
     * @param baseClass base class of the return type.
     * @param root
     * @param root name of the root element.
     * @return list of objects extending with the given root.
     * @since 0.7
     */
    public <T extends Object> Collection<T> loadObjects(
            final Class<T> baseClass, final String root) {
        final Collection<T> beans = new java.util.ArrayList<T>();
        final Collection<File> files;
        try {
            files = getConfigurationFiles(root);
        } catch (IOException e) {
            LOGGER.error("error obtaining the configuration files", e);
            return beans;
        }
        for (File file : files) {
            try {
                LOGGER.info("loading configuration '" + file.getCanonicalPath()
                        + "'...");
                final Resource resource = getResource(file);
                final XmlBeanFactory beanFactory = new XmlBeanFactory(resource);
                final ClassLoader parent =
                    JVoiceXmlConfiguration.class.getClassLoader();
                final URL[] urls = getClasspathEntries(file);
                final ClassLoader loader =
                    new JVoiceXmlClassLoader(urls, parent);
                if (LOGGER.isDebugEnabled()) {
                    for (URL url : urls) {
                        LOGGER.debug("using classpath entry '" + url + "'");
                    }
                }
                beanFactory.setBeanClassLoader(loader);
                final String[] names =
                    beanFactory.getBeanNamesForType(baseClass);
                for (String name : names) {
                    LOGGER.info("loading '" + name + "'...");
                    final Object o = beanFactory.getBean(name, baseClass);
                    final T bean = baseClass.cast(o);
                    beans.add(bean);
                }
            } catch (IOException e) {
                LOGGER.error("error reading '" + file + "'");
            }
        }
        return beans;
    }

    /**
     * Loads the object with the class defined by the given key.
     *
     * @param <T>
     *        Type of the object to load.
     * @param baseClass
     *        Base class of the return type.
     * @param key
     *        Key of the object to load.
     * @return Instance of the class, <code>null</code> if the
     *         object could not be loaded.
     */
    public <T extends Object> T loadObject(final Class<T> baseClass,
                                           final String key) {
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
            object = factory.getBean(key, baseClass);
        } catch (org.springframework.beans.BeansException e) {
            LOGGER.error("error loading bean '" + key + "'", e);

            return null;
        }

        return baseClass.cast(object);
    }

    /**
     * Get a reference to the singleton.
     *
     * @return The only JVoiceXML configuration instance.
     */
    public static JVoiceXmlConfiguration getInstance() {
        return CONFIGURATION;
    }
}
