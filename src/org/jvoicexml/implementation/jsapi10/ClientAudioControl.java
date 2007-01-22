/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $java.LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10;

import java.io.ObjectInputStream;

import org.jvoicexml.implementation.SystemOutputListener;
import org.jvoicexml.implementation.client.AudioEndMessage;
import org.jvoicexml.implementation.client.MarkerMessage;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * Thread to communicate with the client's audio system.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.6
 */
class ClientAudioControl
        extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ClientAudioControl.class);

    /** The input stream to use. */
    private final ObjectInputStream input;

    /** The system output listener. */
    private final SystemOutputListener listener;

    /** Flag if this thread has been asked to terminate. */
    private boolean stopped;

    /**
     * Constructs a new object.
     * @param in The input stream to use.
     * @param outputListener
     *        The output listener to inform about output events that are
     *        retrieved while communicating with the client.
     */
    public ClientAudioControl(final ObjectInputStream in,
                              final SystemOutputListener outputListener) {
        input = in;
        listener = outputListener;

        setDaemon(true);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        stopped = false;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("client communication started");
        }

        do {
            try {
                final Object object = input.readObject();
                if (object instanceof AudioEndMessage) {
                    listener.outputEnded();
                } else if (object instanceof MarkerMessage) {
                    final MarkerMessage msg = (MarkerMessage) object;
                    final String mark = msg.getMarker();
                    listener.markerReached(mark);
                }
            } catch (ClassNotFoundException cnfe) {
                LOGGER.error("unknown class from client", cnfe);
            } catch (java.io.IOException ioe) {
                if (!stopped) {
                    LOGGER.error("error reading client input", ioe);
                }

                stopped = true;
            }
        } while (!stopped);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("client communication stopped");
        }
    }

    /**
     * Makes a stop request.
     */
    public void stopReading() {
        /** @todo Make a real stop request to this thread. */
        stopped = true;
    }
}
