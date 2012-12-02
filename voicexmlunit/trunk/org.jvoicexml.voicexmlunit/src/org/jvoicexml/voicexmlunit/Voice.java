package org.jvoicexml.voicexmlunit;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.spi.NamingManager;

import org.jvoicexml.JVoiceXml;

public class Voice {
	private File configuration = null;
	private Context context = null;
	private JVoiceXml jvxml = null;
	
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
	 * Lookup the JVoiceXML engine
	 */
	public JVoiceXml getJVoiceXml() {
		if (jvxml == null) {
			lookupJVoiceXML();
		}
		return jvxml;
	}
	
	private void lookupJVoiceXML() {
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
	
	public Context getContext() {
		return context;
	}
}