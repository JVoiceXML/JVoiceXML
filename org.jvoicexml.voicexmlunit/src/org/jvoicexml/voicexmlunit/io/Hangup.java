/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit.io;

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * @author thesis
 * Simulation of an hangup event while there's still an assertion to process.
 * This is a wrapper for the assertion and the hangup is compared to it.
 */
public final class Hangup extends Output {
    
    private Assertion assertion;
    
    /**
     * @param statement the final statement
     */
    public Hangup(final Assertion assertion) {
        super("## disconnected ##");
        this.assertion = assertion;
    }
    
    /**
     * @return the statement converted into Ssml
     * @throws ParserConfigurationException
     */
    public SsmlDocument toSsml() 
            throws ParserConfigurationException {
        SsmlDocument doc = new SsmlDocument();
        if (assertion != null) {
            doc.getSpeak().setTextContent(assertion.toString());
        }
        return doc;
    }
}
