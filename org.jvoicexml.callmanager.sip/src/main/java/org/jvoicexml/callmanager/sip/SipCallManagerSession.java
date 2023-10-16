/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.implementation.Telephony;
import org.jvoicexml.zanzibar.telephony.TelephonyClient;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.sip.SipSession;

public class SipCallManagerSession {
    /** The session identifier. */
    private final SessionIdentifier id;
    /** The PBX session. */
    private SipSession pbxSession;
    /** The MRCP session. */
    private SipSession mrcpSession;
    /** The speech cleint. */
    private SpeechClient speechClient;
    /** The telephony client. */
    private TelephonyClient telephonyClient;
    /** The used telephony platform. */
    private final Telephony telephony;
    /** The JVoiceXML session. */
    private Session jvxmlSession;


    /**
     * Constructs a new object.
     * @param sessionIdentifier the session identifier
     * @param pbxSipSession the PBX session
     * @param mrcpSipSession the MRCP session
     * @param cairoSpeechClient the speech client
     * @param teleClient the telephony client
     * @param tel the telephony platform
     */
    public SipCallManagerSession(final SessionIdentifier sessionIdentifier,
            final SipSession pbxSipSession, final SipSession mrcpSipSession,
            final SpeechClient cairoSpeechClient,
            final TelephonyClient teleClient,
            final Telephony tel) {
        super();
        id = sessionIdentifier;
        pbxSession = pbxSipSession;
        mrcpSession = mrcpSipSession;
        speechClient = cairoSpeechClient;
        telephonyClient = teleClient;
        telephony = tel;
    }
    /**
     * @return the id
     */
    public SessionIdentifier getId() {
        return id;
    }

    /**
     * @return the jvxmlSession
     */
    public Session getJvxmlSession() {
        return jvxmlSession;
    }
    /**
     * Sets the JVoiceXML session.
     * @param session the jvxmlSession to set
     */
    public void setJvxmlSession(final Session session) {
        jvxmlSession = session;
    }
    
    
    /**
     * Retrieves the PBX session.
     * @return the pbxSession
     */
    public SipSession getPbxSession() {
        return pbxSession;
    }
    /**
     * Sets the PBX session.
     * @param session the pbxSession to set
     */
    public void setPbxSession(final SipSession session) {
        pbxSession = session;
    }
    /**
     * @return the mrcpSession
     */
    public SipSession getMrcpSession() {
        return mrcpSession;
    }
    
    /**
     * Sets teh MRCP session.
     * @param session the mrcpSession to set
     */
    public void setMrcpSession(final SipSession session) {
        mrcpSession = session;
    }
    
    /**
     * Retrieves the cairo speech client.
     * @return the speech client
     */
    public SpeechClient getSpeechClient() {
        return speechClient;
    }
    /**
     * Sets the speech client.
     * @param client the speechClient to set
     */
    public void setSpeechClient(final SpeechClient client) {
        speechClient = client;
    }
    /**
     * Retrieves the telephony client.
     * @return the telephony client
     */
    public TelephonyClient getTelephonyClient() {
        return telephonyClient;
    }
    
    /**
     * Sets the telephony client.
     * @param client the telephony client to set
     */
    public void setTelephonyClient(final TelephonyClient client) {
        telephonyClient = client;
    }

    /**
     * Retrieves the telephony implementation platform.
     * @return the telephony implementation platform
     * @since 0.7.9
     */
    public Telephony getTelephony() {
        return telephony;
    }
}
