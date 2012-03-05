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

import java.io.IOException;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipProvider;
import javax.sip.SipStack;

import org.apache.log4j.Logger;
import org.jvoicexml.CallManager;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.event.error.NoresourceError;

/**
 * A {@link CallManager} for the SIP protocol, based on JainSip.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class SipCallManager implements CallManager {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(SipCallManager.class);

    /** Reference to JVoiceXML. */
    private JVoiceXml jvxml;

    /** The used SIP stack. */
    private SipStack stack;
    
    /** The current SIP provider. */
    private SipProvider provider;

    /** The created listening point. */
    private ListeningPoint udp;

    /** Registered SipListener. */
    private JVoiceXmlSipListener listener;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setJVoiceXml(final JVoiceXml jvoicexml) {
        jvxml = jvoicexml;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws NoresourceError, IOException {
        Properties properties = new Properties();
        properties.setProperty("javax.sip.USE_ROUTER_FOR_ALL_URIS","false"); 
        properties.setProperty("javax.sip.STACK_NAME", "JVoiceXmlSipStack");
        final SipFactory factory = SipFactory.getInstance();
        factory.setPathName("gov.nist");
        try {
            stack = factory.createSipStack(properties);
            udp = stack.createListeningPoint("127.0.0.1", 5060, "udp");
            provider = stack.createSipProvider(udp);
            listener = new JVoiceXmlSipListener();
            provider.addSipListener(listener);
        } catch (PeerUnavailableException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (SipException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (InvalidArgumentException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (TooManyListenersException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (stack != null) {
            try {
                provider.removeSipListener(listener);
                stack.deleteListeningPoint(udp);
                stack.deleteSipProvider(provider);
            } catch (ObjectInUseException e) {
                LOGGER.warn(e.getMessage(), e);
            } finally {
                udp = null;
                provider = null;
                stack = null;
            }
        }
    }

}
