/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2021 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.datamodel;

import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.event.EventSubscriber;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;

/**
 * A variable container to hold the connection information.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7
 */
public final class Connection implements EventSubscriber {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager.getLogger(Connection.class);

    /** The remote connection info. */
    private ConnectionRemote remote;

    /** The local connection info. */
    private ConnectionLocal local;

    /** The protocol info. */
    private final ConnectionProtocol protocol;

    /** {@code true} if the user did not hung up. */
    private boolean hangup;
    
    /** The encapsulated {@link ConnectionInformation} object. */
    private final ConnectionInformation info;
    
    /**
     * Constructs a new object.
     * 
     * @param connectionInfo
     *            the connection information
     */
    public Connection(final ConnectionInformation connectionInfo) {
        final String protocolName = connectionInfo.getProtocolName();
        final String protocolVersion = connectionInfo.getProtocolVersion();
        protocol = new ConnectionProtocol(protocolName, protocolVersion);
        final URI remoteUri = connectionInfo.getCallingDevice();
        remote = new ConnectionRemote(remoteUri);
        final URI localUri = connectionInfo.getCalledDevice();
        local = new ConnectionLocal(localUri);
        info = connectionInfo;
    }

    /**
     * Sets the remote caller device.
     * 
     * @param uri
     *            URI of the remote caller device.
     */
    public void setRemoteCallerDevice(final URI uri) {
        remote = new ConnectionRemote(uri);
    }

    /**
     * Retrieves the remote attribute.
     * 
     * @return the remote attribute.
     */
    public ConnectionRemote getRemote() {
        return remote;
    }

    /**
     * Sets the remote caller device.
     * 
     * @param uri
     *            URI of the remote caller device.
     */
    public void setLocalCallerDevice(final URI uri) {
        local = new ConnectionLocal(uri);
    }

    /**
     * Retrieves the local attribute.
     * 
     * @return the local attribute.
     */
    public ConnectionLocal getLocal() {
        return local;
    }

    /**
     * Retrieves the protocol attribute.
     * 
     * @return the protocol attribute.
     */
    public ConnectionProtocol getProtocol() {
        return protocol;
    }

    /**
     * Checks if the connection is active, i.e. the user did not hang up.
     * @return {@code true} if the user did hang up
     * @since 0.7.9
     */
    public boolean getHangup() {
        return hangup;
    }
    
    /**
     * Retrieves the connection information object.
     * @return the connection information object
     * @since 0.7.9
     */
    public ConnectionInformation getInfo() {
        return info;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEvent(JVoiceXMLEvent event) {
        if (!event.getEventType().equals(ConnectionDisconnectHangupEvent.EVENT_TYPE)) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("setting hangup in scripting environment to true");
        }
        hangup = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Connection [remote=" + remote + ", local=" + local
                + ", protocol=" + protocol + ", hangup=" + hangup + ", info="
                + info + "]";
    }
}
