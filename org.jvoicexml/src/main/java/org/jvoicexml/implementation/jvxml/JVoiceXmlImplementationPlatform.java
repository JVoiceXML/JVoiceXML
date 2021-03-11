/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.CallControl;
import org.jvoicexml.CallControlProperties;
import org.jvoicexml.Configurable;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.DtmfInput;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.EventBus;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.event.plain.NoinputEvent;
import org.jvoicexml.event.plain.implementation.InputStartedEvent;
import org.jvoicexml.event.plain.implementation.MarkerReachedEvent;
import org.jvoicexml.event.plain.implementation.NomatchEvent;
import org.jvoicexml.event.plain.implementation.OutputEndedEvent;
import org.jvoicexml.event.plain.implementation.OutputStartedEvent;
import org.jvoicexml.event.plain.implementation.QueueEmptyEvent;
import org.jvoicexml.event.plain.implementation.RecognitionEvent;
import org.jvoicexml.event.plain.implementation.RecognitionStartedEvent;
import org.jvoicexml.event.plain.implementation.RecognitionStoppedEvent;
import org.jvoicexml.event.plain.implementation.SpokenInputEvent;
import org.jvoicexml.event.plain.implementation.SynthesizedOutputEvent;
import org.jvoicexml.event.plain.jvxml.TransferEvent;
import org.jvoicexml.implementation.ExternalResource;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.implementation.Telephony;
import org.jvoicexml.implementation.TelephonyEvent;
import org.jvoicexml.implementation.TelephonyListener;
import org.jvoicexml.implementation.dtmf.BufferedDtmfInput;
import org.jvoicexml.implementation.pool.KeyedResourcePool;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Basic implementation of an {@link ImplementationPlatform}.
 * 
 * <p>
 * User actions and system output are not handled by this class but forwarded to
 * the corresponding {@link ExternalResource}s.
 * </p>
 * 
 * <p>
 * External resources are considered to be in a pool. The implementation
 * platform is able to retrieve them from the pool and push them back. This
 * means that all resources that have been borrowed from the implementation
 * platform must be returned to it if they are no longer used.
 * </p>
 * 
 * @author Dirk Schnelle-Walka
 */
public final class JVoiceXmlImplementationPlatform
        implements SpokenInputListener, SynthesizedOutputListener,
        TelephonyListener, ImplementationPlatform, Configurable {

    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(JVoiceXmlImplementationPlatform.class);

    /** Timeout in msec to wait until the resource is not busy. */
    private static final int BUSY_WAIT_TIMEOUT = 1000;

    /** Pool of synthesizer output resource factories. */
    private final KeyedResourcePool<SynthesizedOutput> synthesizerPool;

    /** Lock for the synthesizer pool. */
    private final Object synthesizerPoolLock;
    
    /** Pool of user input resource factories. */
    private final KeyedResourcePool<SpokenInput> recognizerPool;

    /** Lock for the recognizer pool. */
    private final Object recognizerPoolLock;
    
    /** Pool of user calling resource factories. */
    private final KeyedResourcePool<Telephony> telephonyPool;

    /** Lock for the telephony pool. */
    private final Object telephonyPoolLock;
    
    /** Connection information to use. */
    private final ConnectionInformation info;

    /** The output device. */
    private JVoiceXmlSystemOutput output;

    /** Support for audio input. */
    private JVoiceXmlUserInput input;

    /** Input not busy notification lock. */
    private final Object inputLock;

    /** Support for DTMF input. */
    private volatile BufferedDtmfInput dtmfInput;

    /** The calling device. */
    private JVoiceXmlCallControl call;

    /** The event bus to communicate events back to the interpreter. */
    private EventBus eventbus;

    /** A timer to get the noinput timeout. */
    private TimerThread timer;

    /** The name of the mark last executed by the SSML processor. */
    private String markname;

    /**
     * Flag set to <code>true</code> if the implementation platform is closed.
     */
    private boolean closed;

    /**
     * Flag set to {@code true} if the caller hung up the phone.
     */
    private boolean hungup;

    /** The current session. */
    private Session session;

    /**
     * A reaper to monitor that the platform is returned within a predefined
     * timespan.
     */
    private ImplementationPlatformReaper reaper;
    
    /**
     * Constructs a new Implementation platform.
     * 
     * <p>
     * This method should not be called by any application. The implementation
     * platform is accessible via the <code>Session</code>
     * </p>
     * 
     * @param telePool
     *            pool of telephony resource factories
     * @param synthesizedOutputPool
     *            pool of synthesized output resource factories
     * @param spokenInputPool
     *            pool of spoken input resource factories
     * @param connectionInformation
     *            connection information container
     * 
     * @see org.jvoicexml.Session
     */
    JVoiceXmlImplementationPlatform(
            final KeyedResourcePool<Telephony> telePool,
            final KeyedResourcePool<SynthesizedOutput> synthesizedOutputPool,
            final KeyedResourcePool<SpokenInput> spokenInputPool,
            final ConnectionInformation connectionInformation) {
        this(telePool, synthesizedOutputPool, spokenInputPool,
                new BufferedDtmfInput(), connectionInformation);
    }

    /**
     * Constructs a new Implementation platform.
     * 
     * <p>
     * This method should not be called by any application. The implementation
     * platform is accessible via the <code>Session</code>
     * </p>
     * 
     * @param telePool
     *            pool of telephony resource factories
     * @param synthesizedOutputPool
     *            pool of synthesized output resource factories
     * @param spokenInputPool
     *            pool of spoken input resource factories
     * @param bufferedCharacterInput
     *            buffer character input for this platform
     * @param connectionInformation
     *            connection information container
     * 
     * @see org.jvoicexml.Session
     */
    JVoiceXmlImplementationPlatform(
            final KeyedResourcePool<Telephony> telePool,
            final KeyedResourcePool<SynthesizedOutput> synthesizedOutputPool,
            final KeyedResourcePool<SpokenInput> spokenInputPool,
            final BufferedDtmfInput bufferedCharacterInput,
            final ConnectionInformation connectionInformation) {
        info = connectionInformation;
        telephonyPool = telePool;
        telephonyPoolLock = new Object();
        synthesizerPool = synthesizedOutputPool;
        synthesizerPoolLock = new Object();
        recognizerPool = spokenInputPool;
        recognizerPoolLock = new Object();
        dtmfInput = bufferedCharacterInput;
        inputLock = new Object();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final Configuration configuration)
            throws ConfigurationException {
    }

    /**
     * Retrieves the session associated with this platform.
     * @return the session
     * @since 0.7.9
     */
    public Session getSession() {
        return session;
    }
    
    /**
     * Checks if this platform has been closed.
     * @return {@code true} if the platform has been closed
     * @since 0.7.9
     */
    @Override
    public boolean isClosed() {
        return closed;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHungup() {
        return hungup;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SystemOutput getSystemOutput() throws NoresourceError,
            ConnectionDisconnectHangupEvent {
        synchronized (this) {
            if (hungup) {
                throw new ConnectionDisconnectHangupEvent("caller hung up");
            }
            if (closed) {
                throw new NoresourceError("implementation platform closed");
            }
        }

        final String type = info.getSystemOutput();
        synchronized (synthesizerPoolLock) {
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
        synchronized (synthesizerPoolLock) {
            if (output == null) {
                return;
            }

            if (!hungup && !closed && output.isBusy()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("output still busy. Delaying return...");
                }
                maybeStartReaper();
            } else {
                maybeStopReaper();
                final JVoiceXmlSystemOutput systemOutput = output;
                output = null;
                final String type = info.getSystemOutput();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("returning system output of type '" + type
                            + "'...");
                }
                systemOutput.removeListener(this);

                final SynthesizedOutput synthesizedOutput = systemOutput
                        .getSynthesizedOutput();
                returnExternalResourceToPool(synthesizerPool,
                        synthesizedOutput);
                LOGGER.info("returned system output of type '" + type + "'");
            }
        }
    }

    /**
     * Starts a reaper if not already started.
     * 
     * @since 0.7.7
     */
    private void maybeStartReaper() {
        if (reaper != null) {
            return;
        }
        reaper = new ImplementationPlatformReaper(this, input, output);
        reaper.start();
    }
    
    /**
     * Stops a reaper if not already stopped.
     * 
     * @since 0.7.7
     */
    private void maybeStopReaper() {
        if (reaper == null) {
            return;
        }
        reaper.stopReaping();
        reaper = null;
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
        final SynthesizedOutput synthesizedOutput = output
                .getSynthesizedOutput();
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
        final SynthesizedOutput synthesizedOutput = output
                .getSynthesizedOutput();
        synthesizedOutput.waitNonBargeInPlayed();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...non-barge-in played.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserInput getUserInput() throws NoresourceError,
            ConnectionDisconnectHangupEvent {
        synchronized (this) {
            if (hungup) {
                throw new ConnectionDisconnectHangupEvent("caller hung up");
            }
            if (closed) {
                throw new NoresourceError("implementation platform closed");
            }
        }

        final String type = info.getUserInput();
        synchronized (recognizerPoolLock) {
            if (input == null) {
                final SpokenInput spokenInput = getExternalResourceFromPool(
                        recognizerPool, type);
                input = new JVoiceXmlUserInput(spokenInput, dtmfInput);
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
    public boolean isUserInputActive() {
        synchronized (info) {
            return input != null;
        }
    }

    /**
     * Returns a previously obtained user input to the pool.
     */
    private void returnUserInput() {
        synchronized (recognizerPoolLock) {
            if (input == null) {
                return;
            }

            if (!hungup && !closed && input.isBusy()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("input still busy. delaying return");
                }
                maybeStartReaper();
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
    public DtmfInput getCharacterInput() throws NoresourceError,
            ConnectionDisconnectHangupEvent {
        synchronized (this) {
            if (hungup) {
                throw new ConnectionDisconnectHangupEvent("caller hung up");
            }
            if (closed) {
                throw new NoresourceError("implementation platform closed");
            }
        }
        return dtmfInput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallControl getCallControl() throws NoresourceError,
            ConnectionDisconnectHangupEvent {
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
        synchronized (telephonyPoolLock) {
            if (call == null) {
                final Telephony telephony = getExternalResourceFromPool(
                        telephonyPool, type);
                telephony.addListener(this);
                call = new JVoiceXmlCallControl(telephony);
                LOGGER.info("borrowed call control of type '" + type + "'");
            }

            return call;
        }
    }

    /**
     * Returns a previously obtained call control to the pool.
     */
    private void returnCallControl() {
        synchronized (telephonyPoolLock) {
            if (call == null) {
                return;
            }

            // TODO It may happen that two threads borrow this call control
            // and one returns it before the second set it to busy.
            // In this case the resource is returned although the second still
            // possesses it.
            if (!hungup && !closed && call.isBusy()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("call control still busy. returning when queue"
                            + " is empty");
                }
            } else {
                final JVoiceXmlCallControl callControl = call;
                call = null;
                final String type = info.getCallControl();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("returning call control of type '" + type
                            + "'...");
                }

                final Telephony telephony = callControl.getTelephony();
                returnExternalResourceToPool(telephonyPool, telephony);

                LOGGER.info("returned call control of type '" + type + "'");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }

        LOGGER.info("closing implementation platform");
        maybeStopReaper();
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
                input.stopRecognition(null);
            } else {
                try {
                    waitInputNotBusy();
                } catch (Exception e) {
                    LOGGER.warn("error waiting for input not busy", e);
                }
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
                        LOGGER.debug("waiting for input not busy interrupted",
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
    @Override
    public void setEventBus(final EventBus bus) {
        eventbus = bus;
    }

    /**
     * The user has started to speak.
     * 
     * @param type
     *            the mode type of te recognizer that started
     */
    private void inputStarted(final ModeType type) {
        // No need to wait for input
        if (timer != null) {
            timer.stopTimer();
            timer = null;
        }
        if (output == null) {
            return;
        }

        // Try cancel the output only, if the platform supports SPEECH bargeins.
        final Collection<BargeInType> types = input.getSupportedBargeInTypes();
        if (types.contains(BargeInType.SPEECH)) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            if (type == null) {
                LOGGER.debug("input started 'unknown mode':"
                        + " stopping system output...");
            } else {
                LOGGER.debug("input started: '" + type.getMode()
                        + "' stopping system output...");
            }
        }
        try {
            output.cancelOutput(BargeInType.SPEECH);
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
     * 
     * @param result
     *            the accepted recognition result.
     */
    public void resultAccepted(final RecognitionResult result) {
        LOGGER.info("accepted recognition '" + result.getUtterance() + "'");

        if (eventbus != null) {
            result.setMark(markname);

            final SessionIdentifier id = session.getSessionId();
            final RecognitionEvent recognitionEvent = new RecognitionEvent(
                    input.getSpokenInput(), id, result);
            eventbus.publish(recognitionEvent);
        }

        markname = null;

        synchronized (inputLock) {
            inputLock.notifyAll();
        }
    }

    /**
     * The user made an utterance that did not match an active grammar.
     * 
     * @param result
     *            the rejected recognition result.
     */
    public void resultRejected(final RecognitionResult result) {
        LOGGER.info("rejected recognition '" + result.getUtterance() + "'");

        if (eventbus != null) {
            result.setMark(markname);
            final SessionIdentifier id = session.getSessionId();
            final NomatchEvent noMatchEvent = new NomatchEvent(
                    input.getSpokenInput(), id, result);
            eventbus.publish(noMatchEvent);
        }
        synchronized (inputLock) {
            inputLock.notifyAll();
        }
    }

    /**
     * Retrieves a new {@link ExternalResource} from the pool.
     * 
     * @param pool
     *            the resource pool.
     * @param key
     *            key of the resource to retrieve.
     * @param <T>
     *            type of the resource.
     * @return obtained resource.
     * @throws NoresourceError
     *             Error obtaining an instance from the pool.
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
                    + resource.getClass().getCanonicalName() + ").");
        }
        try {
            resource.connect(info);
        } catch (IOException ioe) {
            try {
                pool.returnObject(key, resource);
            } catch (Exception e) {
                LOGGER.error("error returning resource to pool", e);
            }
            throw new NoresourceError("error connecting to resource: "
                    + ioe.getMessage(), ioe);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...connected");
        }

        return resource;
    }

    /**
     * Returns the audio file output resource to the pool.
     * 
     * @param pool
     *            the pool to which to return the resource.
     * @param resource
     *            the resource to return.
     * @param <T>
     *            type of the resource.
     */
    private <T extends ExternalResource> void returnExternalResourceToPool(
            final KeyedResourcePool<T> pool, final T resource) {
        final String type = resource.getType();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("returning external resource '" + type + "' ("
                    + resource.getClass().getCanonicalName() + ") to pool...");
            LOGGER.debug("disconnecting external resource");
        }
        resource.disconnect(info);
        LOGGER.debug(resource.getClass().getCanonicalName() + " disconnected");

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
    @Override
    public void inputStatusChanged(final SpokenInputEvent event) {
        if (event.isType(RecognitionStartedEvent.EVENT_TYPE)) {
            startTimer();
        } else if (event.isType(InputStartedEvent.EVENT_TYPE)) {
            final InputStartedEvent started =
                    (InputStartedEvent) event;
            final ModeType modeType = started.getMode();
            inputStarted(modeType);
        } else if (event.isType(RecognitionEvent.EVENT_TYPE)) {
            final RecognitionEvent recognitionEvent = (RecognitionEvent) event;
            final RecognitionResult result = recognitionEvent
                    .getRecognitionResult();
            resultAccepted(result);
        } else if (event.isType(NomatchEvent.EVENT_TYPE)) {
            final NomatchEvent nomatch = (NomatchEvent) event;
            final RecognitionResult result = nomatch.getRecognitionResult();
            resultRejected(result);
        } else if (event.isType(RecognitionStoppedEvent.EVENT_TYPE)) {
            recognitionStopped();
        } else {
            LOGGER.warn("unknown spoken input event " + event);
        }
        eventbus.publish(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void timeout(final long timeout) {
        LOGGER.info("timeout: no input detected for " + timeout + " msecs");
        final NoinputEvent event = new NoinputEvent(timeout);
        eventbus.publish(event);
    }

    /**
     * Starts the <code>noinput</code> timer with the given timeout that has
     * been collected by the {@link org.jvoicexml.PromptAccumulator}.
     */
    private synchronized void startTimer() {
        // Avoid starting a second timer
        if (timer != null) {
            return;
        }

        if (input == null) {
            return;
        }
        final SpokenInput spokenInput = input.getSpokenInput();
        final long timeout = spokenInput.getNoInputTimeout();
        if (timeout > 0) {
            timer = new TimerThread(this, timeout);
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
    @Override
    public void telephonyCallAnswered(final TelephonyEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dtmfInput(final char dtmf) {
        dtmfInput.addDtmf(dtmf);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void telephonyCallHungup(final TelephonyEvent event) {
        synchronized (this) {
            // Ignore the event if we are already closing the connection
            if (hungup) {
                return;
            }
        }
        LOGGER.info("telephony connection hung up " + event);

        // Stop a possibly active recognition
        if (input != null && input.isBusy()) {
            final Collection<ModeType> types =
                    new java.util.ArrayList<ModeType>();;
            types.add(ModeType.VOICE);
            types.add(ModeType.DTMF);
            input.stopRecognition(types);
        }
        
        // Publish a corresponding event
        if (eventbus != null) {
            final JVoiceXMLEvent hangupEvent =
                    new ConnectionDisconnectHangupEvent("caller hung up");
            eventbus.publish(hangupEvent);
        }

        synchronized (this) {
            hungup = true;
        }
        
        // Immediately return the resources
        LOGGER.info("returning aqcuired resources");
        returnSystemOutput();
        returnUserInput();
        returnCallControl();
        LOGGER.info("implementation platform closed");
    }

    /**
     * {@inheritDoc}
     */
    public void telephonyCallTransferred(final TelephonyEvent event) {
        LOGGER.info("call transfered to '" + event.getParam() + "'");
        if (eventbus != null) {
            final String uri = (String) event.getParam();
            final JVoiceXMLEvent transferEvent = new TransferEvent(uri, null);
            eventbus.publish(transferEvent);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void telephonyMediaEvent(final TelephonyEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputStatusChanged(final SynthesizedOutputEvent event) {
        // Forward this to the event bus
        if (eventbus != null) {
            eventbus.publish(event);
        }
        if (event.isType(OutputStartedEvent.EVENT_TYPE)) {
            final OutputStartedEvent outputStartedEvent =
                    (OutputStartedEvent) event;
            final SpeakableText startedSpeakable = outputStartedEvent
                    .getSpeakable();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("output started " + startedSpeakable);
            }
        } else if (event.isType(OutputEndedEvent.EVENT_TYPE)) {
            final OutputEndedEvent outputEndedEvent = (OutputEndedEvent) event;
            final SpeakableText endedSpeakable = outputEndedEvent
                    .getSpeakable();
            outputEnded(endedSpeakable);
        } else if (event.isType(QueueEmptyEvent.EVENT_TYPE)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("output queue is empty");
            }
        } else if (event.isType(MarkerReachedEvent.EVENT_TYPE)) {
            final MarkerReachedEvent markReachedEvent =
                    (MarkerReachedEvent) event;
            markname = markReachedEvent.getMark();
            LOGGER.info("reached mark '" + markname + "'");
        } else {
            LOGGER.warn("unknown synthesized output event " + event);
        }
    }

    /**
     * The output of the given speakable has ended.
     * 
     * @param speakable
     *            the speakable.
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
            return;
        }

        if (eventbus == null) {
            return;
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
     * 
     * @param error
     *            the error
     * @since 0.7.4
     */
    private void reportError(final ErrorEvent error) {
        if (eventbus == null) {
            LOGGER.warn("no event observer. unable to propagate an error",
                    error);
            return;
        }
        eventbus.publish(error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void queuePrompt(final SpeakableText speakable,
            final DocumentServer server) 
            throws NoresourceError, ConnectionDisconnectHangupEvent,
                BadFetchError {
        final SystemOutput output = getSystemOutput();
        final SessionIdentifier sessionId = session.getSessionId();
        output.queueSpeakable(speakable, sessionId, server);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void playPrompts(DocumentServer server,
            CallControlProperties callProps) throws BadFetchError,
            NoresourceError, ConnectionDisconnectHangupEvent {
        final CallControl call = getCallControl();
        final SystemOutput output = getSystemOutput();
        final SessionIdentifier sessionId = session.getSessionId();
        try {
            call.play(output, callProps);
            output.playPrompts(sessionId, server, callProps);
        } catch (IOException e) {
            throw new BadFetchError("error playing to calling device", e);
        }
    }
}
