/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.CallControl;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.ImplementationPlatform;
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
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
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

    /** Delay between two polls while waiting for end of output. */
    private static final int ACTIVE_OUTPUT_DELAY = 300;

    /** The factory to return the objects on close. */
    private final ImplementationPlatformFactory factory;

    /** The system output device. */
    private final SystemOutput output;

    /** Support for audio input. */
    private final UserInput input;

    /** The calling device. */
    private final CallControl call;

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
     * platform is accessable via the <code>Session</code>
     * </p>
     *
     * @param platformFactory the platform factory.
     * @param callControl the calling device.
     * @param systemOutput system output to use.
     * @param userInput user input to use.
     *
     * @see org.jvoicexml.Session
     */
    JVoiceXmlImplementationPlatform(
            final ImplementationPlatformFactory platformFactory,
            final CallControl callControl, final SystemOutput systemOutput,
            final UserInput userInput) {
        factory = platformFactory;
        call = callControl;
        output = systemOutput;
        input = userInput;

        if (output != null) {
            output.setSystemOutputListener(this);
        }
        if (input != null) {
            input.setUserInputListener(this);
        }

    }

    /**
     * {@inheritDoc}
     */
    public SystemOutput getSystemOutput()
            throws NoresourceError {
        if (output == null) {
            throw new NoresourceError("output device not available!");
        }

        return output;
    }

    /**
     * {@inheritDoc}
     */
    public UserInput getUserInput()
            throws NoresourceError {
        if (input == null) {
            throw new NoresourceError("input device not available!");
        }

        return input;
    }

    /**
     * {@inheritDoc}
     */
    public CharacterInput getCharacterInput()
            throws NoresourceError {
        if (input == null) {
            throw new NoresourceError("input device not available!");
        }

        return input;
    }

    /**
     * {@inheritDoc}
     */
    public CallControl getCallControl()
            throws NoresourceError {
        if (call == null) {
            throw new NoresourceError("calling device not available!");
        }

        return call;
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

        if (call != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("closing call control...");
            }
            call.close();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("...closed");
            }
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("returning implementation platform");
        }

        try {
            factory.returnImplementationPlatform(this);
        } catch (Exception ex) {
            LOGGER.error("error returning implemetnation platorm", ex);
        }
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
