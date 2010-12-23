/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.variables;

import java.net.URI;

import org.jvoicexml.interpreter.ScriptingEngine;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * Component that provides a container for the shadowed variables for the
 * standard session variables.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class SessionShadowVarContainer
        extends ScriptableObject
        implements StandardSessionVariable {
    /** The serial version UID. */
    private static final long serialVersionUID = 8864292176288005582L;

    /** Name of the document variable. */
    public static final String VARIABLE_NAME = "session";

    /** Reference to the scripting engine. */
    private ScriptingEngine scripting;

    /** The connection attribute. */
    private ConnectionVarContainer connection;

    /**
     * Constructs a new objects.
     */
    public SessionShadowVarContainer() {
        defineProperty("connection", SessionShadowVarContainer.class,
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
        return SessionShadowVarContainer.class.getSimpleName();
    }

    /**
     * {@inheritDoc}
     *
     * Retrieves a variable of session scope.
     */
    @Override
    public Object get(final String name, final Scriptable start) {
        if (has(name, start)) {
            return super.get(name, start);
        }
        return scripting.getVariable(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(final String name, final Scriptable start,
            final Object value) {
        if (scripting == null || has(name, start)) {
            super.put(name, start, value);
        } else {
            scripting.setVariable(name, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setScripting(final ScriptingEngine engine) {
        scripting = engine;
    }

    /**
     * Sets the remote caller device.
     * @param uri URI of the remote caller device.
     */
    public void setRemoteCallerDevice(final URI uri) {
        if (connection == null) {
            connection = new ConnectionVarContainer();
        }
        connection.setRemoteCallerDevice(uri);
    }

    /**
     * Sets the local caller device.
     * @param uri URI of the local caller device.
     */
    public void setLocalCallerDevice(final URI uri) {
        if (connection == null) {
            connection = new ConnectionVarContainer();
        }
        connection.setLocalCallerDevice(uri);
    }

    /**
     * Retrieves the connection.
     * @return the connection
     */
    public ConnectionVarContainer getConnection() {
        return connection;
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
        if (connection == null) {
            connection = new ConnectionVarContainer();
        }
        connection.protocol(name, version);
    }
}
