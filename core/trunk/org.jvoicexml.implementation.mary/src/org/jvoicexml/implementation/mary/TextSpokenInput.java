/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.implementation.text/src/org/jvoicexml/implementation/text/TextSpokenInput.java $
 * Version: $LastChangedRevision: 1081 $
 * Date:    $Date: 2008-10-16 19:21:39 +0300 (Πεμ, 16 Οκτ 2008) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.mary;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.ObservableSpokenInput;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.implementation.SrgsXmlGrammarImplementation;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Text based implementation for a {@link SpokenInput}.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1081 $
 * @since 0.6
 */
final class TextSpokenInput implements SpokenInput, ObservableSpokenInput {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(TextSpokenInput.class);

    /** Supported barge-in types. */
    private static final Collection<BargeInType> BARGE_IN_TYPES;

    /** Supported grammar types. */
    private static final Collection<GrammarType> GRAMMAR_TYPES;

    static {
        BARGE_IN_TYPES = new java.util.ArrayList<BargeInType>();
        BARGE_IN_TYPES.add(BargeInType.SPEECH);
        BARGE_IN_TYPES.add(BargeInType.HOTWORD);

        GRAMMAR_TYPES = new java.util.ArrayList<GrammarType>();
        GRAMMAR_TYPES.add(GrammarType.SRGS_XML);
    }

    /** Registered listener for input events. */
    private final Collection<SpokenInputListener> listener;

    /** Flag, if recognition is turned on. */
    private boolean recognizing;

    private TextReceiverThread textReceiverThread;
    
    /**The text input port*/
    private int textInputPort;

    /**
     * Constructs a new object.
     */
    public TextSpokenInput() {
        listener = new java.util.ArrayList<SpokenInputListener>();

    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
        
        
        try {
            
            ServerSocket serverSocket=new ServerSocket(textInputPort);
            
            textReceiverThread= new TextReceiverThread(serverSocket.accept());
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
        textReceiverThread.setSpokenInput(this);
        textReceiverThread.start();
        

    }

    /**
     * {@inheritDoc}
     */
    public void activateGrammars(
            final Collection<GrammarImplementation<?>> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    public void deactivateGrammars(
            final Collection<GrammarImplementation<?>> grammars)
            throws NoresourceError, BadFetchError {
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
    public Collection<GrammarType> getSupportedGrammarTypes() {
        return GRAMMAR_TYPES;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarImplementation<?> loadGrammar(
            final Reader reader, final GrammarType type)
            throws NoresourceError, BadFetchError, UnsupportedFormatError {
        if (type != GrammarType.SRGS_XML) {
            throw new UnsupportedFormatError("Only SRGS XML is supported!");
        }

        final InputSource inputSource = new InputSource(reader);

        SrgsXmlDocument doc;
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
    public void passivate() {
        listener.clear();
        recognizing = false;
    }

    /**
     * {@inheritDoc}
     */
    public void record(final OutputStream out) throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "text";
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws NoresourceError {

    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client) throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client) {
    }

    /**
     * {@inheritDoc}
     */
    public void startRecognition() throws NoresourceError, BadFetchError {
        recognizing = true;
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecognition() {
        recognizing = false;
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
     * Notifies the interpreter about an observer user input.
     * @param text received utterance.
     */
    void notifyRecognitionResult(final String text) {
        if (!recognizing || (listener == null)) {
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("received utterance '" + text + "'");
        }

        final SpokenInputEvent inputStartedEvent =
            new SpokenInputEvent(this, SpokenInputEvent.INPUT_STARTED,
                    ModeType.VOICE);
        fireInputEvent(inputStartedEvent);


               final RecognitionResult result = new TextRecognitionResult(text);
        final SpokenInputEvent acceptedEvent =
            new SpokenInputEvent(this, SpokenInputEvent.RESULT_ACCEPTED,
                    result);
        fireInputEvent(acceptedEvent);
    }

    /**
     * {@inheritDoc}
     */
    public URI getUriForNextSpokenInput() throws NoresourceError {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
       return recognizing;
    }

    /**
     * Notifies all registered listeners about the given event.
     * @param event the event.
     * @since 0.6
     */
    private void fireInputEvent(final SpokenInputEvent event) {
        synchronized (listener) {
            final Collection<SpokenInputListener> copy =
                new java.util.ArrayList<SpokenInputListener>();
            copy.addAll(listener);
            for (SpokenInputListener current : copy) {
                current.inputStatusChanged(event);
            }
        }
    }
    
    
    /**Sets the text input port*/
    public void setTextInputPort(final int port){
        
        textInputPort=port;
        
    }
    
    
    
}
