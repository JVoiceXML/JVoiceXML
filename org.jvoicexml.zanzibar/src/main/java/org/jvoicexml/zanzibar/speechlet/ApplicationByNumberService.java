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

import java.util.Hashtable;
import java.util.Map;

import javax.sip.SipException;
import javax.sip.address.SipURI;
import javax.sip.address.URI;

import org.apache.log4j.Logger;
import org.jvoicexml.zanzibar.server.SpeechletServerMain;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.client.SpeechClientProvider;
import org.speechforge.cairo.sip.SipSession;

import gov.nist.javax.sip.header.SIPHeaderNames;
import gov.nist.javax.sip.header.To;

// TODO: Auto-generated Javadoc
/**
 * An Speech Application Container that creates applications using name in the TO header (the number called) prepended by an underscore.
 * 
 * @author Spencer Lord {@literal <}<a href="mailto:salord@users.sourceforge.net">salord@users.sourceforge.net</a>{@literal >}
 */
public class ApplicationByNumberService implements SpeechletService {
    
    /** The _logger. */
    private static Logger _logger = Logger.getLogger(ApplicationByNumberService.class);
    
    //contains the active Dialogs   (TODO: Maybe should rename Dialog to Speech Applications or Speech Sessions)
    /** The dialogs. */
    private  Map<String, SessionProcessor> dialogs;

    private boolean instrumentation;
    
    
    /**
     * Instantiates a new application by number dialog service.
     */
    public ApplicationByNumberService() {
        super();
    }
    
    /* (non-Javadoc)
     * @see com.speechdynamix.mrcp.client.DialogService#startup()
     */
    public void startup() {
        dialogs = new Hashtable<String, SessionProcessor>();
    }
    
    /* (non-Javadoc)
     * @see com.speechdynamix.mrcp.client.DialogService#shutdown()
     */
    public void shutdown() {
        dialogs = null;
    }

    /* (non-Javadoc)
     * @see com.speechdynamix.mrcp.client.DialogService#startNewDialog(org.speechforge.cairo.util.sip.SipSession)
     */
    public SessionProcessor startNewDialog(SpeechletContext context) throws Exception {

        //Get the Application name from the Sip TO Header. Theis Dialog Services uses the phone number called as the name of the application/session/dialog to run
        //TODO: revisit the location of the application name.  SHould it be in this session rather in the forwarding session? 
        SipSession session = context.getExternalSession();
        To toHeader = (To) session.getRequest().getRequest().getHeader(SIPHeaderNames.TO);
        URI uri = toHeader.getAddress().getURI();
        String aname = null;
        if (uri.isSipURI()) {
            aname = ((SipURI) uri).getUser();
        } else {
            _logger.warn("Unhandled URI type in SIP TO header ");
        }

        
        _logger.debug("session id:" +session.getId());
        _logger.debug("session id.forward:" +session.getForward().getId());
        if (aname == null) 
            throw new Exception("No Application Specified");
        
        // Create an instance of the Speech application/Session  usinh the application name (number called) retrieved from the sip "TO" header
        SessionProcessor dialog = (SessionProcessor) SpeechletServerMain.context.getBean("_"+aname);
        
        //Maybe the number does not have an corresponding application configured in which case the dialog will be null
        if (dialog == null) 
            throw new Exception("No Application Found for "+aname);
        
        //turn on or off the instrumentation for this dialog
        dialog.setInstrumentation(instrumentation);
        
        //Start the session/dialog/application
        dialog.startup(context);
        addDialog(dialog);
        return dialog;
    }

    /* (non-Javadoc)
     * @see com.speechdynamix.mrcp.client.DialogService#StopDialog(org.speechforge.cairo.util.sip.SipSession)
     */
    public void StopDialog(SipSession session) throws SipException {
        _logger.info("Stoping Session: "+session.getId());
        SessionProcessor d = getDialog(session.getId());
        if (d == null) {
            _logger.warn("stopping a dialog -- but not in the list");
        } else {
           d.stop();
           removeDialog(d);
        }
    }
    

    /**
     * Adds the dialog.
     * 
     * @param dialog the dialog
     */
    private synchronized void addDialog(SessionProcessor dialog) {
        _logger.debug("adding Dialog with sessionid: "+dialog.getId());
        if (dialog.getSession() != null) {
            dialogs.put(dialog.getId(), dialog);
        } else {
            // TODO: invalid session
            _logger.info("Can not add to session queue.  Invalid session.  No dialog.");
        }
    }

    /**
     * Removes the dialog.
     * 
     * @param dialog the dialog
     * 
     * @throws SipException the sip exception
     */
    private synchronized void removeDialog(SessionProcessor dialog) throws SipException {
        if (dialog.getSession() != null) {
            //dialogs.get(dialog.getId()).stop();
            SessionProcessor d = dialogs.remove(dialog.getId());
        } else {
            // TODO: invalid session
            _logger.info("Can not remove from session queue.  Invalid session.  No dialog.");
        }
    }

    /**
     * Gets the dialog.
     * 
     * @param key the key
     * 
     * @return the dialog
     */
    private synchronized SessionProcessor getDialog(String key) {
        SessionProcessor sp = dialogs.get(key);
        return sp;
    }

    public void dtmf(SipSession session, char code) {
        _logger.debug("Dialog Sevice got a dtmf signal, code= "+code);

        
        //get the session Processor
        SessionProcessor p = getDialog(session.getId());
        
        //from the session processor get the mrcpClient
        SpeechClient client = p.getClient();
        

        //cast to the provider interface and pass in the dtmf code to the client
        // (it will determine if it should pass it on to application after checking if it was enabled
        //  and if there is a grammar or pattern match)
        ((SpeechClientProvider)client).characterEventReceived(code);
      
        
    }

	/**
     * @return the instrumentation
     */
    public boolean isInstrumentation() {
    	return instrumentation;
    }

	/**
     * @param instrumentation the instrumentation to set
     */
    public void setInstrumentation(boolean instrumentation) {
    	this.instrumentation = instrumentation;
    }

}
