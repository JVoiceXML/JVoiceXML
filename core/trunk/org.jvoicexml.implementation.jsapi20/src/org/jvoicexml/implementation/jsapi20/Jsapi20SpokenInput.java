/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi20;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.UUID;

import javax.speech.AudioException;
import javax.speech.AudioManager;
import javax.speech.EngineException;
import javax.speech.EngineManager;
import javax.speech.EngineStateException;
import javax.speech.SpeechEventExecutor;
import javax.speech.recognition.Grammar;
import javax.speech.recognition.GrammarManager;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.RecognizerEvent;
import javax.speech.recognition.RecognizerListener;
import javax.speech.recognition.RecognizerMode;
import javax.speech.recognition.RecognizerProperties;
import javax.speech.recognition.RuleGrammar;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DtmfRecognizerProperties;
import org.jvoicexml.SpeechRecognizerProperties;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.ObservableSpokenInput;
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
 * Audio input that uses the JSAPI 2.0 to address the recognition engine.
 *
 * <p>
 * Handle all JSAPI calls to the recognizer to make JSAPI transparent
 * to the interpreter.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @author Renato Cassaca
 * @author David Rodriguez
 * @version $Revision$
 * @since 0.6
 */
public final class Jsapi20SpokenInput implements SpokenInput,
        ObservableSpokenInput, RecognizerListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(Jsapi20SpokenInput.class);

    /** The speech recognizer. */
    private Recognizer recognizer;

    /** Listener for user input events. */
    private final Collection<SpokenInputListener> listeners;

    /** The default recognizer mode descriptor. */
    private final RecognizerMode desc;

    /** The recognition listener. */
    private JVoiceXMLResultListener currentResultListener;

    /** The media locator to use. */
    private String mediaLocator;

    /** Media locator factory to create a sink media locator. */
    private final InputMediaLocatorFactory locatorFactory;

    /** Type of this resources. */
    private String type;

    /**
     * Constructs a new audio input.
     * @param defaultDescriptor
     *        the default recognizer mode descriptor.
     * @param mediaLocatorFactory the media locator factory
     */
    public Jsapi20SpokenInput(final RecognizerMode defaultDescriptor,
            final InputMediaLocatorFactory mediaLocatorFactory) {
        desc = defaultDescriptor;
        listeners = new java.util.ArrayList<SpokenInputListener>();
        locatorFactory = mediaLocatorFactory;
        currentResultListener = null;
    }

    /**
     * Sets the media locator.
     * @param locator the media locator to use.
     * @since 0.7
     */
    public void setMediaLocator(final URI locator) {
        if (locator != null) {
            mediaLocator = locator.toString();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open() throws NoresourceError {
        try {
            recognizer = (Recognizer) EngineManager.createEngine(desc);
            if (recognizer == null) {
                throw new NoresourceError("no recognizer found matching "
                        + desc);
            }
            LOGGER.info("allocating JSAPI 2.0 recognizer...");

            try {
                if (mediaLocator != null) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("using media locator '" + mediaLocator
                                + "'");
                    }
                    final AudioManager manager =
                        recognizer.getAudioManager();
                    manager.setMediaLocator(mediaLocator);
                }
                final SpeechEventExecutor executor =
                    new SynchronousSpeechEventExecutor();
                recognizer.setSpeechEventExecutor(executor);
                recognizer.addRecognizerListener(this);
                recognizer.allocate();
                recognizer.waitEngineState(Recognizer.ALLOCATED);
                LOGGER.info("...JSAPI 2.0 recognizer allocated");
            } catch (EngineStateException ex) {
                throw new NoresourceError(ex.getMessage(), ex);
            } catch (EngineException ex) {
                throw new NoresourceError(ex.getMessage(), ex);
            } catch (AudioException ex) {
                throw new NoresourceError(ex.getMessage(), ex);
            } catch (IllegalArgumentException ex) {
                throw new NoresourceError(ex.getMessage(), ex);
            } catch (IllegalStateException ex) {
                throw new NoresourceError(ex.getMessage(), ex);
            } catch (InterruptedException ex) {
                throw new NoresourceError(ex.getMessage(), ex);
            }
        } catch (EngineException ee) {
            throw new NoresourceError(ee);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (recognizer == null) {
            LOGGER.warn("no recognizer: cannot deallocate");
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("closing audio input...");
            LOGGER.debug("deallocating recognizer...");
        }

        try {
            recognizer.deallocate();
        } catch (EngineStateException ex) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("error deallocating the recognizer", ex);
            }
        } catch (AudioException ex) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("error deallocating the recognizer", ex);
            }
        } catch (EngineException ee) {
            LOGGER.error("error deallocating the recognizer", ee);
        } finally {
            recognizer = null;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("audio input closed");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(final SpokenInputListener inputListener) {
        synchronized (listeners) {
            listeners.add(inputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(final SpokenInputListener inputListener) {
        synchronized (listeners) {
            listeners.remove(inputListener);
        }
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
     * {@inheritDoc}
     */
    @Override
    public GrammarImplementation<RuleGrammar> loadGrammar(final Reader reader,
            final GrammarType grammarType)
            throws NoresourceError, BadFetchError, UnsupportedFormatError {
        if (recognizer == null) {
            throw new NoresourceError("recognizer not available");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loading grammar from reader");
        }
        RuleGrammar grammar = null;
        try {
            final InputSource source = new InputSource(reader);
            final SrgsXmlDocument doc = new SrgsXmlDocument(source);
            final org.jvoicexml.xml.srgs.Grammar gram = doc.getGrammar();
            reader.close();
            final String reference;
            String root = gram.getRoot();
            if (root == null) {
                reference = UUID.randomUUID().toString();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("no root rule given. using '" + root
                            + "' as grammar reference");
                }
            } else {
                reference = root;
            }
            final String content = doc.toXml();
            final Reader read = new StringReader(content);
            final GrammarManager manager = recognizer.getGrammarManager();
            grammar = (RuleGrammar) manager.loadGrammar(reference,
                    "application/srgs+xml", read);
            recognizer.processGrammars();
        } catch (EngineException ex) {
            throw new NoresourceError(ex.getMessage(), ex);
        } catch (EngineStateException ex) {
            throw new NoresourceError(ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new NoresourceError(ex.getMessage(), ex);
        } catch (java.io.IOException ex) {
            throw new NoresourceError(ex.getMessage(), ex);
        } catch (javax.speech.recognition.GrammarException ex) {
            throw new NoresourceError(ex.getMessage(), ex);
        } catch (ParserConfigurationException ex) {
            throw new NoresourceError(ex.getMessage(), ex);
        } catch (SAXException ex) {
            throw new NoresourceError(ex.getMessage(), ex);
        }

        return new RuleGrammarImplementation(grammar);
    }

    /**
     * Activates the given grammar.
     * @param name
     *        Name of the grammar.
     * @param activate
     *        <code>true</code> if the grammar should be activated.
     *
     * @return <code>true</code> if the grammar is active.
     * @exception BadFetchError
     *        Error creating the grammar.
     */
    private boolean activateGrammar(final String name, final boolean activate)
        throws BadFetchError {
        RuleGrammar grammar = null;
        final GrammarManager manager = recognizer.getGrammarManager();
        try {
            grammar = (RuleGrammar) manager.getGrammar(name);
            if (grammar == null) {
                return true;
            }
            grammar.setActivatable(activate);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("grammar '" + name + "' activated: "
                             + grammar.isActive());
            }
        } catch (EngineStateException ex) {
            throw new BadFetchError(ex.getMessage(), ex);
        }
        return grammar.isActive() == activate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars)
        throws BadFetchError, UnsupportedLanguageError, NoresourceError {
        if (recognizer == null) {
            throw new NoresourceError("recognizer not available");
        }

        for (GrammarImplementation<? extends Object> current : grammars) {
            if (current instanceof RuleGrammarImplementation) {
                final RuleGrammarImplementation ruleGrammar =
                        (RuleGrammarImplementation) current;
                final String name = ruleGrammar.getName();

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("activating grammar '" + name + "'...");
                }

                activateGrammar(name, true);
            } else if (current instanceof SrgsXmlGrammarImplementation) {
                final SrgsXmlGrammarImplementation implementation =
                    (SrgsXmlGrammarImplementation) current;
                final SrgsXmlDocument document = implementation.getGrammar();
                final String grammar = document.toString();                
                
                final StringReader reader = new StringReader(grammar);
                final GrammarImplementation<RuleGrammar> rule;
                try {
                    rule = loadGrammar(reader, GrammarType.SRGS_XML);
                } catch (UnsupportedFormatError e) {
                    throw new BadFetchError(e.getMessage(), e);
                }
                final String name = rule.getGrammar().getRoot();

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("activating VOICE grammar '\n" + document
                            + "'...");
                }

                activateGrammar(name, true);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars)
        throws BadFetchError {
        if (recognizer == null) {
            return;
        }
        

        for (GrammarImplementation<? extends Object> current : grammars) {

            if (current instanceof SrgsXmlGrammarImplementation) {
                
                final SrgsXmlDocument doc =
                        (SrgsXmlDocument) current.getGrammar();
                final org.jvoicexml.xml.srgs.Grammar srgsXmlGrammar
                    = doc.getGrammar();
                final String reference = srgsXmlGrammar.getRoot();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("deactivating grammar '" + reference + "'...");
                }   
                
                final GrammarManager manager = recognizer.getGrammarManager();
                final Grammar grammar = manager.getGrammar(reference);
                if (grammar != null) {
                    manager.deleteGrammar(grammar);
                }
            }
            
            if (current instanceof RuleGrammarImplementation) {
                final RuleGrammarImplementation ruleGrammar =
                        (RuleGrammarImplementation) current;
                final String name = ruleGrammar.getName();

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("deactivating grammar '" + name + "'...");
                }

                activateGrammar(name, false);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startRecognition(
        final SpeechRecognizerProperties speech,
        final DtmfRecognizerProperties dtmf)
        throws NoresourceError, BadFetchError {
        if (recognizer == null) {
            throw new NoresourceError("recognizer not available");
        }
        
        if (speech != null) {
            final RecognizerProperties recProbs
                = recognizer.getRecognizerProperties();

            // confidence
            final float confidence = speech.getConfidencelevel();
            final int scaledConfidence = scale(confidence,
                    RecognizerProperties.MIN_CONFIDENCE,
                    RecognizerProperties.MAX_CONFIDENCE);
            recProbs.setConfidenceThreshold(scaledConfidence);
            
            // sensitivity
            final float sensitivity = speech.getSensitivity();
            final int scaledSensitivity = scale(sensitivity,
                    RecognizerProperties.MIN_SENSITIVITY,
                    RecognizerProperties.MAX_SENSITIVITY);
            recProbs.setSensitivity(scaledSensitivity);
            
            // speedvsaccuracy
            final float speedVsAccuracs = speech.getSpeedvsaccuracy();
            final int scaledSpeedVsAccuracy = scale(speedVsAccuracs,
                    RecognizerProperties.MIN_ACCURACY,
                    RecognizerProperties.MAX_ACCURACY);
            recProbs.setSensitivity(scaledSpeedVsAccuracy);
            
            // completeTimeout
            final long completeTimeout = speech.getCompletetimeoutAsMsec();
            recProbs.setCompleteTimeout((int) completeTimeout);
            
            // incompleteTimeout
            final long incompleteTimeout = speech.getCompletetimeoutAsMsec();
            recProbs.setIncompleteTimeout((int) incompleteTimeout);
            
            // maxTimeOut
            // TODO search a corresponding option in JSAPI2
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("starting recognition...");
        }

        // Add listeners
        currentResultListener = new JVoiceXMLResultListener(this);
        recognizer.addResultListener(currentResultListener);

        // Start the recognition process
        recognizer.requestFocus();
        try {
            recognizer.waitEngineState(Recognizer.FOCUSED);
            recognizer.resume();
            recognizer.waitEngineState(Recognizer.RESUMED);
        } catch (EngineStateException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new NoresourceError(e.getMessage(), e);
        }

        final SpokenInputEvent event =
            new SpokenInputEvent(this, SpokenInputEvent.RECOGNITION_STARTED);
        fireInputEvent(event);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...recognition started");
        }
    }

    /**
     * Scales the given value into the range from min to max.
     * @param value the value to scale
     * @param min the minimum border of the range
     * @param max the maximum border of the range
     * @return converted value
     * @since 0.7.5
     */
    private int scale(final float value, final int min, final int max) {
        return (int) (value * (max - min) - min);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopRecognition() {
        if (!recognizer.testEngineState(Recognizer.RESUMED)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("recognition not started. No need to stop.");
            }

            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("stopping recognition...");
        }

        final GrammarManager manager = recognizer.getGrammarManager();
        final Grammar[] list = manager.listGrammars();
        for (Grammar gram : list) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Delete Grammar : " + gram.getReference());
            }
            manager.deleteGrammar(gram);
        }
        recognizer.processGrammars();

        // If a result listener exists: Remove it.
        if (currentResultListener != null) {
            recognizer.removeResultListener(currentResultListener);
            currentResultListener = null;
        }

        recognizer.releaseFocus();
        try {
            recognizer.pause();
        } catch (EngineStateException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (IllegalStateException e) {
            LOGGER.warn(e.getMessage(), e);
        }

        final SpokenInputEvent event =
            new SpokenInputEvent(this, SpokenInputEvent.RECOGNITION_STOPPED);
        fireInputEvent(event);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...recognition stopped");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passivate() {
        listeners.clear();
        final GrammarManager manager = recognizer.getGrammarManager();
        final Grammar[] grammars = manager.listGrammars();
        for (Grammar grammar : grammars) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("deleting grammar '" + grammar.getReference()
                        + "'");
            }
            manager.deleteGrammar(grammar);
        }
        recognizer.processGrammars();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(final ConnectionInformation info) throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect(final ConnectionInformation info) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Sets the type of this resource.
     *
     * @param resourceType
     *                type of the resource
     */
    public void setType(final String resourceType) {
        type = resourceType;
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
    public URI getUriForNextSpokenInput()
        throws NoresourceError, URISyntaxException {
        if (recognizer == null) {
            throw new NoresourceError("No recognizer");
        }
        if (locatorFactory == null) {
            return null;
        }
        final AudioManager manager = recognizer.getAudioManager();
        final String locator = manager.getMediaLocator();
        if (locator == null) {
            return null;
        }
        final URI uri = new URI(locator);
        return locatorFactory.getSinkMediaLocator(this, uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBusy() {
        return recognizer.testEngineState(Recognizer.RESUMED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recognizerUpdate(final RecognizerEvent recognizerEvent) {
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
}
