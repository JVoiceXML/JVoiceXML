/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.client.text;

import java.net.InetSocketAddress;

import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Listener for text output.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 */
public interface TextListener {
    /**
     * Notification that a connection with the server has been established.
     * @param remote address of the server.
     * @since 0.7
     */
    void connected(InetSocketAddress remote);

    /**
     * Notification that a portion of text has arrived.
     * @param text the received text.
     */
    void outputText(final String text);

    /**
     * Notification that an SSML document has arrived.
     * @param document the received SSML document.
     */
    void outputSsml(final SsmlDocument document);

    /**
     * Notification about a disconnect from the server.
     * @since 0.7
     */
    void disconnected();
}
