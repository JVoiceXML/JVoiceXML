package org.jvoicexml.callmanager.mrcpv2;

import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.implementation.Telephony;
import org.jvoicexml.zanzibar.telephony.TelephonyClient;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.sip.SipSession;

public class SipCallManagerSession {
    
    private final SessionIdentifier id;

    private SipSession pbxSession;
    private SipSession mrcpSession;
    private SpeechClient speechClient;
    private TelephonyClient telephonyClient;
    private final Telephony telephony;
    private Session jvxmlSession;


    public SipCallManagerSession(SessionIdentifier id, SipSession pbxSession, SipSession mrcpSession,
            SpeechClient speechClient, TelephonyClient telephonyClient,
            final Telephony tel) {
        super();
        this.id = id;
        this.pbxSession = pbxSession;
        this.mrcpSession = mrcpSession;
        this.speechClient = speechClient;
        this.telephonyClient = telephonyClient;
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
     * @param jvxmlSession the jvxmlSession to set
     */
    public void setJvxmlSession(Session jvxmlSession) {
        this.jvxmlSession = jvxmlSession;
    }
    
    
    /**
     * @return the pbxSession
     */
    public SipSession getPbxSession() {
        return pbxSession;
    }
    /**
     * @param pbxSession the pbxSession to set
     */
    public void setPbxSession(SipSession pbxSession) {
        this.pbxSession = pbxSession;
    }
    /**
     * @return the mrcpSession
     */
    public SipSession getMrcpSession() {
        return mrcpSession;
    }
    
    /**
     * @param mrcpSession the mrcpSession to set
     */
    public void setMrcpSession(SipSession mrcpSession) {
        this.mrcpSession = mrcpSession;
    }
    /**
     * @return the speechClient
     */
    public SpeechClient getSpeechClient() {
        return speechClient;
    }
    /**
     * @param speechClient the speechClient to set
     */
    public void setSpeechClient(SpeechClient speechClient) {
        this.speechClient = speechClient;
    }
    /**
     * @return the telephonyClient
     */
    public TelephonyClient getTelephonyClient() {
        return telephonyClient;
    }
    /**
     * @param telephonyClient the telephonyClient to set
     */
    public void setTelephonyClient(TelephonyClient telephonyClient) {
        this.telephonyClient = telephonyClient;
    }

    public Telephony getTelephony() {
        return telephony;
    }
}
