/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
import org.jvoicexml.event.plain.implementation.RecognitionStartedEvent;
import org.jvoicexml.event.plain.implementation.RecognitionStoppedEvent;
import org.jvoicexml.event.plain.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.implementation.SrgsXmlGrammarImplementation;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A spoken input implementation for the use of the Microsoft Kinect.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public final class KinectSpokenInput implements SpokenInput {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(KinectSpokenInput.class);

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
     * 
     * @param value
     *            new value for the type.
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
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("allocating kinect recognizer...");
        }
        // try {
        // recognizer.allocate();
        // } catch (KinectRecognizerException e) {
        // throw new NoresourceError(e.getMessage(), e);
        // }
        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("...allocated kinect recognizer");
        // }
    }

    /**
     * Checks if this spoken input has been opened.
     * 
     * @return <code>true</code> if the spoken input has been opened.
     */
    boolean isOpen() {
        return recognizer != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() throws NoresourceError {
        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("allocating kinect recognizer...");
        // }
        // try {
        // recognizer.allocate();
        // } catch (KinectRecognizerException e) {
        // throw new NoresourceError(e.getMessage(), e);
        // }
        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("...allocated kinect recognizer");
        // }
    }

    /**
     * Checks if the spoken input has been allocated.
     * 
     * @return <code>true</code> if the recognizer is allocated
     */
    boolean isActivated() {
        return (recognizer != null) && recognizer.isAllocated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passivate() throws NoresourceError {
        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("deallocating kinect recognizer...");
        // }
        // try {
        // recognizer.deallocate();
        // } catch (KinectRecognizerException e) {
        // throw new NoresourceError(e.getMessage(), e);
        // }
        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("...deallocated kinect recognizer");
        // }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("deallocating kinect recognizer...");
        // }
        // try {
        // recognizer.deallocate();
        // } catch (KinectRecognizerException e) {
        // // throw new NoresourceError(e.getMessage(), e);
        // }
        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("...deallocated kinect recognizer");
        // }
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
        try {
            recognizer.allocate();
        } catch (KinectRecognizerException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
        recognizer.startRecognition();

        final SpokenInputEvent event = new RecognitionStartedEvent(this, null);
        fireInputEvent(event);
        LOGGER.info("kinect recognition started");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopRecognition() {
        try {
            recognizer.stopRecognition();
            recognizer.deallocate();
        } catch (KinectRecognizerException e) {
            LOGGER.warn(e.getMessage(), e);
        } finally {
            final SpokenInputEvent event = new RecognitionStoppedEvent(this,
                    null);
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
        final Collection<GrammarType> types = new java.util.ArrayList<GrammarType>();
        types.add(GrammarType.SRGS_XML);
        return types;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarImplementation<?> loadGrammar(URI uri, GrammarType type)
            throws NoresourceError, UnsupportedFormatError, IOException {
        if (type != GrammarType.SRGS_XML) {
            throw new UnsupportedFormatError("Only SRGS XML is supported!");
        }

        final URL url = uri.toURL();
        InputStream input = null;
        final SrgsXmlDocument doc;
        try {
            input = url.openStream();
            final InputSource inputSource = new InputSource(input);
            doc = new SrgsXmlDocument(inputSource);
        } catch (ParserConfigurationException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (SAXException e) {
            throw new UnsupportedFormatError(e.getMessage(), e);
        } finally {
            if (input != null) {
                input.close();
            }
        }
        return new SrgsXmlGrammarImplementation(doc, uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<BargeInType> getSupportedBargeInTypes() {
        final Collection<BargeInType> types = new java.util.ArrayList<BargeInType>();

        types.add(BargeInType.SPEECH);
        types.add(BargeInType.HOTWORD);

        return types;
    }

    /**
     * Notifies all registered listeners about the given event.
     * 
     * @param event
     *            the event.
     * @since 0.6
     */
    void fireInputEvent(final SpokenInputEvent event) {
        synchronized (listeners) {
            final Collection<SpokenInputListener> copy = new java.util.ArrayList<SpokenInputListener>();
            copy.addAll(listeners);
            for (SpokenInputListener current : copy) {
                current.inputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all registered listeners about the given error.
     * 
     * @param error
     *            the error.
     * @since 0.6
     */
    void notifyError(final ErrorEvent error) {
        synchronized (listeners) {
            final Collection<SpokenInputListener> copy = new java.util.ArrayList<SpokenInputListener>();
            copy.addAll(listeners);
            for (SpokenInputListener current : copy) {
                current.inputError(error);
            }
        }
    }
}
