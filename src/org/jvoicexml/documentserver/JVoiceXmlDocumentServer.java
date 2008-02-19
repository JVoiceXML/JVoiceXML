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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.log4j.Logger;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.xml.sax.InputSource;

/**
 * Basic implementation of a {@link DocumentServer}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * @see SchemeStrategy
 *
 * <p>
 * Copyright &copy; 2005-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class JVoiceXmlDocumentServer
    implements DocumentServer {
    /** Size of the read buffer when reading objects. */
    private static final int READ_BUFFER_SIZE = 1024;

    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlDocumentServer.class);

    /** Known strategy handler. */
    private final Map<String, SchemeStrategy> strategies;

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
    public VoiceXmlDocument getDocument(final URI uri)
            throws BadFetchError {
        final SchemeStrategy strategy = getSchemeStrategy(uri);
        final InputStream input = strategy.getInputStream(uri);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loading document with URI '" + uri + "...");
        }
        final VoiceXmlDocument document = readDocument(input);

        try {
            input.close();
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...read document");
            LOGGER.debug(document);
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
    public GrammarDocument getGrammarDocument(final URI uri)
            throws BadFetchError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("retrieving grammar '" + uri + "'");
        }

        final String grammar = (String) getObject(uri, TEXT_PLAIN);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("read grammar");
            LOGGER.debug(grammar);
        }

        return new JVoiceXmlGrammarDocument(grammar);
    }

    /**
     * {@inheritDoc}
     */
    public AudioInputStream getAudioInputStream(final URI uri)
            throws BadFetchError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("retrieving audio input stream '" + uri + "'");
        }

        final SchemeStrategy strategy = getSchemeStrategy(uri);
        final InputStream input = strategy.getInputStream(uri);

        try {
            return AudioSystem.getAudioInputStream(input);
        } catch (javax.sound.sampled.UnsupportedAudioFileException uafe) {
            throw new BadFetchError(uafe);
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Currently only <code>text/plain</code> is supported.
     */
    public Object getObject(final URI uri, final String type)
        throws BadFetchError {
        if (type == null) {
            throw new BadFetchError("No type specified!");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("retrieving object with type '" + type + "' from '"
                    + uri + "'");
        }

        // Determine the relevant strategy
        final SchemeStrategy strategy = getSchemeStrategy(uri);
        final InputStream input = strategy.getInputStream(uri);

        final Object object;
        if (type.equals(TEXT_PLAIN)) {
            object = readString(input);
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
}
