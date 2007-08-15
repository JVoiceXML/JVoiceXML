package org.jvoicexml.client.rtp;

import java.net.InetAddress;

import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.media.control.BufferControl;
import javax.media.protocol.DataSource;
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

public class RtpPlayer extends Thread implements ReceiveStreamListener,
        SessionListener, ControllerListener {
    final RTPManager rtpManager;

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
        try {
            SessionAddress clientAddress = new SessionAddress();
            SessionAddress serverAddress;
            rtpManager.addSessionListener(this);
            rtpManager.addReceiveStreamListener(this);
            System.out.println("Added receive stream");
            InetAddress ipAddr = InetAddress.getLocalHost();
            clientAddress = new SessionAddress(InetAddress.getLocalHost(),
                    serverPort);
            serverAddress = new SessionAddress(ipAddr, serverPort);
            System.out.println("Session Addresses created");
            rtpManager.initialize(clientAddress);
            BufferControl buffer = (BufferControl) rtpManager
                    .getControl("javax.media.control.BufferControl");
            if (buffer != null) {
                buffer.setBufferLength(400);
            }
            rtpManager.addTarget(serverAddress);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Wait for data to arrive, from AVReceive

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

    public static void main(String args[]) {
        int i = 1;
        RtpPlayer player = new RtpPlayer(4242);
        player.start();

        try {
            while (!player.finished()) {
                System.out.println("- Not done yet - time " + i + " seconds");
                i++;
                Thread.sleep(1000);
            }
        } catch (Exception e) {
        }
    }

}
