/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.log4j.Logger;
import org.jvoicexml.AudioFileOutput;
import org.jvoicexml.SynthesizedOutput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.xml.SsmlNode;

/**
 * SSML strategy to play back a text node.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5
 */
public final class TextSpeakStrategy
        extends AbstractSpeakStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(TextSpeakStrategy.class);

    /**
     * Creates a new object.
     */
    public TextSpeakStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public void speak(final SynthesizedOutput synthesizer,
            final AudioFileOutput file, final SsmlNode node)
            throws NoresourceError, BadFetchError {
        final String text = node.getNodeValue().trim();

        if (text.length() == 0) {
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("speaking '" + text + "'...");
        }

        synthesizer.queuePlaintext(text);
//        synthesizer.waitOutputEnd();
    }
}
