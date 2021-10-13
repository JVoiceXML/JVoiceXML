/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/variables/ProtocolVarContainer.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $Date: 2013-12-17 09:46:17 +0100 (Tue, 17 Dec 2013) $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.interpreter.datamodel;

/**
 * A standard variable container to hold the protocol information.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4080 $
 * @since 0.7.7
 */
public final class ConnectionProtocol {
    /** Name of the connection protocol. */
    private final String name;

    /** Version of the connection protocol. */
    private final String version;

    /**
     * Constructs a new object.
     * 
     * @param protocolName
     *            name of the protocol.
     * @param protocolVersion
     *            version of the protocol.
     */
    public ConnectionProtocol(final String protocolName,
            final String protocolVersion) {
        name = protocolName;
        version = protocolVersion;
    }

    /**
     * Retrieves the name attribute.
     * 
     * @return the name attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the version attribute.
     * 
     * @return the version attribute.
     */
    public String getVersion() {
        return version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ConnectionProtocol [name=" + name + ", version=" + version
                + "]";
    }
}
