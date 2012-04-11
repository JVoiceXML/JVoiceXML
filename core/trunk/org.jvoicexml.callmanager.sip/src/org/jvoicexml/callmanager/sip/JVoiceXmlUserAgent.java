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

import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.ContactHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ToHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

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

    private AddressFactory addressFactory;

    private MessageFactory messageFactory;

    private HeaderFactory headerFactory;

    private final String sipAddress;

    private Address address;

    private Address contactAddress;

    /** The current dialog. */
    private Dialog dialog;

    /** The server transaction that was created when processing the INVITE. */
    private ServerTransaction inviteTransaction;

    /**
     * Constructs a new object.
     * @param sipListener the listener to this user agent
     */
    public JVoiceXmlUserAgent(final String address) {
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
        } catch (InvalidArgumentException e) {
            throw new SipException(e.getMessage(), e);
        } catch (UnknownHostException e) {
            throw new SipException(e.getMessage(), e);
        }
    }

    /**
     * Adds a listener to this agent. Must be called after {@link #init()}.
     * @param listener the listener to add
     * @throws TooManyListenersException
     *         if there are too many listeners
     */
    public void addListener(final SipListener listener)
            throws TooManyListenersException {
        provider.addSipListener(listener);
    }

    /**
     * Removes the given listener.
     * @param listener the listener to remove 
     */
    public void removeListener(final SipListener listener) {
        provider.removeSipListener(listener);
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
     * Handles an incoming INVITE.
     * @param request the received request
     * @throws ParseException
     *         error parsing the status code
     * @throws SipException
     *         error sending the message
     * @throws InvalidArgumentException
     *         if the creation of the response is invalid
     */
    public void processInvite(final Request request)
        throws ParseException, SipException, InvalidArgumentException {
        final Response ringingResponse =
                messageFactory.createResponse(Response.RINGING, request);
        final ToHeader ringingToHeader =
                (ToHeader) ringingResponse.getHeader(ToHeader.NAME);
        ringingToHeader.setTag("4321");
        final ContactHeader contactHeader =
                headerFactory.createContactHeader();
        final Address ca = getContactAddress();
        contactHeader.setAddress(ca);
        ringingResponse.addHeader(contactHeader);
        final ServerTransaction transaction =
                provider.getNewServerTransaction(request);
        dialog = transaction.getDialog();
        if (dialog != null) {
            LOGGER.info("Dialog: " + dialog);
            LOGGER.info("Dialog state: " + dialog.getState());
        }
        transaction.sendResponse(ringingResponse);
        
        final FromHeader fromHeader =
                (FromHeader) request.getHeader(FromHeader.NAME);
        final Address fromAddress = fromHeader.getAddress();
        LOGGER.info("sent 'RINGING' to '" + fromAddress + "'");

        final Response okResponse =
                messageFactory.createResponse(Response.OK, request);
        final ToHeader okToHeader =
                (ToHeader) okResponse.getHeader(ToHeader.NAME);
        okToHeader.setTag("4321");
        okResponse.addHeader(contactHeader);
        transaction.sendResponse(okResponse);
        LOGGER.info("sent 'OK' to '" + fromAddress + "'");
        inviteTransaction = transaction;
    }

    /**
     * Processes a BYE request.
     * @param request the received request
     * @param transaction the transaction of the request
     * @throws ParseException
     *         error parsing the status code
     * @throws SipException
     *         error sending the message
     * @throws InvalidArgumentException
     *         if the creation of the response is invalid
     */
    public void processBye(final Request request,
            final ServerTransaction transaction)
                    throws ParseException, SipException, InvalidArgumentException {
        final Response response =
                messageFactory.createResponse(Response.OK, request);
        transaction.sendResponse(response);
        dialog = null;
        inviteTransaction = null;
    }

    /**
     * Performs some cleanup of this user agent.
     * @throws SipException
     *         error cleaning up
     */
    public void dispose() throws SipException {
        if (stack != null) {
            stack.deleteListeningPoint(udp);
            stack.deleteSipProvider(provider);
        }
    }
}
