/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/jsapi10/speakstrategy/MarkSpeakStrategy.java $
 * Version: $LastChangedRevision: 262 $
 * Date:    $Date: 2007-03-29 08:44:52 +0100 (Qui, 29 Mar 2007) $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.implementation.jsapi20.speakstrategy;

import org.jvoicexml.AudioFileOutput;
import org.jvoicexml.SynthesizedOutput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.apache.log4j.Logger;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.ssml.Mark;
import org.jvoicexml.implementation.jsapi20.Jsapi20SynthesizedOutput;

/**
 * SSML strategy to play back a <code>&lt;mark&gt;</code> node.
 *
 * @author Dirk Schnelle
 * @version $Revision: 262 $
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5
 */
public final class MarkSpeakStrategy
        extends AbstractSpeakStrategy {
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
    public void speak(final SynthesizedOutput synthesizer,
            final AudioFileOutput file, final SsmlNode node)
            throws NoresourceError, BadFetchError {
        final Mark markNode = (Mark) node;
        final String mark = markNode.getName();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("waiting for mark '" + mark + "'...");
        }

        Jsapi20SynthesizedOutput syn = (Jsapi20SynthesizedOutput) synthesizer;
        syn.waitQueueEmpty();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("reached mark '" + mark + "'");
        }

        syn.reachedMark(mark);
    }
}
