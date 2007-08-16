/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
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

package org.jvoicexml.client.rtp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.media.control.BufferControl;
import javax.media.protocol.DataSource;
import javax.media.rtp.InvalidSessionAddressException;
import javax.media.rtp.Participant;
import javax.media.rtp.RTPControl;
import javax.media.rtp.RTPManager;
import javax.media.rtp.ReceiveStream;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.SessionListener;
import javax.media.rtp.event.ByeEvent;
import javax.media.rtp.event.NewParticipantEvent;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.event.RemotePayloadChangeEvent;
import javax.media.rtp.event.SessionEvent;
import javax.media.rtp.event.StreamMappedEvent;

/**
 * RTP player for playing the output on the client side.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public class RtpPlayer extends Thread implements ReceiveStreamListener,
        SessionListener, ControllerListener {
    /** The RTP Manager. */
    private final RTPManager rtpManager;

    final Object dataSync = new Object();

    boolean dataReceived = false;

    final int serverPort;

    /**
     * Constructs a new object.
     *
     * @param port
     *            server RTP port.
     */
    public RtpPlayer(final int port) {
        rtpManager = RTPManager.newInstance();
        serverPort = port;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        rtpManager.addSessionListener(this);
        rtpManager.addReceiveStreamListener(this);
        System.out.println("Added receive stream");
        InetAddress ipAddr;
        try {
            ipAddr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }
        SessionAddress clientAddress = new SessionAddress(ipAddr,
                serverPort);
        try {
            rtpManager.initialize(clientAddress);
        } catch (InvalidSessionAddressException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        BufferControl buffer = (BufferControl) rtpManager
                .getControl("javax.media.control.BufferControl");
        if (buffer != null) {
            buffer.setBufferLength(400);
        }
        SessionAddress serverAddress = new SessionAddress(ipAddr, serverPort);
        System.out.println("Session Addresses created");
        try {
            rtpManager.addTarget(serverAddress);
        } catch (InvalidSessionAddressException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        long then = System.currentTimeMillis();
        long waitingPeriod = 30000; // wait for a maximum of 30 secs.

        try {
            synchronized (dataSync) {
                while (!dataReceived
                        && (System.currentTimeMillis() - then < waitingPeriod)) {
                    if (!dataReceived)
                        System.err
                                .println("  - Waiting for RTP data to arrive...");
                    dataSync.wait(1000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!dataReceived) {
            System.err.println("No RTP data was received.");
            quit();
            return;
        }
    }

    public boolean finished() {
        return false;
    }

    public void quit() {
        rtpManager.removeTargets("Session over");
        rtpManager.dispose();
    }

    // The SessionListener update method, it listens for new joining users
    public synchronized void update(SessionEvent event) {
        if (event instanceof NewParticipantEvent) {
            Participant participant = ((NewParticipantEvent) event)
                    .getParticipant();
            System.out.println("Participant " + participant.getCNAME()
                    + " joined");
        }
    }

    public synchronized void update(ReceiveStreamEvent event) {
        Participant participant = event.getParticipant();
        ReceiveStream stream = event.getReceiveStream();

        if (event instanceof RemotePayloadChangeEvent) {
            System.out.println("Error: Payload Change");
            System.exit(-1);

            // If new stream, create player for that stream and associate
            // datasource
        } else if (event instanceof NewReceiveStreamEvent) {
            try {
                stream = ((NewReceiveStreamEvent) event).getReceiveStream();
                DataSource dataSource = stream.getDataSource();

                // Get Formats of the New Stream
                RTPControl control = (RTPControl) dataSource
                        .getControl("javax.media.rtp.RTPControl");
                if (control != null) {
                    System.out
                            .println("New RTP Stream: " + control.getFormat());
                } else {
                    System.out.println("New Stream of unknown format");
                }

                if (participant == null) {
                    System.out.println("User of RTP Session unknown");
                } else {
                    System.out.println("User of RTP Session: "
                            + participant.getCNAME());
                }

                // Now that we associated the DataSource with the Stream, the
                // player to handle
                // the media can be created

                Player player = javax.media.Manager.createPlayer(dataSource);
                if (player == null) {
                    return;
                }
                System.out.println("player created and linked to datasource");
                // Add controllerListener to catch Controller changes
                player.addControllerListener(this);
                player.realize();
                System.out.println("player realized");
                // Notify create() that a new stream has arrived
                synchronized (dataSync) {
                    dataReceived = true;
                    dataSync.notifyAll();
                }
            } catch (Exception e) {
                System.out.println("NewReceiveException " + e.getMessage());
            }

            // This event is when a stream that was previously unidentified
            // becomes identified with a
            // participant. When an RTCP packet arrives that has an SSRC that
            // matches the one without
            // a participant arrives, this event is generated
        } else if (event instanceof StreamMappedEvent) {
            if (stream != null && stream.getDataSource() != null) {
                DataSource dataSource = stream.getDataSource();
                // Find out formats
                RTPControl control = (RTPControl) dataSource
                        .getControl("javax.media.rtp.RTPControl");
                if (control != null) {
                    System.out
                            .println("Previously unidentified stream now associated with participant");
                    System.out.println("with format " + control.getFormat()
                            + " from user: " + participant.getCNAME());
                }
            }
            // If this is an instant of the server ending the session, receive
            // the Bye event and quit
        } else if (event instanceof ByeEvent) {
            System.out.println("Stream ended, goodbye - from: "
                    + participant.getCNAME());
        }
    }

    public synchronized void controllerUpdate(ControllerEvent control) {
        Player player = (Player) control.getSourceController();
        // If player wasn't created successfully from controller, return

        if (player == null) {
            System.out.println("Player is null");
            return;
        }

        if (control instanceof RealizeCompleteEvent) {
            player.start();
        }

        if (control instanceof ControllerErrorEvent) {
            player.removeControllerListener(this);
            System.out.println("Error in ControllerErrorEvent: " + control);
        }
    }
}
