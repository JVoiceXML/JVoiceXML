/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.implementation.jsapi10.Jsapi10SynthesizedOutput;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.ssml.Break;

/**
 * SSML strategy to play back a <code>&lt;mark&gt;</code> node.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.5
 */
public final class BreakSpeakStrategy
        extends SpeakStrategyBase {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(BreakSpeakStrategy.class);

    /**
     * Creates a new object.
     */
    public BreakSpeakStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public void speak(final Jsapi10SynthesizedOutput output,
            final SsmlNode node)
            throws NoresourceError, BadFetchError {
        final Break breakNode = (Break) node;
        final long msec = breakNode.getTimeAsMsec();
        if (msec == 0) {
            // Do not wait if there is not wait time given.
            return;
        }
        waitQueueEmpty(output);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("break: delaying " + msec + " msecs");
        }
        try {
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
    }
}
