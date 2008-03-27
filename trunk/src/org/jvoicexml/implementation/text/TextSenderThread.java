/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.text;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Reads asynchronously some text input from the client.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
final class TextSenderThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(TextSenderThread.class);

    /** Delay in msec before ending a play. */
    private static final int DELAY = 1000;

    /** The socket to read from. */
    private final AsynchronousSocket socket;

    /** Reference to the telephony device. */
    private final TextTelephony telephony;

    private final Object object;
    
    /**
     * Constructs a new object.
     * @param asyncSocket the socket to read from.
     * @param textTelephony telephony device.
     */
    public TextSenderThread(final AsynchronousSocket asyncSocket,
            final Object o, final TextTelephony textTelephony) {
        socket = asyncSocket;
        telephony = textTelephony;
        object = o;

        setDaemon(true);
        setName("TextSenderThread");
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("sending output " + object);
        }
        try {
            socket.writeObject(object);
            // TODO Replace this by a timing solution.
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("delay interrupted", e);
                }
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("... done sending output");
            }
            telephony.playStopped();
        } catch (IOException e) {
            return;
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("text sender thread stopped");
            }
        }
    }
}
