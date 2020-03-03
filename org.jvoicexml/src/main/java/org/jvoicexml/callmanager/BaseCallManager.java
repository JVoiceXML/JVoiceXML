/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.CallManager;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;


/**
 * Base implementation of a {@link CallManager}.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7
 */
public abstract class BaseCallManager implements CallManager, TerminalListener {
    /** Logger instance. */
    private static final Logger LOGGER =
        LogManager.getLogger(BaseCallManager.class);

    /** Factory to create the {@link org.jvoicexml.ConnectionInformation} instances. */
    private TerminalConnectionInformationFactory clientFactory;

    /** Reference to JVoiceXml. */
    private JVoiceXmlCore jvxml;

    /** Map of terminal names associated to an application. */
    private final Map<String, ConfiguredApplication> applications;

    /** All known terminals. */
    private Collection<Terminal> terminals;

    /** Established sessions. */
    private final Map<Terminal, Session> sessions;

    /** Flag if the call manager has been started. */
    boolean started;
    
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
    public final void setJVoiceXml(final JVoiceXmlCore jvoicexml) {
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
     * Sets the connection information factory.
     * @param factory the connection information container factory.
     * @since 0.7
     */
    public final void setConnectionInformationFactory(
            final TerminalConnectionInformationFactory factory) {
        clientFactory = factory;
    }

    /**
     * Adds the given list of applications.
     *
     * @param apps
     *            list of application
     */
    public final void setApplications(
            final Collection<ConfiguredApplication> apps) {
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
     *
     * This implementation first creates all terminals by calling
     * {@link #createTerminals()}. These terminals are then requested to wait
     * for incoming connections by calling
     * {@link Terminal#waitForConnections()}.
     */
    @Override
    public final void start() throws NoresourceError, IOException {
       terminals = createTerminals();
       if (terminals == null || terminals.isEmpty()) {
           LOGGER.warn("No terminals created. "
                   + "CallManager might work not propertly!");
           return;
       } else {
           for (Terminal terminal : terminals) {
               terminal.waitForConnections();
           }
           LOGGER.info(terminals.size() + " terminals created");
       }

       // Register the terminal listeners.
       for (Terminal terminal : terminals) {
           if (terminal instanceof ObservableTerminal) {
               final ObservableTerminal observableTerminal =
                   (ObservableTerminal) terminal;
               observableTerminal.addListener(this);
           }
       }
       started = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStarted() {
        return started;
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
     * Creates a session for the given terminal and initiates a call at
     * JVoiceXml.
     *
     * @param id the session identifier
     * @param term
     *            the connecting terminal
     * @param parameters
     *            additional parameters
     * @return created session.
     * @exception ErrorEvent
     *                Error creating the session.
     */
    public final Session createSession(
            final SessionIdentifier id,
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
        final ConnectionInformation remote;
        try {
            remote = clientFactory.createConnectionInformation(
                    this, application, parameters);
        } catch (ConnectionInformationCreationException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
        // Create a session and initiate a call at JVoiceXML.
        final Session session = jvxml.createSession(remote, id);
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
            final SessionIdentifier id = terminal.getSessionIdentifier();
            final Session session = createSession(id, terminal, parameters);
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
    public final void terminalError(final Terminal terminal,
            final String message, final Throwable cause) {
        LOGGER.error("error in terminal '" + terminal.getName() + "': "
                + message, cause);
    }

    /**
     * Checks if the given terminal is connected.
     * @param terminal the terminal
     * @return <code>true</code> if the given terminal is connected
     */
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
     *
     * This method first hangs up all open session by calling
     * {@link #terminalDisconnected(Terminal)}. Next, all terminals are
     * requested to stop waiting for incoming connections by calling
     * {@link Terminal#stopWaiting()}. Afterwards it is possible to do
     * some further cleanup by overriding {@link #handleStop()}.
     */
    @Override
    public final void stop() {
        if (!started) {
            return;
        }
        hangupSessions();
        for (Terminal terminal : terminals) {
            terminal.stopWaiting();
        }
        handleStop();
        started = true;
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
