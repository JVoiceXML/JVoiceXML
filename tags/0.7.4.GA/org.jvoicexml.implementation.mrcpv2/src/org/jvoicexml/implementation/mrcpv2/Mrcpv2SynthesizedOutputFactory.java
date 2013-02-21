/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
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

package org.jvoicexml.implementation.mrcpv2;

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.speechforge.cairo.client.SessionManager;

/**
 * Implementation of a {@link org.jvoicexml.implementation.ResourceFactory}
 * for the {@link org.jvoicexml.implementation.SynthesizedOuput} based on
 * MRCPv2.
 *
 * @author Spencer Lord
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class Mrcpv2SynthesizedOutputFactory
        implements ResourceFactory<SynthesizedOutput> {
    /** Number of instances that this factory will create. */
    private int instances;

    /** Number of created instances. */
    private int currentInstance;

    /** The RTP base port. */
    private int basePort;

    /** Type of the created resources. */
    private final String type;
    
    /** SIP Service used for MRCP channel config and control. */
    private SessionManager sessionManager;

    /**
     * Constructs a new object.
     */
    public Mrcpv2SynthesizedOutputFactory() {
        type = "mrcpv2";
    }

    /**
     * {@inheritDoc}
     */
    public SynthesizedOutput createResource() throws NoresourceError {

        final Mrcpv2SynthesizedOutput output = new Mrcpv2SynthesizedOutput();

        output.setType(type);
        output.setRtpReceiverPort(basePort + (currentInstance++) * 2);
        output.setSessionManager(sessionManager);

        return output;
    }

    /**
     * Sets the number of instances that this factory will create.
     * 
     * @param number
     *                Number of instances to create.
     */
    public void setInstances(final int number) {
        instances = number;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInstances() {
        return instances;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<SynthesizedOutput> getResourceType() {
        return SynthesizedOutput.class;
    }

    /**
     * @return the basePort
     */
    public int getBasePort() {
        return basePort;
    }

    /**
     * Sets the base port.
     * @param port the base port to set
     */
    public void setBasePort(final int port) {
        basePort = port;
    }

    /**
     * Retrieves the session manager.
     * @return the session manager
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * Sets the session manager.
     * @param manager the session manager
     */
    public void setSessionManager(final SessionManager manager) {
        sessionManager = manager;
    }
}
