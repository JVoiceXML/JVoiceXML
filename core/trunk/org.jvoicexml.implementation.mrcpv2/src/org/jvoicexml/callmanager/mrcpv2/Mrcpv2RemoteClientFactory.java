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

import javax.sdp.SdpException;
import javax.sip.SipException;

import org.jvoicexml.CallManager;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.callmanager.CallParameters;
import org.jvoicexml.callmanager.ConfiguredApplication;
import org.jvoicexml.callmanager.RemoteClientCreationException;
import org.jvoicexml.callmanager.RemoteClientFactory;
import org.jvoicexml.client.mrcpv2.Mrcpv2RemoteClient;
import org.speechforge.cairo.client.SessionManager;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.client.SpeechClientImpl;
import org.speechforge.cairo.sip.SipSession;

/**
 * A factory for MRCPv2 remote clients.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.3
 */
public final class Mrcpv2RemoteClientFactory implements RemoteClientFactory {
    /** The session manager. */
    private SessionManager sessionManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteClient createRemoteClient(final CallManager callManager,
            final ConfiguredApplication application,
            final CallParameters parameters)
            throws RemoteClientCreationException {
        // TODO check the parameters
        final SipCallParameters sipparams = (SipCallParameters) parameters;
        final Mrcpv2RemoteClient client = new Mrcpv2RemoteClient();
        final int clientPort = sipparams.getClientPort();
        final String clientAddress = sipparams.getClientAddress();
        final SipSession session;
        try {
            session = sessionManager.newRecogChannel(clientPort, clientAddress,
                "Session Name");
        } catch (SdpException e) {
            throw new RemoteClientCreationException(e.getMessage(), e);
        } catch (SipException e) {
            throw new RemoteClientCreationException(e.getMessage(), e);
        }
        final SpeechClient ttsClient = 
            new SpeechClientImpl(null, session.getRecogChannel());
        client.setTtsClient(ttsClient);
        final SpeechClient asrClient =
            new SpeechClientImpl(session.getRecogChannel(), null);
        client.setAsrClient(asrClient);
        return client;
    }

    /**
     * Sets the session manager.
     * @param manager the session manager
     * @since 0.7.3
     */
    public void setSessionManager(final SessionManager manager) {
        sessionManager = manager;
    }
}
