/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.implementation.jsapi10/src/org/jvoicexml/implementation/jsapi10/VNXIVRSynthesizedOutput.java $
 * Version: $LastChangedRevision: 3164 $
 * Date:    $Date: 2012-05-30 15:24:52 +0700 (Wed, 30 May 2012) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date: 2012-05-30 15:24:52 +0700 (Wed, 30 May 2012) $, Dirk Schnelle-Walka, project lead
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
package org.jvoicexml.implementation.mobicents;

import org.apache.log4j.Logger;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.synthesis.SpeakableEvent;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.MarkerReachedEvent;
import org.jvoicexml.implementation.ObservableSynthesizedOutput;
import org.jvoicexml.implementation.OutputEndedEvent;
import org.jvoicexml.implementation.OutputStartedEvent;
import org.jvoicexml.implementation.QueueEmptyEvent;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;
import org.mobicents.servlet.sip.restcomm.callmanager.mgcp.MgcpCallTerminal;
import org.mobicents.servlet.sip.restcomm.callmanager.mgcp.MgcpIvrEndpoint;
import org.mobicents.servlet.sip.restcomm.media.api.Call;
import org.mobicents.servlet.sip.restcomm.media.api.CallObserver;
import org.util.ExLog;

/**
 * Audio output that uses the Mobicents 1.0 to address the TTS engine.
 *
 * <p>
 * Handle all Mobicents calls to the TTS engine to make Mobicents transparent to the
 * interpreter.
 * </p>
 *
 * <p>
 * The queued {@link SpeakableText}s are maintained in a list. Once a speakable
 * has ended (this is detected via the {@link #speakableEnded(SpeakableEvent)}
 * method) this implementation processes the next {@link SpeakableText} from
 * the queue.
 * </p>
 *
 * <p>
 * SSML is supported via {@link SSMLSpeakStrategy}s.
 * </p>
 *
 * <p>
 * This implementation offers 2 ways to overcome the lack in Mobicents 1.0 not
 * to be able to stream audio data.
 * <ol>
 * <li>use custom streaming via {@link SpokenInputConnectionHandler} and</li>
 * <li>direct streaming via {@link StreamableSynthesizedOutput}.
 * </ol>
 * </p>
 *
 * <p>
 * Note that these ways are not fully tested and might be changed towards
 * the Mobicents 2 way.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3164 $
 */
public final class MobicentsSynthesizedOutput
        implements SynthesizedOutput, ObservableSynthesizedOutput,
        CallObserver, StreamableSynthesizedOutput {

    private static final Logger LOGGER = Logger.getLogger(MobicentsSynthesizedOutput.class);
    /** Factory for SSML speak strategies. */
    private static final SSMLSpeakStrategyFactory SPEAK_FACTORY;
    /** Size of the read buffer when reading objects. */
    private static final int READ_BUFFER_SIZE = 1024;
    /** Number of msec to wait when waiting for an empty queue. */
    private static final int WAIT_EMPTY_TIMEINTERVALL = 1000;
    /** The used synthesizer. */
    private Synthesizer synthesizer;
    /** The default synthesizer mode descriptor. */
    private final SynthesizerModeDesc desc;
    /** The system output listener. */
    private final Collection<SynthesizedOutputListener> listener;
    /** A custom handler to handle remote connections. */
    private SynthesizedOutputConnectionHandler handler;
    /** Type of this resources. */
    private String type;
    /** Information about the current connection. */
    private ConnectionInformation info;
    /** Set to <code>true</code> if SSML output is active. */
    private boolean queueingSsml;
    /**
     * Set to <code>true</code> if SSML output is active and there was a cancel
     * output request.
     */
    private boolean outputCanceled;
    /** Streams to be played by the streamable output. */
    private final BlockingQueue<InputStream> synthesizerStreams;
    /** Current synthesizer stream of the streamable output. */
    private InputStream currentSynthesizerStream;
    /** Stream  buffer that is used for streamable outputs. */
    private ByteArrayOutputStream streamBuffer;
    /** Reference to the document server. */
    private DocumentServer documentServer;
    /**
     * Flag to indicate that TTS output and audio of the current speakable can
     * be canceled.
     */
    private boolean bargein;
    /** Suggested behavior for the bargein. */
    private BargeInType bargeInType;
    /** Queued speakables. */
    private final Queue<SpeakableText> queuedSpeakables;
    /** Object lock for an empty queue. */
    private final Object emptyLock;
    /** Object lock to signal the end of a speakable. */
    private final Object endplayLock;
    /** The Id of the current session. */
    private String sessionId;

    static {
        SPEAK_FACTORY = new org.jvoicexml.implementation.mobicents.speakstrategy.JVoiceXmlSpeakStratgeyFactory();
    }

    /**
     * Constructs a new audio output.
     *
     * @param defaultDescriptor
     *            the default synthesizer mode descriptor.
     */
    public MobicentsSynthesizedOutput(
            final SynthesizerModeDesc defaultDescriptor) {
        LOGGER.debug("constructor ...:SynthesizerModeDesc:" + defaultDescriptor);
        desc = defaultDescriptor;
        listener = new java.util.ArrayList<SynthesizedOutputListener>();
        queuedSpeakables = new java.util.LinkedList<SpeakableText>();
        emptyLock = new Object();
        endplayLock = new Object();
        synthesizerStreams =
                new java.util.concurrent.LinkedBlockingQueue<InputStream>();
    }

    /**
     * {@inheritDoc}
     */
    public void open()
            throws NoresourceError {
        try {
            LOGGER.debug("allocating synthesizer...");
            synthesizer = Central.createSynthesizer(desc);
            if (synthesizer == null) {
                throw new NoresourceError("Error creating the synthesizer!");
            }
            LOGGER.debug("allocating synthesizer..." + synthesizer);
            synthesizer.allocate();
            synthesizer.resume();
            LOGGER.debug("...synthesizer allocated");
        } catch (EngineException ee) {
            throw new NoresourceError(ee);
        } catch (AudioException ae) {
            throw new NoresourceError(ae);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        if (synthesizer == null) {
            LOGGER.warn("no synthesizer: cannot deallocate");
            return;
        }

        LOGGER.debug("closing audio output...");

        waitQueueEmpty();

        LOGGER.debug("deallocating synthesizer...");

        try {
            synthesizer.deallocate();
            LOGGER.debug("...synthesizer deallocated");
        } catch (EngineException ee) {
            LOGGER.warn("error deallocating synthesizer", ee);
        } catch (Exception ee) {
            ExLog.exception(LOGGER, ee);
        } finally {
            synthesizer = null;
        }

        LOGGER.debug("...audio output closed");
    }

    /**
     * Retrieves the encapsulated synthesizer.
     * @return the encapsulated synthesizer.
     * @since 0.6
     */
    public Synthesizer getSynthesizer() {
        return synthesizer;
    }

    /**
     * Retrieves the document server.
     * @return the document server
     * @since 0.7.5
     */
    public DocumentServer getDocumentServer() {
        return documentServer;
    }

    /**
     * Retrieves the Id of the current session.
     * @return the Id of the current session
     * @since 0.7.5
     */
    public String getSessionid() {
        return sessionId;
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(
            final SynthesizedOutputListener outputListener) {
        synchronized (listener) {
            listener.add(outputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(
            final SynthesizedOutputListener outputListener) {
        synchronized (listener) {
            listener.remove(outputListener);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Checks the type of the given speakable and forwards it either as for SSML
     * output or for plain text output.
     */
    @Override
    public void queueSpeakable(final SpeakableText speakable,
            final String id,
            final DocumentServer server)
            throws NoresourceError, BadFetchError {
        if (synthesizer == null) {
            throw new NoresourceError("no synthesizer: cannot speak");
        }
        sessionId = id;
        
        synchronized (queuedSpeakables) {
            queuedSpeakables.offer(speakable);
        }
        LOGGER.debug("\t offered a speakable:" + speakable + " \n id :" + id
                +" \n current queuedSpeakables:"+queuedSpeakables);
        documentServer = server;
        // Do not process the speakable if there is some ongoing processing
        synchronized (queuedSpeakables) {
            if (queuedSpeakables.size() > 1) {
                LOGGER.error("Do not process the speakable if there is some ongoing processing:" + queuedSpeakables);
                return;
            }
        }
        outputCanceled = false;
        // Otherwise process the added speakable asynchronous.
        final Runnable runnable = new Runnable() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                try {
                    processNextSpeakable();
                } catch (NoresourceError e) {
                    notifyError(e);
                } catch (BadFetchError e) {
                    notifyError(e);
                }
            }
        };
        final Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Processes the next speakable in the queue.
     * @throws NoresourceError
     *         error processing the speakable.
     * @throws BadFetchError
     *         error processing the speakable.
     * @since 0.7.1
     */
    private synchronized void processNextSpeakable()
            throws NoresourceError, BadFetchError {
        // Reset all flags of the previous output.
        queueingSsml = false;
        bargeInType = null;
        bargein = false;

        // Check if there are more speakables to process
        final SpeakableText speakable;
        synchronized (queuedSpeakables) {
            if (queuedSpeakables.isEmpty()) {
                LOGGER.debug("no more speakables to process");
                fireQueueEmpty();
                synchronized (emptyLock) {
                    emptyLock.notifyAll();
                }
                return;
            }
            speakable = queuedSpeakables.peek();
        }
        LOGGER.debug("processing next speakable :" + speakable);
        // Really process the next speakable
        fireOutputStarted(speakable);

        if (speakable instanceof SpeakableSsmlText) {
            final SpeakableSsmlText ssml = (SpeakableSsmlText) speakable;
            bargein = ssml.isBargeInEnabled();
            bargeInType = ssml.getBargeInType();
            LOGGER.debug("bargein:" + bargein + " bargeInType:" + bargeInType);
            speakSSML(ssml, documentServer);
        } else {
            LOGGER.error("unsupported speakable: " + speakable);
        }
    }

    /**
     * Speaks the speakable SSML formatted text.
     *
     * @param text
     *            SSML formatted text.
     * @param server
     *            The DocumentServer to use.
     * @exception NoresourceError
     *                The output resource is not available.
     * @exception BadFetchError
     *                Error reading from the <code>AudioStream</code>.
     */
    private void speakSSML(final SpeakableSsmlText text,
            final DocumentServer server)
            throws NoresourceError, BadFetchError {

        final SsmlDocument document = text.getDocument();
        LOGGER.debug("speaking SSML:outputCanceled:" + outputCanceled + " document:" + document.toString());

        final SsmlNode speak = document.getSpeak();
        if (speak == null) {
            return;
        }

        final SSMLSpeakStrategy strategy =
                SPEAK_FACTORY.getSpeakStrategy(speak);
        if (strategy != null) {
            strategy.speak(this, speak);
        }
//        if (!outputCanceled) {
//            final SpeakableEvent event =
//                new SpeakableEvent(document, SpeakableEvent.SPEAKABLE_ENDED);
//            speakableEnded(event);
//        }
    }

    /**
     * Notifies all listeners that output has started.
     * @param speakable the current speakable.
     */
    private void fireOutputStarted(final SpeakableText speakable) {
        final SynthesizedOutputEvent event =
                new OutputStartedEvent(this, null, speakable);
        fireOutputEvent(event);
    }

    /**
     * Notifies all listeners that the given marker has been reached.
     * @param mark the reached marker.
     */
    private void fireMarkerReached(final String mark) {
        final SynthesizedOutputEvent event =
                new MarkerReachedEvent(this, null, mark);
        fireOutputEvent(event);
    }

    /**
     * Notifies all listeners that output has ended.
     * @param speakable the current speakable.
     */
    private void fireOutputEnded(final SpeakableText speakable) {
        final SynthesizedOutputEvent event =
                new OutputEndedEvent(this, null, speakable);
        fireOutputEvent(event);
        synchronized (endplayLock) {
            endplayLock.notifyAll();
        }
    }

    /**
     * Notifies all listeners that output queue is empty.
     */
    private void fireQueueEmpty() {
        final SynthesizedOutputEvent event = new QueueEmptyEvent(this, null);
        fireOutputEvent(event);
    }

    /**
     * Speaks a plain text string.
     *
     * @param text
     *            String containing the plain text to be spoken.
     * @exception NoresourceError
     *                No recognizer allocated.
     * @exception BadFetchError
     *                Recognizer in wrong state.
     */
    public void speakPlaintext(final String text)
            throws NoresourceError, BadFetchError {
        if (synthesizer == null) {
            throw new NoresourceError("no synthesizer: cannot speak");
        }
        LOGGER.info("speaking '" + text + "'...");
        try {
            LOGGER.error("currently, don't support this function");
        } catch (EngineStateError e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsBargeIn() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void cancelOutput()
            throws NoresourceError {
        if (synthesizer == null) {
            throw new NoresourceError("No synthesizer: Cannot cancel output");
        }

        if (!bargein) {
            LOGGER.debug("bargein not active for current output");
            return;
        }

        LOGGER.debug("cancelling current output with bargein is active...");
        outputCanceled = true;
        synchronized (queuedSpeakables) {
            try {
                synthesizer.cancelAll();
            } catch (EngineStateError ee) {
                throw new NoresourceError(ee);
            }

            final Collection<SpeakableText> skipped =
                    new java.util.ArrayList<SpeakableText>();
            for (SpeakableText speakable : queuedSpeakables) {
                if (speakable.isBargeInEnabled()) {
                    skipped.add(speakable);
                } else {
                    // Stop iterating after the first non-bargein speakable
                    // has been detected
                    break;
                }
            }
            LOGGER.debug("removing all skipped:"+skipped);
            queuedSpeakables.removeAll(skipped);
            if (queuedSpeakables.isEmpty()) {
                fireQueueEmpty();
                synchronized (emptyLock) {
                    emptyLock.notifyAll();
                }
            } else {
                final Runnable runnable = new Runnable() {

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void run() {
                        try {
                            outputCanceled = false;
                            processNextSpeakable();
                        } catch (NoresourceError e) {
                            notifyError(e);
                        } catch (BadFetchError e) {
                            notifyError(e);
                        }
                    }
                };
                final Thread thread = new Thread(runnable);
                thread.start();
            }
        }
        //reset handle
        handler = null;
        LOGGER.debug("...output cancelled. queuedSpeakables:" + queuedSpeakables + " reset handler:" + handler + " this:" + this);

    }

//    public void cancelAllSpeakers()
//            throws NoresourceError {
//        if (synthesizer == null) {
//            LOGGER.error("No synthesizer: Cannot cancel output");
//        }
//
//        LOGGER.debug("forcing current output cancelled...");
//        outputCanceled = true;
//        synchronized (queuedSpeakables) {
//            try {
//                synthesizer.cancelAll();
//            } catch (EngineStateError ee) {
//                throw new NoresourceError(ee);
//            }
//            queuedSpeakables.clear();
//            if (queuedSpeakables.isEmpty()) {
//                fireQueueEmpty();
//                synchronized (emptyLock) {
//                    emptyLock.notifyAll();
//                }
//            }
//        }
//        //reset handle
//        handler = null;
//        LOGGER.debug("...output cancelled. queuedSpeakables:" + queuedSpeakables + " handler:" + handler + " this:" + this);
//
//    }

    /**
     * Checks if there was a request to cancel the current output.
     * @return <code>true</code> if there was a request to cancel the current
     * output.
     * @since 0.7.3
     */
    public boolean isOutputCanceled() {
        return outputCanceled;
    }

    /**
     * Blocks the calling thread until the Engine is in a specified state.
     * <p>
     * All state bits specified in the state parameter must be set in order for
     * the method to return, as defined for the testEngineState method. If the
     * state parameter defines an unreachable state (e.g. PAUSED | RESUMED) an
     * exception is thrown.
     * </p>
     * <p>
     * The waitEngineState method can be called successfully in any Engine
     * state.
     * </p>
     *
     * @param state
     *            State to wait for.
     * @exception java.lang.InterruptedException
     *                If another thread has interrupted this thread.
     */
    public void waitEngineState(final long state)
            throws java.lang.InterruptedException {
        if (synthesizer == null) {
            LOGGER.warn("no synthesizer: cannot wait for engine state");
            return;
        }

        if (true) {
            LOGGER.debug("waiting for synthesizer engine state " + state);
        }

        final long current = synthesizer.getEngineState();
        if (current != state) {
            synthesizer.waitEngineState(state);
        }

        if (true) {
            LOGGER.debug("...reached engine state " + state);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitNonBargeInPlayed() 
    {
        long starttime = System.currentTimeMillis();
        if (bargein == false) {
            LOGGER.debug("waiting until all non-barge-in has been played...");
        } else if (bargein == true) {
            LOGGER.debug("bargein is active for current output");
            return;
        }
        boolean stopWaiting = false;
        while (!stopWaiting) {
            synchronized (endplayLock) {
                try {
                    LOGGER.debug("WAIT_EMPTY_TIMEINTERVALL or endplayLock ... "
                            + "\n\t queuedSpeakables:"+queuedSpeakables  ); 
                    endplayLock.wait(WAIT_EMPTY_TIMEINTERVALL);
                } catch (InterruptedException e) {
                    return;
                }
            }
            synchronized (queuedSpeakables) {
                if (queuedSpeakables.isEmpty()) {
                    stopWaiting = true;
                } else {
//                    final SpeakableText speakable = queuedSpeakables.peek();
//                    if (speakable instanceof SpeakableSsmlText) 
//                    {
//                        final SpeakableSsmlText ssml =
//                                (SpeakableSsmlText) speakable;
//                        LOGGER.debug("stopWaiting:"+stopWaiting + " getBargeInType:"+ssml.getBargeInType());
//                        stopWaiting = ssml.getBargeInType() == null;
//                    }
                }
            }
        }
        LOGGER.debug("...all non barge-in has been played: waittedTime:" 
                + (System.currentTimeMillis() - starttime) + " queuedSpeakables:"+queuedSpeakables);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitQueueEmpty() {
        LOGGER.warn("waiting queuedSpeakables is emply:" + queuedSpeakables);
        while (!queuedSpeakables.isEmpty()) {
            synchronized (emptyLock) {
                try {
                    emptyLock.wait(WAIT_EMPTY_TIMEINTERVALL);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    /**
     * A mark in an SSML output has been reached.
     * @param mark
     *            Name of the mark.
     */
    public void reachedMark(final String mark) {
        if (listener == null) {
            return;
        }

        fireMarkerReached(mark);
    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        LOGGER.debug("passivating output...");
        // Clear all lists and reset the flags.
        listener.clear();
        queuedSpeakables.clear();
        queueingSsml = false;
        outputCanceled = false;
        info = null;
        documentServer = null;
        bargein = false;
        bargeInType = null;
        LOGGER.debug("...passivated output");
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final ConnectionInformation connectionInformation)
            throws IOException {
        LOGGER.debug("...connecting with  connectionInformation:" + connectionInformation + " handle: " + handler);
        if (handler != null) {
            handler.connect(connectionInformation, this, synthesizer);
        }

        info = connectionInformation;
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final ConnectionInformation connectionInformation) 
    {
        LOGGER.debug("...disconnecting with  connectionInformation:" + connectionInformation + " handle: " + handler);
        if (handler != null) 
        {
            handler.disconnect(connectionInformation, this, synthesizer);
        }
        info = null;
        //
//        try {
////            cancelAllSpeakers();
//        } catch (NoresourceError err) {
//            ExLog.exception(LOGGER, err);
//        }
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of this resource.
     * @param resourceType type of the resource
     */
    public void setType(final String resourceType) {
        type = resourceType;
    }

    /**
     * Sets a custom connection handler.
     * @param connectionHandler the connection handler.
     */
    public void setSynthesizedOutputConnectionHandler(
            final SynthesizedOutputConnectionHandler connectionHandler) throws NoresourceError {
        try {
            if (connectionHandler != null) {
                LOGGER.info(" connectionHandler is :" + connectionHandler
                        + " uri:" + connectionHandler.getUriForNextSynthesisizedOutput(info));
            }
            handler = connectionHandler;
        } catch (Exception ex) {
            ExLog.exception(LOGGER, ex);
        }

    }

    /**
     * {@inheritDoc}
     */
    public URI getUriForNextSynthesisizedOutput() throws NoresourceError {
        try {
            // Do not process the speakable if there is some ongoing processing
            while (handler == null || queuedSpeakables.size() > 1) 
            {
                LOGGER.warn("waiting for handling..." + handler + " queueSpeaker:"+queuedSpeakables.size());
                Thread.sleep(50);
            }
            if (handler != null) {
                LOGGER.info(" handler is :" + handler
                        + " uri:" + handler.getUriForNextSynthesisizedOutput(info) + " this:" + this
                        + " queuedSpeakables:" + queuedSpeakables);
                return handler.getUriForNextSynthesisizedOutput(info);
            } else {
                LOGGER.error(" handler is null:" + handler + " info :" + info
                        + " documentServer:" + documentServer);
            }
        } catch (Exception ex) {
            ExLog.exception(LOGGER, ex);
        }
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void markerReached(final SpeakableEvent event) 
    {
        LOGGER.debug("... with event:"+event);
        final String mark = event.getText();
        fireMarkerReached(mark);
    }

    /**
     * {@inheritDoc}
     */
    public void speakableCancelled(final SpeakableEvent event) 
    {
        LOGGER.debug("... with event:"+event);
    }

    /**
     * {@inheritDoc}
     */
    public void speakableEnded(final SpeakableEvent event) 
    {
        LOGGER.debug("... with event:"+event);
        final Object source = event.getSource();
        if (source instanceof SsmlDocument) {
            queueingSsml = false;
            LOGGER.debug("ending Synthesis of an SSML document started");
        }
        final boolean removeSpeakable = !queueingSsml;
        if (removeSpeakable) {
            // TODO this will fail if we end with an audio or break tag.
            final SpeakableText speakable;
            synchronized (queuedSpeakables) {
                speakable = queuedSpeakables.poll();
            }
            if (true) {
                if (speakable != null) 
                {
                    LOGGER.debug("speakable ended: "
                            + speakable.getSpeakableText());
                    if(handler !=null)
                    {
                        try
                        {
                            URI uri=handler.getUriForNextSynthesisizedOutput(info);
                            if(speakable.getSpeakableText().indexOf(uri.toString()) != -1)
                            {
                                LOGGER.debug("reset  handler: "+handler + " with uri:"+uri);
                                handler=null;
                            }
                        }catch(NoresourceError ex)
                        {
                            ExLog.exception(LOGGER, ex);
                        }
                    }
                }
            }

            // If streaming is supported, add the stream to the queue.
            if (streamBuffer != null) {
                final byte[] buffer = streamBuffer.toByteArray();
                final InputStream input = new ByteArrayInputStream(buffer);
                try {
                    synthesizerStreams.put(input);
                } catch (InterruptedException e) {
                    LOGGER.debug("unable to add a synthesizer stream", e);
                }
                streamBuffer = null;
            }

            if (speakable != null) {
                fireOutputEnded(speakable);
            }
            try {
                processNextSpeakable();
            } catch (NoresourceError e) {
                notifyError(e);
            } catch (BadFetchError e) {
                notifyError(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void speakablePaused(final SpeakableEvent event) 
    {
        LOGGER.debug("... with event:"+event);
    }

    /**
     * {@inheritDoc}
     */
    public void speakableResumed(final SpeakableEvent event) 
    {
        LOGGER.debug("... with event:"+event);
    }

    /**
     * {@inheritDoc}
     */
    public void speakableStarted(final SpeakableEvent event) 
    {
        LOGGER.debug("... with event:"+event);
        final Object source = event.getSource();
        if (source instanceof SsmlDocument) {
            LOGGER.debug("synthesis of an SSML document started: " + source);
            queueingSsml = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void topOfQueue(final SpeakableEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void wordStarted(final SpeakableEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        synchronized (queuedSpeakables) {
            return !queuedSpeakables.isEmpty();
        }
    }

    /**
     * {@inheritDoc}
     */
    public int readSynthesizerStream(final byte[] buffer, final int offset,
            final int length) throws IOException {
        if (currentSynthesizerStream == null) {
            try {
                currentSynthesizerStream = synthesizerStreams.take();
            } catch (InterruptedException e) {
                throw new IOException(e.getMessage());
            }
        }

        return currentSynthesizerStream.read(buffer, offset, length);
    }

    /**
     * Reads from the given output stream and adds the result to the list of
     * streams.
     * @param input stream to read from.
     * @exception IOException
     *            Error reading from the given stream.
     */
    public void addSynthesizerStream(final InputStream input)
            throws IOException {
        if (streamBuffer == null) {
            streamBuffer = new ByteArrayOutputStream();
        }

        final byte[] buffer = new byte[READ_BUFFER_SIZE];
        int num;
        do {
            num = input.read(buffer);
            if (num >= 0) {
                streamBuffer.write(buffer, 0, num);
            }
        } while (num >= 0);
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
     * Notifies all registered listeners about the given error.
     * @param error the error.
     * @since 0.7.4
     */
    private void notifyError(final ErrorEvent error) {
        synchronized (listener) {
            final Collection<SynthesizedOutputListener> copy =
                    new java.util.ArrayList<SynthesizedOutputListener>();
            copy.addAll(listener);
            for (SynthesizedOutputListener current : copy) {
                current.outputError(error);
            }
        }
    }

    /**
     * 
     * @param call 
     * @Override
     */
    public void onStatusChanged(Call call) 
    {
        try 
        {
            MgcpCallTerminal term=(MgcpCallTerminal)call;
            LOGGER.debug("current is " + call.toString() + " \n queuedSpeakables:"+queuedSpeakables);
            if ((Call.Status.COMPLETED == call.getStatus() || Call.Status.IN_PROGRESS == call.getStatus())
                    && term.getIVREndPointState() ==MgcpIvrEndpoint.IDLE
                    && (term.getIVREndPointOldState() ==MgcpIvrEndpoint.PLAY
                    || term.getIVREndPointOldState() ==MgcpIvrEndpoint.PLAY_COLLECT
                    || term.getIVREndPointOldState() ==MgcpIvrEndpoint.PLAY_RECORD))
            {
                if (documentServer == null) 
                {
                    LOGGER.error("  documentServer is null");
                }
                else
                {
                    //must check the speaker is already play, before ended the speaker.
                    List<URI> listURI=term.getCurrentAnnouncements();
                    if(listURI!=null && !listURI.isEmpty())
                    {
                        final SpeakableText speakable;
                        synchronized (queuedSpeakables) {
                            speakable = queuedSpeakables.peek();
                        }
                        if(queuedSpeakables.isEmpty() || speakable==null ) return;
                        for(URI uri:listURI)
                        {
                            if(speakable.getSpeakableText().indexOf(uri.toString()) != -1)
                            {
                                //if a speaker is already playing, it's will be ended by endedEvent
                                final SpeakableEvent event = new SpeakableEvent(documentServer
                                        , SpeakableEvent.SPEAKABLE_ENDED);
                                speakableEnded(event);
                            }
                        }
                    }
                    else
                    {
                        LOGGER.warn("  wrong mapping ended event");
                        return;
                    }
                    
                }
            }
        } catch (Exception ex) {
            ExLog.exception(LOGGER, ex);
        }

    }
}
