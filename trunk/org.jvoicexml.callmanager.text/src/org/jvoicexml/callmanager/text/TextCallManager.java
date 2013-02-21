/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.implementation.text/src/org/jvoicexml/callmanager/text/TextCallManager.java $
 * Version: $LastChangedRevision: 2980 $
 * Date:    $Date: 2012-02-13 09:37:27 +0100 (Mo, 13 Feb 2012) $
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

package org.jvoicexml.callmanager.text;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.CallManager;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.event.error.NoresourceError;

/**
 * A {@link org.jvoicexml.CallManager} for text based clients.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2980 $
 * @since 0.7
 */
public final class TextCallManager implements CallManager {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(TextCallManager.class);

    /** Know applications. */
    private final Collection<TextApplication> applications;

    /** Reference to JVoiceXML. */
    private JVoiceXml jvxml;

    /** The server thread waiting for incoming connections. */
    private final Collection<TextServerThread> servers;

    /**
     * Constructs a new object.
     */
    public TextCallManager() {
        applications = new java.util.ArrayList<TextApplication>();
        servers = new java.util.ArrayList<TextServerThread>();
    }

    /**
     * Adds the given list of applications.
     *
     * @param apps
     *            list of application
     */
    public void setApplications(
            final Collection<TextApplication> apps) {
        for (TextApplication application : apps) {
            applications.add(application);
            LOGGER.info("added application '" + application.getUri() + "'");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setJVoiceXml(final JVoiceXml jvoicexml) {
        jvxml = jvoicexml;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws NoresourceError, IOException {
        for (TextApplication application : applications) {
            final int port = application.getPort();
            final URI uri = application.getUriObject();
            final TextServerThread server =
                    new TextServerThread(port, uri, jvxml);
            server.start();
            servers.add(server);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        for (TextServerThread server : servers) {
            try {
                server.stopServer();
            } catch (IOException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        servers.clear();
    }
}
