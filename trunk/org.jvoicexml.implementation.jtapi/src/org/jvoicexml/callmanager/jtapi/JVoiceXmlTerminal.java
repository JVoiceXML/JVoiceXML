/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/org.jvoicexml/src/org/jvoicexml/callmanager/jtapi/JVoiceXmlTerminal.java $
 * Version: $LastChangedRevision: 888 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.telephony.Address;
import javax.telephony.CallEvent;
import javax.telephony.Connection;
import javax.telephony.ConnectionEvent;
import javax.telephony.ConnectionListener;
import javax.telephony.InvalidArgumentException;
import javax.telephony.InvalidStateException;
import javax.telephony.MetaEvent;
import javax.telephony.MethodNotSupportedException;
import javax.telephony.PrivilegeViolationException;
import javax.telephony.Provider;
import javax.telephony.ResourceUnavailableException;
import javax.telephony.Terminal;
import javax.telephony.TerminalConnection;
import javax.telephony.callcontrol.CallControlCall;

import net.sourceforge.gjtapi.media.GenericMediaService;

import org.apache.log4j.Logger;
import org.jvoicexml.Session;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ObservableTelephony;
import org.jvoicexml.implementation.TelephonyEvent;
import org.jvoicexml.implementation.TelephonyListener;

/**
 * A connection to a JTAPI terminal.
 *
 * @author Dirk Schnelle-Walka
 * @author Renato Cassaca
 * @version $Revision: 888 $
 * @since 0.6
 */
public final class JVoiceXmlTerminal
    implements ConnectionListener, ObservableTelephony {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
                                         .getLogger(JVoiceXmlTerminal.class);

    /** Reference to the call manager. */
    private final JtapiCallManager callManager;

    /** Media service to stream the audio. */
    private final GenericMediaService mediaService;

    /** Name of the terminal. */
    private final String terminalName;

    /** established telephony connection. */
    private Connection connection;

    /** A related JVoiceXML session. */
    private Session session;

    /** Object that will play audio .*/
    private final TerminalPlayer terminalPlayer;

    /** Object that will record audio. */
    private final TerminalRecorder terminalRecorder;

    /** Currently established call. */
    private CallControlCall currentCall;

    /** CallControl Listeners. */
    private List<TelephonyListener> callControlListeners;

    /**
     * Constructs a new object.
     *
     * @param cm
     *            the call manager.
     * @param service
     *            GenericMediaService
     */
    public JVoiceXmlTerminal(final JtapiCallManager cm,
                             final GenericMediaService service) {
        callManager = cm;
        mediaService = service;
        currentCall = null;
        callControlListeners = new ArrayList<TelephonyListener>();
        terminalPlayer = new TerminalPlayer(this, mediaService);
        terminalRecorder = new TerminalRecorder(this, mediaService);
        terminalPlayer.start();
        terminalRecorder.start();

        // Adds a listener to a Call object when this Address object first
        // becomes part of that Call.
        final Terminal terminal = mediaService.getTerminal();
        terminalName = terminal.getName();
        final Provider provider = terminal.getProvider();

        try {
            final Address address = provider.getAddress(terminalName);
            address.addCallListener(this); // add a call Listener
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("added a listener to terminal "
                        + terminalName);
            }
        } catch (MethodNotSupportedException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (ResourceUnavailableException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (InvalidArgumentException ex) {
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
        final TerminalConnection[] connections =
            connection.getTerminalConnections();
        try {
            if (connections.length > 0) {
                connections[0].answer();

                currentCall = call;
            } else {
                LOGGER.warn("no connection: cannot answer call");
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

        final TelephonyEvent telephonyEvent = new TelephonyEvent(this,
                TelephonyEvent.ANSWERED);
        fireCallAnsweredEvent(telephonyEvent);

        try {
            final Map<String, Object> parameters =
                new java.util.HashMap<String, Object>();
            final URI calledId = getUriFromAddress(calledAddress);
            parameters.put(JtapiRemoteClientFactory.CALLED_ID, calledId);
            final URI callingId = getUriFromAddress(callingAddress);
            parameters.put(JtapiRemoteClientFactory.CALLING_ID, callingId);
            session = callManager.createSession(this, parameters);
        } catch (ErrorEvent e) {
            LOGGER.error("error creating a session", e);
            currentCall = call;
            disconnect();
            return;
        }

        currentCall = call;
    }

    /**
     * Creates a URI representation for the given address.
     * @param address the address
     * @return URI representation of the address
     * @since 0.7
     */
    private URI getUriFromAddress(final Address address) {
        final String name = address.getName();
        if (name == null) {
            return null;
        }
        try {
            return new URI(name);
        } catch (URISyntaxException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("unable to create a URI from '" + name + "'", e);
            }
            return null;
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
        LOGGER.info("connection disconnected");
        try {
            final TelephonyEvent telephonyEvent = new TelephonyEvent(this,
                    TelephonyEvent.HUNGUP);
            fireCallHungup(telephonyEvent);

            currentCall = null;
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
            session.hangup();
            session = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connectionFailed(final ConnectionEvent event) {
        currentCall = null;
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
        LOGGER.info("received unknown event: " + event);
    }

    /**
     * {@inheritDoc}
     */
    public void callActive(final CallEvent arg) {
    }

    /**
     * {@inheritDoc}
     */
    public void callEventTransmissionEnded(final CallEvent event) {
        currentCall = null;
    }

    /**
     * {@inheritDoc}
     */
    public void callInvalid(final CallEvent event) {
        currentCall = null;
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
     * @param parameters
     *            configuration parameters.
     * @exception NoresourceError
     *                Error accessing the terminal
     * @exception IOException
     *                Error accessing the given URI.
     */
    public void play(final URI uri, final Map<String, String> parameters) throws
            NoresourceError, IOException {
        terminalPlayer.startProcessing();
        terminalPlayer.processURI(uri, parameters);
    }

    /**
     * Starts recording to the given URI.
     *
     * @param uri
     *            destination URI for recording.
     * @param parameters
     *            configuration parameters.
     * @exception NoresourceError
     *                Error accessing the terminal
     * @exception IOException
     *                Error accessing the given URI.
     * @since 0.6
     */
    public void record(final URI uri,
            final Map<String, String> parameters) throws
            NoresourceError, IOException {
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
     * Stops a previously started play.
     *
     * @exception NoresourceError
     *                Error accessing the terminal
     *
     * @since 0.6
     */
    public void stopPlay() throws NoresourceError {
        terminalPlayer.stopProcessing();
    }


    /**
     *
     * @param dest String
     * @throws NoresourceError
     *         error accessing the call control
     *
     * @todo What events are raised during call.transfer()?
     *       Have to interrupt call transfer if it's taking
     *       more than a requested "max connect time"
     *       <code>&lt;transfer connecttimeout="X"&gt;</code>
     * @todo Have to have a way to give back specific connection errors
     */
    public void transfer(final String dest) throws NoresourceError {
        if (currentCall == null) {
            throw new NoresourceError("No valid ongoing CallControlCall!");
        }

        //Get connections in current ongoing call
        Connection[] cons = currentCall.getConnections();
        Address toAddr = currentCall.getCallingAddress();
        if (toAddr == null) {
            throw new NoresourceError("Cannot find calling address...");
        }

        try {
            for (int i = 0; i < cons.length; i++) {
                // find the remote connection
                if (cons[i].getAddress().getName().equals(toAddr)) {
                    Connection conn = cons[i];

                    // See if it has any terminal connections
                    TerminalConnection[] tc = conn.getTerminalConnections();

                    if (tc != null && tc.length > 0) {
                        TerminalConnection termConn = tc[0];

                        currentCall.setTransferController(termConn);
                        currentCall.transfer(dest);
                    } else {
                        LOGGER.info("Failed to find any TerminalConnections");
                    }
                }
            }
        } catch (Exception e) {
            throw new NoresourceError(e);
        }
    }

    /**
     * Disconnects the connection.
     * @since 0.7
     */
    public synchronized void disconnect() {
        if (currentCall != null) {
            try {
                currentCall.drop();
                LOGGER.info("disconnected call");
            } catch (PrivilegeViolationException ex) {
                LOGGER.error("error in disconnect", ex);
            } catch (ResourceUnavailableException ex) {
                LOGGER.error("error in disconnect", ex);
            } catch (MethodNotSupportedException ex) {
                LOGGER.error("error in disconnect", ex);
            } catch (InvalidStateException ex) {
                LOGGER.error("error in disconnect", ex);
            }
            currentCall = null;
        }
    }

    /**
     * Checks if this terminal is busy.
     * @return <code>true</code> if this terminal is busy.
     */
    public boolean isBusy() {
        return terminalRecorder.isBusy() | terminalPlayer.isBusy();
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(final TelephonyListener
                                       callControlListener) {
        synchronized (callControlListeners) {
            callControlListeners.add(callControlListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(final TelephonyListener
                                          callControlListener) {
        synchronized (callControlListeners) {
            callControlListeners.remove(callControlListener);
        }
    }

    /**
     * Notifies all listeners about the given media event.
     * @param event the event to publish.
     */
    private void fireCallAnsweredEvent(final TelephonyEvent event) {
        synchronized (callControlListeners) {
            final Collection<TelephonyListener> copy =
                new java.util.ArrayList<TelephonyListener>();
            copy.addAll(callControlListeners);
            for (TelephonyListener current : copy) {
                current.telephonyCallAnswered(event);
            }
        }
    }

    /**
     * Notifies all listeners about the given media event.
     * @param event the event to publish.
     */
    void fireMediaEvent(final TelephonyEvent event) {
        synchronized (callControlListeners) {
            final Collection<TelephonyListener> copy =
                new java.util.ArrayList<TelephonyListener>();
            copy.addAll(callControlListeners);
            for (TelephonyListener current : copy) {
                current.telephonyMediaEvent(event);
            }
        }
    }

    /**
     * Notifies all listeners about the given media event.
     * @param event the event to publish.
     */
    private void fireCallHungup(final TelephonyEvent event) {
        synchronized (callControlListeners) {
            final Collection<TelephonyListener> copy =
                new java.util.ArrayList<TelephonyListener>();
            copy.addAll(callControlListeners);
            for (TelephonyListener current : copy) {
                current.telephonyCallHungup(event);
            }
        }
    }
}
