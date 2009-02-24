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
import javax.telephony.callcontrol.CallControlCall;
import javax.telephony.media.MediaConfigException;
import javax.telephony.media.MediaException;
import javax.telephony.media.MediaProvider;

import net.sourceforge.gjtapi.GenericJtapiPeer;
import net.sourceforge.gjtapi.media.GenericMediaService;

import org.apache.log4j.Logger;

/**
 * Demo implementation for JTAPI access to JVoiceXML.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public class JtapiDemo {
    /** Logger instance. */
    private static final Logger LOGGER = Logger.getLogger(JtapiDemo.class);

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
            "net.sourceforge.gjtapi.raw.mjsip.MjSipProvider";
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
            final CallControlCall call =
                (CallControlCall) provider.createCall();
            final String sip = "sip:jvoicexml@127.0.0.1:4242";
            LOGGER.info("calling '" + sip + "'...");
            Connection[] connections =
                call.connect(terminal, address, sip);
            final GenericMediaService ms =
                new DesktopMediaService((MediaProvider) provider);
            ms.bindToTerminal(null, terminal);
            synchronized (listener) {
                listener.wait();
            }
            final Address callingAddress = call.getCallingAddress();
            final Address calledAddress = call.getCalledAddress();
            LOGGER.info("call connected from " + callingAddress.getName()
                        + " to " + calledAddress.getName());
            ms.play("rtp://localhost:30000/audio?rate=8000&keepAlive=false",
                    0, null, null);
        } catch (JtapiPeerUnavailableException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (ResourceUnavailableException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (InvalidArgumentException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (MediaException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (InvalidStateException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (PrivilegeViolationException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (MethodNotSupportedException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (InvalidPartyException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
