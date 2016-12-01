/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/variables/ConnectionVarContainer.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $Date: 2013-12-17 09:46:17 +0100 (Tue, 17 Dec 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.ConnectionInformation;

/**
 * A variable container to hold the connection information.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4080 $
 * @since 0.7
 */
public final class Connection {
    /** The remote connection info. */
    private ConnectionRemote remote;

    /** The local connection info. */
    private ConnectionLocal local;

    /** The protocol info. */
    private final ConnectionProtocol protocol;

    /**
     * Constructs a new object.
     * 
     * @param info
     *            the connection information
     */
    public Connection(final ConnectionInformation info) {
        final String protocolName = info.getProtocolName();
        final String protocolVersion = info.getProtocolVersion();
        protocol = new ConnectionProtocol(protocolName, protocolVersion);
        final URI remoteUri = info.getCallingDevice();
        remote = new ConnectionRemote(remoteUri);
        final URI localUri = info.getCalledDevice();
        local = new ConnectionLocal(localUri);
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
}
