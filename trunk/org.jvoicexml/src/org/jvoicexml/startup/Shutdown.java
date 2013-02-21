/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.startup;

import java.rmi.RMISecurityManager;
import java.util.Collection;
import java.util.ServiceLoader;

import org.apache.log4j.Logger;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;

/**
 * Shutdown of the JVoiceXML browser using the configured JNDI implementation.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class Shutdown {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(Shutdown.class);

    /**
     * Do not make instances.
     */
    private Shutdown() {
    }

    /**
     * Retrieves the configuration.
     * @return the configuration
     */
    private Configuration getConfiguration() {
        final ServiceLoader<Configuration> services =
            ServiceLoader.load(Configuration.class);
        for (Configuration config : services) {
            return config;
        }
        return null;
    }

    /**
     * Retrieves the remote shutdown implementation.
     * @param configuration the configuration to use
     * @return remote shutdown implementation, <code>null</code> if there is
     *           none.
     * @throws ConfigurationException
     *          error reading the configuration
     */
    private RemoteShutdown getRemoteShutdown(final Configuration configuration)
            throws ConfigurationException {
        final Collection<RemoteShutdown> shutdowns =
                configuration.loadObjects(RemoteShutdown.class, "jndi");
        for (RemoteShutdown shutdown : shutdowns) {
            return shutdown;
        }
        return null;
    }

    /**
     * The main method, which starts the interpreter.
     *
     * @param args Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null) {
            securityManager = new RMISecurityManager();
            System.setSecurityManager(securityManager);
            LOGGER.info("security manager set to " + securityManager);
        }
        final Shutdown shutdown = new Shutdown();
        final Configuration configuration = shutdown.getConfiguration();
        if (configuration == null) {
            LOGGER.fatal("No configuration found.");
            return;
        }
        try {
            final RemoteShutdown remote =
                    shutdown.getRemoteShutdown(configuration);
            if (remote == null) {
                LOGGER.fatal("no remote shutdown configured");
                return;
            }
            remote.shutdown();
        } catch (ConfigurationException e) {
            LOGGER.fatal(e.getMessage(), e);
            return;
        }
    }
}
