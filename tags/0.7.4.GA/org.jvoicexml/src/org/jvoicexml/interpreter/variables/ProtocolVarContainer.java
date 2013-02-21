/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.mozilla.javascript.ScriptableObject;

/**
 * A variable container to hold the protocol information.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
@SuppressWarnings("serial")
public final class ProtocolVarContainer extends ScriptableObject {
    /** Name of the connection protocol. */
    private final String name;

    /** Version of the connection protocol. */
    private final String version;

    /**
     * Constructs a new object.
     * @param protocolName name of the protocol.
     * @param protocolVersion version of the protocol.
     */
    public ProtocolVarContainer(final String protocolName,
            final String protocolVersion) {
        defineProperty("name", ProtocolVarContainer.class,
                READONLY);
        defineProperty("version", ProtocolVarContainer.class,
                READONLY);
        name = protocolName;
        version = protocolVersion;
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
        return ProtocolVarContainer.class.getSimpleName();
    }

    /**
     * Retrieves the name attribute.
     * @return the name attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the version attribute.
     * @return the version attribute.
     */
    public String getVersion() {
        return version;
    }
}
