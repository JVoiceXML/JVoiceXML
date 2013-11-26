/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

import java.net.URI;

import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.Session;
import org.jvoicexml.client.GenericClient;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.ErrorEvent;

/**
 * Voice provides direct access to JVoiceXML via GenericClient.
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 */
public final class VoiceXmlAccessor {
    /**
     * the back-end client to communicate with.
     */
    private GenericClient client;

    /**
     * the session currently open.
     */
    private Session session;

    /**
     * Set the client.
     * @param client the client
     */
    public void setClient(final GenericClient client) {
         this.client = client;
    }

    /**
     * Get the actual client.
     */
     public GenericClient getClient() {
          if (client == null) {
               setClient(new GenericClient());
          }
          return client;
     }


    /**
     * Calls the client to get a new textual Session object for
     * the specified dialog and runs it.
     * Blocks while server is connected and till session ends.
     *
     * @param server
     *            the server object
     * @param dialog
     *            the dialog to use
     * @throws Exception
     *            the error happened during the session was active
     * @throws ErrorEvent
     *            the error happened during execution of the dialog
     */
     public void call(final TextServer server, final URI dialog)
          throws Exception, ErrorEvent {
          try {
              final ConnectionInformation info =
                    server.getConnectionInformation();
              session = getClient().call(dialog, info);
              session.waitSessionEnd();
          } finally {
              if (session != null) {
                  session.hangup();
              }
              session = null;
          }
    }

    /**
     * Close the active Communication.
     */
    public void close() {
          if (session != null) {
               session.hangup();
               session = null;
          }
          if (client != null) {
               client.close();
               client = null;
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
