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

import org.apache.log4j.Logger;
import org.jvoicexml.zanzibar.jvoicexml.impl.VoiceXmlSessionProcessor;
import org.jvoicexml.zanzibar.server.SpeechletServerMain;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.client.SpeechClientProvider;
import org.speechforge.cairo.sip.SipSession;


/**
 *  A Speech Application Container that creates applications using a custom sip header which contains a sting of the form
 *  applicationType|applicationName. (for example vxml:HelloWorld or basic:Parrot)
 * 
 * @author Spencer Lord {@literal <}<a href="mailto:salord@users.sourceforge.net">salord@users.sourceforge.net</a>{@literal >}
 */
public class ApplicationBySipHeaderService implements SpeechletService {
    
    private static Logger _logger = Logger.getLogger(ApplicationBySipHeaderService.class);
    
    //contains the active Dialogs   (TODO: Maybe should rename Dialog to Speech Applications or Speech Sessions)
    private  Map<String, SessionProcessor> dialogs;
    
    private SpeechletContext _context;

    private boolean instrumentation;
    
    /**
     * Instantiates a new dialog service impl.
     */
    public ApplicationBySipHeaderService() {
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

        _context = context;
       
        // Get the application name (origninally set  in the underlying platform.  and sent in the sip header
        // It is of the form applicationType|applicationName (for example vxml:HelloWorld or basic|Parrot)
        String aname = context.getExternalSession().getApplicationName();
        if (aname == null) 
            throw new Exception("No Application Specified");

        String app[] = aname.trim().split("\\|");
        _logger.debug("Starting ne dialog, app name: "+aname);
        SessionProcessor dialog = null;
        if (app[0].equals("vxml")) {
            dialog = new VoiceXmlSessionProcessor();
        } else if (app[0].equals("beanId")) {
            // Create an instance of the Speech application/Session  usinh the application name to lookup beanId
            dialog = (SessionProcessor) SpeechletServerMain.context.getBean(app[1]);
        } else if (app[0].equals("classname")) {
            //TODO: catch no class found exception
            Class clazz = Class.forName(app[1]);
            dialog = (SessionProcessor) clazz.newInstance();
        } else {
            throw new Exception("Application Type "+ app[0] + " not supported");  
        }
        
        //setup the context (for speechlet to communicate back to container)
        //SpeechletContext c = new SpeechletContextImpl(this,dialog);
        //dialog.setContext(c);
        
        //turn on or off the instrumentation for this dialog
        dialog.setInstrumentation(instrumentation);
        
        _logger.info("Starting a new "+context.getExternalSession().getApplicationName()+" speechlet with session id = "+ context.getExternalSession().getId());
        dialog.startup(context, app[1]);
        addDialog(dialog);
        return dialog;
    }

    /* (non-Javadoc)
     * @see com.speechdynamix.mrcp.client.DialogService#StopDialog(org.speechforge.cairo.util.sip.SipSession)
     */
    public void StopDialog(SipSession session) throws SipException {
        _logger.info("Stopping a "+session.getApplicationName()+ " speechlet with session id = "+ session.getId());
        SessionProcessor d = getDialog(session.getId());
        d.stop();
        removeDialog(d);
    }
    
    public void dtmf(SipSession session, char code) {

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
     * Adds the dialog.
     * 
     * @param dialog the dialog
     */
    private synchronized void addDialog(SessionProcessor dialog) {
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
            dialogs.get(dialog.getId()).stop();
            dialogs.remove(dialog.getId());           
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
        return dialogs.get(key);
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
