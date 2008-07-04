/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioFileOutput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.jsapi10.Jsapi10SynthesizedOutput;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.ssml.Mark;

/**
 * SSML strategy to play back a <code>&lt;mark&gt;</code> node.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5
 */
public final class MarkSpeakStrategy
        extends SpeakStrategyBase {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(MarkSpeakStrategy.class);

    /**
     * Creates a new object.
     */
    public MarkSpeakStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public void speak(final SynthesizedOutput output,
            final AudioFileOutput file, final SsmlNode node)
            throws NoresourceError, BadFetchError {
        final Mark markNode = (Mark) node;
        final String mark = markNode.getName();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("waiting for mark '" + mark + "'...");
        }

        Jsapi10SynthesizedOutput syn = (Jsapi10SynthesizedOutput) output;
        output.waitQueueEmpty();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("reached mark '" + mark + "'");
        }

        syn.reachedMark(mark);
    }
}
