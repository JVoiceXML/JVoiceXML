/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
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

package org.jvoicexml.callmanager;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.Session;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;


/**
 * Base implementation of a {@link CallManager}.
 *
 * @author Dirk Schnelle-Walka
 *
 * @version $Revision$
 * @since 0.7
 */
public abstract class BaseCallManager implements CallManager {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(BaseCallManager.class);

    /** Factory to create the {@link org.jvoicexml.RemoteClient} instances. */
    private RemoteClientFactory clientFactory;

    /** Reference to JVoiceXml. */
    private JVoiceXml jvxml;

    /** Map of terminal names associated to an application. */
    private final Map<String, ConfiguredApplication> applications;

    /** All known terminals. */
    private Collection<Terminal> terminals;

    /** Established sessions. */
    private final Map<Terminal, Session> sessions;

    /**
     * Constructs a new object.
     */
    public BaseCallManager() {
        applications = new java.util.HashMap<String, ConfiguredApplication>();
        sessions = new java.util.HashMap<Terminal, Session>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setJVoiceXml(final JVoiceXml jvoicexml) {
        jvxml = jvoicexml;
    }

    /**
     * Retrieves the reference to the interpreter.
     * @return the interpreter
     */
    public final JVoiceXml getJVoiceXml() {
        return jvxml;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setRemoteClientFactory(
            final RemoteClientFactory factory) {
        clientFactory = factory;
    }

    /**
     * Adds the given list of applications.
     *
     * @param apps
     *            list of application
     */
    public final void setApplications(
            final List<ConfiguredApplication> apps) {
        for (ConfiguredApplication application : apps) {
            final String terminal = application.getTerminal();
            addTerminal(terminal, application);
        }
    }

    /**
     * Adds the terminal with the given URI to the list of known terminals.
     *
     * @param terminal
     *            identifier for the terminal
     * @param application
     *            URI of the application to add.
     * @return <code>true</code> if the terminal was added.
     */
    public final boolean addTerminal(final String terminal,
            final ConfiguredApplication application) {
        applications.put(terminal, application);
        LOGGER.info("added terminal '" + terminal + "' for application '"
                + application.getUri() + "'");

        return true;
    }

    /**
     * Retrieves the application for the given terminal.
     * @param terminal name of the terminal
     * @return application
     */
    public final ConfiguredApplication getApplication(final String terminal) {
        return applications.get(terminal);
    }

    /**
     * Retrieves all configured applications.
     * @return all configured applications.
     * @since 0.7.3
     */
    public final Collection<ConfiguredApplication> getApplications() {
        return applications.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void start() throws NoresourceError, IOException {
       terminals = createTerminals();
       for (Terminal terminal : terminals) {
           terminal.waitForConnections();
       }
       LOGGER.info(terminals.size() + " terminals created");
    }

    /**
     * Creates all terminals without starting them.
     * @return all terminals.
     * @exception NoresourceError
     *      error creating a terminal.
     * @since 0.7.3
     */
    protected abstract Collection<Terminal> createTerminals()
        throws NoresourceError;

    /**
     * {@inheritDoc}
     */
    @Override
    public final Session createSession(
            final org.jvoicexml.callmanager.Terminal term,
            final CallParameters parameters)
            throws ErrorEvent {
        final String name = term.getName();
        final ConfiguredApplication application = applications.get(name);
        if (application == null) {
            throw new BadFetchError("No application defined for terminal '"
                    + name + "'");
        }
        parameters.setTerminal(term);
        final RemoteClient remote;
        try {
            remote = clientFactory.createRemoteClient(
                    this, application, parameters);
        } catch (RemoteClientCreationException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
        // Create a session and initiate a call at JVoiceXML.
        final Session session = jvxml.createSession(remote);
        final URI uri = application.getUriObject();
        session.call(uri);

        return session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void terminalConnected(final Terminal terminal,
            final CallParameters parameters) {
        try {
            final Session session = createSession(terminal, parameters);
            synchronized (sessions) {
                sessions.put(terminal, session);
            }
        } catch (ErrorEvent e) {
            LOGGER.error("error creating the session", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void terminalDisconnected(final Terminal terminal) {
        synchronized (sessions) {
            final Session session = sessions.get(terminal);
            if (session == null) {
                return;
            }
            session.hangup();
            sessions.remove(terminal);
            LOGGER.info("hung up session for terminal '" + terminal.getName()
                    + "'");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isConnected(final Terminal terminal) {
        synchronized (sessions) {
            return sessions.containsKey(terminal);
        }
    }

    /**
     * Calls the hangup for each connected session and stops the terminals.
     */
    protected final void hangupSessions() {
       synchronized (sessions) {
           final Collection<Terminal> openTerminals = sessions.keySet();
           for (Terminal terminal : openTerminals) {
               terminalDisconnected(terminal);
           }
       }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void stop() {
        hangupSessions();
        for (Terminal terminal : terminals) {
            terminal.stopWaiting();
        }
        handleStop();
    }

    /**
     * Possible post processing when the call manager is shut down after
     * all terminals have been stopped.
     * 
     * @since 0.7.3
     */
    protected void handleStop() {
    }
}
