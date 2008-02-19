/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
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

package org.jvoicexml.implementation.jsapi10.jvxml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.jlibrtp.DataFrame;
import org.jlibrtp.Participant;
import org.jlibrtp.RTPAppIntf;
import org.jlibrtp.RTPSession;

/**
 * A general purpose RTP server based on
 * <a href="http://jlibrtp.org">jlibrtp</a>.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2007-2008 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.6
 */
final class RtpServer implements RTPAppIntf {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(RtpServer.class);

    /** Size of the send buffer. */
    private static final int SEND_BUFFER_SIZE = 1024;

    /** The encapsulated {@link RTPSession}. */
    private RTPSession session;

    /**
     * Constructs a new object taking a free random port and this computer
     * as the local address.
     */
    public RtpServer() {
    }

    /**
     * Opens the server.
     * @exception IOException
     *         Error creating the RTP session.
     */
    public void open() throws IOException {
        DatagramSocket rtpSocket = new DatagramSocket();
        DatagramSocket rtpcSocket = new DatagramSocket();

        session = new RTPSession(rtpSocket, rtpcSocket);
        session.registerRTPSession(this, null, null);
    }

    /**
     * Adds a remote RTP player on the specified remote computer.
     * @param remoteHost name of the remote host.
     * @param rtpPort port number of the JMF player.
     * @param rtpcPort port number of the RTP control stream.
     * @throws IOException
     *         Error resolving the remote address.
     */
    public void addTarget(final InetAddress remoteHost, final int rtpPort,
            final int rtpcPort)
            throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("adding RTP target " + remoteHost + ":" + rtpPort
                    + " (" + rtpcPort + ")");
        }
        Participant participant = new Participant(
                remoteHost.getCanonicalHostName(), rtpPort, rtpcPort);
        session.addParticipant(participant);
    }

    /**
     * Removes a remote RTP player on the specified remote computer.
     * @param remoteHost name of the remote host.
     * @param remotePort port number of the JMF player.
     * @throws IOException
     *         Error resolving the remote address.
     */
    public synchronized void removeTarget(final InetAddress remoteHost,
            final int remotePort)
            throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("removing RTP target " + remoteHost + ":"
                    + remotePort);
        }

        if (session != null) {
            Participant participant = new Participant(
                    remoteHost.getCanonicalHostName(), remotePort, -1);
            session.removeParticipant(participant);
        }
    }

    /**
     * Sends the given <code>buffer</code> over the RTP stream.
     * @param buffer the buffer to send.
     * @throws IOException
     *         Error sending.
     */
    public synchronized void sendData(final byte[] buffer) throws IOException {
        if (session == null) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("sending " + buffer.length + " RTP bytes");
        }
        final ByteArrayInputStream in = new ByteArrayInputStream(buffer);
        sendData(in);
    }

    /**
     * Sends the given data from the given stream over the RTP stream.
     * @param in stream to the data to send.
     * @throws IOException
     *         Error sending.
     */
    public synchronized void sendData(final InputStream in) throws IOException {
        final byte[] sendBuffer = new byte[SEND_BUFFER_SIZE];
        int num = 0;
        do {
            num = in.read(sendBuffer);
            if (num == sendBuffer.length) {
                session.sendData(sendBuffer);
            } else if (num > 0) {
                final byte[] tmpBuffer = new byte[num];
                System.arraycopy(tmpBuffer, 0, sendBuffer, 0, num);
                session.sendData(tmpBuffer);
            }
        } while (num >= 0);
        
    }

    /**
     * {@inheritDoc}
     */
    public int frameSize(final int size) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public void receiveData(final DataFrame frame,
            final Participant participant) {
    }

    /**
     * {@inheritDoc}
     */
    public void userEvent(final int type, final Participant[] participants) {
    }

    /**
     * Closes this RTP server.
     */
    public synchronized void close() {
        if (session == null) {
            return;
        }

        session.endSession();
        session = null;
    }
}
