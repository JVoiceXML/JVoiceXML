/*
 * JVoiceXML JST server plugin
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jvoicexml.eclipse.jst.server;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Ping the JVoiceXML server.
 *
 * @author Aurelian Maga
 * @author Dirk Schnelle
 * @version 0.1
 *
 */
final class JVoiceXMLPingThread extends Thread {
    /** Delay in msec between the checks. */
    private static final int SLEEP = 5000;
    /** Reference to the server behavior to update the status. */
    private JVoiceXMLServerBehaviour behaviour;
    /** <code>true</code> if checking should be performed. */
    private boolean check;
    /** JNDI context to lookup the server. */
    private Context context;

    /**
     * Constructs a new object.
     * @param jsb reference to the server behavior to update the status.
     */
    public JVoiceXMLPingThread(final JVoiceXMLServerBehaviour jsb) {
        behaviour = jsb;
        check = false;
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.rmi.registry.RegistryContextFactory");
        env.put(Context.PROVIDER_URL, "rmi://localhost:1099");

        try {
            context = new InitialContext(env);
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Stops pinging.
     */
    public void stopPinging() {
        check = false;
        interrupt();
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        check = true;
        while (check) {
            try {
                Thread.sleep(SLEEP);
            } catch (Exception ignore) {
            }
            check();
        }
    }

    /**
     * Checks if JVoiceXml is alive.
     */
    private void check() {
        try {
            final Object jvxml = context.lookup("JVoiceXml");
            behaviour.setStarted(jvxml != null);
        } catch (Exception ignore) {
        }
    }
}
