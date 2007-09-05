/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.AudioFileOutput;
import org.jvoicexml.CallControl;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpokenInput;
import org.jvoicexml.SynthesizedOuput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.EventObserver;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.NomatchEvent;
import org.jvoicexml.event.plain.jvxml.RecognitionEvent;
import org.jvoicexml.implementation.external.ExternalRecognitionListener;
import org.jvoicexml.implementation.external.ExternalRecognitionResult;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Basic implementation of an {@link ImplementationPlatform}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class JVoiceXmlImplementationPlatform
        implements UserInputListener, SystemOutputListener,
            ImplementationPlatform {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(JVoiceXmlImplementationPlatform.class);

    /** Pool of synthesizer output resource factories. */
    private final KeyedResourcePool<SynthesizedOuput> synthesizerPool;

    /** Pool of audio file output resource factories. */
    private final KeyedResourcePool<AudioFileOutput> fileOutputPool;

    /** Pool of user input resource factories. */
    private final KeyedResourcePool<SpokenInput> recognizerPool;

    /** Pool of user calling resource factories. */
    private final KeyedResourcePool<CallControl> callPool;

    /** The remote client to connect to. */
    private RemoteClient client;

    /** The system output device. */
    private JVoiceXmlSystemOutput output;

    /** Support for audio input. */
    private JVoiceXmlUserInput input;

    /** The calling device. */
    private CallControl call;

    /** The event observer to communicate events back to the interpreter. */
    private EventObserver eventObserver;

    /** A timer to get the noinput timeout. */
    private TimerThread timer;

    /** The name of the mark last executed by the SSML processor. */
    private String markname;

    /** Number of active output message, i.e. synthesized text. */
    private int activeOutputCount;

    /** An external recognition listener. */
    private ExternalRecognitionListener externalRecognitionListener;

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
     * Constructs a new Implementation platform.
     *
     * <p>
     * This method should not be called by any application. The implemntation
     * platform is accessible via the <code>Session</code>
     * </p>
     *
     * @param callControlPool  pool of call control resource factories
     * @param synthesizedOutputPool pool of synthesized output resource
     *        factories
     * @param audioFileOutputPool pool of audio file output resources.
     * @param spokenInputPool pool of spoken input resource factories
     * @param remoteClient the remote client to connect to.
     *
     * @see org.jvoicexml.Session
     */
    JVoiceXmlImplementationPlatform(
            final KeyedResourcePool<CallControl> callControlPool,
            final KeyedResourcePool<SynthesizedOuput> synthesizedOutputPool,
            final KeyedResourcePool<AudioFileOutput> audioFileOutputPool,
            final KeyedResourcePool<SpokenInput> spokenInputPool,
            final RemoteClient remoteClient) {
        client = remoteClient;
        callPool = callControlPool;
        synthesizerPool = synthesizedOutputPool;
        fileOutputPool = audioFileOutputPool;
        recognizerPool = spokenInputPool;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized SynthesizedOuput getSystemOutput()
            throws NoresourceError {
        if (output == null) {
            output = getSystemOutputFromPool();
        }

        return output;
    }

    /**
     * Retrieves a new {@link SystemOutput} from the pool.
     * @return obtained system output
     * @throws NoresourceError
     *         Error obtaining an instance from the pool.
     *
     * @since 0.5.5
     */
    private JVoiceXmlSystemOutput getSystemOutputFromPool()
        throws NoresourceError {
        final SynthesizedOuput synthesizer = getSynthesizedOutputFromPool();
        final AudioFileOutput file = getAudioFileOutputFromPool();

        return new JVoiceXmlSystemOutput(synthesizer, file);
    }

    /**
     * Retrieves a new {@link SynthesizedOuput} from the pool.
     * @return obtained synthesized output
     * @throws NoresourceError
     *         Error obtaining an instance from the pool.
     *
     * @since 0.5.5
     */
    private SynthesizedOuput getSynthesizedOutputFromPool()
        throws NoresourceError {
        final String outputKey = client.getSystemOutput();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("obtaining synthesized output '" + outputKey
                    + "' from pool...");
        }

        final SynthesizedOuput synthesizedOutput;

        try {
            synthesizedOutput = synthesizerPool.borrowObject(outputKey);
            if (LOGGER.isDebugEnabled()) {
                final String key = synthesizedOutput.getType();
                final int active = synthesizerPool.getNumActive();
                final int idle = synthesizerPool.getNumIdle();
                LOGGER.debug("synthesizer output pool has now " + active
                             + " active/" + idle + " idle for key '" + key);
            }
        } catch (Exception ex) {
            throw new NoresourceError(ex);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connecting synthesizer output to remote client..");
        }
        try {
            synthesizedOutput.connect(client);
        } catch (IOException ioe) {
            returnCallControl();

            throw new NoresourceError("error connecting to synthesizer output",
                    ioe);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...connected");
        }

        if (synthesizedOutput instanceof ObservableSystemOutput) {
            final ObservableSystemOutput observableSystemOutput =
                (ObservableSystemOutput) synthesizedOutput;

            observableSystemOutput.setSystemOutputListener(this);
        }

        return synthesizedOutput;
    }

    /**
     * Retrieves a new {@link AudioFileOutput} from the pool.
     * @return obtained file output
     * @throws NoresourceError
     *         Error obtaining an instance from the pool.
     *
     * @since 0.5.5
     */
    private AudioFileOutput getAudioFileOutputFromPool()
        throws NoresourceError {
        final String outputKey = client.getSystemOutput();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("obtaining file output '" + outputKey
                    + "' from pool...");
        }

        final AudioFileOutput fileOutut;

        try {
            fileOutut = fileOutputPool.borrowObject(outputKey);
            if (LOGGER.isDebugEnabled()) {
                final String key = fileOutut.getType();
                final int active = fileOutputPool.getNumActive();
                final int idle = fileOutputPool.getNumIdle();
                LOGGER.debug("file output pool has now " + active + " active/"
                             + idle + " idle for key '" + key);
            }
        } catch (Exception ex) {
            throw new NoresourceError(ex);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connecting file output to remote client..");
        }
        try {
            fileOutut.connect(client);
        } catch (IOException ioe) {
            returnCallControl();

            throw new NoresourceError("error connecting to file output",
                    ioe);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...connected");
        }

        if (fileOutut instanceof ObservableSystemOutput) {
            final ObservableSystemOutput observableSystemOutput =
                (ObservableSystemOutput) fileOutut;

            observableSystemOutput.setSystemOutputListener(this);
        }

        return fileOutut;
    }

    /**
     * {@inheritDoc}
     */
    public UserInput getUserInput()
            throws NoresourceError {
        if (input == null) {
            input = getUserInputFromPool();
        }

        return input;
    }

    /**
     * Retrieve a new {@link UserInput} from the pool.
     * @return obtained user input.
     * @throws NoresourceError
     *         Error obtaining an instance from the pool.
     *
     * @since 0.5.5
     */
    private JVoiceXmlUserInput getUserInputFromPool() throws NoresourceError {
        final String inputKey = client.getUserInput();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("obtaining input '" + inputKey + "' from pool...");
        }

        final JVoiceXmlUserInput userInput;

        try {
            final SpokenInput spokenInput =
                recognizerPool.borrowObject(inputKey);
            userInput = new JVoiceXmlUserInput(spokenInput);

            if (LOGGER.isDebugEnabled()) {
                final String key = spokenInput.getType();
                final int active = recognizerPool.getNumActive();
                final int idle = recognizerPool.getNumIdle();
                LOGGER.debug("output pool has now " + active + " active/" + idle
                        + " idle for key '" + key);
            }
        } catch (Exception ex) {
            throw new NoresourceError(ex);
         }

         if (LOGGER.isDebugEnabled()) {
             LOGGER.debug("connecting input to remote client..");
         }
         try {
             userInput.connect(client);
         } catch (IOException ioe) {
             returnCallControl();

             throw new NoresourceError("error connecting to user input",
                     ioe);
         }
         if (LOGGER.isDebugEnabled()) {
             LOGGER.debug("...connected");
         }

         userInput.setUserInputListener(this);

         return userInput;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized CharacterInput getCharacterInput()
            throws NoresourceError {
        return getUserInput();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized CallControl getCallControl()
            throws NoresourceError {
        if (call == null) {
            call = getCallControlFromPool();
        }

        return call;
    }

    /**
     * Retrieves a new {@link CallControl} from the pool.
     * @return obtained call control
     * @throws NoresourceError
     *         Error obtaining an instance from the pool.
     *
     * @since 0.5.5
     */
    private CallControl getCallControlFromPool() throws NoresourceError {
        final String callKey = client.getCallControl();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("obtaining call control '" + callKey
                    + "' from pool...");
        }

        final CallControl callControl;
        try {
            callControl = callPool.borrowObject(callKey);
            if (LOGGER.isDebugEnabled()) {
                final String key = callControl.getType();
                final int active = callPool.getNumActive();
                final int idle = callPool.getNumIdle();
                LOGGER.debug("call pool has now " + active + " active/"
                        + idle + " idle for key '" + key);
            }
        } catch (Exception ex) {
            throw new NoresourceError(ex);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connecting call to remote client..");
        }
        try {
            callControl.connect(client);
        } catch (IOException ioe) {
            returnCallControl();

            throw new NoresourceError("error connecting to call control",
                    ioe);
        }

        return callControl;
    }

    /**
     * Returns the input resource to the pool.
     */
    private void returnSpokenInput() {
        if (input == null) {
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("returning spoken input resource to pool...");
        }

        final SpokenInput spokenInput = input.getSpokenInput();

        try {
            final String type = spokenInput.getType();
            recognizerPool.returnObject(type, spokenInput);
            final int active = recognizerPool.getNumActive();
            final int idle = recognizerPool.getNumIdle();
            LOGGER.debug("input pool has now " + active + " active/" + idle
                    + " idle for key '" + type);
        } catch (Exception e) {
            LOGGER.error("error returning spoken input to pool", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...returned spoken input resource to pool");
        }
    }

    /**
     * Returns the call control resource to the pool.
     */
    private void returnCallControl() {
        if (call == null) {
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("returning call control resource to pool...");
        }

        try {
            final String type = call.getType();
            callPool.returnObject(type, call);
            final int active = callPool.getNumActive();
            final int idle = callPool.getNumIdle();
            LOGGER.debug("call pool has now " + active + " active/" + idle
                    + " idle for key '" + type);
        } catch (Exception e) {
            LOGGER.error("error returning call control to pool", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...returned spoken input resource to pool");
        }
    }

    /**
     * Returns the synthesized output resource to the pool.
     */
    private void returnSynthesizedOutput() {
        if (output == null) {
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("returning system output resource to pool...");
        }

        final SynthesizedOuput synthesizedOutput =
            output.getSynthesizedOutput();

        try {
            final String type = synthesizedOutput.getType();
            synthesizerPool.returnObject(type, synthesizedOutput);
            final int active = synthesizerPool.getNumActive();
            final int idle = synthesizerPool.getNumIdle();
            LOGGER.debug("synthesized output pool has now " + active
                    + " active/" + idle + " idle for key '" + type);
        } catch (Exception e) {
            LOGGER.error("error returning synthesized output to pool", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...returned synthesized output resource to pool");
        }
    }

    /**
     * Returns the audio file output resource to the pool.
     */
    private void returnAudioFileOutput() {
        if (output == null) {
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("returning system output resource to pool...");
        }

        final AudioFileOutput audioFileOutput =
            output.getAudioFileOutput();

        try {
            final String type = audioFileOutput.getType();
            fileOutputPool.returnObject(type, audioFileOutput);
            final int active = fileOutputPool.getNumActive();
            final int idle = fileOutputPool.getNumIdle();
            LOGGER.debug("audio file output pool has now " + active
                    + " active/" + idle + " idle for key '" + type);
        } catch (Exception e) {
            LOGGER.error("error returning audio file output to pool", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...returned audio file output resource to pool");
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void close() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("waiting for empty output queue...");
        }

        /** @todo check for empty output queue. */

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("...output queue empty.");
        }

        if (timer != null) {
            timer.stopTimer();
            timer = null;
        }

        if (call != null) {
            call.disconnect(client);
        }
        returnCallControl();
        call = null;

        input.stopRecognition();
        input.disconnect(client);
        returnSpokenInput();
        input = null;

        try {
            output.cancelOutput();
        } catch (NoresourceError e) {
            // Should not happen.
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("error cancelling output.");
            }
        }
        output.disconnect(client);
        returnSynthesizedOutput();
        returnAudioFileOutput();
        output = null;
    }

    /**
     * {@inheritDoc}
     */
    public void setEventHandler(final EventObserver observer) {
        eventObserver = observer;
    }

    /**
     * {@inheritDoc}
     */
    public void speechStarted(final BargeInType type) {
        if (timer != null) {
            timer.stopTimer();
            timer = null;
        }

        if (output == null) {
            return;
        }

        /** @todo Check the bargein type. */
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("speech started: '" + type.getType()
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
     * {@inheritDoc}
     */
    public void resultAccepted(final RecognitionResult result) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("accepted recognition '" + result.getUtterance()
                        + "'");
        }

        if (eventObserver != null) {
            result.setMark(markname);

            final RecognitionEvent recognitionEvent =
                    new RecognitionEvent(result);
            eventObserver.notifyEvent(recognitionEvent);
        }

        markname = null;

        if (externalRecognitionListener != null) {
            final ExternalRecognitionResult externalResult =
                new ExternalRecognitionResult(result);
            externalRecognitionListener.resultAccepted(externalResult);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void resultRejected(final RecognitionResult result) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("rejected recognition'" + result.getUtterance() + "'");
        }

        if (eventObserver != null) {
            result.setMark(markname);

            final NomatchEvent noMatchEvent = new NomatchEvent();
            eventObserver.notifyEvent(noMatchEvent);
        }
        if (externalRecognitionListener != null) {
            final ExternalRecognitionResult externalResult =
                new ExternalRecognitionResult(result);
            externalRecognitionListener.resultRejected(externalResult);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void outputStarted() {
        ++activeOutputCount;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("output started: active output count: "
                         + activeOutputCount);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void outputEnded() {
        --activeOutputCount;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("output ended: active output count: "
                    + activeOutputCount);
        }

        if (eventObserver == null) {
            return;
        }

        timer = new TimerThread(eventObserver);
        timer.start();
    }

    /**
     * {@inheritDoc}
     */
    public void markerReached(final String mark) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("reached mark '" + mark + "'");
        }

        markname = mark;
    }
}
