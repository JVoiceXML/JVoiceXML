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

import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.xml.ssml.SsmlDocument;



/**
 * Listener to receive text events from a TextServer.
 * 
 * @author Aurelian Maga
 */

public class TextServerListener implements TextListener {
	
	/** The browser */
	private JVoiceXmlBrowser browser;

	public TextServerListener(JVoiceXmlBrowser voiceXmlBrowser) {
		// TODO Auto-generated constructor stub
		browser = voiceXmlBrowser;
	}

	public void outputText(String document) {
		// TODO Auto-generated method stub
		browser.logMessage(document.toString());
	}

	public void outputSsml(final SsmlDocument document){
		// TODO Auto-generated method stub
		browser.logMessage(document.toString());
	}
	
}
