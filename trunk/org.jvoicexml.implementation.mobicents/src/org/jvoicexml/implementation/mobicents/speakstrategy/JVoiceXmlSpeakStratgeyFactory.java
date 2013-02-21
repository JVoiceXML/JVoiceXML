/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.implementation.jsapi10/src/org/jvoicexml/implementation/jsapi10/speakstrategy/JVoiceXmlSpeakStratgeyFactory.java $
 * Version: $LastChangedRevision: 2355 $
 * Date:    $Date: 2010-10-08 01:28:03 +0700 (Fri, 08 Oct 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.log4j.Logger;
import java.util.Map;

import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.ssml.Break;
import org.jvoicexml.xml.ssml.Emphasis;
import org.jvoicexml.xml.ssml.Mark;
import org.jvoicexml.xml.ssml.P;
import org.jvoicexml.xml.ssml.Prosody;
import org.jvoicexml.xml.ssml.S;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.Sub;
import org.jvoicexml.xml.ssml.Voice;
import org.jvoicexml.implementation.mobicents.SSMLSpeakStrategy;
import org.jvoicexml.implementation.mobicents.SSMLSpeakStrategyFactory;

/**
 * Factory for {@link SSMLSpeakStrategy}s.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2355 $
 * @since 0.5
 */
public final class JVoiceXmlSpeakStratgeyFactory
    implements SSMLSpeakStrategyFactory {
        private static final Logger LOGGER = Logger.getLogger(JVoiceXmlSpeakStratgeyFactory.class);
    /** Known speak strategies. */
    private final Map<String, SpeakStrategyBase> strategies;

    /**
     * Creates a new object.
     */
    public JVoiceXmlSpeakStratgeyFactory() {
        // TODO this is not very performant.
        strategies = new java.util.HashMap<String, SpeakStrategyBase>();

        strategies.put(Audio.TAG_NAME, new AudioSpeakStrategy());
//        strategies.put(Break.TAG_NAME, new BreakSpeakStrategy());
//        strategies.put(Emphasis.TAG_NAME, new EmphasisSpeakStrategy());
//        strategies.put(Mark.TAG_NAME, new MarkSpeakStrategy());
        strategies.put(Speak.TAG_NAME, new SpeakSpeakStrategy());
//        strategies.put(P.TAG_NAME, new PSpeakStrategy());
//        strategies.put(Prosody.TAG_NAME, new ProsodySpeakStrategy());
//        strategies.put(S.TAG_NAME, new SSpeakStrategy());
//        strategies.put(Sub.TAG_NAME, new SubSpeakStrategy());
//        strategies.put(Text.TAG_NAME, new TextSpeakStrategy());
//        strategies.put(Voice.TAG_NAME, new VoiceSpeakStrategy());
    }

    /**
     * {@inheritDoc}
     */
    public SSMLSpeakStrategy getSpeakStrategy(final SsmlNode node) 
    {
        if (node == null) {
            return null;
        }
        final String tag = node.getTagName();
        
        SpeakStrategyBase strategy = strategies.get(tag);
        LOGGER.debug("get SpeakStrategyBase:"+strategy+" with tag:"+tag );
        if(strategy!=null)
            strategy.setSSMLSpeakStrategyFactory(this);
        else
            LOGGER.error("error when getting SpeakStrategyBase:"+strategy+" with tag:"+tag + " strategies:"+strategies);
        return strategy;
    }
}
