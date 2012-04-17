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
package org.jvoicexml.callmanager.sip;

import java.io.IOException;
import java.util.TooManyListenersException;

import javax.sip.SipException;

import org.apache.log4j.Logger;
import org.jvoicexml.callmanager.Terminal;

/**
 * Implementation of a terminal for the SIP callmanager
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class SipTerminal implements Terminal {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(SipTerminal.class);

    /** The SIP user agent. */
    private JVoiceXmlUserAgent agent;

    /** Registered SipListener. */
    private JVoiceXmlSipListener listener;

    /** SIP user name. */
    private String user;

    /** SIP port. */
    private int port;

    /**
     * Sets the SIP user name.
     * @param sipUser name of the SIP user.
     */
    public void setUser(final String sipUser) {
        user = sipUser;
    }

    /**
     * Sets the SIP port.
     * @param sipPort SIP port.
     */
    public void setPort(int sipPort) {
        port = sipPort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        final StringBuilder str = new StringBuilder();
        str.append("sip:");
        str.append(user);
        str.append("@127.0.0.2:");
        str.append(port);
        return str.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitForConnections() throws IOException {
        final String name = getName();
        agent = new JVoiceXmlUserAgent(name);
        try {
            agent.init();
            listener = new JVoiceXmlSipListener(agent);
            agent.addListener(listener);
        } catch (SipException e) {
            throw new IOException(e.getMessage(), e);
        } catch (TooManyListenersException e) {
            throw new IOException(e.getMessage(), e);
        }
        LOGGER.info("listening at '" + name + "' for SIP calls");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopWaiting() {
        if (agent != null) {
            try {
                agent.removeListener(listener);
                agent.dispose();
            } catch (SipException e) {
                LOGGER.warn(e.getMessage(), e);
            } finally {
                agent = null;
            }
            final String name = getName();
            LOGGER.info("'" + name + "' stopped listening for SIP calls");
        }
    }
}
