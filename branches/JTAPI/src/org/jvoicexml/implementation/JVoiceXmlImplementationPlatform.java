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

import org.jvoicexml.CallControl;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpokenInput;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.EventObserver;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.NomatchEvent;
import org.jvoicexml.event.plain.jvxml.RecognitionEvent;
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

    /** Pool of system output resource factories. */
    private final KeyedResourcePool<SystemOutput> outputPool;

    /** Pool of user input resource factories. */
    private final KeyedResourcePool<SpokenInput> inputPool;

    /** Pool of user calling resource factories. */
    private final KeyedResourcePool<CallControl> callPool;

    /** The remote client to connect to. */
    private RemoteClient client;

    /** The system output device. */
    private SystemOutput output;

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

    /**
     * Constructs a new Implementation platform.
     *
     * <p>
     * This method should not be called by any application. The implemntation
     * platform is accessible via the <code>Session</code>
     * </p>
     *
     * @param callControlPool  pool of call control resource factories
     * @param systemOutputPool pool of system output resource factories
     * @param spokenInputPool pool of spoken input resource factories
     * @param remoteClient the remote client to connect to.
     *
     * @see org.jvoicexml.Session
     */
    JVoiceXmlImplementationPlatform(
            final KeyedResourcePool<CallControl> callControlPool,
            final KeyedResourcePool<SystemOutput> systemOutputPool,
            final KeyedResourcePool<SpokenInput> spokenInputPool,
            final RemoteClient remoteClient) {
        client = remoteClient;
        callPool = callControlPool;
        outputPool = systemOutputPool;
        inputPool = spokenInputPool;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized SystemOutput getSystemOutput()
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
    private SystemOutput getSystemOutputFromPool() throws NoresourceError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("obtaining system output from pool...");
        }

        final SystemOutput systemOutput;

        try {
            final String outputKey = client.getSystemOutput();
            systemOutput = outputPool.borrowObject(outputKey);
            if (LOGGER.isDebugEnabled()) {
                final String key = systemOutput.getType();
                final int active = outputPool.getNumActive();
                final int idle = outputPool.getNumIdle();
                LOGGER.debug("output pool has now " + active + " active/" + idle
                        + " idle for key '" + key);
            }
        } catch (Exception ex) {
            throw new NoresourceError(ex);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connecting output to remote client..");
        }
        try {
            systemOutput.connect(client);
        } catch (IOException ioe) {
            returnCallControl();

            throw new NoresourceError("error connecting to system output",
                    ioe);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...connected");
        }

        if (output instanceof ObservableSystemOutput) {
            final ObservableSystemOutput observableSystemOutput =
                (ObservableSystemOutput) systemOutput;

            observableSystemOutput.setSystemOutputListener(this);
        }

        return systemOutput;
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
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("obtaining input from pool...");
        }

        final JVoiceXmlUserInput userInput;

        try {
            final String inputKey = client.getUserInput();
            final SpokenInput spokenInput = inputPool.borrowObject(inputKey);
            userInput = new JVoiceXmlUserInput(spokenInput);

            if (LOGGER.isDebugEnabled()) {
                final String key = spokenInput.getType();
                final int active = inputPool.getNumActive();
                final int idle = inputPool.getNumIdle();
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
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("obtaining call control from pool...");
        }

        final CallControl callControl;
        try {
            final String callKey = client.getCallControl();
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
            //call.connect(client);
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
            inputPool.returnObject(type, spokenInput);
            final int active = inputPool.getNumActive();
            final int idle = inputPool.getNumIdle();
            LOGGER.debug("input pool has now " + active + " active/" + idle
                    + " idle for key '" + type);
        } catch (Exception e) {
            LOGGER.error("error returning spoken input to pool", e);
        }

        input = null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...returned spoken input resource to pool");
        }
    }

    /**
     * Returns the input resource to the pool.
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

        call = null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...returned spoken input resource to pool");
        }
    }

    /**
     * Returns the input resource to the pool.
     */
    private void returnSystemOutput() {
        if (output == null) {
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("returning system output resource to pool...");
        }

        try {
            final String type = output.getType();
            outputPool.returnObject(type, output);
            final int active = outputPool.getNumActive();
            final int idle = outputPool.getNumIdle();
            LOGGER.debug("output pool has now " + active + " active/" + idle
                    + " idle for key '" + type);
        } catch (Exception e) {
            LOGGER.error("error returning system output to pool", e);
        }

        output = null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...returned spoken input resource to pool");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("waiting for empty output queue...");
        }

        /** @todod check for empty output queue. */

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("...output queue empty.");
        }

        if (timer != null) {
            timer.stopTimer();
            timer = null;
        }

        returnCallControl();
        returnSpokenInput();
        returnSystemOutput();
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
     * @todo Implement this method.
     */
    public void markerReached(final String mark) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("reached mark '" + mark + "'");
        }

        markname = mark;
    }
}
