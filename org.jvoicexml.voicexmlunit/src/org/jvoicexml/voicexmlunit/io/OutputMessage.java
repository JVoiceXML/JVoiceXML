/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.voicexmlunit/src/org/jvoicexml/voicexmlunit/io/Assertion.java $
 * Version: $LastChangedRevision: 3974 $
 * Date:    $Date: 2013-11-23 21:55:27 +0100 (Sat, 23 Nov 2013) $
 * Author:  $LastChangedBy: schnelle $
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

import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * An output message. Output messages are those that are received from JVoicXML.
 * 
 * @author Raphael Groner
 */
public interface OutputMessage {
    /**
     * Receive an output.
     * 
     * @param actual
     *            the received output
     */
    void receive(final SsmlDocument actual);
}