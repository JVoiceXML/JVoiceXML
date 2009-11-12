/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision: $
 * Date:    $LastChangedDate: $
 * Author:  $LastChangedBy: $
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

package org.jvoicexml.implementation.mrcpv2;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SpokenInput;
import org.speechforge.cairo.client.SessionManager;

/**
 * Implementation of a {@link org.jvoicexml.implementation.ResourceFactory} for
 * the {@link SpokenInput} based on MRCPv2.
 * 
 * @author Spencer Lord
 * @version $Revision: $
 * @since 0.7
 */
public final class Mrcpv2SpokenInputFactory
        implements ResourceFactory<SpokenInput> {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(Mrcpv2SpokenInputFactory.class);

    /** Number of instances that this factory will create. */
    private int instances;

    private int currentInstance = 0;

    private int basePort;

    private final String type;

    /** SIP Service used for MRCP channel config and control */
    private SessionManager sessionManager;

    /**
     * Constructs a new object.
     */
    public Mrcpv2SpokenInputFactory() {
        type = "mrcpv2";
    }

    /**
     * {@inheritDoc}
     */
    public SpokenInput createResource() throws NoresourceError {

        final Mrcpv2SpokenInput input = new Mrcpv2SpokenInput();
        input.setRtpReceiverPort(basePort + (currentInstance++) * 2);
        input.setSessionManager(sessionManager);

        return input;
    }

    /**
     * Sets the number of instances that this factory will create.
     * 
     * @param number
     *            Number of instances to create.
     */
    public void setInstances(final int number) {
        instances = number;
    }

    /**
     * {@inheritDoc}
     */
    public int getInstances() {
        return instances;
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public Class<SpokenInput> getResourceType() {
        return SpokenInput.class;
    }

    /**
     * @return the currentInstance
     */
    public int getCurrentInstance() {
        return currentInstance;
    }

    /**
     * @param currentInstance
     *            the currentInstance to set
     */
    public void setCurrentIntance(int currentInstance) {
        this.currentInstance = currentInstance;
    }

    /**
     * @return the basePort
     */
    public int getBasePort() {
        return basePort;
    }

    /**
     * @param basePort
     *            the basePort to set
     */
    public void setBasePort(int basePort) {
        this.basePort = basePort;
    }

    /**
     * @return the sipService
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * @param sipService
     *            the sipService to set
     */
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
}
