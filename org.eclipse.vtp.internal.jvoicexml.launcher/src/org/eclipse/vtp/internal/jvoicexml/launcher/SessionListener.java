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

import org.jvoicexml.Session;

/**
 * Listener to wait for the end of the session.
 * 
 * @author Dirk Schnelle
 */
class SessionListener
        extends Thread {
    /** The session to listen at. */
    private final Session session;

    /** The current browser. */
    private final JVoiceXmlBrowser browser;

    /**
     * Constructs a new object.
     * 
     * @param sess
     *        The current session.
     * @param jvxml
     *        The current browser.
     */
    public SessionListener(final Session sess, final JVoiceXmlBrowser jvxml) {
        super();

        setDaemon(true);

        session = sess;
        browser = jvxml;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        try {
            session.waitSessionEnd();
        } catch (org.jvoicexml.event.JVoiceXMLEvent e) {
            final Throwable cause = e.getCause();
            browser.logMessage(cause.getMessage());
        } finally {
            browser.logMessage("session terminated");
        }
    }
}
