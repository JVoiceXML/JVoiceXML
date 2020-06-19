package org.jvoicexml.zanzibar.sip;

import org.speechforge.cairo.sip.SipSession;

/**
 * Mapping of MRCP session and PBX session.
 * 
 * @author Spencer Lord
 * @author Dirk Schnelle-Walka
 *
 */
class SessionPair {
    private SipSession mrcpSession;
    private SipSession pbxSession;

    public SessionPair(SipSession i, SipSession e) {
        mrcpSession = i;
        pbxSession = e;
    }

    /**
     * @return the pbx session
     */
    public SipSession getPbxSession() {
        return pbxSession;
    }

    /**
     * @param external
     *            the pbxSession to set
     */
    public void setPbxSession(SipSession external) {
        this.pbxSession = external;
    }

    /**
     * @return the mrcp sesion
     */
    public SipSession getMrcpSession() {
        return mrcpSession;
    }

    /**
     * @param internal
     *            the mrcp session to set
     */
    public void setMrcpSession(SipSession internal) {
        this.mrcpSession = internal;
    }

}
