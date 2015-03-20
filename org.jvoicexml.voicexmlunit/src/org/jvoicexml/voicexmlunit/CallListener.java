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

package org.jvoicexml.voicexmlunit;

import java.net.URI;

import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * A listener to monitor the call including the conversation.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.7
 */
public interface CallListener {
    /**
     * The given URI has been called.
     * @param uri the called URI
     */
    void called(final URI uri);

    /**
     * The given document has been heard.
     * @param document the document that has been heard
     */
    void heard(final SsmlDocument document);

    /**
     * The given utterance has been said.
     * @param utterance the utterance
     */
    void said(final String utterance);

    /**
     * The given DTMF has been entered.
     * @param dtmf the DTMF
     */
    void entered(final String dtmf);

    /**
     * The given error happened during calling.
     * @param error the caught error
     */
    void error(final AssertionError error);

    /**
     * The call has been hung up.
     */
    void hungup();
}
