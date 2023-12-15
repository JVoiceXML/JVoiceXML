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
    /** The MRCP session. */
    private SipSession mrcpSession;
    /** The PBX session. */
    private SipSession pbxSession;

    /**
     * Creates a new session pair.
     * 
     * @param mrcp the MRCP session
     * @param pbx the PBX session
     */
    public SessionPair(final SipSession mrcp, final SipSession pbx) {
        mrcpSession = mrcp;
        pbxSession = pbx;
    }

    /**
     * Returns the PBX session.
     * @return the pbx session
     */
    public SipSession getPbxSession() {
        return pbxSession;
    }

    /**
     * Sets the PBX session.
     * @param external
     *            the pbxSession to set
     */
    public void setPbxSession(SipSession external) {
        pbxSession = external;
    }

    /**
     * Returns the MRCP session.
     * @return the mrcp sesion
     */
    public SipSession getMrcpSession() {
        return mrcpSession;
    }

    /**
     * Sets the MRCP session.
     * @param internal
     *            the mrcp session to set
     */
    public void setMrcpSession(SipSession internal) {
        mrcpSession = internal;
    }

}
