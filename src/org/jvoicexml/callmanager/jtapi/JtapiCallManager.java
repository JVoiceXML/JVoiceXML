/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/KeyedResourcePool.java $
 * Version: $LastChangedRevision: 330 $
 * Date:    $Date: 2007-06-21 09:15:10 +0200 (Do, 21 Jun 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.URI;
import java.util.HashMap;

import javax.telephony.Address;
import javax.telephony.InvalidArgumentException;
import javax.telephony.JtapiPeer;
import javax.telephony.JtapiPeerFactory;
import javax.telephony.JtapiPeerUnavailableException;
import javax.telephony.Provider;
import javax.telephony.ProviderUnavailableException;
import javax.telephony.ResourceUnavailableException;
import javax.telephony.Terminal;
import javax.telephony.media.MediaBindException;
import javax.telephony.media.MediaConfigException;
import javax.telephony.media.MediaException;
import javax.telephony.media.MediaProvider;

import net.sourceforge.gjtapi.GenericJtapiPeer;
import net.sourceforge.gjtapi.media.GenericMediaService;

import org.jvoicexml.CallControl;
import org.jvoicexml.callmanager.CallManager;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.jtapi.JtapiCallControl;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * JTAPI based implementation of a {@link CallManager}.
 * 
 * @author Hugo Monteiro
 * @author Renato Cassaca
 * @version $Revision: 206 $
 * 
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 * 
 * @since 0.6
 */
public final class JtapiCallManager implements CallManager {
    /** Logger instance. */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(JtapiCallManager.class);

    /** Provider. */
    private Provider provider = null;

    /**
     * Name of the provider used
     * ex:net.sourceforge.gjtapi.raw.sipprovider.SipProvider.
     */
    private String providerName = null;

    /** Map of terminals associated to an application. */
    private HashMap<String, URI> terminals = new HashMap<String, URI>();

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
        JtapiPeer peer = null;
        try {
            peer = JtapiPeerFactory.getJtapiPeer(GenericJtapiPeer.class
                    .getCanonicalName());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("successfully loaded the jtapi peer");
            }
        } catch (JtapiPeerUnavailableException jpue) {
            throw new NoresourceError("Failed to locate peer with the factory",
                    jpue);
        }

        // initialize and load properties
        try {
            provider = peer.getProvider(providerName);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("successfully loaded provider '" + providerName
                        + "'");
            }
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
     * {@inheritDoc}
     */
    public void start() throws NoresourceError {
        final Provider prov = getProvider();
        Address[] address;
        try {
            address = prov.getAddresses();
        } catch (ResourceUnavailableException ex) {
            throw new NoresourceError(ex.getMessage(), ex);
        }

        for (int i = 0; i < address.length; i++) {
            // address
            String addr = address[i].getName();
            JtapiCallControl callControl;
            try {
                callControl = createCallControl(prov, addr);
            } catch (MediaBindException ex) {
                throw new NoresourceError(ex.getMessage(), ex);
            } catch (MediaConfigException ex) {
                throw new NoresourceError(ex.getMessage(), ex);
            } catch (InvalidArgumentException ex) {
                throw new NoresourceError(ex.getMessage(), ex);
            }
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("initialized terminal '"
                        + callControl.getTerminalName() + "'");
            }
        }
    }

    /**
     * Creates a {@link CallControl} object for the gien provider and terminal
     * address.
     * @param prov the provider to use.
     * @param address address of the terminal
     * @return created call control.
     * @throws InvalidArgumentException
     * @throws MediaConfigException
     * @throws MediaBindException
     */
    private JtapiCallControl createCallControl(final Provider prov,
            final String address) throws InvalidArgumentException,
            MediaConfigException, MediaBindException {
        final Terminal terminal = prov.getTerminal(address);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("terminal name: " + terminal.getName());
        }

        // Create a media service
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Attempting to create a Media Service...");
        }
        final GenericMediaService ms = new GenericMediaService(
                (MediaProvider) provider);

        // we have only one terminal per Address
        ms.bindToTerminal(null, terminal);

        return new JtapiCallControl(ms);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        /**
         * @todo may be it is necessary to stop all the listeners
         */
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ShutingDown the provider");
        }
        provider.shutdown();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            LOGGER.debug(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean addTerminal(final String terminal, final URI application) {
        terminals.put(terminal, application);

        return true;
    }
}
