/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

import java.rmi.RMISecurityManager;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

/**
 * Remote shutdown utility for the VoiceXML interpreter.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.4
 */
public final class RemoteShutdown {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(RemoteShutdown.class);

    /** The JNDI context. */
    private Context context;

    /**
     * Constructs a new object.
     */
    public RemoteShutdown() {
        try {
            context = new InitialContext();
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error creating initial context", ne);

            context = null;
        }
    }

    /**
     * Shutdown the interpreter.
     */
    public void shutdown() {
        JVoiceXml jvxml;
        try {
            jvxml = (JVoiceXml) context.lookup("JVoiceXml");
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error obtaining JVoiceXml. Server not running?", ne);

            return;
        }

        LOGGER.info("shutting down JVoiceXML");

        jvxml.shutdown();

        LOGGER.info("shutdown request sent");
    }

    /**
     * The main method.
     * @param args Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null) {
            securityManager = new RMISecurityManager();
            System.setSecurityManager(securityManager);
            LOGGER.info("security manager set to " + securityManager);
        }
        RemoteShutdown shutdown = new RemoteShutdown();

        shutdown.shutdown();
    }
}
