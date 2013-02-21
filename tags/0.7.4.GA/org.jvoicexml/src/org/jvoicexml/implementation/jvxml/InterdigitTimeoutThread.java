/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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
     * Constructs a new object.
     * @param characterInput the related character input.
     */
    public InterdigitTimeoutThread(
            final BufferedCharacterInput characterInput) {
        setDaemon(true);
        setName("InterdigitTimeoutThread");
        input = characterInput;
        lock = new Object();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("started interdigit timeout thread");
        }
        do {
            enteredDigit = false;
            synchronized (lock) {
                try  {
                    lock.wait(1000);
                } catch (InterruptedException e) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("interdigit timeout thread interrupted", e);
                    }
                    return;
                }
            }
        } while (enteredDigit);
        input.addCharacter((char) 0);
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
