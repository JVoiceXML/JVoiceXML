/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager.jtapi;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.callmanager.CallManager;
import org.jvoicexml.callmanager.ConfiguredApplication;
import org.jvoicexml.callmanager.RemoteClientCreationException;
import org.jvoicexml.callmanager.RemoteClientFactory;

/**
 * A factory for the {@link JtapiRemoteClient}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class JtapiRemoteClientFactory implements RemoteClientFactory {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(JtapiRemoteClientFactory.class);

    /** The name of the terminal parameter. */
    public static final String TERMINAL = "terminal";

    /** The name of the called id parameter. */
    public static final String CALLED_ID = "calledId";

    /** The name of the calling id parameter. */
    public static final String CALLING_ID = "callingId";

    /**
     * {@inheritDoc}
     */
    public RemoteClient createRemoteClient(final CallManager callManager,
            final ConfiguredApplication application,
            final Map<String, Object> parameters)
        throws RemoteClientCreationException {
        final JVoiceXmlTerminal term =
            (JVoiceXmlTerminal) parameters.get(TERMINAL);
        final String output = application.getOutputType();
        final String input = application.getInputType();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating remote client with output '" + output
                    + "' and input '" + input + "' for terminal '"
                    + term.getTerminalName() + "'");
        }
        try {
            JtapiRemoteClient client =
                new JtapiRemoteClient(term, output, input);
            final URI calledId = (URI) parameters.get(CALLED_ID);
            client.setCalledDevice(calledId);
            final URI callingId = (URI) parameters.get(CALLING_ID);
            client.setCallingDevice(callingId);
            return client;
        } catch (UnknownHostException e) {
            throw new RemoteClientCreationException(e.getMessage(), e);
        }
    }

}
