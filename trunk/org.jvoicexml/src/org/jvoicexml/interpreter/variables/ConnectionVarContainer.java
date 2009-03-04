/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

package org.jvoicexml.interpreter.variables;

import java.net.URI;

import org.mozilla.javascript.ScriptableObject;

/**
 * A variable container to hold the connection information.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
@SuppressWarnings("serial")
public final class ConnectionVarContainer extends ScriptableObject {
    /** The remote connection info. */
    private ConnectionRemoteVarContainer remoteConnection;

    /** The local connection info. */
    private ConnectionLocalVarContainer localConnection;

    /** The protocol info. */
    private ProtocolVarContainer protocol;

    /**
     * Constructs a new object.
     */
    public ConnectionVarContainer() {
        defineProperty("remote", ConnectionVarContainer.class,
                READONLY);
        defineProperty("local", ConnectionVarContainer.class,
                READONLY);
        defineProperty("protocol", ConnectionVarContainer.class,
                READONLY);
    }

    /**
     * This method is a callback for rhino which gets called on instantiation.
     * (virtual js constructor)
     */
    public void jsContructor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassName() {
        return ConnectionVarContainer.class.getSimpleName();
    }

    /**
     * Sets the remote caller device.
     * @param uri URI of the remote caller device.
     */
    public void setRemoteCallerDevice(final URI uri) {
        remoteConnection = new ConnectionRemoteVarContainer(uri);
    }

    /**
     * Retrieves the remote attribute.
     * @return the remote attribute.
     */
    public ConnectionRemoteVarContainer getRemote() {
        return remoteConnection;
    }

    /**
     * Sets the remote caller device.
     * @param uri URI of the remote caller device.
     */
    public void setLocalCallerDevice(final URI uri) {
        localConnection = new ConnectionLocalVarContainer(uri);
    }

    /**
     * Retrieves the local attribute.
     * @return the local attribute.
     */
    public ConnectionLocalVarContainer getLocal() {
        return localConnection;
    }

    /**
     * Sets the protocol information.
     * <p>
     * This method must not be called <code>setProtocol</code> due to naming
     * restrictions of the javascript API.
     * </p>
     * @param name name of the protocol.
     * @param version version of the protocol.
     */
    public void protocol(final String name, final String version) {
        protocol = new ProtocolVarContainer(name, version);
    }

    /**
     * Retrieves the protocol attribute.
     * @return the protocol attribute.
     */
    public ProtocolVarContainer getProtocol() {
        return protocol;
    }
}
