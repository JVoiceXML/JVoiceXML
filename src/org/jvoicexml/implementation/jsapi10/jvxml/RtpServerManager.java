/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10.jvxml;

import java.io.IOException;
import java.util.Map;

import javax.media.MediaException;
import javax.media.rtp.SessionManagerException;

import org.jvoicexml.client.rtp.RtpConfiguration;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * Manages multiple RTP server resources.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
final class RtpServerManager {
    /** Logger for this class. */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(RtpServerManager.class);

    /** All known servers. */
    private static final Map<RtpConfiguration, RtpServer> SERVERS;

    static {
        SERVERS = new java.util.HashMap<RtpConfiguration, RtpServer>();
    }

    /**
     * Do not create from outside.
     */
    private RtpServerManager() {

    }

    /**
     * Retrieves a server instance for the given client.
     *
     * @param client
     *            the client.
     * @return RTP server instance.
     * @throws IOException
     *             Error creating the server.
     * @throws SessionManagerException
     *             Error creating the server.
     * @throws MediaException
     *             Error creating the server.
     */
    public static synchronized RtpServer getServer(
            final RtpConfiguration client)
            throws IOException, SessionManagerException, MediaException {
        RtpServer server = SERVERS.get(client);
        if (server != null) {
            return server;
        }

        server = new RtpServer();
        SERVERS.put(client, server);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("created new RTP server for client "
                    + client.getAddress() + ":" + client.getPort());
        }
        return server;
    }

    /**
     * Removes the RTP server for the given client connection.
     *
     * @param client
     *            the client.
     * @return removed server instance.
     */
    public static synchronized RtpServer removeServer(
            final RtpConfiguration client) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("removing RTP server for client "
                    + client.getAddress() + ":" + client.getPort());
        }
        return SERVERS.remove(client);
    }
}
