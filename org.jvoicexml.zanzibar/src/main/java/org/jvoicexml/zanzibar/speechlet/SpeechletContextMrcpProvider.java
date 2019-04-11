package org.jvoicexml.zanzibar.speechlet;

import org.speechforge.cairo.sip.SipSession;

public interface SpeechletContextMrcpProvider {

	/**
	 * @return the internalSession
	 */
	public SipSession getMRCPv2Session();

	/**
	 * @param internalSession the internalSession to set
	 */
	public void setMRCPSession(SipSession internalSession);

}