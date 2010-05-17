/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.mary;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

import marytts.client.MaryClient;

import org.apache.log4j.Logger;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioFileOutput;
import org.jvoicexml.implementation.MarkerReachedEvent;
import org.jvoicexml.implementation.ObservableSynthesizedOutput;
import org.jvoicexml.implementation.OutputEndedEvent;
import org.jvoicexml.implementation.OutputStartedEvent;
import org.jvoicexml.implementation.QueueEmptyEvent;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;


/**
 * An implementation of the {@link SynthesizedOutput} for the Mary TTS System.
 * @author Dirk Schnelle-Walka
 * @author Giannis Assiouras
 * @version $Revision: $
 * @since 0.7.3
 */
public class MarySynthesizedOutput implements SynthesizedOutput,
    ObservableSynthesizedOutput, SynthesizedOutputListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(MarySynthesizedOutput.class);

    /** The system output listener. */
    private final Collection<SynthesizedOutputListener> listener;

    /** Type of this resources. */
    private String type;

    /** Object lock for an empty queue. */
    private final Object emptyLock;

    
    /**
     * Flag to indicate that TTS output and audio of the current speakable can
     * be canceled.
     */
    private boolean enableBargeIn;

    /**Reference to SynthesisQueue Thread.*/
    private final SynthesisQueue synthesisQueue;

    /** Mary Request serverTimeout Parameter. */
    private final int serverTimeout = 5000;

    /**
     * Reference to the MaryClient object that will be used.
     * to send the request to Mary server
     */
    private MaryClient processor;

    /**
     * Flag that indicates that synthesisQueue Thread is Currently.
     * processing a speakable.
     */
    private boolean isBusy;

    /**Flag that indicates that speakable queue is empty.*/
    private boolean speakableQueueEmpty = true;

    /**The HashTable that contains Mary synthesis request parameters.
     * e.g audioType,voiceName,voiceEffects and their value
     */
    private final Map<String, String> maryRequestParameters;

    /**
     * Constructs a new MarySynthesizedOutput object.
     */
    public MarySynthesizedOutput() {
        synthesisQueue = new SynthesisQueue();
        synthesisQueue.addListener(this);
        listener = new java.util.ArrayList<SynthesizedOutputListener>();
        emptyLock = new Object();
        maryRequestParameters = new java.util.HashMap<String, String>();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final URI getUriForNextSynthesisizedOutput() throws NoresourceError,
            URISyntaxException {
        return null;
    }


  /**The queueSpeakable method simply offers a speakable to the queue.
   *it notifies the synthesisQueue Thread and then it returns
   *@param speakable the speakable to be stored in the queue
   *@param server document server is not used in this implementation
   *@throws NoresourceError if no MaryClient has been created
   */
    public final void queueSpeakable(final SpeakableText speakable,
            final DocumentServer server) throws NoresourceError {
        if (processor == null) {
            throw new NoresourceError("no synthesizer: cannot speak");
        }
        synthesisQueue.queueSpeakables(speakable);
        speakableQueueEmpty = false;
      }


    @Override
    public final boolean requiresAudioFileOutput() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAudioFileOutput(final AudioFileOutput fileOutput) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void waitNonBargeInPlayed() {
        if (enableBargeIn) {
            waitQueueEmpty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void waitQueueEmpty() {
        isBusy();
    }

    /**It creates the MaryClient and starts the synthesisQueue thread.*/
    public final void activate() {
        try {
            processor = MaryClient.getMaryClient();
            

            } catch (Exception e1) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Error Creating Mary Client");
                }
            }
           synthesisQueue.setProcessor(processor);
           synthesisQueue.setRequestParameters(maryRequestParameters);
           synthesisQueue.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void close() {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("closing audio output...");
        }

        waitQueueEmpty();


        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...audio output closed");
        }

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String getType() {
        return type;

    }


    @Override
    public void open() throws NoresourceError {
    }

    
    /**
     * {@inheritDoc}
     */
    public final void passivate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("passivating output...");
        }
        // Clear all lists and reset the flags.
        listener.clear();
        synthesisQueue.clearQueue();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...passivated output");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void connect(final RemoteClient remoteClient)
        throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void disconnect(final RemoteClient remoteClient) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void cancelOutput() {
        synthesisQueue.cancelOutput();
    }

    /**
     * {@inheritDoc}
     * @return <code>true</code>
     */
    public final boolean supportsBargeIn() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addListener(final SynthesizedOutputListener
                outputListener) {
        synchronized (listener) {
            listener.add(outputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeListener(final SynthesizedOutputListener
            outputListener) {

        synchronized (listener) {
            listener.remove(outputListener);
        }
    }

    /**
     * Notifies all listeners that output has started.
     * @param speakable the current speakable.
     */
    private void fireOutputStarted(final SpeakableText speakable) {
        final SynthesizedOutputEvent event =
            new OutputStartedEvent(this, speakable);
        fireOutputEvent(event);
    }

    /**
     * Notifies all listeners that the given marker has been reached.
     * @param mark the reached marker.
     */
    private void fireMarkerReached(final String mark) {
        final SynthesizedOutputEvent event =
            new MarkerReachedEvent(this, mark);
        fireOutputEvent(event);
    }

    /**
     * Notifies all listeners that output has ended.
     * @param speakable the current speakable.
     */
    private void fireOutputEnded(final SpeakableText speakable) {
        final SynthesizedOutputEvent event =
            new OutputEndedEvent(this, speakable);
        fireOutputEvent(event);

    }

    /**
     * Notifies all listeners that output queue is empty.
     */
    private void fireQueueEmpty() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Queue empty event fired to Implementation Platform");
        }

        final SynthesizedOutputEvent event = new QueueEmptyEvent(this);
        fireOutputEvent(event);
    }

    /**
     * Notifies all registered listeners about the given event.
     * @param event the event.
     * @since 0.6
     */
    private void fireOutputEvent(final SynthesizedOutputEvent event) {
        synchronized (listener) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>();
            copy.addAll(listener);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Sets the type of this resource.
     * @param resourceType type of the resource
     */
    public final void setType(final String resourceType) {
        type = resourceType;
    }


    /**
     * Gets the events fired from SynthesisQueue thread and it forwards them.
     * to ImplementationPlatform
     * it also sets the appropriate flags
     * @param event the event.
     */
    public final void outputStatusChanged(final SynthesizedOutputEvent event) {
        final int id = event.getEvent();
        switch (id) {
        case SynthesizedOutputEvent.OUTPUT_STARTED:
            final OutputStartedEvent outputStartedEvent =
                (OutputStartedEvent) event;
            final SpeakableText startedSpeakable =
                outputStartedEvent.getSpeakable();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("output started " + startedSpeakable);
            }
            isBusy = true;
            fireOutputStarted(startedSpeakable);
            break;
        case SynthesizedOutputEvent.OUTPUT_ENDED:
            final OutputEndedEvent outputEndedEvent =
                (OutputEndedEvent) event;
            final SpeakableText endedSpeakable =
                outputEndedEvent.getSpeakable();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("audio playing ended");
            }
            isBusy = false;
            fireOutputEnded(endedSpeakable);
            break;
        case SynthesizedOutputEvent.QUEUE_EMPTY:
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("output queue is empty");
            }
            speakableQueueEmpty = true;
            fireQueueEmpty();
            synchronized (emptyLock) {
                emptyLock.notifyAll();
            }
            break;
      /*  case SynthesizedOutputEvent.MARKER_REACHED:
            final MarkerReachedEvent markReachedEvent =
                (MarkerReachedEvent) event;
            markname = markReachedEvent.getMark();
            LOGGER.info("reached mark '" + markname + "'");
            break;
          case SynthesizedOutputEvent.OUTPUT_UPDATE:
            break;*/
        default:
            fireOutputEvent(event);
            break;
        }
    }


    @Override
    public final boolean isBusy() {

        while (!speakableQueueEmpty || isBusy) {
            synchronized (emptyLock) {
                try {
                    emptyLock.wait();
                } catch (InterruptedException e) {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Stops the currently playing Audio.
     * @throws NoresourceError .
     * */
    public final void cancelAudioOutput() throws NoresourceError {
        synthesisQueue.cancelAudioOutput();
    }

    /**
     * Sets the audio output.
     * @param value the new audio type
     */
    public final void setAudioType(final String value) {
        if (value == null) {
            return;
        }
        maryRequestParameters.put("audioType", value);
    }

    /**
     * Sets the name of the voice to use.
     * @param name the voice name
     */
    public final void setVoiceName(final String name) {
        if (name == null) {
            return;
        }
        maryRequestParameters.put("voiceName", name);
       
   
    }
    /**Sets the lang*/
    public final void setLang(final String lang){
        if(lang == null) {
            return;
        }
        maryRequestParameters.put("lang",lang);
    }
}



