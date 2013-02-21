/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.FetchAttributes;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.Session;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.vxml.RequestMethod;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Basic implementation of a {@link DocumentServer}.
 *
 * <p>
 * This implementation offers an extensible support for multiple schemes. All
 * known handlers for schemes are held in a list of {@link SchemeStrategy}s.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class JVoiceXmlDocumentServer
    implements DocumentServer {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlDocumentServer.class);

    /** Size of the read buffer when reading objects. */
    private static final int READ_BUFFER_SIZE = 1024;

    /** Known strategy handler. */
    private final Map<String, SchemeStrategy> strategies;

    /** The default fetch attributes. */
    private FetchAttributes attributes;

    /**
     * Creates a new object.
     *
     * <p>
     * This method should not be called by any application. Use
     * <code>JVoiceXml.getDocumentServer()</code> to obtain a reference to the
     * document server.
     * </p>
     */
    public JVoiceXmlDocumentServer() {
        strategies = new java.util.Hashtable<String, SchemeStrategy>();
    }

    /**
     * Adds the given list of strategies for schemes to the supported schemes.
     * @param schemeStrategies List with strategies.
     *
     * @since 0.5
     */
    public void setSchemeStrategies(
            final List<SchemeStrategy> schemeStrategies) {
        for (SchemeStrategy strategy : schemeStrategies) {
            addSchemeStrategy(strategy);
        }
    }

    /**
     * Sets the default fetch attributes.
     * @param attrs default fetch attributes.
     * @since 0.7
     */
    public void setFetchAttributes(final FetchAttributes attrs) {
        attributes = attrs;
        LOGGER.info("default fetch timeout: " + attributes.getFetchTimeout()
                + "msec");
    }

    /**
     * Merges the default fetch attributes with the given fetch attributes.
     * Any setting in the given fetch attributes will overwrite the default
     * setting.
     * @param attrs fetch attributes to merge with.
     * @return merged fetch attributes
     * @since 0.7
     */
    private FetchAttributes mergeFetchAttributes(final FetchAttributes attrs) {
        if (attributes == null) {
            if (attrs == null) {
                return new FetchAttributes();
            }
            return attrs;
        }
        if (attrs == null) {
            return attributes;
        }
        final FetchAttributes merge = new FetchAttributes(attributes);
        final URI fetchAudio = attrs.getFetchAudio();
        if (fetchAudio != null) {
            merge.setFetchAudio(fetchAudio);
        }
        final String fetchHint = attrs.getFetchHint();
        if (fetchHint != null) {
            merge.setFetchHint(fetchHint);
        }
        final long fetchTimeout = attrs.getFetchTimeout();
        if (fetchTimeout > 0) {
            merge.setFetchTimeout(fetchTimeout);
        }
        final long maxAge = attrs.getMaxage();
        if (maxAge > 0) {
            merge.setMaxage(maxAge);
        }
        final long maxStale = attrs.getMaxstale();
        if (maxStale > 0) {
            merge.setMaxage(maxStale);
        }
        return merge;
    }

    /**
     * Reads the VoiceXML document from the given <code>InputStream</code>.
     *
     * @param input
     *        <code>InputStream</code> for the VoiceXML document.
     * @return Retrieved VoiceXML document.
     * @exception BadFetchError
     *            Error reading from the input stream.
     *
     * @since 0.3
     */
    private VoiceXmlDocument readDocument(final InputStream input)
            throws BadFetchError {
        final InputSource inputSource = new InputSource(input);

        try {
            return new VoiceXmlDocument(inputSource);
        } catch (javax.xml.parsers.ParserConfigurationException pce) {
            throw new BadFetchError(pce);
        } catch (org.xml.sax.SAXException saxe) {
            throw new BadFetchError(saxe);
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        }
    }

    /**
     * {@inheritDoc}
     */
    public VoiceXmlDocument getDocument(final Session session,
            final DocumentDescriptor descriptor)
            throws BadFetchError {
        final URI uri = descriptor.getUri();
        final SchemeStrategy strategy = getSchemeStrategy(uri);
        final RequestMethod method = descriptor.getMethod();
        final Map<String, Object> parameters = descriptor.getParameters();
        final FetchAttributes attrs = descriptor.getAttributes();
        final FetchAttributes mergedAttrs = mergeFetchAttributes(attrs);
        final long timeout = mergedAttrs.getFetchTimeout();
        LOGGER.info("loading document with URI '" + uri + "...");
        final InputStream input = strategy.getInputStream(session, uri, method,
                timeout, parameters);
        final VoiceXmlDocument document;
        try {
            document = readDocument(input);
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                throw new BadFetchError(e);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...read document");
            LOGGER.debug(document);
        }

        final Vxml vxml = document.getVxml();
        final String version = vxml.getVersion();
        if (version == null) {
            throw new BadFetchError("The document at '"
                    + uri + "' does not provide a version attribute!");
        }
        return document;
    }

    /**
     * Retrieve the <code>SchemeStrategy</code> that is responsible for this
     * URI.
     *
     * @param uri
     *        The URI of a document to retrieve.
     * @return Responsible <code>SchemeStrategy</code>, never
     *         <code>null</code>.
     * @throws BadFetchError
     *         The URI does not reference a document or no valid strategy.
     *
     * @since 0.3
     */
    private SchemeStrategy getSchemeStrategy(final URI uri)
            throws BadFetchError {
        if (uri == null) {
            throw new BadFetchError("Cannot get a strategy for a null URI!");
        }

        final String scheme = uri.getScheme();
        if (scheme == null) {
            throw new BadFetchError("Unable to find scheme in '" + uri + "'!");
        }

        final SchemeStrategy strategy = strategies.get(scheme);
        if (strategy == null) {
            throw new BadFetchError("no strategy for scheme '" + scheme + "'!");
        }

        return strategy;
    }

    /**
     * Adds the given scheme strategy.
     *
     * @param strategy
     *        Scheme strategy to be added.
     */
    public void addSchemeStrategy(final SchemeStrategy strategy) {
        if (strategy == null) {
            LOGGER.warn("cannot add null scheme strategy");

            return;
        }

        final String scheme = strategy.getScheme();

        LOGGER.info("adding scheme strategy for scheme '" + scheme + "': "
                + strategy.getClass().getName());

        strategies.put(strategy.getScheme(), strategy);
    }

    /**
     * {@inheritDoc}
     */
    public GrammarDocument getGrammarDocument(final Session session,
            final URI uri, final FetchAttributes attrs)
            throws BadFetchError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("retrieving grammar '" + uri + "'");
        }

        final DocumentDescriptor descriptor = new DocumentDescriptor(uri);
        final String grammar = (String) getObject(session, descriptor,
                TEXT_PLAIN);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("read grammar");
            LOGGER.debug(grammar);
        }

        return new JVoiceXmlGrammarDocument(grammar);
    }

    /**
     * {@inheritDoc}
     */
    public AudioInputStream getAudioInputStream(final Session session,
            final URI uri)
            throws BadFetchError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("retrieving audio input stream '" + uri + "'");
        }

        final SchemeStrategy strategy = getSchemeStrategy(uri);
        final FetchAttributes attrs = mergeFetchAttributes(null);
        final long timeout = attrs.getFetchTimeout();
        final InputStream input = strategy.getInputStream(session, uri,
                RequestMethod.GET, timeout, null);

        try {
            // Some InputStreams do not support mark/reset which is required
            // by the AudioSystem. So we use a BufferedInputStream that
            // guarantees these features.
            final BufferedInputStream buf = new BufferedInputStream(input);
            return AudioSystem.getAudioInputStream(buf);
        } catch (javax.sound.sampled.UnsupportedAudioFileException uafe) {
            throw new BadFetchError(uafe);
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Currently only <code>text/plain</code> and <code>text/xml</code> are
     * supported.
     */
    public Object getObject(final Session session,
            final DocumentDescriptor descriptor, final String type)
        throws BadFetchError {
        if (type == null) {
            throw new BadFetchError("No type specified!");
        }
        final URI uri = descriptor.getUri();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("retrieving object with type '" + type + "' from '"
                    + uri + "'");
        }

        // Determine the relevant strategy
        final RequestMethod method = descriptor.getMethod();
        final Map<String, Object> parameters = descriptor.getParameters();
        final FetchAttributes attrs = descriptor.getAttributes();
        final FetchAttributes mergedAttrs = mergeFetchAttributes(attrs);
        final long timeout = mergedAttrs.getFetchTimeout();
        final SchemeStrategy strategy = getSchemeStrategy(uri);
        final InputStream input = strategy.getInputStream(session, uri,
                method, timeout, parameters);

        final Object object;
        if (type.equals(TEXT_PLAIN)) {
            object = readString(input);
        } else if (type.equals(TEXT_XML)) {
            object = readXml(input);
        } else {
            // The spec leaves it open, what happens, if there is no type
            // specified. We throw an error in this case.
            throw new BadFetchError("Type '" + type + "' is not supported!");
        }

        return object;
    }

    /**
     * Reads a {@link String} from the given {@link InputStream}.
     * @param input the input stream to use.
     * @return read string.
     * @throws BadFetchError
     *         Error reading.
     */
    private String readString(final InputStream input) throws BadFetchError {
        // Read from the input
        final byte[] buffer = new byte[READ_BUFFER_SIZE];
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        int num;
        try {
            do {
                num = input.read(buffer);
                if (num >= 0) {
                    out.write(buffer, 0, num);
                }
            } while(num >= 0);
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        }

        final byte[] readBytes = out.toByteArray();
        return new String(readBytes);
    }

    /**
     * Reads a {@link Document} from the given {@link InputStream}.
     * @param in the input stream to use.
     * @return read document.
     * @throws BadFetchError
     *         Error reading.
     * @since 0.7
     */
    private Document readXml(final InputStream in) throws BadFetchError {
        final DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        final DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            final InputSource source = new InputSource(in);
            return builder.parse(source);
        } catch (ParserConfigurationException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (SAXException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (IOException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public URI storeAudio(final AudioInputStream in) throws BadFetchError {
        try {
            final File directory = getRecordingsDirectory();
            final File file = File.createTempFile("rec-", ".wav", directory);
            AudioSystem.write(in, AudioFileFormat.Type.WAVE, file);
            LOGGER.info("recorded to file '" + file.toURI() + "'");
            return file.toURI();
        } catch (IOException ex) {
            throw new BadFetchError(ex.getMessage(), ex);
        }
    }

    /**
     * Retrieves the recording directory. If it does not exist, create it.
     * @return recording directory.
     */
    private File getRecordingsDirectory() {
        final File directory = new File("work/recordings/");
        if (!directory.exists()) {
            LOGGER.info("created recordings directory '" + directory.toURI()
                    + "'");
            directory.mkdirs();
        }
        return directory;
    }

    /**
     * {@inheritDoc}
     */
    public void sessionClosed(final Session session) {
        final Collection<SchemeStrategy> knownStrategies = strategies.values();
        for (SchemeStrategy strategy : knownStrategies) {
            strategy.sessionClosed(session);
        }
    }
}
