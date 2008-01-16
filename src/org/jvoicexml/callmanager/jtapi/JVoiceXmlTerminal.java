/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
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

package org.jvoicexml.callmanager.jtapi;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Map;

import javax.telephony.Address;
import javax.telephony.CallEvent;
import javax.telephony.CallListener;
import javax.telephony.Connection;
import javax.telephony.ConnectionEvent;
import javax.telephony.ConnectionListener;
import javax.telephony.InvalidStateException;
import javax.telephony.MetaEvent;
import javax.telephony.MethodNotSupportedException;
import javax.telephony.PrivilegeViolationException;
import javax.telephony.ResourceUnavailableException;
import javax.telephony.Terminal;
import javax.telephony.TerminalConnection;
import javax.telephony.callcontrol.CallControlCall;

import net.sourceforge.gjtapi.media.GenericMediaService;
import org.apache.log4j.Logger;
import org.jvoicexml.Session;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;

/**
 * A connection to a JTAPI terminal.
 *
 *@author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.6
 */
public final class JVoiceXmlTerminal
        implements ConnectionListener {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(JVoiceXmlTerminal.class);

    /** Reference to the call manager. */
    private final JtapiCallManager callManager;

    /** Media service to stream the audio. */
    private final GenericMediaService mediaService;

    /** Port for the RTP source. */
    private final int port;

    /** Name of the terminal. */
    private final String terminalName;

    /** established telephony connection. */
    private Connection connection = null;

    /** A related JVoiceXML session. */
    private Session session;

    /** Input type that should be used */
    private String inputType;

    /** Output type that should be used */
    private String outputType;

    /** Class that will play audio */
    private final TerminalPlayer terminalPlayer;

    /** Class that will record audio */
    private final TerminalRecorder terminalRecorder;

    /**
     * Constructs a new object.
     *
     * @param cm
     *            the call manager.
     * @param service
     *            GenericMediaService
     * @param rtpPort
     *            RTP port.
     */
    public JVoiceXmlTerminal(final JtapiCallManager cm,
            final GenericMediaService service, final int rtpPort,
            final String outputType, final String inputType) {
        callManager = cm;
        mediaService = service;
        port = rtpPort;
        this.inputType = inputType;
        this.outputType = outputType;
        terminalPlayer = new TerminalPlayer(mediaService);
        terminalRecorder = new TerminalRecorder(mediaService);
        terminalPlayer.start();
        terminalRecorder.start();

        // Adds a listener to a Call object when this Address object first
        // becomes part of that Call.
        final Terminal terminal = mediaService.getTerminal();
        final Address[] addrs = terminal.getAddresses();
        terminalName = terminal.getName();

        final CallListener[] listener = terminal.getCallListeners();
        try {
            // validate if the terminal already has a listener.
            if (listener == null) {
                for (int i = 0; i < addrs.length; i++) {
                    // Search the address that corresponds to this terminal.
                    if (terminalName.equals(addrs[i].getName())) {
                        addrs[i].addCallListener(this); // add a call Listener
                        // addrs[i].addCallObserver(this); // add a call
                        // Observer
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("added a listener to terminal "
                                    + terminalName);
                        }
                    }
                }
            }
        } catch (MethodNotSupportedException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (ResourceUnavailableException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connectionAlerting(final ConnectionEvent event) {
        final CallControlCall call = (CallControlCall) event.getCall();
        final Address address = call.getCallingAddress();
        if (address == null) {
            LOGGER.info("connection alerting " + call);
        } else {
            final String caller = address.getName();
            LOGGER.info("connection alerting from " + caller);
        }

        connection = event.getConnection();
        final TerminalConnection[] connections = connection
                .getTerminalConnections();
        try {
            if (connections.length > 0) {
                connections[0].answer();
            }
        } catch (PrivilegeViolationException e) {
            LOGGER.error("error answering call", e);
            connection = null;
        } catch (ResourceUnavailableException e) {
            LOGGER.error("error answering call", e);
            connection = null;
        } catch (MethodNotSupportedException e) {
            LOGGER.error("error answering call", e);
            connection = null;
        } catch (InvalidStateException e) {
            LOGGER.error("error answering call", e);
            connection = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connectionConnected(final ConnectionEvent event) {
        final CallControlCall call = (CallControlCall) event.getCall();
        final Address callingAddress = call.getCallingAddress();
        final Address calledAddress = call.getCalledAddress();
        LOGGER.info("call connected from " + callingAddress.getName()
                + " to " + calledAddress.getName());

        // fireAnswerEvent();

        // establishes a connection to JVoiceXML
        JtapiRemoteClient remote;
        try {
            remote = new JtapiRemoteClient(this, outputType, inputType,
                    port);
        } catch (UnknownHostException e) {
            LOGGER.error("error creating a session", e);
            try {
                connection.disconnect();
            } catch (PrivilegeViolationException ex) {
                LOGGER.error("error in disconnect", ex);
            } catch (ResourceUnavailableException ex) {
                LOGGER.error("error in disconnect", ex);
            } catch (MethodNotSupportedException ex) {
                LOGGER.error("error in disconnect", ex);
            } catch (InvalidStateException ex) {
                LOGGER.error("error in disconnect", ex);
            }
            return;
        }
        try {
            session = callManager.createSession(remote);
        } catch (ErrorEvent e) {
            LOGGER.error("error creating a session", e);
            try {
                connection.disconnect();
            } catch (PrivilegeViolationException ex) {
                LOGGER.error("error in disconnect", ex);
            } catch (ResourceUnavailableException ex) {
                LOGGER.error("error in disconnect", ex);
            } catch (MethodNotSupportedException ex) {
                LOGGER.error("error in disconnect", ex);
            } catch (InvalidStateException ex) {
                LOGGER.error("error in disconnect", ex);
            }
            return;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connectionCreated(final ConnectionEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void connectionDisconnected(final ConnectionEvent event) {
        /**
         * @todo doesn't entry when HangUp- fix this problem
         */
        // stopPlay();
        // firehangedUpEvent();
        try {
            if (connection != null) {
                LOGGER.info("disconnecting the connection");
                connection.disconnect();
            }
        } catch (InvalidStateException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (MethodNotSupportedException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (ResourceUnavailableException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (PrivilegeViolationException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        if (session != null) {
            session.close();
            session = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connectionFailed(final ConnectionEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void connectionInProgress(final ConnectionEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void connectionUnknown(final ConnectionEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void callActive(final CallEvent arg0) {
    }

    /**
     * {@inheritDoc}
     */
    public void callEventTransmissionEnded(final CallEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void callInvalid(final CallEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void multiCallMetaMergeEnded(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void multiCallMetaMergeStarted(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void multiCallMetaTransferEnded(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void multiCallMetaTransferStarted(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void singleCallMetaProgressEnded(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void singleCallMetaProgressStarted(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void singleCallMetaSnapshotEnded(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void singleCallMetaSnapshotStarted(final MetaEvent event) {
    }

    /**
     * Retrieves the terminal name.
     *
     * @return name of the terminal.
     */
    public String getTerminalName() {
        return terminalName;
    }

    /**
     * Plays a stream from the given URI.
     *
     * @param uri
     *            URI with audio data.
     * @exception NoresourceError
     *                Error accessing the terminal
     * @exception IOException
     *                Error accessing the given URI.
     */
    public void play(final URI uri, Map<String, String> parameters) throws NoresourceError, IOException {
        terminalPlayer.startProcessing();
        terminalPlayer.processURI(uri, parameters);
    }

    /**
     * Starts recording to the given URI.
     *
     * @param uri
     *            destination URI for recording.
     * @exception NoresourceError
     *                Error accessing the terminal
     * @exception IOException
     *                Error accessing the given URI.
     * @since 0.6
     */
    public void record(final URI uri, Map<String, String> parameters) throws NoresourceError, IOException {
        terminalRecorder.startProcessing();
        terminalRecorder.processURI(uri, parameters);
    }

    /**
     * Stops a previously started recording.
     *
     * @exception NoresourceError
     *                Error accessing the terminal
     * @since 0.6
     */
    public void stopRecord() throws NoresourceError {
        terminalRecorder.stopProcessing();
    }

    /**
     * Stops a previously started play
     *
     * @exception NoresourceError
     *                Error accessing the terminal
     *
     * @since 0.6
     */
    public void stopPlay()  throws NoresourceError {
        terminalPlayer.stopProcessing();
    }
}
