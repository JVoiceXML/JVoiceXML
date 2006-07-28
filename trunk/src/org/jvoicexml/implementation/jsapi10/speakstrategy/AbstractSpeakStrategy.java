/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10.speakstrategy;

import org.jvoicexml.documentserver.DocumentServer;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.jsapi10.AudioOutput;
import org.jvoicexml.implementation.jsapi10.SSMLSpeakStrategy;
import org.jvoicexml.xml.SsmlNode;
import org.w3c.dom.NodeList;

/**
 * Base strategy to play back a node of a SSML document via JSAPI.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5
 */
abstract class AbstractSpeakStrategy
        implements SSMLSpeakStrategy {

    /**
     * Constructs a new object.
     */
    public AbstractSpeakStrategy() {
    }

    /**
     * Calls the speak method for all child nodes of the given node.
     * @param audioOutput The system output to use.
     * @param documentServer The document server.
     * @param node The current node.
     * @exception NoresourceError
     *            No recognizer allocated.
     * @exception BadFetchError
     *            Recognizer in wrong state.
     */
    protected void speakChildNodes(final AudioOutput audioOutput,
                                   final DocumentServer documentServer,
                                   final SsmlNode node)
            throws NoresourceError, BadFetchError {
        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final SsmlNode child = (SsmlNode) children.item(i);
            final SSMLSpeakStrategy strategy =
                    SpeakStratgeyFactory.getSpeakStrategy(child);

            if (strategy != null) {
                strategy.speak(audioOutput, documentServer, child);
            }
        }
    }
}
