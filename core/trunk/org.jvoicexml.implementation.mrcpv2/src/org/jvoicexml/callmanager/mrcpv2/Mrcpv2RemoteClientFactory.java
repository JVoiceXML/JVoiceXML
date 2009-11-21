/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
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
package org.jvoicexml.callmanager.mrcpv2;

import org.jvoicexml.RemoteClient;
import org.jvoicexml.callmanager.CallManager;
import org.jvoicexml.callmanager.CallParameters;
import org.jvoicexml.callmanager.ConfiguredApplication;
import org.jvoicexml.callmanager.RemoteClientCreationException;
import org.jvoicexml.callmanager.RemoteClientFactory;

/**
 * A factory for MRCPv2 remote clients.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.3
 */
public final class Mrcpv2RemoteClientFactory implements RemoteClientFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteClient createRemoteClient(final CallManager callManager,
            final ConfiguredApplication application,
            final CallParameters parameters)
            throws RemoteClientCreationException {
        // TODO create the remote client
        // Parse the parameters and initialize the client object with it
        return null;
    }

}
