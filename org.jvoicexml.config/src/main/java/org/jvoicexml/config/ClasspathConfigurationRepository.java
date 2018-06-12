package org.jvoicexml.config;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Cached configuration classpath based configuration files.
 * @author Stuart 'Stevie' Leitch
 * @since 0.7.8
 */
public class ClasspathConfigurationRepository implements ConfigurationRepository {

    /** Logger for this class. */
    private static final Logger LOGGER = LogManager.getLogger(ClasspathConfigurationRepository.class);

    /**
     * XML document builder
     */
    private DocumentBuilder documentBuilder;

    /** Known configuration files. */
    private Map<File, byte[]> configurationFiles;

    public ClasspathConfigurationRepository(final List<String> resourceFiles) throws IOException {
        try {
            this.documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOGGER.error("Failed to initialize ClasspathConfigurationRepository. Cannot create DocumentBuilder.", e);
            throw new IOException(e);
        }

        configurationFiles = resourceFiles.stream()
                .filter(filename -> filename.endsWith(".xml"))
                .collect(Collectors.toMap(this::toFile, this::toByteArray));

    }

    @Override
    public byte[] getConfigurationFile(File file) {
        return configurationFiles.get(file);
    }

    @Override
    public Collection<File> getConfigurationFiles(String root) {
        final Collection<File> configFiles = new ArrayList<>();
        for (Map.Entry<File, byte[]> configEntry : configurationFiles.entrySet()) {
            final File file = configEntry.getKey();
            final byte[] fileContents = configEntry.getValue();
            if (containsXmlRoot(file, fileContents, root)) {
                configFiles.add(file);
            }
        }
        LOGGER.debug("Found config files with root " + root + ": " + configFiles);
        return configFiles;
    }

    /**
     * Return true iff given file contains XML root node with given name
     */
    private boolean containsXmlRoot(File file, byte[] fileContents, String root) {
        boolean xmlRootMatch = false;
        try {
            Document xmlDoc = documentBuilder.parse(new ByteArrayInputStream(fileContents));
            Element rootElement = xmlDoc.getDocumentElement();
            xmlRootMatch = rootElement.getTagName().equals(root);
        } catch (IOException e) {
            LOGGER.warn("Failed to read file: " + file, e);
        } catch (SAXException e) {
            LOGGER.warn("Failed to read XML from file: " + file, e);
        }
        return xmlRootMatch;
    }

    /**
     * Return contents of given file as byte array
     * @param filename file accessible to classloader
     * @return file contents. Null if file cannot be read.
     */
    private byte[] toByteArray(String filename) {
        InputStream is = ClasspathConfigurationRepository.class.getClassLoader().getResourceAsStream(filename);
        try {
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            LOGGER.warn("Failed to load config file " + filename, e);
            return null;
        }
    }

    private File toFile(String filename) {
        URL url = ClasspathConfigurationRepository.class.getClassLoader().getResource(filename);
        return new File(url.getFile());
    }

}
