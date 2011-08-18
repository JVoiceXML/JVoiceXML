/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.implementation.marc/src/org/jvoicexml/implementation/marc/MarcSynthesizedOutput.java $
 * Version: $LastChangedRevision: 2742 $
 * Date:    $Date: 2011-06-29 13:33:07 +0200 (Mi, 29 Jun 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date: 2011-06-29 13:33:07 +0200 (Mi, 29 Jun 2011) $, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.implementation.marc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.log4j.Logger;

/**
 * Feedback channel from Marc.
 * @author Dirk Schnelle-Walka
 *
 */
class MarcFeedback extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(MarcFeedback.class);

    /** The feedback port from MARC. */
    private int port;

    /**
     * Constructs a new object.
     * @param portNumber the feedback port number from MARC.
     */
    public MarcFeedback(final int portNumber) {
        setDaemon(true);
        port = portNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            final DatagramSocket socket = new DatagramSocket(port);
            LOGGER.info("receiving feedback from MARC at port " + port);
            final byte[] buffer = new byte[1024];
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            while (true) {
                final DatagramPacket packet =
                        new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                out.write(buffer, 0, packet.getLength());
                LOGGER.info("received from MARC: " + out.toString());
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
