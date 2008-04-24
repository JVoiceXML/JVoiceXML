/*
 * JVoiceXML VTP Plugin
 *
 * Copyright (C) 2006 Dirk Schnelle
 *
 * Copyright (c) 2006 Dirk Schnelle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.vtp.internal.jvoicexml.launcher;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.jvoicexml.Session;

/**
 * Server for <code>LoggingEvent</code>s sent over the socket appender
 * of JVoiceXml.
 *
 * @author Dirk Schnelle
 */
class LoggingReceiver
    implements Runnable {
    /** Reference to the browser. */
    private JVoiceXmlBrowser browser;

    /** The current session. */
    private Session session;

    /** The server socket for the socket appender. */
    private ServerSocket server;

    /** A connected client (JvoiceXml) */
    private Socket client;

    /** The level to monitor. */
    private Level level;

    /** Flag, if the thread is started. */
    private boolean started;

    /**
     * Constructs a new object.
     */
    public LoggingReceiver() {
        started = false;
    }

    /**
     * Sets the borwoser.
     *
     * @param jvxml
     *        The browser.
     */
    public void setBrowser(JVoiceXmlBrowser jvxml) {
        browser = jvxml;
    }

    /**
     * Sets the current session.
     *
     * @param current
     *        The current session.
     */
    public void setSession(final Session current) {
        session = current;
    }

    /**
     * Sets the minimal logging level.
     *
     * @param lev
     *        The minimal logging level.
     */
    public void setLevel(final String lev) {
        level = Level.toLevel(lev);
    }

    /**
     * Connects to the JVoiceXml socket appender.
     *
     * <p>
     * If the connection is already established, this method returns
     * immediatly. Otherwise, a new server socket is opened, waiting for
     * log4j to connect. This may take a while until a timeout of the
     * socket appender occurs to reconnect.
     * </p>
     *
     * @param port
     *        Port to listen at.
     * @return <code>true</code> if a connection could be established.
     */
    public boolean connect(final int port) {
        if (server != null) {
            final int currentPort = server.getLocalPort();

            if (currentPort == port) {
                return true;
            }

            close();
        }

        try {
            server = new ServerSocket();
            server.setReuseAddress(true);
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        }

        final SocketAddress address = new InetSocketAddress(port);
        try {
            server.bind(address);
            browser.logMessage("waiting for a connection at port " + port
                               + ". This may take a while...");
            client = server.accept();
            browser.logMessage("connected");
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Checks, if this thread is started.
     *
     * @return <code>true</code> if the thread is started.
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        if (client == null) {
            return;
        }

        started = true;

        try {
            final InputStream in = client.getInputStream();
            final ObjectInputStream oin = new ObjectInputStream(in);

            while (client.isConnected()) {
                final LoggingEvent event = (LoggingEvent) oin.readObject();
                logMessage(event);
            }

            oin.close();
            in.close();
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }

        started = false;
    }

    /**
     * Convenience method to send a log message to the debug pane. The event is
     * displayed, if a session exists and the configured logging level is equal
     * or greater to the level of the <code>logevent</code>.
     *
     * @param logevent
     *        The received logging message.
     */
    private void logMessage(final LoggingEvent logevent) {
        if (session == null) {
            return;
        }

        final String id = session.getSessionID();
        final String threadName = logevent.getThreadName();
        if (!id.equalsIgnoreCase(threadName)) {
            return;
        }

        final Level currentLevel = logevent.getLevel();
        if (!currentLevel.isGreaterOrEqual(level)) {
            return;
        }

        final long timestamp = logevent.timeStamp;
        final Date date = new Date(timestamp);
        final String message = logevent.getMessage().toString();
        browser.logMessage(date, message);
    }

    /**
     * Closes the receiver and all open connections.
     */
    public void close() {
        if (server != null) {
            final int currentPort = server.getLocalPort();
            if (browser != null) {
                browser.logMessage("closing server at port " + currentPort);
            }

            try {
                server.close();
            } catch (java.io.IOException ioe) {
                ioe.printStackTrace();
            }

            server = null;

            if (browser != null) {
                browser.logMessage("server closed");
            }
        }

        if (client != null) {
            try {
                client.close();
            } catch (java.io.IOException ioe) {
                ioe.printStackTrace();
            }

            client = null;
        }
    }
}
