/*
 * File:    $RCSfile: ImplementationPlatform.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.InputStream;
import java.io.OutputStream;

import org.jvoicexml.event.EventObserver;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.NomatchEvent;
import org.jvoicexml.event.plain.jvxml.RecognitionEvent;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * The <em>implementation platform</em> is controlled by the VoiceXML
 * interpreter context and by the VoiceXML interpreter.
 *
 * <p>
 * The implementation platform generates events in response to user actions
 * (e.g. spoken or character input received, disconnect) and system events (e.g.
 * timer expiration). Some of these events are acted upon the VoiceXML
 * interpreter itself, as specified by the VoiceXML document, while others are
 * acted upon by the VoiceXML interpreter context.
 * </p>
 *
 * @see org.jvoicexml.interpreter.VoiceXmlInterpreter
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class ImplementationPlatform
        implements UserInputListener, SystemOutputListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ImplementationPlatform.class);

    /** A mapping of platform types to <code>ImplementationPlatform</code>s. */
    private final KeyedPlatformPool platforms;

    /** Platform implementation to use. */
    private Platform platform;

    /** The system output device. */
    private SystemOutput output;

    /** Support for audio input. */
    private UserInput input;

    /** The calling device. */
    private CallControl call;

    /** The event observer to communicate events back to the interpreter. */
    private EventObserver eventObserver;

    /** A timer to get the noinput timeout. */
    private TimerThread timer;

    /** The name of the mark last executed by the SSML processor. */
    private String markname;

    /**
     * Constructs a new Implementation platform.
     *
     * <p>
     * This method should not be called by any application. The implemntation
     * platform is accessable via the <code>Session</code>
     * </p>
     *
     * @param pool The pool to use.
     *
     * @see org.jvoicexml.Session
     */
    ImplementationPlatform(KeyedPlatformPool pool) {
        platforms = pool;
    }

    /**
     * Retrieves the <code>SystemOutput</code> and tries to open it.
     *
     * @param p
     *        The platform.
     * @return Created <code>SystemOutput</code> if successful,
     *         <code>null</code> else.
     */
    private SystemOutput getSystemOutput(final Platform p) {
        if (p == null) {
            return null;
        }

        final SystemOutput systemOutput = p.getSystemOutput();

        systemOutput.setSystemOutputListener(this);

        return systemOutput;
    }

    /**
     * Get the <code>UserInput</code> and try to open it.
     *
     * @param p
     *        The platform.
     * @return Created <code>UserInput</code> if successful, <code>null</code>
     *         else.
     */
    private UserInput getUserInput(final Platform p) {
        if (p == null) {
            return null;
        }

        final SpokenInput spokenInput = p.getSpokenInput();
        final UserInput systemInput = new JVoiceXmlUserInput(spokenInput);

        systemInput.setUserInputListener(this);

        return systemInput;
    }

    /**
     * Selector for the audio output device.
     *
     * @return Audio output device to use.
     * @exception NoresourceError
     *            Output device is not available.
     */
    public SystemOutput getSystemOutput()
            throws NoresourceError {
        if (output == null) {
            throw new NoresourceError("output device not available!");
        }

        return output;
    }

    /**
     * Retrieves the user input device.
     *
     * @return User input device to use.
     * @exception NoresourceError
     *            Input device is not available.
     */
    public UserInput getUserInput()
            throws NoresourceError {
        if (input == null) {
            throw new NoresourceError("input device not available!");
        }

        return input;
    }

    /**
     * Retrieves the DTMF input device.
     *
     * @return DTMF input device to use.
     * @exception NoresourceError
     *            Input device is not available.
     */
    public CharacterInput getCharacterInput()
            throws NoresourceError {
        if (input == null) {
            throw new NoresourceError("input device not available!");
        }

        return input;
    }

    /**
     * Sets the call control for this platform.
     *
     * @param callControl
     *        The calling device.
     * @exception NoresourceError
     *            Engines not ready.
     */
    void setCallControl(final CallControl callControl)
            throws NoresourceError {
        call = callControl;

        if (call != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("opening call control...");
            }

            call.open();

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("...call control opened");
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("setting call control to " + call);
            }

            final OutputStream out = call.getOutputStream();
            output.setOutputStream(out);

            final InputStream in = call.getInputStream();
            input.setInputStream(in);
        }
    }

    /**
     * Retrieves the calling device.
     *
     * @return Calling device to use.
     * @exception NoresourceError
     *            Calling device is not available.
     */
    public CallControl getCallControl()
            throws NoresourceError {
        if (call == null) {
            throw new NoresourceError("calling device not available!");
        }

        return call;
    }

    /**
     * Closes all open resources.
     */
    public void close() {
        if (timer != null) {
            timer.stopTimer();
            timer = null;
        }

        if (call != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("closing call control...");
            }
            call.close();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("...closed");
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("return platform of type '" + platform.getType() + "'");
        }

        try {
            platforms.returnObject(platform.getType(), platform);
        } catch (Exception ex) {
            LOGGER.error("error returning platorm to pool", ex);
        }
    }

    /**
     * Sets the platform implementation.
     * @param pf The platform.
     *
     * @since 0.5
     */
    public void setPlatform(final Platform pf) {
        platform = pf;

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("using platform '" + platform + "'");
        }

        output = getSystemOutput(platform);
        input = getUserInput(platform);

    }

    /**
     * Retrieves a unique type of this implementation platform.
     * @return Type of this implementation platform.
     * @since 0.5
     */
    String getType() {
        if (platform == null) {
            return null;
        }

        return platform.getType();
    }

    /**
     * Sets the event observer to communicate events back to the interpreter.
     * @param observer The event observer.
     *
     * @since 0.5
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
    public void outputEnded() {
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
