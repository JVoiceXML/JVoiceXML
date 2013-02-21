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

import java.net.InetSocketAddress;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.eclipse.debug.ui.launching.IVoiceXMLBrowserConstants;
import org.jvoicexml.eclipse.debug.ui.launching.VoiceXMLDialogMessage;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Listener to receive text events from a TextServer.
 * 
 * @author Aurelian Maga
 * @author Dirk Schnelle-Walka
 */
public class TextServerListener implements TextListener {

    /** The browser */
    private JVoiceXmlBrowser browser;

    public TextServerListener(JVoiceXmlBrowser voiceXmlBrowser) {
        browser = voiceXmlBrowser;
    }

    public void outputText(String document) {
        browser.logMessage(document.toString());

        final DebugEvent event[] = new DebugEvent[1];
        event[0] = new DebugEvent(this, DebugEvent.MODEL_SPECIFIC,
                IVoiceXMLBrowserConstants.EVENT_LOG_MESSAGE);

        final VoiceXMLDialogMessage utterance = new VoiceXMLDialogMessage(
                "System", document);
        event[0].setData(utterance);

        DebugPlugin.getDefault().fireDebugEventSet(event);
    }

    public void outputSsml(final SsmlDocument document) {
        browser.logMessage(document.toString());

        final DebugEvent event[] = new DebugEvent[1];
        event[0] = new DebugEvent(this, DebugEvent.MODEL_SPECIFIC,
                IVoiceXMLBrowserConstants.EVENT_DIALOG_MESSAGE);

        final VoiceXMLDialogMessage utterance = new VoiceXMLDialogMessage(
                "System", document.toString());
        event[0].setData(utterance);

        DebugPlugin.getDefault().fireDebugEventSet(event);
    }

    @Override
    public void connected(InetSocketAddress address) {
        System.out.println("connected to " + address);
    }

    @Override
    public void disconnected() {
        System.out.println("disconnected");
        browser.stop();
    }

	@Override
	public void started() {
		// TODO Auto-generated method stub
		
	}
}
