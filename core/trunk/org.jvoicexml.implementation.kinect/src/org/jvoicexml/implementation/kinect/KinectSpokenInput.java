/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.implementation.kinect/src/org/jvoicexml/implementation/kinect/KinectSpokenInput.java $
 * Version: $LastChangedRevision: 3346 $
 * Date:    $Date: 2012-11-28 15:50:05 +0100 (Mi, 28 Nov 2012) $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.implementation.kinect;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DtmfRecognizerProperties;
import org.jvoicexml.SpeechRecognizerProperties;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.implementation.SrgsXmlGrammarImplementation;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A spoken input implementation for the use of the Microsoft Kinect.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3346 $
 * @since 0.7.6
 */
public final class KinectSpokenInput implements SpokenInput {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(KinectSpokenInput.class);

    /** Listener for user input events. */
    private final Collection<SpokenInputListener> listeners;
    
    /** Type of the created resources. */
    private String type;

    /** Reference to the Kinect. */
    private KinectRecognizer recognizer;

    /**
     * Constructs a new object
     */
    public KinectSpokenInput() {
        listeners = new java.util.ArrayList<SpokenInputListener>();
    }

    /**
     * Sets the type.
     * @param value new value for the type.
     */
    public void setType(final String value) {
        type = value;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open() throws NoresourceError {
        recognizer = new KinectRecognizer(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() throws NoresourceError {
        try {
            recognizer.allocate();
        } catch (KinectRecognizerException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passivate() throws NoresourceError {
        try {
            recognizer.deallocate();
        } catch (KinectRecognizerException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        recognizer = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBusy() {
        return recognizer.isRecognizing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(ConnectionInformation client) throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect(ConnectionInformation client) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startRecognition(SpeechRecognizerProperties speech,
            DtmfRecognizerProperties dtmf) throws NoresourceError,
            BadFetchError {
        recognizer.startRecognition();

        final SpokenInputEvent event =
                new SpokenInputEvent(this, SpokenInputEvent.RECOGNITION_STARTED);
        fireInputEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopRecognition() {
        try {
            recognizer.stopRecognition();
        } catch (KinectRecognizerException e) {
            LOGGER.warn(e.getMessage(), e);
        } finally {
            final SpokenInputEvent event = new SpokenInputEvent(this,
                            SpokenInputEvent.RECOGNITION_STOPPED);
            fireInputEvent(event);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(final SpokenInputListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(final SpokenInputListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateGrammars(
            final Collection<GrammarImplementation<?>> grammars)
            throws BadFetchError, UnsupportedLanguageError,
            UnsupportedFormatError, NoresourceError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivateGrammars(
            final Collection<GrammarImplementation<?>> grammars)
            throws NoresourceError, BadFetchError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getUriForNextSpokenInput() throws NoresourceError,
            URISyntaxException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<GrammarType> getSupportedGrammarTypes() {
        final Collection<GrammarType> types =
                new java.util.ArrayList<GrammarType>();
        types.add(GrammarType.SRGS_XML);
        return types;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarImplementation<?> loadGrammar(Reader reader, GrammarType type)
            throws NoresourceError, BadFetchError, UnsupportedFormatError {
        if (type != GrammarType.SRGS_XML) {
            throw new UnsupportedFormatError("Only SRGS XML is supported!");
        }

        final InputSource inputSource = new InputSource(reader);
        final SrgsXmlDocument doc;
        try {
            doc = new SrgsXmlDocument(inputSource);
        } catch (ParserConfigurationException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (SAXException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (IOException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
        return new SrgsXmlGrammarImplementation(doc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<BargeInType> getSupportedBargeInTypes() {
        final Collection<BargeInType> types =
                new java.util.ArrayList<BargeInType>();

        types.add(BargeInType.SPEECH);
        types.add(BargeInType.HOTWORD);

        return types;
    }

    /**
     * Notifies all registered listeners about the given event.
     * @param event the event.
     * @since 0.6
     */
    void fireInputEvent(final SpokenInputEvent event) {
        synchronized (listeners) {
            final Collection<SpokenInputListener> copy =
                new java.util.ArrayList<SpokenInputListener>();
            copy.addAll(listeners);
            for (SpokenInputListener current : copy) {
                current.inputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all registered listeners about the given error.
     * @param error the error.
     * @since 0.6
     */
    void notifyError(final ErrorEvent error) {
        synchronized (listeners) {
            final Collection<SpokenInputListener> copy =
                new java.util.ArrayList<SpokenInputListener>();
            copy.addAll(listeners);
            for (SpokenInputListener current : copy) {
                current.inputError(error);
            }
        }
    }
}
