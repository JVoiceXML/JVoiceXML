package org.jvoicexml.voicexmlunit;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.net.URI;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.spi.NamingManager;

import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.event.ErrorEvent;

public class Voice {
	private File configuration = null;
	private Context context = null;
	private JVoiceXml jvxml = null;
	private Session session = null;
	
	public void setPolicy(String path) {
		System.setProperty("java.security.policy",path);
	}
	
	/**
	 * Loads a configuration for JNDI from file
	 * @param configuration path of configuration file with settings for JNDI
	 */
	public void loadConfiguration(String path) {
		configuration = new File(path);
		jvxml = null;
	}
	
	/**
	 * Get the JVoiceXML ibject
	 */
	public JVoiceXml getJVoiceXml() {
		if (jvxml == null) {
			lookupJVoiceXML();
		}
		return jvxml;
	}
	
	/**
	 * Lookup the JVoiceXML object via JNDI
	 */
	public void lookupJVoiceXML() {
		try {
			if (configuration == null) {
				context = new InitialContext();
			}
			else {
				final Properties environment = new Properties();
				environment.load(new FileReader(configuration));
				context = NamingManager.getInitialContext(environment);
			}
			jvxml = (JVoiceXml)context.lookup(JVoiceXml.class.getSimpleName());
		} catch (javax.naming.NamingException | IOException ne) {
			ne.printStackTrace();
		}
	}
	
	/**
	 * @return the recently used Context object for JNDI
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * Connects a new Session object with a dialog
	 * @param connectionInformation the conection details of the server object
	 * @param dialog the dialog to use
	 * @throws ErrorEvent the error happened during the session was active
	 */
	public void connect(ConnectionInformation connectionInformation,
			URI dialog) throws ErrorEvent {
		session = getJVoiceXml().createSession(connectionInformation);
		session.call(dialog);
		session.waitSessionEnd();		
		session.hangup();
		session = null;
	}
	
	/**
	 * Get the currently active Session object
	 * @return the active Session or null if there's none
	 */
	public Session getSession() {
		return session;
	}
}