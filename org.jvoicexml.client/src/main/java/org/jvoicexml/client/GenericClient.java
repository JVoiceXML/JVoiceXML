/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.UuidSessionIdentifier;
import org.jvoicexml.client.jndi.JVoiceXmlStub;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;

/**
 * A generic client to make calls to the JVoiceXML voice browser.
 * @author Dirk Schnelle-Walka
 * @since 0.7.4
 */
public final class GenericClient {
    /** Reference to the JVoiceXML server. */
    private JVoiceXml jvxml;

    /** The connection information that was created by the factory. */
    private ConnectionInformationController infoController;

    /**
     * Retrieves a reference to the JVoiceXML server.
     * @return reference to the JVoiceXML server.
     * @throws NamingException
     *         JVoiceXML server could not be found.
     */
    private JVoiceXml getJVoiceXml() throws NamingException {
        if (jvxml == null) {
            final Context context = new InitialContext();
            jvxml = new JVoiceXmlStub(context);
        }
        return jvxml;
    }

    /**
     * Checks, if the given identifier occurs in the list of identifiers.
     * @param identifier the identifier to look for
     * @param identifiers the available identifiers
     * @return <code>true</code> if the identifier is among the available
     *           identifiers.
     * @since 0.7.6
     */
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
     * able to create a {@link ConnectionInformationController} with the
     * specified identifiers and calls the factory method for the first matching
     * factory.
     * @param call unique identifier for the {@link org.jvoicexml.CallControl}.
     * @param output unique identifier for the
     *  {@link org.jvoicexml.SystemOutput}.
     * @param input unique identifier for the {@link org.jvoicexml.UserInput}.
     * @return created connection information controller
     * @throws UnsupportedResourceIdentifierException
     *         if the combination of the identifiers is invalid
     */
    private ConnectionInformationController createConnectionInformation(
            final String input, final String output, final String call)
                    throws UnsupportedResourceIdentifierException {
        final ServiceLoader<ConnectionInformationFactory> services =
                ServiceLoader.load(ConnectionInformationFactory.class);
        for (ConnectionInformationFactory current : services) {
            final String[] calls = current.getCallControlIdentifiers();
            if (!contains(call, calls)) {
                continue;
            }
            final String[] outputs = current.getSystemOutputIdentifiers();
            if (!contains(output, outputs)) {
                continue;
            }
            final String[] inputs = current.getUserInputIdentifiers();
            if (!contains(input, inputs)) {
                continue;
            }
            return current.createConnectionInformation(call, output, input);
        }
        final String message = String.format(
                "No matching factory found for '%s', %s', '%s'", call, output,
                input);
        throw new UnsupportedResourceIdentifierException(message);
    }

    /**
     * Calls JVoiceXML with the given URI and the specified platform
     * configuration.
     * <p>
     * After the session has ended and {@link Session#hangup()} has been called,
     * {@link #close()} must be called to free potentially acquired resources.
     * </p>
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
        // Cleanup if the user forgot to call cleanup.
        if (infoController != null) {
            close();
        }

        // Create a new connection info
        infoController = createConnectionInformation(input, output, call);
        ConnectionInformation info = infoController.getConnectionInformation();
        return call(uri, info);
    }

    /**
     * Calls JVoiceXML with the given URI and the specified platform
     * configuration.
     * <p>
     * After the session has ended and {@link Session#hangup()} has been called,
     * {@link #close()} must be called to free potentially acquired resources.
     * </p>
     * @param uri the URI to call
     * @param call unique identifier for the {@link org.jvoicexml.CallControl}.
     * @param output unique identifier for the
     *  {@link org.jvoicexml.SystemOutput}.
     * @param input unique identifier for the {@link org.jvoicexml.UserInput}.
     * @param id the session identifier
     * @return JVoiceXML session for the call
     * @throws NamingException
     *         JVoiceXML server could not be found.
     * @throws ErrorEvent
     *         if an error occurs when calling JVoiceXML
     * @throws UnsupportedResourceIdentifierException
     *         if the combination of the identifiers is invalid
     */
    public Session call(final URI uri, final String input, final String output,
            final String call, final SessionIdentifier id)
                    throws NamingException, ErrorEvent,
            UnsupportedResourceIdentifierException {
        // Cleanup if the user forgot to call cleanup.
        if (infoController != null) {
            close();
        }

        // Create a new connection info
        infoController = createConnectionInformation(input, output, call);
        ConnectionInformation info = infoController.getConnectionInformation();
        return call(uri, info, id);
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
        final SessionIdentifier id = new UuidSessionIdentifier(); 
        return call(uri, info, id);
    }

    /**
     * Calls JVoiceXML with the given URI and the specified platform
     * configuration.
     * @param uri the URI to call
     * @param info the connection information to use.
     * @param id the session identifier
     * @return JVoiceXML session for the call
     * @throws NamingException
     *         JVoiceXML server could not be found.
     * @throws ErrorEvent
     *         if an error occurs when calling JVoiceXML
     * @since 0.7.9
     */
    public Session call(final URI uri, final ConnectionInformation info,
            final SessionIdentifier id)
            throws NamingException, ErrorEvent {
    final JVoiceXml jvoicexml = getJVoiceXml();
        if (jvoicexml == null) {
          throw new NoresourceError(
                    "JVoiceXML server could not be found");
        }
        final Session session = jvoicexml.createSession(info, id);
        if (session == null) {
          throw new NoresourceError(
                    "Session unavailable, no usable implementation");
        } else {
          session.call(uri);
        }
        return session;
    }
    
    /**
     * Release potentially acquired resources.
     *
     * @since 0.7.6
     */
    public void close() {
        if (infoController == null) {
            return;
        }

        infoController.cleanup();
        infoController = null;
    }
}
