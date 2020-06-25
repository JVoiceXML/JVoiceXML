/*
 * Zanzibar - Open source speech application server.
 *
 * Copyright (C) 2008-2009 Spencer Lord 
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Contact: salord@users.sourceforge.net
 *
 */
package org.jvoicexml.zanzibar.speechlet;

import java.io.IOException;

import javax.sip.SipException;

import org.jvoicexml.zanzibar.telephony.TelephonyClient;
import org.jvoicexml.zanzibar.telephony.TelephonyClientImpl;
import org.mrcp4j.client.MrcpInvocationException;
import org.speechforge.cairo.client.NoMediaControlChannelException;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.client.SpeechClientImpl;
import org.speechforge.cairo.sip.SipSession;

public class SpeechletContextMrcpv2Impl implements SpeechletContext, SpeechletContextMrcpProvider {
    
    SpeechletService container;
    SessionProcessor speechlet;
    
    SipSession mrcpSession;
    SipSession pbxSession;
    
    SpeechClient speechClient;
    TelephonyClient telephonyClient;

    
    public void init() throws InvalidContextException {
        if (mrcpSession == null )
            throw new InvalidContextException();
        
        this.speechClient = new SpeechClientImpl(mrcpSession.getTtsChannel(),mrcpSession.getRecogChannel());
        this.telephonyClient = new TelephonyClientImpl(pbxSession.getChannelName());
    }
    
    public void cleanup() {
        mrcpSession = null;
        pbxSession = null;
        speechClient = null;
        telephonyClient = null;
    }
    
    public void dialogCompleted() throws InvalidContextException {
        
        if (container == null)
            //TODO add an exception for uninitialized context
            throw new InvalidContextException();
                
        try {    
            // send the bye request (to the platform -- not speech server)
            // only need to do this if dialog completed gracefully
            // like here whne the speech applet notifies the container that it completed
            //other scenario is that a bye received from teh remote side (phone was hungup)
            pbxSession.getAgent().sendBye(pbxSession);
            //platformSession.getAgent().dispose();
            
            //cancel any active recognition requests
            speechClient.stopActiveRecognitionRequests();
            
            //clean up the dialog (the speech server session is cleaned up here)
            container.stopDialog(pbxSession);
        } catch (SipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MrcpInvocationException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (InterruptedException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (NoMediaControlChannelException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }

    }


    /**
     * @return the container
     */
    public SpeechletService getContainer() {
        return container;
    }


    /**
     * @param container the container to set
     */
    public void setContainer(SpeechletService container) {
        this.container = container;
    }


    /**
     * @return the speechlet
     */
    public SessionProcessor getSpeechlet() {
        return speechlet;
    }


    /**
     * @param speechlet the speechlet to set
     */
    public void setSpeechlet(SessionProcessor speechlet) {
        this.speechlet = speechlet;
    }


    /**
     * @return the externalSession
     */
    public SipSession getPBXSession() {
        return pbxSession;
    }


    /**
     * @param externalSession the externalSession to set
     */
    public void setPBXSession(SipSession externalSession) {
        this.pbxSession = externalSession;
    }


    /**
     * @return the internalSession
     */
    public SipSession getMRCPv2Session() {
        return mrcpSession;
    }


    /**
     * @param internalSession the internalSession to set
     */
    public void setMRCPSession(SipSession internalSession) {
        this.mrcpSession = internalSession;
    }

    /**
     * @return the speechClient
     */
    public SpeechClient getSpeechClient() {
        return speechClient;
    }

    /**
     * @return the telephonyClient
     */
    public TelephonyClient getTelephonyClient() {
        return telephonyClient;
    }



}