/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import javax.telephony.media.MediaProvider;

import net.sourceforge.gjtapi.GenericJtapiPeer;
import net.sourceforge.gjtapi.media.GenericMediaService;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.callmanager.CallManager;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;

/**
 * JTAPI based implementation of a {@link CallManager}.
 *
 * @author Hugo Monteiro
 * @author Renato Cassaca
 * @author Dirk Schnelle
 *
 * @version $Revision$
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
    private static final Logger LOGGER =
        Logger.getLogger(JtapiCallManager.class);

    /** Provider. */
    private Provider provider = null;

    /** Reference to JVoiceXml. */
    private JVoiceXml jvxml;

    /**
     * Name of the provider used
     * ex:net.sourceforge.gjtapi.raw.sipprovider.SipProvider.
     */
    private String providerName = null;

    /** Map of terminals associated to an application. */
    private Map<String, JtapiConfiguredApplication> terminals =
        new java.util.HashMap<String, JtapiConfiguredApplication>();

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
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("successfully loaded provider '" + providerName
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
        final Address[] addresses;
        try {
            addresses = prov.getAddresses();
        } catch (ResourceUnavailableException ex) {
            throw new NoresourceError(ex.getMessage(), ex);
        }

        for (Address address: addresses) {
            String addr = address.getName();
            final JVoiceXmlTerminal terminal;
            try {
                terminal = createTerminal(prov, addr);
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
     * @throws javax.telephony.MediaConfigException
     *             Error creating the terminal.
     * @throws javax.telephony.MediaBindException
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
        final JtapiConfiguredApplication application =
            terminals.get(terminalName);
        if (application == null) {
            throw new InvalidArgumentException(
                    "No configuration for terminal '" + terminalName + "'");
        }
        final int port = application.getPort();

        return new JVoiceXmlTerminal(this, ms, port, application.getInputType(), application.getOutputType());
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        /**
         * @todo may be it is necessary to stop all the listeners
         */
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("shuting down the provider...");
        }
        provider.shutdown();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            LOGGER.debug(ex.getMessage(), ex);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...provider shut down");
        }
    }

    /**
     * Adds the given list of applications.
     *
     * @param applications
     *            list of application
     */
    public void setApplications(
            final List<JtapiConfiguredApplication> applications) {
        final Iterator<JtapiConfiguredApplication> iterator = applications
                .iterator();
        while (iterator.hasNext()) {
            final JtapiConfiguredApplication application = iterator.next();
            final String terminal = application.getTerminal();
            addTerminal(terminal, application);
        }
    }

    /**
     * Adds the terminal with the given URI to the list of known terminals.
     *
     * @param terminal
     *            identifier for the terminal
     * @param application
     *            URI of the application to add.
     * @return <code>true</code> if the terminal was added.
     */
    public boolean addTerminal(final String terminal,
            final JtapiConfiguredApplication application) {
        terminals.put(terminal, application);
        LOGGER.info("added terminal '" + terminal + "' for application '"
                + application.getUri() + "'");

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void setJVoiceXml(final JVoiceXml jvoicexml) {
        jvxml = jvoicexml;
    }

    /**
     * Creates a session for the given terminal and initiates a call at
     * JVoiceXml.
     *
     * @param remote
     *            remote connection to the terminal.
     * @return created session.
     * @exception ErrorEvent
     *                Error creating the session.
     */
    public Session createSession(final JtapiRemoteClient remote)
            throws ErrorEvent {
        final String name = remote.getTerminalName();
        final JtapiConfiguredApplication application = terminals.get(name);
        if (application == null) {
            throw new BadFetchError("No application defined for terminal '"
                    + name + "'");
        }

        // Create a session and initiate a call at JVoiceXML.
        final Session session = jvxml.createSession(remote);
        final URI uri = application.getUriObject();
        session.call(uri);

        return session;
    }
}
