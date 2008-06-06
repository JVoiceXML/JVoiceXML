/*
 * JVoiceXML VTP Plugin
 *
 * Copyright (C) 2006 Dirk Schnelle
 *
 * Copyright (c) 2006 Dirk Schnelle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.vtp.internal.jvoicexml.launcher;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.vtp.launching.IVoiceXMLBrowser;
import org.eclipse.vtp.launching.IVoiceXMLBrowserConstants;
import org.eclipse.vtp.launching.VoiceXMLBrowserInput;
import org.eclipse.vtp.launching.VoiceXMLBrowserProcess;
import org.eclipse.vtp.launching.VoiceXMLLogMessage;
//import org.jvoicexml.Application;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.Session;
import org.jvoicexml.client.text.TextRemoteClient;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.CharacterInput;

/**
 * Interface to the JVoiceXml VoiceXML browser.
 * 
 * @author Dirk Schnelle
 * @author Aurelian Maga
 */
public final class JVoiceXmlBrowser
        implements IVoiceXMLBrowser {
    /** Reference to the related browserProcess. */
    protected VoiceXMLBrowserProcess browserProcess;

    /** URLof the application tp launch. */
    private String launchUrl;

    /** Name of the application. */
    private String applicationName;

    /** Location of the security policy. */
    private String policy;

    /** RMI provider URL. */
    private String providerUrl;

    /** Initial context factory for JNDI. */
    private String initialContextFactory;

    /** Hierarchical URI of the codebase. */
    private String codebase;

    /** Receiver port. */
    private int port;

    /** Debugging level. */
    private String level;

    /** The current session. */
    private Session session;

    /** The session client */
    private RemoteClient client;
    
    /** Text client port number */
    private int textPort;
    
    /** The text server */
    private TextServer textServer;
    
    /**
     * Constructs a new object.
     */
    public JVoiceXmlBrowser() {
    }

    /**
     * Retrieves the inital context.
     * 
     * @return The initial context to use, <code>null</code> in case of an
     *         error.
     */
    private Context getContext() {
        logMessage("trying to obtain the initial context...");
        /** @todo Find a better solution to set the system properties. */
        System.setProperty("java.security.policy", policy);
        System.setProperty("java.rmi.server.codebase", codebase);
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY,initialContextFactory);
        
        //System.setProperty("javax.xml.stream.XMLInputFactory", "com.sun.xml.stream.ZephyrParserFactory");
        //System.setProperty("javax.xml.stream.XMLOutputFactory", "com.sun.xml.stream.ZephyrWriterFactor");
        //System.setProperty("javax.xml.stream.XMLEventFactory", "com.sun.xml.stream.ZephyrEventFactory");
        

        /** @todo Make this configurable. */
        final Hashtable<String, String> env = new Hashtable<String, String>();
        //env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        env.put(Context.PROVIDER_URL, providerUrl);
        env.put("java.naming.rmi.security.manager", "true");

        final Context context;

        try {
            context = new InitialContext(env);
        } catch (javax.naming.NamingException ne) {
            ne.printStackTrace();
            logMessage(ne.getMessage());

            return null;
        }

        logMessage("got initial context");

        return context;
    }

    /**
     * Registers the application at JVoiceXml.
     * 
     * @param context
     *        The context to use.
     * @return The registered application, <code>null</code> in case of an
     *         error.
     */
    /*
    private Application createApplication(final Context context) {
        logMessage("retrieving the application registry...");

        ApplicationRegistry registry;
        try {
            registry = (ApplicationRegistry) context
                    .lookup("ApplicationRegistry");
        } catch (javax.naming.NamingException ne) {
            ne.printStackTrace();
            logMessage(ne.getMessage());

            return null;
        }

        logMessage("got application registry");

        logMessage("Registering application '" + applicationName + "'...");

        final URI uri;

        try {
            uri = new URI(launchUrl);
        } catch (java.net.URISyntaxException use) {
            logMessage(use.getMessage());

            return null;
        }

        final Application application = registry
                .createApplication(applicationName, uri);

        logMessage("registered");

        registry.register(application);

        return application;
    }
	*/
    
    private void startTextServer() {
 
    	 textServer = new TextServer(textPort);
         
    	 final TextServerListener textServerListener = new TextServerListener(this);
    	 textServer.addTextListener(textServerListener);
         
    	 textServer.start();
         
    }
    
    private RemoteClient getClient(){
    	RemoteClient aClient = null;
    	
    	try{
          aClient = textServer.getRemoteClient();
    	}catch(UnknownHostException uhe){
    		 logMessage(uhe.getMessage());
    	}
          
    	
    	return aClient;
    }
    
    private Session getSession(final Context context,RemoteClient client) {
        final JVoiceXml jvxml;
        try {
            jvxml = (JVoiceXml) context.lookup("JVoiceXml");
        } catch (javax.naming.NamingException ne) {
            logMessage(ne.getMessage());

            return null;
        }

        try {
            return jvxml.createSession(client);
        } catch (ErrorEvent ee) {
            logMessage(ee.getMessage());
            
            return null;
        }
    }
    
    /**
     * Calls the voicexml interpreter context to process the given application.
     * 
     * @param context
     *        The current JNDI context.
     */
    private void interpret(final Context context) {
        logMessage("calling application...");

        final URI uri;

        try {
            uri = new URI(launchUrl);
        } catch (java.net.URISyntaxException use) {
            logMessage(use.getMessage());

            return;
        }

        
        try {
            session.call(uri);

            final SessionListener listener = new SessionListener(session, this);
            listener.start();
        } catch (org.jvoicexml.event.JVoiceXMLEvent e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the logging receiver.
     * 
     * @return Started logging receiver.
     */
    private LoggingReceiver startLoggingReceiver() {
        logMessage("starting logging receiver...");
        final JVoiceXmlPlugin plugin = JVoiceXmlPlugin.getDefault();
        final LoggingReceiver receiver = plugin.getReceiver();

        receiver.setBrowser(this);
        receiver.setSession(session);
        receiver.setLevel(level);

        if (!receiver.connect(port)) {
            return null;
        }

        if (!receiver.isStarted()) {
            final Thread thread = new Thread(receiver);
            thread.setDaemon(true);

            thread.start();
        }

        logMessage("logging receiver started");

        return receiver;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        final Context context = getContext();
        if (context == null) {
            return;
        }

        /*
        final Application application = createApplication(context);
        if (application == null) {
            return;
        }
        */
        
        startTextServer();
        
        
        client = getClient();
        if( client == null){
        	return;
        }
        
        session = getSession(context,client);
        if (session == null) {
            return;
        }

        startLoggingReceiver();

        interpret(context);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        if (browserProcess == null) {
            return;
        }
        
        if(textServer!=null){
        	textServer.stopServer();
        }
        
        final JVoiceXmlPlugin plugin = JVoiceXmlPlugin.getDefault();
        final LoggingReceiver receiver = plugin.getReceiver();
        receiver.setSession(null);
        receiver.setBrowser(null);

        try {
            if (session != null) {
                logMessage("stopping session...");
                session.hangup();
                session = null;
                logMessage("session closed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            final DebugEvent event[] = new DebugEvent[2];
            event[0] = new DebugEvent(browserProcess, DebugEvent.TERMINATE);
            event[1] = new DebugEvent(browserProcess.getLaunch(), DebugEvent.CHANGE);
            DebugPlugin.getDefault().fireDebugEventSet(event);

            browserProcess.setTerminated(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void sendInput(final VoiceXMLBrowserInput input) {
    	int inputType = input.getInputType();
        if (inputType==VoiceXMLBrowserInput.TYPE_DTMF) {
            final String dtmf = input.getInput().toString();
            sendDtmf(dtmf);
        }else if(inputType==VoiceXMLBrowserInput.TYPE_VOICE){
        	final String text = input.getInput().toString();
        	sendText(text);
        }
    }

    /**
     * Sends the DTMF to the browser.
     * @param dtmf The DTMF to send.
     */
    private void sendDtmf(final String dtmf) {
        final CharacterInput input;
        
        try {
            input = session.getCharacterInput();
        } catch (NoresourceError nre) {
            logMessage(nre.getMessage());
            
            return;
        }
        
        final char dtmfChar = dtmf.charAt(0);
        input.addCharacter(dtmfChar);

        logMessage(dtmf);
    }

    /**
     * Sends the TEXT to the browser.
     * @param text The TEXT to send.
     */
    private void sendText(final String text) {        
       
        try {
			textServer.sendInput(text);
		} catch (IOException ioe) {
			logMessage(ioe.getMessage());
            return;
		}
        
        logMessage(text);
    }

    
    /**
     * {@inheritDoc}
     */
    public void setProperty(final String name, final Object value) {
        if (IVoiceXMLBrowserConstants.LAUNCH_URL.equals(name)) {
            launchUrl = value.toString();
        } else if (JVoiceXmlPluginConstants.APPLICATION_NAME.equals(name)) {
            applicationName = value.toString();
        } else if (JVoiceXmlPluginConstants.JNDI_POLICY.equals(name)) {
            policy = value.toString();
        } else if (JVoiceXmlPluginConstants.JNDI_CONTEXT_FACTORY.equals(name)) {
            initialContextFactory = value.toString();
        } else if (JVoiceXmlPluginConstants.JNDI_PROVIDER_URL.equals(name)) {
            providerUrl = value.toString();
        } else if (JVoiceXmlPluginConstants.JNDI_CODEBASE.equals(name)) {
            codebase = value.toString();
        } else if (JVoiceXmlPluginConstants.LOGGING_PORT.equals(name)) {
            final Integer configuredPort = (Integer) value;
            port = configuredPort.intValue();
        } else if (JVoiceXmlPluginConstants.LOGGING_LEVEL.equals(name)) {
            level = value.toString();
        } else if (JVoiceXmlPluginConstants.TEXT_PORT.equals(name)) {
            final Integer configuredPort = (Integer) value;
            textPort = configuredPort.intValue();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasCapability(final String capability) {
        if (IVoiceXMLBrowser.CAPABILITY_LOG_EVENT.equals(capability)) {
            return true;
        } else if (IVoiceXMLBrowser.CAPABILITY_INTERACTIVE.equals(capability)) {
            return true;
        } else if (IVoiceXMLBrowser.CAPABILITY_DTMF.equals(capability)) {
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setProcess(final VoiceXMLBrowserProcess process) {
        browserProcess = process;
    }

    /**
     * {@inheritDoc}
     */
    public VoiceXMLBrowserProcess getProcess() {
        return browserProcess;
    }

    /**
     * Convenience method to send a log message to the debug panle.
     * 
     * @param date
     *        Logging timestamp.
     * @param message
     *        The message.
     */
    void logMessage(final Date date, final String message) {
        final DebugEvent event[] = new DebugEvent[1];

        event[0] = new DebugEvent(this, DebugEvent.MODEL_SPECIFIC, IVoiceXMLBrowserConstants.EVENT_LOG_MESSAGE);

        final VoiceXMLLogMessage log = new VoiceXMLLogMessage(date, message);
        event[0].setData(log);

        DebugPlugin.getDefault().fireDebugEventSet(event);
       
    }

    /**
     * Convenience method to send a log message to the debug panle.
     * 
     * @param message
     *        The message.
     */
    void logMessage(final String message) {
        final Date now = new Date();
        logMessage(now, message);
    }
    
 
}
