/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision: $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.mrcpv2;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.util.Collection;
import javax.speech.recognition.RuleGrammar;
import org.apache.log4j.Logger;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.client.mrcpv2.Mrcpv2RemoteClient;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.ObservableSpokenInput;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.vxml.BargeInType;
import org.mrcp4j.client.MrcpInvocationException;
import org.mrcp4j.message.MrcpEvent;
import org.mrcp4j.message.header.IllegalValueException;
import org.speechforge.cairo.client.NoMediaControlChannelException;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.client.SpeechEventListener;
import org.speechforge.cairo.client.recog.RecognitionResult;


/**
 * Audio input that uses a mrcpv2 client to use a recognition resource.
 *
 * <p>
 * Handle all MRCPv2 calls to the recognizer to make MRCPv2 transparent
 * to the interpreter.
 * </p>
 *
 * @author Spencer Lord
 * @version $Revision: $
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class Mrcpv2SpokenInput implements SpokenInput,
        ObservableSpokenInput, SpeechEventListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(Mrcpv2SpokenInput.class);

    /** Listener for user input events. */
    private final Collection<SpokenInputListener> listeners;
   
    //private JSGFGrammar _grammar =  new JSGFGrammar();
    
    //TODO:  Handle load and activate grammars properly on the server.  At present the mrcpv2 server does not support it.  So just saving the grammar
    // to be passed to the server with the recognize request.  Should work ok for now for recognize request with a single grammar.
    private Reader _loadedGrammarReader;
    private GrammarType _loadedGrammarType;
    private String _activatedGrammarName;
    private boolean _activatedGrammarState;
  
	private Mrcpv2RemoteClient mrcpv2Client;  
    
    public Mrcpv2SpokenInput() {
        listeners = new java.util.ArrayList<SpokenInputListener>();
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws NoresourceError {
        LOGGER.info("Opening mrcpv2 spoken input.  (not implemented)");
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        LOGGER.info("Closing mrcpv2 spoken input.  (not implemented)");
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(final SpokenInputListener inputListener) {
        synchronized (listeners) {
            listeners.add(inputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(final SpokenInputListener inputListener) {
        synchronized (listeners) {
            listeners.remove(inputListener);
        }
    }


    /**
     * {@inheritDoc}
     */
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
    public GrammarImplementation<RuleGrammar> newGrammar(final GrammarType type) throws
            NoresourceError, UnsupportedFormatError {
        
        LOGGER.warn("NewGrammar not implemented!.  returning a null grammar");
        return null;

    }

    /**
     * {@inheritDoc}
     */
    public GrammarImplementation<RuleGrammar> loadGrammar(final Reader reader,
            final GrammarType type) throws NoresourceError, BadFetchError,
            UnsupportedFormatError {
        try {
	        mrcpv2Client.getRecogClient();
        } catch (NoMediaControlChannelException e) {
        	throw new NoresourceError("recognizer not available");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loading grammar from reader");
        }

        _loadedGrammarReader = reader;
        _loadedGrammarType = type;
        
        //TODO:  Determine why this method needs to return the RuleGrammarImplementation.  Hopefully it does not really need to...
        return null;
        //return new RuleGrammarImplementation(grammar);
        
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
    private boolean activateGrammar(final String name, final boolean activate) throws
            BadFetchError {
        
        _activatedGrammarName = name;
        _activatedGrammarState = activate;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void activateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars) throws
            BadFetchError, UnsupportedLanguageError, NoresourceError {

    	//make sure that the resoure (mrcp channel) is setup
    	try {
	        mrcpv2Client.getRecogClient();
        } catch (NoMediaControlChannelException e) {
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
            }
        }
        
    }

    /**
     * {@inheritDoc}
     */
    public void deactivateGrammars(
            final Collection<GrammarImplementation<? extends Object>> grammars) throws
            BadFetchError {

        for (GrammarImplementation<? extends Object> current : grammars) {
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
     * @todo Implement this record() method.
     */
    public void record(final OutputStream out) throws NoresourceError {
        throw new NoresourceError("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void startRecognition() throws NoresourceError, BadFetchError {

    	SpeechClient speechClient = null;
    	//make sure that the resoure (recognition mrcp channel) is setup
    	try {
    		speechClient = mrcpv2Client.getRecogClient();
        } catch (NoMediaControlChannelException e) {
        	throw new NoresourceError("recognizer not available");
        }
    	

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("starting recognition...");
        }
        
        try {
            _loadedGrammarReader.reset();
            long noInputTimeout = 0;
            speechClient.recognize(_loadedGrammarReader, false, false, noInputTimeout);
        } catch (MrcpInvocationException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Mrcpv2 invocation exception while initiating a recognition request",e);
            }
            throw new NoresourceError(e);
        } catch (IllegalValueException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Illegal Value exception while initiating a recognition request",e);
            }
            throw new NoresourceError(e);
        } catch (IOException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("IO exception while initiating a recognition request",e);
            }
            throw new NoresourceError(e);
        } catch (InterruptedException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Interruped exception while initiating a recognition request",e);
            }
            throw new NoresourceError(e);
        } catch (NoMediaControlChannelException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No Media Control Channel exception while initiating a recognition request",e);
            }
            throw new NoresourceError(e);
        }
       
        final SpokenInputEvent event =
            new SpokenInputEvent(this, SpokenInputEvent.RECOGNITION_STARTED);
        fireInputEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecognition() {

    	SpeechClient speechClient = null;
    	//make sure that the resoure (recognition mrcp channel) is setup
    	try {
    		speechClient = mrcpv2Client.getRecogClient();
        } catch (NoMediaControlChannelException e) {
           LOGGER.warn("No MRCP recognition media control channel to stop recocognition.  Ignoring request.");
        }
    	if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("stoping recognition...");
        }
    	try {
    		speechClient.stopActiveRecognitionRequests();
        } catch (MrcpInvocationException e) {
	        LOGGER.warn("MrcpException while stopping recognition."+e.getLocalizedMessage());
        } catch (IOException e) {
        	LOGGER.warn("IOException while stopping recognition."+e.getLocalizedMessage());
        } catch (InterruptedException e) {
        	LOGGER.warn("InteruptedException while stopping recognition."+e.getLocalizedMessage());
        } catch (NoMediaControlChannelException e) {
        	LOGGER.warn("No Media Control Channel Exception while stopping recognition."+e.getLocalizedMessage());
        }
    	
    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("activating input...");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("passivating input...");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client) throws IOException {

        mrcpv2Client = (Mrcpv2RemoteClient) client;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...connected");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("disconnecting input...");
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "mrcpv2";
    }

    /**
     * {@inheritDoc}
     */
    public Collection<GrammarType> getSupportedGrammarTypes() {

        final Collection<GrammarType> types =
                new java.util.ArrayList<GrammarType>();

        types.add(GrammarType.JSGF);

        return types;
    }

    //TODO: Determine if this is needed in the mrcpv2 case.  Hopefully returning null is ok.
    public URI getUriForNextSpokenInput() throws NoresourceError {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
         LOGGER.warn("isBusy check is not implemented.");  
        //TODO: Implement this.  Is it checking if there is a recognition request active?
        return false;
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


    public void recognitionEventReceived(MrcpEvent event, RecognitionResult r) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Recognition event received: " + event);
        }


        final org.jvoicexml.RecognitionResult recognitionResult =
                new Mrcpv2RecognitionResult(r);

        final SpokenInputEvent spokenInputEvent =
            new SpokenInputEvent(this, SpokenInputEvent.RESULT_ACCEPTED,
                    recognitionResult);
        fireInputEvent(spokenInputEvent);  
    }


    public void characterEventReceived(String c, EventType status) {
        LOGGER.warn("Character received event occurred in Mrcpv2 Spoken Input implementation.  Not implemeneted");
    }

    public void speechSynthEventReceived(MrcpEvent event) {
        LOGGER.warn("Speech Synth event received not implemented in SpokenInput: " + event);
    }
    
}
