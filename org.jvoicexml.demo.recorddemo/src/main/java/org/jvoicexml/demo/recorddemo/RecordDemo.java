/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.demo.recorddemo;

import java.io.File;
import java.net.URI;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.jvoicexml.Session;
import org.jvoicexml.client.GenericClient;
import org.jvoicexml.client.UnsupportedResourceIdentifierException;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;

/**
 * Demo implementation for the <code>&lt;record&gt;</code> tag.
 * <p>
 * Must be run with the system property
 * <code>-Djava.security.policy=${config}/jvoicexml.policy</code> and the
 * <code>config</code> folder added to the classpath.
 * </p>
 * <p>
 * This demo requires that JVoiceXML is configured with the jsapi20
 * implementation platform.
 * </p>
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.6
 */
public final class RecordDemo {
	/** Logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(RecordDemo.class);

	/**
	 * Do not create from outside.
	 */
	private RecordDemo() {
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            Command line arguments. None expected.
	 */
	public static void main(final String[] args) {
		LOGGER.info("Starting 'record' demo for JVoiceXML...");
		LOGGER.info("(c) 2008-2017 by JVoiceXML group - "
				+ "http://jvoicexml.sourceforge.net/");

		final GenericClient client = new GenericClient();
		final File file = new File("record.vxml");
		final URI dialog = file.toURI();
		try {
			Session session = client
					.call(dialog, "jsapi20", "jsapi20", "dummy");
			session.waitSessionEnd();
			session.hangup();
		} catch (NamingException e) {
			LOGGER.fatal(e.getMessage(), e);
		} catch (NoresourceError e) {
			LOGGER.info("do you have the jsapi20 implementation platform"
					+ " configured?");
			LOGGER.fatal(e.getMessage(), e);
		} catch (ErrorEvent e) {
			LOGGER.fatal(e.getMessage(), e);
		} catch (UnsupportedResourceIdentifierException e) {
			LOGGER.fatal(e.getMessage(), e);
		} finally {
			client.close();
		}
	}
}
