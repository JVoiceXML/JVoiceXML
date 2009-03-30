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

package org.jvoicexml.callmanager;

import java.util.Map;

import org.jvoicexml.RemoteClient;

/**
 * Some {@link CallManager}s will require to use a custom implementation of a
 * {@link RemoteClient}. This factory allows to create those custom
 * implementations.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public interface RemoteClientFactory {
    /**
     * Factory method to retrieve a new {@link RemoteClient}.
     * @param callManager the calling call manager instance
     * @param application the called configured application.
     * @param parameters additional optional parameters.
     * @return created remote client.
     * @exception RemoteClientCreationException
     *            error creating the remote client
     */
    RemoteClient createRemoteClient(final CallManager callManager,
            final ConfiguredApplication application,
            final Map<String, Object> parameters)
        throws RemoteClientCreationException;
}
