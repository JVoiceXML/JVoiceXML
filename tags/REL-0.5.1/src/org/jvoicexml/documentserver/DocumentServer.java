/*
 * File:    $RCSfile: DocumentServer.java,v $
 * Version: $Revision: 1.29 $
 * Date:    $Date: 2006/05/26 15:33:51 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.grammar.ExternalGrammar;
import org.jvoicexml.interpreter.grammar.ExternalGrammarImpl;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.xml.sax.InputSource;

/**
 * A <em>document server</em> processes <em>requests</em> from a client
 * application.
 *
 * <p>
 * The document server evaluates the scheme of the incoming requests and calls
 * the appropriate <code>SchemeStrategy</code>.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.29 $
 *
 * @see SchemeStrategy
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class DocumentServer {
    /** Configuration key. */
    public static final String CONFIG_KEY = "documentserver";

    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DocumentServer.class);

    /** Known strategy handler. */
    private final Map<String, SchemeStrategy> strategies;

    /**
     * Create a new object.
     *
     * <p>
     * This method should not be called by any application. Use
     * <code>JVoiceXml.getDocumentServer()</code> to obtain a reference to the
     * document server.
     * </p>
     *
     * @see org.jvoicexml.JVoiceXmlCore#getDocumentServer()
     */
    public DocumentServer() {
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
     * Get the document with the given URI.
     *
     * @param uri
     *        URI of the document.
     * @return VoiceXML document with the given URI.
     * @exception BadFetchError
     *            The URI does not reference a document or an error occurred
     *            retrieving the document.
     */
    public VoiceXmlDocument getDocument(final URI uri)
            throws BadFetchError {
        final SchemeStrategy strategy = getSchemeStrategy(uri);
        final InputStream input = strategy.getInputStream(uri);

        final VoiceXmlDocument document = readDocument(input);

        try {
            input.close();
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("read document");
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

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("adding scheme strategy for scheme '" + scheme + "': "
                         + strategy.getClass().getName());
        }

        strategies.put(strategy.getScheme(), strategy);
    }

    /**
     * Retrieve an externam grammar from the given input stream.
     *
     * @param input
     *        <code>InputStream</code> for a plain text grammar.
     * @return Read gramamr.
     * @exception BadFetchError
     *            Error reading from the input source.
     *
     * @since 0.3
     */
    public ExternalGrammar getExternalGrammar(final InputStream input)
            throws BadFetchError {
        final Reader inReader = new InputStreamReader(input);
        final BufferedReader reader = new BufferedReader(inReader);

        final StringBuilder contents = new StringBuilder();
        try {
            String line = reader.readLine();
            while (line != null) {
                contents.append(line);
                contents.append(System.getProperty("line.separator"));
                line = reader.readLine();
            }
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("read grammar");
            LOGGER.debug(contents.toString());
        }

        return new ExternalGrammarImpl(null, contents.toString());
    }

    /**
     * Returns the external Grammar referenced by uri.
     *
     * <p>
     * If more than one grammar is available at the given uri, the grammar with
     * the optional type is preferred. If preferredType is null, the
     * ContentServer does not have to care about the preferredType.
     * </p>
     *
     * @param uri
     *        Where to find the grammar.
     *
     * @return ExternalGrammar the grammar referenced by the uri.
     *
     * @throws BadFetchError
     *         The URI does not reference a document or an error occurred
     *         retrieving the document.
     */
    public ExternalGrammar getGrammar(final URI uri)
            throws BadFetchError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("retrieving grammar '" + uri + "'");
        }

        final SchemeStrategy strategy = getSchemeStrategy(uri);
        final InputStream input = strategy.getInputStream(uri);

        return getExternalGrammar(input);
    }

    /**
     * Retrieves an <code>AudioStream</code> to the audio file with the given
     * <code>uri</code>.
     *
     * @param uri
     *        URI of the audio file.
     * @return <code>AudioInputStream</code> for the audio file.
     * @exception BadFetchError
     *            Error retrieving the audio file.
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
}
