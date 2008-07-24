/*
 * File:    $RCSfile: VoiceXmlInterpreter.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.demo.jtapidemo;

import javax.telephony.Address;
import javax.telephony.Call;
import javax.telephony.CallListener;
import javax.telephony.Connection;
import javax.telephony.ConnectionListener;
import javax.telephony.InvalidArgumentException;
import javax.telephony.InvalidPartyException;
import javax.telephony.InvalidStateException;
import javax.telephony.JtapiPeer;
import javax.telephony.JtapiPeerFactory;
import javax.telephony.JtapiPeerUnavailableException;
import javax.telephony.MethodNotSupportedException;
import javax.telephony.PrivilegeViolationException;
import javax.telephony.Provider;
import javax.telephony.ResourceUnavailableException;
import javax.telephony.Terminal;
import javax.telephony.media.MediaConfigException;
import javax.telephony.media.MediaException;
import javax.telephony.media.MediaProvider;

import net.sourceforge.gjtapi.GenericJtapiPeer;
import net.sourceforge.gjtapi.media.GenericMediaService;

import org.apache.log4j.Logger;
import org.jvoicexml.callmanager.jtapi.JtapiCallManager;

/**
 * Demo implementation for JTAPI access to JVoiceXML.
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.7
 */
public class JtapiDemo {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(JtapiCallManager.class);

    /** Provider. */
    private Provider provider = null;

    /**
     * Gets the provider.
     *
     * @return the provider to use.
     * @throws JtapiPeerUnavailableException
     *         error creating the provider.
     */
    private Provider getProvider() throws JtapiPeerUnavailableException {
        if (provider != null) {
            return provider;
        }

        // Get a JTAPI Peer
        JtapiPeer peer = JtapiPeerFactory.getJtapiPeer(GenericJtapiPeer.class
                .getCanonicalName());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("successfully loaded the jtapi peer");
        }
        final String providerName =
            "net.sourceforge.gjtapi.raw.sipprovider.SipProvider";
        provider = peer.getProvider(providerName);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("successfully loaded provider '" + providerName
                    + "'");
        }

        return provider;
    }

    /**
     * Retrieves the terminal to use.
     * @param prov the current provider
     * @param address the local address
     * @return terminal to use
     * @throws InvalidArgumentException
     * @throws MediaException
     * @throws MediaConfigException
     */
    private Terminal getTerminal(final Provider prov, final Address address)
        throws InvalidArgumentException, MediaException, MediaConfigException {
        final String addr = address.getName();
        final Terminal terminal = prov.getTerminal(addr);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating terminal '" + terminal.getName() + "'...");
        }

        // Create a media service
        final GenericMediaService ms = new GenericMediaService(
                (MediaProvider) provider);

        // we have only one terminal per Address
        ms.bindToTerminal(null, terminal);
        return terminal;
    }

    /**
     * @param args command line parameters
     */
    public static void main(final String[] args) {
        final JtapiDemo demo = new JtapiDemo();
        try {
            final Provider provider = demo.getProvider();
            Address[] addresses = provider.getAddresses();
            final Address address = addresses[0];
            final Terminal terminal = demo.getTerminal(provider, address);
            final ConnectionListener listener =
                new DemoConnectionListener();
            terminal.addCallListener(listener);
            final Call call = provider.createCall();
            Connection[] connections =
                call.connect(terminal, address,
                        "sip:jvoicexml@127.0.0.1:5064");
        } catch (JtapiPeerUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ResourceUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MediaConfigException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MediaException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (PrivilegeViolationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MethodNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidPartyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
