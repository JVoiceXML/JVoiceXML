/*
 * File:    $RCSfile: SpeakStratgeyFactory.java,v $
 * Version: $Revision: 1.2 $
 * Date:    $Date: 2006/05/16 07:26:21 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
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

import java.util.Map;

import org.jvoicexml.implementation.jsapi10.SSMLSpeakStrategy;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.ssml.Mark;
import org.jvoicexml.xml.ssml.Speak;

/**
 * Factory for <code>SSMLSpeakStrategy</code>.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.2 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5
 */
public final class SpeakStratgeyFactory {
    /** Known speak strategies. */
    private static final Map<String, SSMLSpeakStrategy> STRATEGIES;

    static {
        STRATEGIES = new java.util.HashMap<String, SSMLSpeakStrategy>();

        STRATEGIES.put(Audio.TAG_NAME, new AudioSpeakStrategy());
        STRATEGIES.put(Mark.TAG_NAME, new MarkSpeakStrategy());
        STRATEGIES.put(Speak.TAG_NAME, new SpeakSpeakStrategy());
        STRATEGIES.put(Text.TAG_NAME, new TextSpeakStrategy());
    }

    /**
     * Do not create.
     */
    private SpeakStratgeyFactory() {
    }

    /**
     * Retrieves the strategy to play back the given node.
     * @param node The SSML node to play back.
     * @return Strategy to play back the node, <code>null</code> if there
     * is none.
     */
    public static SSMLSpeakStrategy getSpeakStrategy(
            final SsmlNode node) {
        final String tag = node.getTagName();

        return STRATEGIES.get(tag);
    }
}