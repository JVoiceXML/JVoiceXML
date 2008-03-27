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
final class TextReceiverThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(TextReceiverThread.class);

    /** The socket to read from. */
    private final AsynchronousSocket socket;

    /** Reference to the spoken input device. */
    private final TextSpokenInput input;

    /**
     * Constructs a new object.
     * @param asyncSocket the socket to read from.
     * @param spokenInput the received input.
     */
    public TextReceiverThread(final AsynchronousSocket asyncSocket,
            final TextSpokenInput spokenInput) {
        socket = asyncSocket;
        input = spokenInput;

        setDaemon(true);
        setName("TextReceiverThread");
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("text receiver thread started");
        }
        try {
            while (!interrupted()) {
                final String str = (String) socket.readObject();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("read '" + str + "'");
                }
                input.notifyRecognitionResult(str);
            }
        } catch (IOException e) {
            return;
        } catch (ClassNotFoundException e) {
            return;
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("text receiver thread stopped");
            }
        }
    }
}
