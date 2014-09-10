/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.implementation.mobicents;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.Collection;

import javax.speech.recognition.RecognizerModeDesc;
import javax.speech.recognition.ResultListener;
import javax.speech.recognition.RuleGrammar;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DtmfRecognizerProperties;
import org.jvoicexml.SpeechRecognizerProperties;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.event.plain.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Spoken input that uses the Mobicents 1.0 to address the recognition engine.
 *
 * <p>
 * Handle all Mobicents calls to the recognizer to make Mobicents transparent
 * to the interpreter.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @author Shadowman
 * @version $Revision$
 */
public final class MobicentsSpokenInput
        implements SpokenInput, StreamableSpokenInput {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(MobicentsSpokenInput.class);

    /** Buffer size when reading a grammar. */
    private static final int BUFFER_SIZE = 1024;

    /** Supported barge-in types. */
    private static final Collection<BargeInType> BARGE_IN_TYPES;

    /** Supported grammar types. */
    private static final Collection<GrammarType> GRAMMAR_TYPES;


    /** Listener for user input events. */
    private final Collection<SpokenInputListener> listener;

    /** The default recognizer mode descriptor. */
    private  RecognizerModeDesc desc;

    /** A custom handler to handle remote connections. */
    private SpokenInputConnectionHandler handler;

    /** Information about the used connection. */
    private ConnectionInformation info;

    /** Listener for recognition results. */
    private ResultListener resultListener;

    /** The encapsulated streamable input. */
    private StreamableSpokenInput streamableInput;

    static {
        BARGE_IN_TYPES = new java.util.ArrayList<BargeInType>();
        BARGE_IN_TYPES.add(BargeInType.SPEECH);
        BARGE_IN_TYPES.add(BargeInType.HOTWORD);
        GRAMMAR_TYPES = new java.util.ArrayList<GrammarType>();
        GRAMMAR_TYPES.add(GrammarType.JSGF);
    }

    /**
     * Constructs a new audio input.
     * @param defaultDescriptor
     *        the default recognizer mode descriptor.
     */
    public MobicentsSpokenInput(final RecognizerModeDesc defaultDescriptor) {
        desc = defaultDescriptor;
        listener = new java.util.ArrayList<SpokenInputListener>();
    }

    /**
     * {@inheritDoc}
     */
    public void open()
            throws NoresourceError {
        try {
                LOGGER.debug("...Mobicents 1.0  recognizer allocated");
        } catch (Exception ee) {
            LOGGER.error(ee);
            throw new NoresourceError(ee);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() 
    {
            LOGGER.debug("audio input closed");
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(final SpokenInputListener inputListener) {
        synchronized (listener) {
            listener.add(inputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(final SpokenInputListener inputListener) {
        synchronized (listener) {
            listener.remove(inputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<BargeInType> getSupportedBargeInTypes() {
        return BARGE_IN_TYPES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarImplementation<?> loadGrammar(final Reader reader,
            final GrammarType type)
            throws NoresourceError, BadFetchError, UnsupportedFormatError 
    {
        return null;
    }

    /**
     * Dumps all loaded grammars to the LOGGER in debug mode.
     * @since 0.7.3
     */
    private void dumpLoadedGrammars() 
    {

    }

    /**
     * {@inheritDoc}
     * 
     * Activation of grammars means to enable the grammar in the Mobicents jargon.
     */
    public void activateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError 
    {

    }

    /**
     * {@inheritDoc}
     * 
     * Activation of grammars means to disable the grammar in the Mobicents jargon.
     */
    public void deactivateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars)
            throws BadFetchError 
    {

    }

    /**
     * Retrieves all enabled grammars.
     * @return enabled grammars.
     * @since 0.7.3
     */
    Collection<RuleGrammar> getActiveGrammars() 
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startRecognition
            (
            final SpeechRecognizerProperties speech,
            final DtmfRecognizerProperties dtmf)
            throws NoresourceError, BadFetchError 
    {
        LOGGER.debug("starting recognition...");
        LOGGER.debug("...recognition started");
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecognition() 
    {
        LOGGER.debug("...recognition stopped");
    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
        LOGGER.info("activated spoken input");
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() 
    {
        LOGGER.info("passivated spoken input");
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final ConnectionInformation connectionInformation)
        throws IOException 
    {
        LOGGER.info("connectionInformation:"+connectionInformation);
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final ConnectionInformation connectionInformation) {
        LOGGER.info("connectionInformation:"+connectionInformation);
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "mobicents";
    }

    /**
     * {@inheritDoc}
     */
    public Collection<GrammarType> getSupportedGrammarTypes() {
        return GRAMMAR_TYPES;
    }

    /**
     * Sets a custom connection handler.
     * @param connectionHandler the connection handler.
     */
    public void setSpokenInputConnectionHandler(
            final SpokenInputConnectionHandler connectionHandler) {
        handler = connectionHandler;
    }

    /**
     * {@inheritDoc}
     */
    public URI getUriForNextSpokenInput() throws NoresourceError {
        if (handler != null) {
            return handler.getUriForNextSpokenInput(info);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() 
    {
        return false;
    }

    /**
     * Sets the streamable input.
     * @param streamable the streamable input to set.
     */
    public void setStreamableSpokenInput(
            final StreamableSpokenInput streamable) {
        streamableInput = streamable;
    }

    /**
     * {@inheritDoc}
     */
    public void writeRecognizerStream(final byte[] buffer, final int offset,
            final int length) throws IOException {
        if (streamableInput == null) {
            return;
        }

        streamableInput.writeRecognizerStream(buffer, offset, length);
    }

    /**
     * Notifies all registered listeners about the given event.
     * @param event the event.
     * @since 0.6
     */
    void fireInputEvent(final SpokenInputEvent event) 
    {
        LOGGER.info("SpokenInputEvent:"+event);
        synchronized (listener) 
        {
            final Collection<SpokenInputListener> copy =
                new java.util.ArrayList<SpokenInputListener>();
            copy.addAll(listener);
            for (SpokenInputListener current : copy) {
                current.inputStatusChanged(event);
            }
        }
    }
}
