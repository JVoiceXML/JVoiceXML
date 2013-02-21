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

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Properties;
import java.util.TooManyListenersException;
import java.util.Vector;

import javax.sdp.Connection;
import javax.sdp.MediaDescription;
import javax.sdp.Origin;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;
import javax.sdp.SessionName;
import javax.sdp.TimeDescription;
import javax.sdp.Version;
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
import javax.sip.header.ContentTypeHeader;
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

    /** Listeners for this user agent. */
    private final Collection<UserAgentListener> listeners;

    /** A related SIP session. */
    private SipSession session;

    /**
     * Constructs a new object.
     * @param address the SIP address of this agent
     */
    public JVoiceXmlUserAgent(final String address) {
        sipAddress = address;
        listeners = new java.util.ArrayList<UserAgentListener>();
    }

    /**
     * Adds the given user agent listener to the list of known user agent
     * listeners
     * @param listener the listener to add
     */
    public void addUserAgentListener(final UserAgentListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the given user agent listener from the list of known user agent
     * listeners
     * @param listener the listener to remove
     */
    public void removeUserAgentListener(final UserAgentListener listener) {
        listeners.remove(listener);
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
     * @throws SdpException 
     */
    public void processInvite(final Request request)
        throws ParseException, SipException, InvalidArgumentException, SdpException {
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
        ContentTypeHeader contentTypeHeader =
                headerFactory.createContentTypeHeader("application", "sdp");
        String sdp;
        try {
            final Vector<MediaDescription> mediaDescriptions
                = createMediaDescriptions(request);
            final SessionDescription description =
                    createSessionDescription(InetAddress.getLocalHost(), null,
                            mediaDescriptions);
            sdp = description.toString();
        } catch (UnknownHostException e) {
            throw new SdpException(e.getMessage(), e);
        }
        okResponse.setContent(sdp, contentTypeHeader);
        transaction.sendResponse(okResponse);
        LOGGER.info("sent 'OK' to '" + fromAddress + "'");
        inviteTransaction = transaction;

        // Create a new session
        session = new SipSession();
        for (UserAgentListener listener : listeners) {
            listener.sessionCreated(session);
        }
    }

    /**
     * Creates a list of media descriptions. For now, this simply copies
     * the received description.
     * TODO 
     * @return list of media descriptions
     * @throws SdpException
     *         error parsing the media descriptions from the request
     */
    @SuppressWarnings("unchecked")
    private Vector<MediaDescription> createMediaDescriptions(
            final Request request) throws SdpException {
        byte[] content = request.getRawContent();
        if (content == null) {
            return null;
        }
        final String sdp = new String(content);
        final SdpFactory factory = SdpFactory.getInstance();
        final SessionDescription description =
                factory.createSessionDescription(sdp);
        return description.getMediaDescriptions(false);
    }

    /**
     * Creates an empty instance of a <tt>SessionDescription</tt> with
     * preinitialized  <tt>s</tt>, <tt>v</tt>, <tt>c</tt>, <tt>o</tt> and
     * <tt>t</tt> parameters.
     *
     * @param localAddress the <tt>InetAddress</tt> corresponding to the local
     * address that we'd like to use when talking to the remote party.
     * @param userName the user name to use in the origin parameter or
     * <tt>null</tt> in case we'd like to use a default.
     * @param mediaDescriptions a <tt>Vector</tt> containing the list of
     * <tt>MediaDescription</tt>s that we'd like to advertise (leave
     * <tt>null</tt> if you'd like to add these later).
     *
     * @return an empty instance of a <tt>SessionDescription</tt> with
     * preinitialized <tt>s</tt>, <tt>v</tt>, and <tt>t</tt> parameters.
     * @throws SdpException if the SDP creation failed
     */
    private SessionDescription createSessionDescription(
            InetAddress localAddress, String userName,
            Vector<MediaDescription> mediaDescriptions)
            throws SdpException {
        final SdpFactory factory = SdpFactory.getInstance();
        final SessionDescription description =
                factory.createSessionDescription();

        //"v=0"
        final Version version = factory.createVersion(0);
        description.setVersion(version);

        //"s=-"
        final SessionName sessionName = factory.createSessionName("-");
        description.setSessionName(sessionName);

        //"t=0 0"
        final TimeDescription timeDescription =
                factory.createTimeDescription();
        final Vector<TimeDescription> timeDescs =
                new Vector<TimeDescription>();
        timeDescs.add(timeDescription);
        description.setTimeDescriptions(timeDescs);

        final String addrType = localAddress instanceof Inet6Address
            ? Connection.IP6
            : Connection.IP4;

        //o
        if (userName == null) {
            userName = "jvoicexml";
        }

        final Origin origin = factory.createOrigin(
            userName, 0, 0, "IN", addrType, localAddress.getHostAddress());
        description.setOrigin(origin);

        //c=
        final Connection connection = factory.createConnection(
            "IN", addrType, localAddress.getHostAddress());
        description.setConnection(connection);

        if ( mediaDescriptions != null) {
            description.setMediaDescriptions(mediaDescriptions);
        }

        return description;
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
        for (UserAgentListener listener : listeners) {
            listener.sessionDropped(session);
        }
        session = null;
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
