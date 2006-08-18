/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * Starter for the RMI registry.
 *
 * @author Dirk Schnelle
 * @version $LastChangedRevision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5.1
 */
public class JVoiceXmlRegistry {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(JVoiceXmlRegistry.class);

    /** Configuration key. */
    public static final String CONFIG_KEY = "registry";

    /** Port of the registry. */
    private int port;

    /** The registry. */
    private Registry registry;

    /**
     * Constructs a new object.
     */
    public JVoiceXmlRegistry() {
        port = Registry.REGISTRY_PORT;
    }

    /**
     * Sets the port number.
     * @param portnumber int
     */
    public void setPort(final int portnumber) {
        port = portnumber;
    }

    /**
     * Starts the RMI registry at the specified port.
     */
    public void start() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("starting RMI registry at port " + port + "...");
        }

        try {
            registry = LocateRegistry.createRegistry(port);
        } catch (RemoteException re) {
            LOGGER.error("error starting the registry", re);

            return;
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("...RMI registry started");
        }
    }

    /**
     * Shutdown the registry.
     */
    public void shutdown() {
        try {
            UnicastRemoteObject.unexportObject(registry, true);
        } catch (java.rmi.NoSuchObjectException nsoe) {
            LOGGER.error("error in shutdown", nsoe);
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("RMI registry stopped");
        }
    }
}
