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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;

import org.apache.log4j.Logger;

/**
 * A SIP user agent implementation.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public class JVoiceXmlUserAgent {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlUserAgent.class);

    /** The used SIP stack. */
    private SipStack stack;
    
    /** The current SIP provider. */
    private SipProvider provider;

    /** The created listening point. */
    private ListeningPoint udp;

    private final SipListener listener;

    private AddressFactory addressFactory;

    private MessageFactory messageFactory;

    private HeaderFactory headerFactory;

    private final String sipAddress;

    private Address address;

    private Address contactAddress;

    /**
     * Constructs a new object.
     * @param sipListener the listener to this user agent
     */
    public JVoiceXmlUserAgent(final String address, final SipListener sipListener) {
        listener = sipListener;
        sipAddress = address;
    }

    /**
     * Initializes this user agent
     * @throws SipException
     *         error initializing the user agent 
     */
    public void init() throws SipException {
        Properties properties = new Properties();
        properties.setProperty("javax.sip.USE_ROUTER_FOR_ALL_URIS","false"); 
        properties.setProperty("javax.sip.STACK_NAME", "JVoiceXmlSipStack");
        final SipFactory factory = SipFactory.getInstance();
        factory.setPathName("gov.nist");
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            final String host = localhost.getHostAddress();
            properties.setProperty("javax.sip.IP_ADDRESS", host);
            LOGGER.info("Listening at '" + host + "'");
            stack = factory.createSipStack(properties);
            headerFactory = factory.createHeaderFactory();
            addressFactory = factory.createAddressFactory();
            messageFactory = factory.createMessageFactory();
            udp = stack.createListeningPoint(host, 4242, "udp");
            provider = stack.createSipProvider(udp);
            provider.addSipListener(listener);
        } catch (InvalidArgumentException e) {
            throw new SipException(e.getMessage(), e);
        } catch (TooManyListenersException e) {
            throw new SipException(e.getMessage(), e);
        } catch (UnknownHostException e) {
            throw new SipException(e.getMessage(), e);
        }
    }

    /**
     * Retrieves the SIP address.
     * @return the SIP address
     * @throws SipException
     *         if the specified SIP address is not valid
     */
    private Address getAddress() throws SipException {
        if (address == null) {
            URI uri;
            try {
                uri = addressFactory.createURI(sipAddress);
            } catch (ParseException e) {
                throw new SipException(e.getMessage(), e);
            }
            if (uri.isSipURI() == false) {
                throw new SipException("Invalid sip uri: " + sipAddress);
            }
            address = addressFactory.createAddress(uri);
        }
        return address;
    }

    /**
     * Retrieves the contact address.
     * @return the contact address
     * @throws SipException
     *         error creating the contact address
     */
    private Address getContactAddress() throws SipException {
        if (contactAddress == null) {
            final Address address = getAddress();
            final URI uri = address.getURI();
            final SipURI sipUri = (SipURI) uri;
            final String user = sipUri.getUser();
            try {
                final String host = stack.getIPAddress();
                final SipURI contactUri = addressFactory.createSipURI(user, host);
                contactUri.setPort(udp.getPort());
                contactUri.setTransportParam("udp");
                contactAddress = addressFactory.createAddress(contactUri);
            } catch (ParseException e) {
                throw new SipException("Could not create contact URI.", e);
            }
        }
        return contactAddress;
    }

    /**
     * Performs some cleanup of this user agent.
     * @throws SipException
     *         error cleaning up
     */
    public void dispose() throws SipException {
        if (provider != null) {
            provider.removeSipListener(listener);
        }
        if (stack != null) {
            stack.deleteListeningPoint(udp);
            stack.deleteSipProvider(provider);
        }
    }
}
