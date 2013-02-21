package org.jvoicexml.callmanager.mrcpv2;

import org.jvoicexml.Session;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.sip.SipSession;
import org.speechforge.zanzibar.telephony.TelephonyClient;

public class SipCallManagerSession {
    
    private String id;

    private SipSession pbxSession;
    private SipSession mrcpSession;
    private SpeechClient speechClient;
    private TelephonyClient telephonyClient;
    private Session jvxmlSession;


    public SipCallManagerSession(String id,SipSession pbxSession, SipSession mrcpSession,
            SpeechClient speechClient, TelephonyClient telephonyClient) {
        super();
        this.id = id;
        this.pbxSession = pbxSession;
        this.mrcpSession = mrcpSession;
        this.speechClient = speechClient;
        this.telephonyClient = telephonyClient;
    }
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
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

    
}
