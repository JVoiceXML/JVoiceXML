/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.client;

import java.net.URI;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jvoicexml.JVoiceXml;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.Session;
import org.jvoicexml.event.ErrorEvent;

/**
 * A generic client to make calls to the JVoiceXML voice browser.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.4
 */
public final class GenericClient {
    /** Reference to the JVoiceXML server. */
    private JVoiceXml jvxml;

    /**
     * Retrieves a reference to the JVoiceXML server.
     * @return reference to the JVoiceXML server.
     * @throws NamingException
     *         JVoiceXML server could not be found.
     */
    private JVoiceXml getJVoiceXml() throws NamingException {
        if (jvxml == null) {
            final Context context = new InitialContext();
            jvxml = (JVoiceXml) context.lookup("JVoiceXml");
        }
        return jvxml;
    }

    /**
     * Calls JVoiceXML with the given URI and the specified platform
     * configuration.
     * @param uri the URI to call
     * @param call unique identifier for the {@link org.jvoicexml.CallControl}.
     * @param output unique identifier for the
     *  {@link org.jvoicexml.SystemOutput}.
     * @param input unique identifier for the {@link org.jvoicexml.UserInput}.
     * @return JVoiceXML session for the call
     * @throws NamingException 
     *         JVoiceXML server could not be found.
     * @throws ErrorEvent
     *         if an error occurs when calling JVoiceXML 
     */
    public Session call(final URI uri, final String input, final String output,
            final String call) throws NamingException, ErrorEvent {
        final JVoiceXml jvoicexml = getJVoiceXml();
        final ConnectionInformation client = new BasicConnectionInformation(call, output,
            input);
        final Session session = jvoicexml.createSession(client);
        session.call(uri);
        return session;
    }
}
