package org.jvoicexml.voicexmlunit;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.spi.NamingManager;

import org.jvoicexml.JVoiceXml;

public class Voice {	
	/**
	 * Lookup the JVoiceXML engine
	 * @param configuration Configuration file with settings for JNDI
	 */
	public static JVoiceXml lookup(final File configuration) {
		try {
			final Properties environment = new Properties();
			environment.load(new FileReader(configuration));
			final Context context = NamingManager.getInitialContext(environment);
			return (JVoiceXml)context.lookup("JVoiceXml");
		} catch (javax.naming.NamingException | IOException ne) {
			ne.printStackTrace();
		}
		return null;
	}
}
