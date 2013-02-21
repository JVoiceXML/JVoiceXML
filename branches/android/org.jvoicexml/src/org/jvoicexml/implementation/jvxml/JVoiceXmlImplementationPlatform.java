/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/jvxml/JVoiceXmlImplementationPlatform.java $
 * Version: $LastChangedRevision: 2913 $
 * Date:    $Date: 2012-01-30 02:41:09 -0600 (lun, 30 ene 2012) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jvxml;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jvoicexml.CallControl;
import org.jvoicexml.CallControlProperties;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.Configurable;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.Session;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.EventObserver;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.event.plain.NomatchEvent;
import org.jvoicexml.event.plain.jvxml.RecognitionEvent;
import org.jvoicexml.event.plain.jvxml.TransferEvent;
import org.jvoicexml.implementation.ExternalRecognitionListener;
import org.jvoicexml.implementation.ExternalResource;
import org.jvoicexml.implementation.ExternalSynthesisListener;
import org.jvoicexml.implementation.ImplementationGrammarProcessor;
import org.jvoicexml.implementation.MarkerReachedEvent;
import org.jvoicexml.implementation.OutputEndedEvent;
import org.jvoicexml.implementation.OutputStartedEvent;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.implementation.Telephony;
import org.jvoicexml.implementation.TelephonyEvent;
import org.jvoicexml.implementation.TelephonyListener;
import org.jvoicexml.implementation.grammar.JVoiceXmlImplementationGrammarProcessor;
import org.jvoicexml.implementation.pool.KeyedResourcePool;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Basic implementation of an {@link ImplementationPlatform}.
 *
 * <p>
 * User actions and system output are not handled by this class but forwarded
 * to the corresponding {@link ExternalResource}s.
 * </p>
 *
 * <p>
 * External resources are considered to be in a pool. The implementation
 * platform is able to retrieve them from the pool and push them back.
 * This means that all resources that have been borrowed from the
 * implementation platform must be returned to it if they are no longer used.
 * </p>
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2913 $
 */
public final class JVoiceXmlImplementationPlatform
        implements SpokenInputListener, SynthesizedOutputListener,
            TelephonyListener, ImplementationPlatform, Configurable {

    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlImplementationPlatform.class);

    /** Timeout in msec to wait until the resource is not busy. */
    private static final int BUSY_WAIT_TIMEOUT = 1000;

    /** Pool of synthesizer output resource factories. */
    private final KeyedResourcePool<SynthesizedOutput> synthesizerPool;

    /** Pool of user input resource factories. */
    private final KeyedResourcePool<SpokenInput> recognizerPool;

    /** Pool of user calling resource factories. */
    private final KeyedResourcePool<Telephony> telephonyPool;

    /** Connection information to use. */
    private final ConnectionInformation info;

    /** The output device. */
    private JVoiceXmlSystemOutput output;

    /** Support for audio input. */
    private JVoiceXmlUserInput input;

    /** The grammar processor. */
    private final ImplementationGrammarProcessor processor;

    /** Input not busy notification lock. */
    private final Object inputLock;

    /** Support for DTMF input. */
    private final BufferedCharacterInput characterInput;

    /** The calling device. */
    private JVoiceXmlCallControl call;

    /** The event observer to communicate events back to the interpreter. */
    private EventObserver eventObserver;

    /** A timer to get the noinput timeout. */
    private TimerThread timer;

    /** The name of the mark last executed by the SSML processor. */
    private String markname;

    /** An external recognition listener. */
    private ExternalRecognitionListener externalRecognitionListener;

    /** An external synthesis listener. */
    private ExternalSynthesisListener externalSynthesisListener;

    /**
     * Flag set to <code>true</code> if the implementation platform is closed.
     */
    private boolean closed;

    /**
     * Flag set to <code>true</code> if the caller hung up the phone.
     */
    private boolean hungup;

    /** The current session. */
    private Session session;

    /** The prompt accumulator methods are implemented as a delegate. */
    private final JVoiceXmlPromptAccumulator promptAccumulator;

    /**
     * Constructs a new Implementation platform.
     *
     * <p>
     * This method should not be called by any application. The implementation
     * platform is accessible via the <code>Session</code>
     * </p>
     *
     * @param telePool  pool of telephony resource factories
     * @param synthesizedOutputPool pool of synthesized output resource
     *        factories
     * @param spokenInputPool pool of spoken input resource factories
     * @param connectionInformation connection information container
     *
     * @see org.jvoicexml.Session
     */
    JVoiceXmlImplementationPlatform(
            final KeyedResourcePool<Telephony> telePool,
            final KeyedResourcePool<SynthesizedOutput> synthesizedOutputPool,
            final KeyedResourcePool<SpokenInput> spokenInputPool,
            final ConnectionInformation connectionInformation) {
        info = connectionInformation;
        telephonyPool = telePool;
        synthesizerPool = synthesizedOutputPool;
        recognizerPool = spokenInputPool;
        characterInput = new BufferedCharacterInput();
        inputLock = new Object();
        processor = new JVoiceXmlImplementationGrammarProcessor();
        promptAccumulator = new JVoiceXmlPromptAccumulator(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final Configuration configuration)
        throws ConfigurationException {
        processor.init(configuration);
    }

    /**
     * Sets an external recognition listener and starts it.
     * @param listener the external recognition listener.
     * @since 0.6
     */
    public void setExternalRecognitionListener(
            final ExternalRecognitionListener listener) {
        externalRecognitionListener = listener;
        if (listener != null) {
            try {
                externalRecognitionListener.start();
            } catch (Throwable t) {
                LOGGER.debug("Could not start externalRecognitionListener:", t);
            }
        }
    }

    /**
     * Sets an external synthesis listener and starts it.
     * @param listener the external synthesis listener.
     * @since 0.6
     */
    public void setExternalSynthesisListener(
            final ExternalSynthesisListener listener) {
      externalSynthesisListener = listener;
      if (listener != null) {
          try {
              externalSynthesisListener.start();
          } catch (Throwable t) {
              LOGGER.debug("Could not start externalSynthesisListener:", t);
          }
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SystemOutput getSystemOutput()
            throws NoresourceError, ConnectionDisconnectHangupEvent {
        synchronized (this) {
            if (hungup) {
                throw new ConnectionDisconnectHangupEvent("caller hung up");
            }
            if (closed) {
                throw new NoresourceError("implementation platform closed");
            }
        }

        final String type = info.getSystemOutput();
        synchronized (info) {
            if (output == null) {
                final SynthesizedOutput synthesizer =
                    getExternalResourceFromPool(synthesizerPool, type);
                output = new JVoiceXmlSystemOutput(synthesizer, session);
                output.addListener(this);
                LOGGER.info("borrowed system output of type '" + type + "'");
            }

            return output;
        }
    }

    /**
     * Returns a previously borrowed system output to the pool.
     */
    private void returnSystemOutput() {
        synchronized (info) {
            if (output == null) {
                return;
            }

            if (!hungup && !closed && output.isBusy()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(
                            "output still busy. returning when queue is empty");
                }
            } else {
                final JVoiceXmlSystemOutput systemOutput = output;
                output = null;
                final String type = info.getSystemOutput();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("returning system output of type '" + type
                            + "'...");
                }
                systemOutput.removeListener(this);

                final SynthesizedOutput synthesizedOutput =
                    systemOutput.getSynthesizedOutput();
                returnExternalResourceToPool(synthesizerPool,
                        synthesizedOutput);
                LOGGER.info("returned system output of type '" + type + "'");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitOutputQueueEmpty() {
        if (output == null) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("waiting for empty output queue...");
        }
        final SynthesizedOutput synthesizedOutput =
            output.getSynthesizedOutput();
        synthesizedOutput.waitQueueEmpty();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...output queue empty.");
        }
    }


    /**
     * {@inheritDoc}
     */
    public void waitNonBargeInPlayed() {
        if (output == null) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("waiting for non-barge-in played...");
        }
        final SynthesizedOutput synthesizedOutput =
            output.getSynthesizedOutput();
        synthesizedOutput.waitNonBargeInPlayed();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...non-barge-in played.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserInput getUserInput()
            throws NoresourceError, ConnectionDisconnectHangupEvent {
        synchronized (this) {
            if (hungup) {
                throw new ConnectionDisconnectHangupEvent("caller hung up");
            }
            if (closed) {
                throw new NoresourceError("implementation platform closed");
            }
        }

        final String type = info.getUserInput();
        synchronized (info) {
            if (input == null) {
                final SpokenInput spokenInput =
                    getExternalResourceFromPool(recognizerPool, type);
                input = new JVoiceXmlUserInput(spokenInput, characterInput,
                        processor);
                input.addListener(this);
                LOGGER.info("borrowed user input of type '" + type + "'");
            }
        }

        return input;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasUserInput() {
        synchronized (info) {
            return input != null;
        }
    }

    /**
     * Returns a previously obtained user input to the pool.
     */
    private void returnUserInput() {
        synchronized (info) {
            if (input == null) {
                return;
            }

            if (!hungup && !closed && input.isBusy()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(
                    "input still busy. returning when recognition is stopped");
                }
            } else {
                final JVoiceXmlUserInput userInput = input;
                input = null;
                final String type = info.getUserInput();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("returning user input of type '" + type
                            + "'...");
                }
                userInput.removeListener(this);

                final SpokenInput spokenInput = userInput.getSpokenInput();
                returnExternalResourceToPool(recognizerPool, spokenInput);

                LOGGER.info("returned user input of type '" + type + "'");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharacterInput getCharacterInput()
            throws NoresourceError, ConnectionDisconnectHangupEvent {
        synchronized (this) {
            if (hungup) {
                throw new ConnectionDisconnectHangupEvent("caller hung up");
            }
            if (closed) {
                throw new NoresourceError("implementation platform closed");
            }
        }
        return characterInput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallControl getCallControl()
            throws NoresourceError, ConnectionDisconnectHangupEvent {
        synchronized (this) {
            if (hungup) {
                throw new ConnectionDisconnectHangupEvent("caller hung up");
            }
            if (closed) {
                throw new NoresourceError("implementation platform closed");
            }
        }

        // In contrast to SystemOutput and UserInput the CallControl
        // resource may be used concurrently.
        // So we must not have a semaphore to avoid shared use.
        final String type = info.getCallControl();
        synchronized (info) {
            if (call == null) {
                final Telephony telephony =
                    getExternalResourceFromPool(telephonyPool, type);
                call = new JVoiceXmlCallControl(telephony);
                call.addListener(this);
                LOGGER.info("borrowed call control of type '" + type + "'");
            }

            return call;
        }
    }

    /**
     * Returns a previously obtained call control to the pool.
     */
    private void returnCallControl() {
        synchronized (info) {
            if (call == null) {
                return;
            }

            // TODO It may happen that two threads borrow this call control
            // and one returns it before the second set it to busy.
            // In this case the resource is returned although the second still
            // possesses it.
            if (!hungup && !closed && call.isBusy()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(
                    "call control still busy. returning when queue is empty");
                }
            } else {
                final JVoiceXmlCallControl callControl = call;
                call = null;
                final String type = info.getCallControl();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("returning call control of type '" + type
                            + "'...");
                }
                callControl.removeListener(this);

                final Telephony telephony = callControl.getTelephony();
                returnExternalResourceToPool(telephonyPool, telephony);

                LOGGER.info("returned call control of type '" + type + "'");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }

            closed = true;
        }

        if (externalRecognitionListener != null) {
            LOGGER.debug("stopping externalRecognitionListener");
            externalRecognitionListener.stop();
        }
        
        if (externalSynthesisListener != null) {
            LOGGER.debug("stopping externalSynthesisListener");
            externalSynthesisListener.stop();
        }
        
        LOGGER.info("closing implementation platform");
        if (output != null) {
            if (!hungup) {
                // If the user did not hang up, wait until all output has been
                // played.
                waitOutputQueueEmpty();
            }
        }

        if (timer != null) {
            timer.stopTimer();
            timer = null;
        }

        if (input != null) {
            if (hungup) {
                input.stopRecognition();
            } 
            try {
                waitInputNotBusy();
            } catch (Exception e) {
                LOGGER.warn("error waiting for input not busy", e);
            }
        }
        LOGGER.info("returning aqcuired resources");
        returnSystemOutput();
        returnUserInput();
        returnCallControl();
        LOGGER.info("implementation platform closed");
    }

    /**
     * Delays until the input is no more busy.
     */
    private void waitInputNotBusy() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("waiting for empty input not busy...");
        }
        while (input.isBusy()) {
            synchronized (info) {
                try {
                    inputLock.wait(BUSY_WAIT_TIMEOUT);
                } catch (InterruptedException e) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(
                                "waiting for input not busy interrupted",
                                e);
                    }
                    return;
                }
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...input not busy.");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setEventHandler(final EventObserver observer) {
        eventObserver = observer;
    }

    /**
     * The user has started to speak.
     * @param type the barge-in type
     */
    private void inputStarted(final ModeType type) {
        if (timer != null) {
            timer.stopTimer();
            timer = null;
        }

        if (output == null) {
            return;
        }

        /** @todo Check the bargein type. */
        if (LOGGER.isDebugEnabled()) {
            if (type == null) {
                LOGGER.debug("speech started 'unknown mode':"
                        + " stopping system output...");
            } else {
                LOGGER.debug("speech started: '" + type.getMode()
                        + "' stopping system output...");
            }
        }

        try {
            output.cancelOutput();
        } catch (NoresourceError nre) {
            LOGGER.warn("unable to stop speech output", nre);

            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...system output stopped");
        }
    }

    /**
     * The user made an utterance that matched an active grammar.
     * @param result the accepted recognition result.
     */
    public void resultAccepted(final RecognitionResult result) {
        LOGGER.info("accepted recognition '" + result.getUtterance()
                + "'");

        if (eventObserver != null) {
            result.setMark(markname);

            final RecognitionEvent recognitionEvent =
                    new RecognitionEvent(result);
            eventObserver.notifyEvent(recognitionEvent);
        }

        markname = null;

        if (externalRecognitionListener != null) {
            externalRecognitionListener.resultAccepted(result);
        }
        synchronized (inputLock) {
            inputLock.notifyAll();
        }
    }

    /**
     * The user made an utterance that did not match an active grammar.
     * @param result the rejected recognition result.
     */
    public void resultRejected(final RecognitionResult result) {
        LOGGER.info("rejected recognition '" + result.getUtterance() + "'");

        if (eventObserver != null) {
            result.setMark(markname);

            final NomatchEvent noMatchEvent = new NomatchEvent();
            eventObserver.notifyEvent(noMatchEvent);
        }
        if (externalRecognitionListener != null) {
            externalRecognitionListener.resultRejected(result);
        }
        synchronized (inputLock) {
            inputLock.notifyAll();
        }
    }

    /**
     * Retrieves a new {@link ExternalResource} from the pool.
     * @param pool the resource pool.
     * @param key key of the resource to retrieve.
     * @param <T> type of the resource.
     * @return obtained resource.
     * @throws NoresourceError
     *         Error obtaining an instance from the pool.
     *
     * @since 0.5.5
     */
    private <T extends ExternalResource> T getExternalResourceFromPool(
            final KeyedResourcePool<T> pool, final String key)
        throws NoresourceError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("obtaining resource '" + key + "' from pool...");
        }

        final T resource = pool.borrowObject(key);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connecting external resource ("
                    + resource.getClass().getCanonicalName()
                    + ").");
        }
        try {
            resource.connect(info);
        } catch (IOException ioe) {
            try {
                pool.returnObject(key, resource);
            } catch (Exception e) {
                LOGGER.error("error returning resource to pool", e);
            }
            throw new NoresourceError("error connecting to resource",
                    ioe);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...connected");
        }

        return resource;
    }

    /**
     * Returns the audio file output resource to the pool.
     * @param pool the pool to which to return the resource.
     * @param resource the resource to return.
     * @param <T> type of the resource.
     */
    private <T extends ExternalResource> void returnExternalResourceToPool(
            final KeyedResourcePool<T> pool, final T resource) {
        final String type = resource.getType();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("returning external resource '" + type + "' ("
                    + resource.getClass().getCanonicalName() + ") to pool...");
            LOGGER.debug(
                "disconnecting external resource.");
        }
        resource.disconnect(info);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...disconnected");
        }

        try {
            pool.returnObject(type, resource);
        } catch (NoresourceError e) {
            LOGGER.error("error returning external resource to pool", e);
        }

        LOGGER.info("returned external resource '" + type + "' ("
                + resource.getClass().getCanonicalName() + ") to pool");
    }

    /**
     * {@inheritDoc}
     */
    public void inputStatusChanged(final SpokenInputEvent event) {
        final int id = event.getEvent();
        switch (id) {
        case SpokenInputEvent.RECOGNITION_STARTED:
            // Start the timer with a default timeout if there were no
            // prompts to queue.
            final SpeakableText lastSpeakable =
                    promptAccumulator.getLastSpeakableText();
            if (lastSpeakable == null) {
                startTimer();
            }
            break;
        case SpokenInputEvent.RECOGNITION_STOPPED:
            recognitionStopped();
            break;
        case SpokenInputEvent.INPUT_STARTED:
            final ModeType type = (ModeType) event.getParam();
            inputStarted(type);
            break;
        case SpokenInputEvent.RESULT_ACCEPTED:
            final RecognitionResult acceptedResult =
                (RecognitionResult) event.getParam();
            resultAccepted(acceptedResult);
            break;
        case SpokenInputEvent.RESULT_REJECTED:
            final RecognitionResult rejectedResult =
                (RecognitionResult) event.getParam();
            resultRejected(rejectedResult);
            break;
        default:
            LOGGER.warn("unknown synthesized output event " + event);
            break;
        }
    }

    /**
     * Starts the <code>noinput</code> timer with the given timeout that
     * has been collected by the {@link org.jvoicexml.PromptAccumulator}.
     */
    private synchronized void startTimer() {
        if (timer != null) {
            return;
        }

        final long timeout = promptAccumulator.getPromptTimeout();
        if (timeout > 0) {
            timer = new TimerThread(eventObserver, timeout);
            timer.start();
        }
    }

    /**
     * The recognition has been stopped.
     */
    private void recognitionStopped() {
        LOGGER.info("recognition stopped");
        if (timer != null) {
            timer.stopTimer();
            timer = null;
        }
        if (call != null) {
            LOGGER.info("will stop call recording");
            try {
                call.stopRecord();
            } catch (NoresourceError ex) {
                ex.printStackTrace();
            }
            LOGGER.info("done stop record request");
        }
        if (hungup) {
            returnUserInput();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void telephonyCallAnswered(final TelephonyEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void telephonyCallHungup(final TelephonyEvent event) {
        synchronized (this) {
            if (hungup) {
                return;
            }
            hungup = true;
        }
        LOGGER.info("telephony connection closed");
        returnCallControl();
        returnSystemOutput();
        returnUserInput();
    }

    /**
     * {@inheritDoc}
     */
    public void telephonyCallTransferred(final TelephonyEvent event) {
        LOGGER.info("call transfered to '" + event.getParam() + "'");

        if (eventObserver != null) {
            final String uri = (String) event.getParam();
            final JVoiceXMLEvent transferEvent = new TransferEvent(uri, null);
            eventObserver.notifyEvent(transferEvent);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void telephonyMediaEvent(final TelephonyEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void outputStatusChanged(final SynthesizedOutputEvent event) {
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
            break;
        case SynthesizedOutputEvent.OUTPUT_ENDED:
            final OutputEndedEvent outputEndedEvent =
                (OutputEndedEvent) event;
            final SpeakableText endedSpeakable =
                outputEndedEvent.getSpeakable();
            outputEnded(endedSpeakable);
            break;
        case SynthesizedOutputEvent.QUEUE_EMPTY:
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("output queue is empty");
            }
            break;
        case SynthesizedOutputEvent.MARKER_REACHED:
            final MarkerReachedEvent markReachedEvent =
                (MarkerReachedEvent) event;
            markname = markReachedEvent.getMark();
            LOGGER.info("reached mark '" + markname + "'");
            break;
          case SynthesizedOutputEvent.OUTPUT_UPDATE:
            break;
        default:
            LOGGER.warn("unknown synthesized output event " + event);
            break;
        }

        if (externalSynthesisListener != null) {
            externalSynthesisListener.outputStatusChanged(event);
        }
    }

    /**
     * The output of the given speakable has ended.
     * @param speakable the speakable.
     */
    private void outputEnded(final SpeakableText speakable) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("output ended " + speakable.getSpeakableText());
        }

        if (call != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("will stop call playing");
            }
            try {
                call.stopPlay();
            } catch (NoresourceError ex) {
                LOGGER.warn("error stopping play", ex);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("done stop play request");
            }
        }

        if (hungup) {
            returnSystemOutput();
            // No need to start any timers in this case.
            return;
        }

        if (eventObserver == null) {
            return;
        }

        // The timer is only needed within the field.
        // Here we have only prompts which produces SSML.
        // If the platform is using JSAPI2,
        // this code must be commented.
        final SpeakableText lastSpeakable =
                promptAccumulator.getLastSpeakableText();
        if (speakable.equals(lastSpeakable)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("reached last speakable. starting timer");
            }
            startTimer();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setSession(final Session currentSession) {
        session = currentSession;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputError(final ErrorEvent error) {
        reportError(error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void inputError(final ErrorEvent error) {
        reportError(error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void telephonyError(final ErrorEvent error) {
        reportError(error);
    }

    /**
     * Reports an error that happened while communicating with the user.
     * @param error the error
     * @since 0.7.4
     */
    private void reportError(final ErrorEvent error) {
        if (eventObserver == null) {
            LOGGER.warn(
                    "no event observer. unable to propagate an error",
                    error);
            return;
        }
        eventObserver.notifyEvent(error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPromptTimeout(final long timeout) {
        promptAccumulator.setPromptTimeout(timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void queuePrompt(final SpeakableText speakable) {
        promptAccumulator.queuePrompt(speakable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderPrompts(final String sessionId,
            final DocumentServer server, final CallControlProperties props)
            throws BadFetchError, NoresourceError,
                ConnectionDisconnectHangupEvent {
        promptAccumulator.renderPrompts(sessionId, server, props);
    }
}
