/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.jndi;

import java.rmi.RMISecurityManager;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.startup.RemoteShutdown;

/**
 * Remote shutdown utility for the VoiceXML interpreter.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.4
 */
public final class JndiRemoteShutdown implements RemoteShutdown {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(JndiRemoteShutdown.class);

    /** The default port number. */
    private static final int DEFAULT_PORT = 1099;

    /** The JNDI port number. */
    private int port;

    /**
     * Constructs a new object.
     */
    public JndiRemoteShutdown() {
        port = DEFAULT_PORT;
    }

    /**
     * Sets the port number for the remote shutdown.
     * @param portNumber JNDI port number
     */
    public void setPort(final int portNumber) {
        port = portNumber;
    }

    /**
     * Retrieves the initial context.
     * @return The context to use or <code>null</code> in case of an error.
     * @throws NamingException
     *         error obtaining the initial context
     * @since 0.7.5
     */
    Context getInitialContext() throws NamingException {
        // We take the values from jndi.properties but override the port
        final Hashtable<String, String> environment =
            new Hashtable<String, String>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.rmi.registry.RegistryContextFactory");
        environment.put(Context.PROVIDER_URL, "rmi://localhost:" + port);
        return new InitialContext(environment);
    }

    /**
     * Shutdown the interpreter.
     */
    public void shutdown() {
        final JVoiceXml jvxml;
        final String providerUrl;
        Context context = null;
        try {
            context = getInitialContext();
            final Map<?, ?> environment = context.getEnvironment();
            providerUrl = environment.get(Context.PROVIDER_URL).toString();
            jvxml = (JVoiceXml) context.lookup("JVoiceXml");
        } catch (javax.naming.NamingException e) {
            LOGGER.error("error obtaining JVoiceXml. Server not running?", e);
            if (context != null) {
                try {
                    final Map<?, ?> environment = context.getEnvironment();
                    for (Object key : environment.keySet()) {
                        final Object value = environment.get(key);
                        LOGGER.error(key + ": " + value);
                    }
                } catch (NamingException ignore) {
                }
            }
            return;
        }

        LOGGER.info("shutting down JVoiceXML at '" + providerUrl + "'...");
        jvxml.shutdown();
        LOGGER.info("...shutdown request sent");
    }

    /**
     * The main method.
     * @param args Command line arguments. First argument can be the 
     *             JNDI port number.
     */
    public static void main(final String[] args) {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null) {
            securityManager = new RMISecurityManager();
            System.setSecurityManager(securityManager);
            LOGGER.info("security manager set to " + securityManager);
        }
        final int portNumber;
        if (args.length > 0) {
            final String arg = args[0];
            portNumber = Integer.parseInt(arg);
        } else {
            portNumber = DEFAULT_PORT;
        }
        final JndiRemoteShutdown shutdown = new JndiRemoteShutdown();
        shutdown.setPort(portNumber);
        shutdown.shutdown();
    }
}
