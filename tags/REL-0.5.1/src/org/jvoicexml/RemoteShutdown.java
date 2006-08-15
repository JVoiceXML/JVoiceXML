/*
 * File:    $RCSfile: RemoteShutdown.java,v $
 * Version: $Revision: 1.2 $
 * Date:    $Date: 2006/03/27 17:24:13 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
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

package org.jvoicexml;

import javax.naming.Context;

import org.apache.log4j.Logger;
import javax.naming.InitialContext;

/**
 * Remote shutdown utility for the VoiceXML interpreter.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.2 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.4
 */
public final class RemoteShutdown {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(RemoteShutdown.class);

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

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("shutting down JVoiceXML");
        }

        jvxml.shutdown();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("shutdwon request sent");
        }
    }

    /**
     * The main method.
     * @param args Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        RemoteShutdown shutdown = new RemoteShutdown();

        shutdown.shutdown();
    }
}
