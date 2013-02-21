/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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
package org.jvoicexml.implementation.jvxml;

import org.apache.log4j.Logger;

/**
 * Waits after the first digit has been entered for a certain time until the
 * next digit has been entered.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.4
 */
class InterdigitTimeoutThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(CharacterInputThread.class);

    /** The related character input. */
    private final BufferedCharacterInput input;

    /** Set to true if a digit has been entered. */
    private boolean enteredDigit;

    /** Locking object. */
    private final Object lock;

    /**
     * The inter-digit timeout value to use when recognizing DTMF input.
     */
    private final long interdigittimeout;

    /** The terminate character. */
    private final char termchar;

    /**
     * Constructs a new object.
     * @param characterInput the related character input.
     * @param timeout inter-digit timeout value to use when recognizing DTMF
     *          input
     * @param term the terminate character
     */
    public InterdigitTimeoutThread(
            final BufferedCharacterInput characterInput,
            final long timeout, final char term) {
        setDaemon(true);
        setName("InterdigitTimeoutThread");
        input = characterInput;
        lock = new Object();
        interdigittimeout = timeout;
        termchar = term;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("started interdigit timeout thread with a timeout of "
                    + interdigittimeout + " msecs");
        }
        do {
            enteredDigit = false;
            synchronized (lock) {
                try  {
                    lock.wait(interdigittimeout);
                } catch (InterruptedException e) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("interdigit timeout thread interrupted",
                                e);
                    }
                    return;
                }
            }
        } while (enteredDigit);
        input.addCharacter(termchar);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("interdigit timeout thread terminated");
        }
    }

    /**
     * The user has entered another digit.
     */
    public void enteredDigit() {
        enteredDigit = true;
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
