/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/client/GenericClient.java $
 * Version: $LastChangedRevision: 2931 $
 * Date:    $Date: 2012-02-06 08:57:38 +0100 (Mo, 06 Feb 2012) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.ServiceLoader;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.event.ErrorEvent;

/**
 * A generic client to make calls to the JVoiceXML voice browser.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2931 $
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

    private boolean contains(final String identifier,
            final String[] identifiers) {
        for (String current : identifiers) {
            if (current.equals(identifier)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Scans all found {@link ConnectionInformationFactory}s if they are
     * able to create a {@link ConnectionInformation} with the specified
     * identifiers and calls the factory method for the first matching factory.
     * @param call unique identifier for the {@link org.jvoicexml.CallControl}.
     * @param output unique identifier for the
     *  {@link org.jvoicexml.SystemOutput}.
     * @param input unique identifier for the {@link org.jvoicexml.UserInput}.
     * @return created connection inf
     * @throws UnsupportedResourceIdentifierException
     *         if the combination of the identifiers is invalid
     */
    private ConnectionInformation createConnectionInformation(
            final String input, final String output, final String call)
                    throws UnsupportedResourceIdentifierException {
        final ServiceLoader<ConnectionInformationFactory> services =
                ServiceLoader.load(ConnectionInformationFactory.class);
        for (ConnectionInformationFactory factory : services) {
            final String[] calls = factory.getCallControlIdentifiers();
            if (!contains(call, calls)) {
                continue;
            }
            final String[] outputs = factory.getSystemOutputIdentifiers();
            if (!contains(output, outputs)) {
                continue;
            }
            final String[] inputs = factory.getUserInputIdentifiers();
            if (!contains(input, inputs)) {
                continue;
            }
            return factory.createConnectionInformation(call, output, input);
        }
        final String message = String.format(
                "No matching factoy found for '%s', %s', '%s'", call, output,
                input);
        throw new UnsupportedResourceIdentifierException(message);
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
     * @throws UnsupportedResourceIdentifierException 
     *         if the combination of the identifiers is invalid
     */
    public Session call(final URI uri, final String input, final String output,
            final String call) throws NamingException, ErrorEvent,
            UnsupportedResourceIdentifierException {
        final ConnectionInformation info =
                createConnectionInformation(input, output, call);
            new BasicConnectionInformation(call, output, input);
        return call(uri, info);
    }

    /**
     * Calls JVoiceXML with the given URI and the specified platform
     * configuration.
     * @param uri the URI to call
     * @param info the connection information to use.
     * @return JVoiceXML session for the call
     * @throws NamingException 
     *         JVoiceXML server could not be found.
     * @throws ErrorEvent
     *         if an error occurs when calling JVoiceXML 
     */
    public Session call(final URI uri, final ConnectionInformation info)
            throws NamingException, ErrorEvent {
        final JVoiceXml jvoicexml = getJVoiceXml();
        final Session session = jvoicexml.createSession(info);
        session.call(uri);
        return session;
    }
}
