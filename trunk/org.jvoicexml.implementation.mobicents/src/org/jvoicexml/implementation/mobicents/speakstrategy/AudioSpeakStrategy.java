/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.implementation.jsapi10/src/org/jvoicexml/implementation/jsapi10/speakstrategy/AudioSpeakStrategy.java $
 * Version: $LastChangedRevision: 2830 $
 * Date:    $Date: 2011-09-23 18:04:56 +0700 (Fri, 23 Sep 2011) $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.implementation.mobicents.speakstrategy;

import java.net.URI;

import org.apache.log4j.Logger;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.implementation.mobicents.SynthesizedOutputConnectionHandler;
import org.jvoicexml.implementation.mobicents.MobicentsSynthesizedOutput;
import org.jvoicexml.implementation.mobicents.jvxml.MobicentsConnectionHandler;

/**
 * SSML strategy to play back an <code>&lt;audio&gt;</code> node.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2830 $
 * @since 0.5
 */
class AudioSpeakStrategy
    extends SpeakStrategyBase {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(AudioSpeakStrategy.class);

    /**
     * Constructs a new object.
     */
    public AudioSpeakStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public void speak(final MobicentsSynthesizedOutput output,
            final SsmlNode node)
        throws NoresourceError, BadFetchError 
    {
        LOGGER.debug("with output:"+output + " SsmlNode:"+node);
        final Audio audio = (Audio) node;
        final String src = audio.getSrc();
        final URI uri;
        try {
            uri = new URI(src);
        } catch (java.net.URISyntaxException use) {
            throw new BadFetchError(use);
        }
        // Wait until all audio data is delivered before the file playback is
        // started.
        waitQueueEmpty(output);
//        // Play the audio.
        try {
            final DocumentServer server = output.getDocumentServer();
            final String sessionId = output.getSessionid();
            MobicentsConnectionHandler connectionHandler=new MobicentsConnectionHandler(uri);
            output.setSynthesizedOutputConnectionHandler(connectionHandler);
//            Thread.sleep(10000);
        } catch (Exception ex) {
                LOGGER.debug("unable to obtain audio file", ex);
            speakChildNodes(output, node);
        }
    }
}
