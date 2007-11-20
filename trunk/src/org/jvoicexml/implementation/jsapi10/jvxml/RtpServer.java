/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10.jvxml;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.media.format.AudioFormat;

import org.apache.log4j.Logger;
import org.jlibrtp.Participant;
import org.jlibrtp.RTPSession;

/**
 * A general purpose RTP server based on JMF.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.6
 */
final class RtpServer {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(RtpServer.class);

    /** Audio format. */
    public static final AudioFormat FORMAT_ULAR_RTP = new AudioFormat(
            AudioFormat.ULAW_RTP, 8000d, 8, 1, AudioFormat.LITTLE_ENDIAN,
            AudioFormat.SIGNED);

    /** The encapsulated {@link RTPSession}. */
    private final RTPSession session;

    /**
     * Constructs a new object taking a free random port and this computer
     * as the local address.
     * @throws IOException
     *         Error creating the RTP session.
     */
    public RtpServer() throws IOException {
        DatagramSocket rtpSocket = new DatagramSocket(16386);
        DatagramSocket rtpcSocket = new DatagramSocket(16387);
        
        session = new RTPSession(rtpSocket, rtpcSocket);
    }

    /**
     * Adds a remote JMF player on the specified remote computer.
     * @param remoteHost name of the remote host.
     * @param remotePort port number of the JMF player.
     * @throws IOException
     *         Error resolving the remote address.
     */
    public void addTarget(final InetAddress remoteHost, final int remotePort)
            throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("adding RTP target " + remoteHost + ":" + remotePort);
        }
        Participant participant = new Participant(
                remoteHost.getCanonicalHostName(), remotePort, -1);
        session.addParticipant(participant);
    }

    /**
     * removes a remote JMF player on the specified remote computer.
     * @param remoteHost name of the remote host.
     * @param remotePort port number of the JMF player.
     * @throws IOException
     *         Error resolving the remote address.
     */
    public void removeTarget(final InetAddress remoteHost, final int remotePort)
            throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("removing RTP target " + remoteHost + ":"
                    + remotePort);
        }
        
        Participant participant = new Participant(
                remoteHost.getCanonicalHostName(), remotePort, -1);
        session.removeParticipant(participant);
    }
    
    public void sendData(byte[] buffer) {
        session.sendData(buffer);
    }
}
