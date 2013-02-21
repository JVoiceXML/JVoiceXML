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

package org.jvoicexml.eclipse.debug.ui.launching.jvoicexml;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.Session;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.eclipse.debug.ui.launching.IVoiceXMLBrowser;
import org.jvoicexml.eclipse.debug.ui.launching.IVoiceXMLBrowserConstants;
import org.jvoicexml.eclipse.debug.ui.launching.VoiceXMLBrowserInput;
import org.jvoicexml.eclipse.debug.ui.launching.VoiceXMLBrowserProcess;
import org.jvoicexml.eclipse.debug.ui.launching.VoiceXMLLogMessage;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;

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

    /** URL of the application to launch. */
    private String launchUrl;

    /** Location of the security policy. */
    private String policy;

    /** RMI provider URL. */
    private String providerUrl;

    /** Initial context factory for JNDI. */
    private String initialContextFactory;

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
        logMessage("obtaining the initial context...");
        /** @todo Find a better solution to set the system properties. */
        System.setProperty("java.security.policy", policy);
        
        final Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
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


    private void startTextServer() {
        logMessage("starting text server...");
        textServer = new TextServer(textPort);

        final TextServerListener textServerListener = new TextServerListener(
                this);
        textServer.addTextListener(textServerListener);

        textServer.start();
        logMessage("text server started");
    }
    
    private RemoteClient getClient() {
        RemoteClient client = null;
        try {
            client = textServer.getRemoteClient();
        } catch (UnknownHostException uhe) {
            logMessage(uhe.getMessage());
        }
        return client;
    }
    
    private Session getSession(final Context context, RemoteClient client) {
        logMessage("creating a JVoiceXML session...");
        final JVoiceXml jvxml;
        try {
            jvxml = (JVoiceXml) context.lookup("JVoiceXml");
        } catch (javax.naming.NamingException ne) {
            Throwable root = ne.getRootCause();
            if (root == null) {
                logMessage(ne.getMessage());
            } else {
                logMessage(root.getMessage());
            }
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

        startTextServer();
        
        client = getClient();
        if(client == null) {
            stop();
            return;
        }
        
        session = getSession(context, client);
        if (session == null) {
            stop();
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
        receiver.close();
        receiver.setBrowser(null);

        try {
            if (session != null) {
                logMessage("stopping session...");
                session.hangup();
                session = null;
                logMessage("session closed");
            }
        } catch (Exception e) {
            logMessage(e.getMessage());
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
        if (inputType == VoiceXMLBrowserInput.TYPE_DTMF) {
            final String dtmf = input.getInput().toString();
            sendDtmf(dtmf);
        } else if (inputType == VoiceXMLBrowserInput.TYPE_VOICE){
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
        } catch (ConnectionDisconnectHangupEvent e) {
            logMessage(e.getMessage());
            
            return;
        }
        final char dtmfChar = dtmf.charAt(0);
        input.addCharacter(dtmfChar);
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
    }

    
    /**
     * {@inheritDoc}
     */
    public void setProperty(final String name, final Object value) {
        if (IVoiceXMLBrowserConstants.LAUNCH_URL.equals(name)) {
            launchUrl = value.toString();
        } else if (JVoiceXmlPluginConstants.JNDI_POLICY.equals(name)) {
            policy = value.toString();
        } else if (JVoiceXmlPluginConstants.JNDI_CONTEXT_FACTORY.equals(name)) {
            initialContextFactory = value.toString();
        } else if (JVoiceXmlPluginConstants.JNDI_PROVIDER_URL.equals(name)) {
            providerUrl = value.toString();
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
     * Convenience method to send a log message to the debug panel.
     * 
     * @param date
     *        Logging time stamp.
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
     * Convenience method to send a log message to the debug panel.
     * 
     * @param message
     *        The message.
     */
    void logMessage(final String message) {
        if (message == null) {
            return;
        }
        final Date now = new Date();
        logMessage(now, message);
    }

	@Override
	public void hangup() {
		if (session == null) {
			return;
		}
		logMessage("hanging up...");
		session.hangup();
		session = null;
	}
}
