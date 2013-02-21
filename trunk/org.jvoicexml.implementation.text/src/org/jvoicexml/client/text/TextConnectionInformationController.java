/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.client.text;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.client.ConnectionInformationController;

/**
 * A {@link ConnectionInformationController} for the text platform.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
class TextConnectionInformationController
        implements ConnectionInformationController {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(TextConnectionInformationController.class);

        /** Default port for the text server. */ 
    private static final int DEFAULT_SERVER_PORT = 4242;

    /** The text server. */
    private final TextServer server;

    /**
     * Constructs a new object.
     */
    public TextConnectionInformationController() {
        server = new TextServer(DEFAULT_SERVER_PORT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionInformation getConnectionInformation() {
        if (!server.isStarted()) {
            server.start();
        }
        try {
            return server.getConnectionInformation();
        } catch (UnknownHostException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        if (server.isStarted()) {
            server.stopServer();
        }
    }
}
