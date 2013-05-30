/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/client/jndi/JVoiceXmlStub.java $
 * Version: $LastChangedRevision: 2430 $
 * Date:    $Date: 2010-12-21 09:21:06 +0100 (Di, 21 Dez 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;

import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.client.GenericClient;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.ErrorEvent;

/**
 * Voice provides direct access to JVoiceXML.
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 */
public final class Voice {
    private GenericClient client;
    private Session session;
    
    public GenericClient getClient() {
        if (client == null) {
            client = new GenericClient();
        }
        return client;
    }

    /**
     * Sets the given policy.
     * @param path the policy
     */
    public void setPolicy(final String path) {
        System.setProperty("java.security.policy", path);
    }

    /**
     * Connects a new Session object with a dialog and runs it.
     * 
     * @param server
     *            the server object
     * @param dialog
     *            the dialog to use
     * @throws IOException 
     *            the error happened during the session was active
     */
    public void operate(final TextServer server, final URI dialog)
            throws IOException {
        try {
            final ConnectionInformation info = server.getConnectionInformation();
            session = getClient().call(dialog, info);
            server.waitConnected();
            session.waitSessionEnd();
            session.hangup();
        } catch (NamingException | ErrorEvent e) {
            throw new IOException(e);
        } finally {
            session = null;
        }
    }

    /**
     * Get the currently active Session object.
     * 
     * @return the active Session or null if there's none
     */
    public Session getSession() {
        return session;
    }
}