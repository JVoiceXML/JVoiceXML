/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date: $
 * Author:  $java.LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.log4j.Logger;
import org.jvoicexml.implementation.SystemOutputListener;
import org.jvoicexml.implementation.client.AudioEndMessage;

/**
 * Thread to communicate with the client's audio system.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group -
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
            Logger.getLogger(ClientAudioControl.class);

    /** The input stream to use. */
    private final ObjectInputStream input;

    /** The system output listener. */
    private final SystemOutputListener listener;

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
        do {
            try {
                final Object object = input.readObject();
                if (object instanceof AudioEndMessage) {
                    listener.outputEnded();
                }
                System.out.println("*** " + object);
            } catch (ClassNotFoundException ex) {
            } catch (java.io.IOException ex) {
                ex.printStackTrace();

                return;
            }
        } while (true);
    }
}
