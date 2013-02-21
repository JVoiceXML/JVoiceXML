/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager.jtapi;

import java.util.Collection;

import javax.telephony.Address;
import javax.telephony.InvalidArgumentException;
import javax.telephony.JtapiPeer;
import javax.telephony.Provider;
import javax.telephony.ProviderUnavailableException;
import javax.telephony.ResourceUnavailableException;
import javax.telephony.Terminal;
import javax.telephony.media.MediaBindException;
import javax.telephony.media.MediaConfigException;
import javax.telephony.media.MediaProvider;

import net.sourceforge.gjtapi.GenericJtapiPeer;
import net.sourceforge.gjtapi.media.GenericMediaService;

import org.apache.log4j.Logger;
import org.jvoicexml.CallManager;
import org.jvoicexml.callmanager.BaseCallManager;
import org.jvoicexml.callmanager.ConfiguredApplication;
import org.jvoicexml.event.error.NoresourceError;

/**
 * JTAPI based implementation of a {@link CallManager}.
 *
 * @author Hugo Monteiro
 * @author Renato Cassaca
 * @author Dirk Schnelle-Walka
 *
 * @version $Revision$
 * @since 0.6
 */
public final class JtapiCallManager extends BaseCallManager
    implements CallManager {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(JtapiCallManager.class);

    /** Number of msec to wait after the provider was requested to shutdown. */
    private static final int PROVIDER_WAIT_SHUTDOWN = 1000;

    /** Provider. */
    private Provider provider;

    /**
     * Name of the provider used
     * ex:net.sourceforge.gjtapi.raw.sipprovider.SipProvider.
     */
    private String providerName;

    /**
     * Provider initialization and properties for the terminals.
     */
    public JtapiCallManager() {
    }

    /**
     * Gets the provider.
     *
     * @return the provider to use.
     * @exception NoresourceError
     *                Error creating the provider.
     */
    private Provider getProvider() throws NoresourceError {
        if (provider != null) {
            return provider;
        }

        // Get a JTAPI Peer
        final JtapiPeer peer;
        try {
            // We can not use the JTtapPeerFactory since this factory
            // uses the system class loader.
            final ClassLoader loader = JtapiPeer.class.getClassLoader();
            final String peerName = GenericJtapiPeer.class.getCanonicalName();
            @SuppressWarnings("unchecked")
            final Class<JtapiPeer> peerClass =
                (Class<JtapiPeer>) loader.loadClass(peerName);
            peer = peerClass.newInstance();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("successfully loaded the jtapi peer");
            }
        } catch (ClassNotFoundException e) {
            throw new NoresourceError("Failed to load thejtapi peer", e);
        } catch (InstantiationException e) {
            throw new NoresourceError("Failed to load the jtapi peer", e);
        } catch (IllegalAccessException e) {
            throw new NoresourceError("Failed to load the jtapi peer", e);
        }

        // initialize and load properties
        try {
            provider = peer.getProvider(providerName);
            LOGGER.info("successfully loaded provider '" + providerName
                    + "'");
        } catch (ProviderUnavailableException pue) {
            throw new NoresourceError("Failed to load provider", pue);
        }

        return provider;
    }

    /**
     * Sets the name of the provider.
     *
     * @param name
     *            of the provider.
     */
    public void setProvidername(final String name) {
        providerName = name;
    }

    /**
     * Creates a terminal object for the given provider and terminal address.
     *
     * @param prov
     *            the provider to use.
     * @param address
     *            address of the terminal
     * @return created call control.
     * @throws javax.telephony.InvalidArgumentException
     *             Error creating the terminal.
     * @throws MediaConfigException
     *             Error creating the terminal.
     * @throws MediaBindException
     *             Error creating the terminal.
     */
    private JVoiceXmlTerminal createTerminal(final Provider prov,
            final String address) throws InvalidArgumentException,
            MediaConfigException, MediaBindException {
        final Terminal terminal = prov.getTerminal(address);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating terminal '" + terminal.getName() + "'...");
        }

        // Create a media service
        final GenericMediaService ms = new GenericMediaService(
                (MediaProvider) provider);

        // we have only one terminal per Address
        ms.bindToTerminal(null, terminal);

        final String terminalName = ms.getTerminalName();
        final ConfiguredApplication application = getApplication(terminalName);
        if (application == null) {
            throw new InvalidArgumentException(
                    "No configuration for terminal '" + terminalName + "'");
        }
        return new JVoiceXmlTerminal(this, ms);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleStop() {
        hangupSessions();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("shuting down the provider...");
        }
        provider.shutdown();
        try {
            Thread.sleep(PROVIDER_WAIT_SHUTDOWN);
        } catch (InterruptedException ex) {
            LOGGER.debug(ex.getMessage(), ex);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...provider shut down");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<org.jvoicexml.callmanager.Terminal> createTerminals()
        throws NoresourceError{
        final Provider prov = getProvider();
        final Address[] addresses;
        try {
            addresses = prov.getAddresses();
        } catch (ResourceUnavailableException ex) {
            throw new NoresourceError(ex.getMessage(), ex);
        }

        final Collection<org.jvoicexml.callmanager.Terminal> terminals =
            new java.util.ArrayList<org.jvoicexml.callmanager.Terminal>();
        for (Address address : addresses) {
            final String addr = address.getName();
            final JVoiceXmlTerminal terminal;
            try {
                terminal = createTerminal(prov, addr);
                terminals.add(terminal);
            } catch (MediaBindException ex) {
                throw new NoresourceError(ex.getMessage(), ex);
            } catch (MediaConfigException ex) {
                throw new NoresourceError(ex.getMessage(), ex);
            } catch (InvalidArgumentException ex) {
                throw new NoresourceError(ex.getMessage(), ex);
            }
            LOGGER.info("initialized terminal '"
                    + terminal.getTerminalName() + "'");
        }
        return terminals;
    }
}
