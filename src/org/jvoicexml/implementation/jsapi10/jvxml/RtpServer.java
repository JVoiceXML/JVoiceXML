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
import java.net.InetAddress;

import javax.media.Manager;
import javax.media.MediaException;
import javax.media.Player;
import javax.media.Processor;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.rtp.RTPManager;
import javax.media.rtp.SendStream;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.SessionManagerException;

import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

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
        LoggerFactory.getLogger(RtpServer.class);

    /** Time in msec to wait before polling for the state again. */
    private static final int WAIT_STATE_DELAY = 300;

    /** Audio format. */
    public static final AudioFormat FORMAT_ULAR_RTP = new AudioFormat(
            AudioFormat.ULAW_RTP, 8000d, 8, 1, AudioFormat.LITTLE_ENDIAN,
            AudioFormat.SIGNED);

    /** The RTP manager. */
    private RTPManager rtpManager;

    /** The stream to send data. */
    private SendStream sendStream;

    /** The local IP address. */
    private SessionAddress localAddress;

    /**
     * Constructs a new object taking a free random port and this computer
     * as the local address.
     * @throws IOException
     *         Error creating the RTP manager.
     * @throws SessionManagerException
     *         Error creating the RTP manager.
     * @throws MediaException
     *         Error creating the RTP manager.
     */
    public RtpServer() throws IOException, SessionManagerException,
            MediaException {
        rtpManager = RTPManager.newInstance();
        InetAddress localIp = InetAddress.getLocalHost();
        localAddress = new SessionAddress(localIp, SessionAddress.ANY_PORT);
        rtpManager.initialize(localAddress);
    }

    /**
     * Adds a remote JMF player on the specified remote computer.
     * @param remoteHost name of the remote host.
     * @param remotePort port number of the JMF player.
     * @throws IOException
     *         Error resolving the remote address.
     * @throws SessionManagerException
     *         Error adding the target.
     */
    public void addTarget(final InetAddress remoteHost, final int remotePort)
            throws IOException, SessionManagerException {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("adding RTP target " + remoteHost + ":" + remotePort);
        }
        final SessionAddress remoteAddress =
            new SessionAddress(remoteHost, remotePort);
        rtpManager.addTarget(remoteAddress);
    }

    /**
     * removes a remote JMF player on the specified remote computer.
     * @param remoteHost name of the remote host.
     * @param remotePort port number of the JMF player.
     * @throws IOException
     *         Error resolving the remote address.
     * @throws SessionManagerException
     *         Error adding the target.
     */
    public void removeTarget(final InetAddress remoteHost, final int remotePort)
            throws IOException, SessionManagerException {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("removing RTP target " + remoteHost + ":" + remotePort);
        }
        final SessionAddress remoteAddress =
            new SessionAddress(remoteHost, remotePort);
        rtpManager.removeTarget(remoteAddress, "disconnect");
    }

    /**
     * Initialize the send stream.
     * @param sendStreamSource datasource to send data from.
     * @throws IOException
     *         Error initializing the stream.
     * @throws MediaException
     *         Error initializing the stream.
     */
    public void initSendStream(final DataSource sendStreamSource)
        throws IOException, MediaException {
        Processor proc = Manager.createProcessor(sendStreamSource);
        proc.configure();
        waitForState(proc, Processor.Configured);
        proc.setContentDescriptor(new ContentDescriptor(
                ContentDescriptor.RAW_RTP));
        proc.start();
        proc.getTrackControls()[0].setFormat(FORMAT_ULAR_RTP);
        waitForState(proc, Player.Started);
        sendStream = rtpManager.createSendStream(proc.getDataOutput(), 0);
    }

    /**
     * Start sending.
     * @throws IOException
     *         Error starting the send stream.
     */
    public void startSending() throws IOException {
        sendStream.start();
    }

    /**
     * Stop sending.
     * @throws IOException
     *         Error stopping the send stream.
     */
    public void stopSending() throws IOException {
        sendStream.stop();
    }

    /**
     * Dispose.
     */
    public void dispose() {
        rtpManager.removeTargets("Disconnected!");
        rtpManager.dispose();
    }

    /**
     * Delays until the processor reaches the specified state.
     * @param processor the processor
     * @param state the state to reach.
     */
    public static void waitForState(final Processor processor,
            final int state) {
        while (true) {
            int actState = processor.getState();
            if (state == actState) {
                return;
            }

            try {
                Thread.sleep(WAIT_STATE_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
