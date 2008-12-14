/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jvoicexml.CallControl;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.Session;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.EventObserver;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.NomatchEvent;
import org.jvoicexml.event.plain.jvxml.RecognitionEvent;
import org.jvoicexml.event.plain.jvxml.TransferEvent;
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
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class JVoiceXmlImplementationPlatform
        implements SpokenInputListener, SynthesizedOutputListener,
            TelephonyListener, ImplementationPlatform {

    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlImplementationPlatform.class);

    /** Timeout in msec to wait until the resource is not busy. */
    private static final int BUSY_WAIT_TIMEOUT = 1000;

    /** Pool of synthesizer output resource factories. */
    private final KeyedResourcePool<SynthesizedOutput> synthesizerPool;

    /** Pool of audio file output resource factories. */
    private final KeyedResourcePool<AudioFileOutput> fileOutputPool;

    /** Pool of user input resource factories. */
    private final KeyedResourcePool<SpokenInput> recognizerPool;

    /** Pool of user calling resource factories. */
    private final KeyedResourcePool<Telephony> telephonyPool;

    /** The remote client to connect to. */
    private RemoteClient client;

    /** The output device. */
    private JVoiceXmlSystemOutput output;

    /** Support for audio input. */
    private JVoiceXmlUserInput input;

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

    /** The current session. */
    private Session session;

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
     * @param audioFileOutputPool pool of audio file output resources.
     * @param spokenInputPool pool of spoken input resource factories
     * @param remoteClient the remote client to connect to.
     *
     * @see org.jvoicexml.Session
     */
    JVoiceXmlImplementationPlatform(
            final KeyedResourcePool<Telephony> telePool,
            final KeyedResourcePool<SynthesizedOutput> synthesizedOutputPool,
            final KeyedResourcePool<AudioFileOutput> audioFileOutputPool,
            final KeyedResourcePool<SpokenInput> spokenInputPool,
            final RemoteClient remoteClient) {
        client = remoteClient;
        telephonyPool = telePool;
        synthesizerPool = synthesizedOutputPool;
        fileOutputPool = audioFileOutputPool;
        recognizerPool = spokenInputPool;
        characterInput = new BufferedCharacterInput();
        inputLock = new Object();
    }

    /**
     * Sets an external recognition listener.
     * @param listener the external recognition listener.
     * @since 0.6
     */
    public void setExternalRecognitionListener(
            final ExternalRecognitionListener listener) {
        externalRecognitionListener = listener;
    }

    /**
     * Sets an external synthesis listener.
     * @param listener the external synthesis listener.
     * @since 0.6
     */
    public void setExternalSynthesisListener(
            final ExternalSynthesisListener listener) {
      externalSynthesisListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized SystemOutput getSystemOutput()
            throws NoresourceError {
        if (closed) {
            throw new NoresourceError("implementation platform closed");
        }

        final String type = client.getSystemOutput();
        synchronized (synthesizerPool) {
            if (output == null) {
                final SynthesizedOutput synthesizer =
                    getExternalResourceFromPool(synthesizerPool, type);
                final AudioFileOutput file;
                if (synthesizer.requiresAudioFileOutput()) {
                    try {
                        file = getExternalResourceFromPool(fileOutputPool,
                                type);
                    } catch (NoresourceError e) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("error obtaining file output. "
                                    + "Returning synthesizer.");
                        }
                        returnExternalResourceToPool(synthesizerPool,
                                synthesizer);
                        throw e;
                    }
                } else {
                    file = null;
                }
                output = new JVoiceXmlSystemOutput(synthesizer, file, session);
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
        synchronized (synthesizerPool) {
            if (output == null) {
                return;
            }

            if (output.isBusy() && !closed) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(
                            "output still busy. returning when queue is empty");
                }
            } else {
                output.removeListener(this);

                final SynthesizedOutput synthesizedOutput =
                    output.getSynthesizedOutput();
                returnExternalResourceToPool(synthesizerPool,
                        synthesizedOutput);
                final AudioFileOutput audioFileOutput =
                    output.getAudioFileOutput();
                if (audioFileOutput != null) {
                    returnExternalResourceToPool(fileOutputPool,
                            audioFileOutput);
                }
                final String type = client.getSystemOutput();
                LOGGER.info("returned system output of type '" + type + "'");
                output = null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void waitOutputQueueEmpty() {
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
    public UserInput getUserInput()
            throws NoresourceError {
        if (closed) {
            throw new NoresourceError("implementation platform closed");
        }

        final String type = client.getUserInput();
        synchronized (recognizerPool) {
            if (input == null) {
                final SpokenInput spokenInput =
                    getExternalResourceFromPool(recognizerPool, type);
                input = new JVoiceXmlUserInput(spokenInput, characterInput);
                input.addListener(this);
                LOGGER.info("borrowed user input of type '" + type + "'");
            }
        }

        return input;
    }

    /**
     * Returns a previously obtained user input to the pool.
     */
    private void returnUserInput() {
        synchronized (recognizerPool) {
            if (input == null) {
                return;
            }

            if (input.isBusy() && !closed) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(
                    "input still busy. returning when recognition is stopped");
                }
            } else {
                input.removeListener(this);

                final SpokenInput spokenInput = input.getSpokenInput();
                returnExternalResourceToPool(recognizerPool, spokenInput);

                final String type = client.getUserInput();
                LOGGER.info("returned user input of type '" + type + "'");
                input = null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized CharacterInput getCharacterInput()
            throws NoresourceError {
        return characterInput;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized CallControl getCallControl()
            throws NoresourceError {
        if (closed) {
            throw new NoresourceError("implementation platform closed");
        }

        // In contrast to SystemOutput and UserInput the CallControl
        // resource may be used concurrently.
        // So we must not have a semaphore to avoid shared use.
        final String type = client.getCallControl();
        synchronized (telephonyPool) {
            if (call == null) {
                Telephony telephony =
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
        synchronized (telephonyPool) {
            if (call == null) {
                return;
            }

            // TODO It may happen that two threads borrow this call control
            // and one returns it before the second set it to busy.
            // In this case the resource is returned although the second still
            // possesses it.
            if (call.isBusy() && !closed) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(
                    "call control still busy. returning when queue is empty");
                }
            } else {
                call.removeListener(this);

                final Telephony telephony = call.getTelephony();
                returnExternalResourceToPool(telephonyPool, telephony);

                final String type = client.getCallControl();
                LOGGER.info("returned call control of type '" + type + "'");
                call = null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        LOGGER.info("clearing all pending requests");
        if (output != null) {
            try {
                output.cancelOutput();
            } catch (NoresourceError e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("error canceling output", e);
                }
            }
        }
        if (input != null) {
            input.stopRecognition();
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void close() {
        if (closed) {
            return;
        }

        closed = true;

        LOGGER.info("closing implementation platform");
        if (output != null) {
            waitOutputQueueEmpty();
        }

        if (timer != null) {
            timer.stopTimer();
            timer = null;
        }

        if (input != null) {
            waitInputNotBusy();
        }
        returnSystemOutput();

        returnUserInput();

        returnCallControl();
    }

    /**
     * Delays until the input is no more busy.
     */
    private void waitInputNotBusy() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("waiting for empty input not busy...");
        }
        while (input.isBusy()) {
            synchronized (inputLock) {
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
            LOGGER.debug("speech started: '" + type.getMode()
                         + "' system output...");
        }

        try {
            output.cancelOutput();
        } catch (NoresourceError nre) {
            LOGGER.warn("unable to stop speech output", nre);

            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("system output stopped");
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
        LOGGER.info("rejected recognition'" + result.getUtterance() + "'");

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

        final T resource;

        try {
            resource = pool.borrowObject(key);
        } catch (Exception ex) {
            throw new NoresourceError(ex);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connecting external resource ("
                    + resource.getClass().getCanonicalName()
                    + ") to remote client..");
        }
        try {
            resource.connect(client);
        } catch (IOException ioe) {
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
                    + resource.getClass().getCanonicalName() + ")' to pool...");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "disconnecting external resource from remote client..");
        }
        resource.disconnect(client);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...disconnected");
        }

        try {
            pool.returnObject(type, resource);
        } catch (Exception e) {
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
            startTimer();
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
     * Starts the <code>noinput</code> timer.
     */
    private void startTimer() {
        if (timer != null) {
            return;
        }

        timer = new TimerThread(eventObserver, -1);
        timer.start();
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
        LOGGER.info("try to stopRecord @ call=" + call);
        if (call != null) {
            LOGGER.info("will stop call recording");
            try {
                call.stopRecord();
            } catch (NoresourceError ex) {
                ex.printStackTrace();
            }
            LOGGER.info("done stop record request");
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
        LOGGER.info("telephony connection closed");
        clear();
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
            final SpeakableText startedSpeakable =
                (SpeakableText) event.getParam();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("output started " + startedSpeakable);
            }
            break;
        case SynthesizedOutputEvent.OUTPUT_ENDED:
            final SpeakableText endedSpeakable =
                (SpeakableText) event.getParam();
            outputEnded(endedSpeakable);
            break;
        case SynthesizedOutputEvent.QUEUE_EMPTY:
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("output queue is empty");
            }
            break;
        case SynthesizedOutputEvent.MARKER_REACHED:
            markname = (String) event.getParam();
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

        LOGGER.info("try to stop @ call=" + call);
        if (call != null) {
            LOGGER.info("will stop call playing");
            try {
                call.stopPlay();
            } catch (NoresourceError ex) {
                ex.printStackTrace();
            }
            LOGGER.info("done stop play request");
        }

        if (eventObserver == null) {
            return;
        }

        // The timer is only needed within the field.
        // Here we have only prompts which produces SSML.
        // If the platform is using JSAPI2,
        // this code must be commented.
        if (speakable instanceof SpeakableSsmlText) {
            final SpeakableSsmlText ssml = (SpeakableSsmlText) speakable;
            final long timeout = ssml.getTimeout();
            if (timer != null) {
                timer.stopTimer();
            }
            timer = new TimerThread(eventObserver, timeout);
            timer.start();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setSession(final Session currentSession) {
        session = currentSession;
    }
}
