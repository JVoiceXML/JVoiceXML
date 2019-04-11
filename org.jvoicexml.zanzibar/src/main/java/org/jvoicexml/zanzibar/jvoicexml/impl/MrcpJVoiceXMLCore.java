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
package org.jvoicexml.zanzibar.jvoicexml.impl;

import org.apache.log4j.Logger;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.Session;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.interpreter.GrammarProcessor;
import org.jvoicexml.zanzibar.server.SpeechletServerMain;

public class MrcpJVoiceXMLCore implements JVoiceXmlCore {
    private static final Logger _logger = Logger.getLogger(MrcpJVoiceXMLCore.class);

    public DocumentServer getDocumentServer() {
        return  (DocumentServer) SpeechletServerMain.context.getBean("documentserver");
    }

    public GrammarProcessor getGrammarProcessor() {
        return (GrammarProcessor) SpeechletServerMain.context.getBean("grammarprocessor");
    }

    public Session createSession(ConnectionInformation info) throws ErrorEvent {
        _logger.debug("MrcpJVoiceXMLCore.getSession(remoteclient) called.  Not implemented.");
        return null;
    }

    public String getVersion() {
        _logger.debug("MrcpJVoiceXMLCore.getVersion() called.  Not implemented.");
        return null;
    }

    public void shutdown() {
        _logger.debug("MrcpJVoiceXMLCore.shutdown() called.  Not implemented.");

    }

	@Override
	public Configuration getConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
