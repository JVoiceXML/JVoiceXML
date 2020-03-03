/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

import java.io.IOException;

/**
 * Objects that implement this interface can be connected to a client using the
 * information given in {@link ConnectionInformation}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision:161 $
 * @since 0.5.5
 */
public interface RemoteConnectable {
    /**
     * Establishes a connection from the given {@link ConnectionInformation} to
     * this object.
     * 
     * @param client
     *            data container with connection relevant data.
     * @throws IOException
     *             error establishing the connection.
     */
    void connect(final ConnectionInformation client)
            throws IOException;

    /**
     * Closes a previously established connection.
     * 
     * @param client
     *            data container with connection relevant data.
     * @since 0.6
     */
    void disconnect(final ConnectionInformation client);
}
