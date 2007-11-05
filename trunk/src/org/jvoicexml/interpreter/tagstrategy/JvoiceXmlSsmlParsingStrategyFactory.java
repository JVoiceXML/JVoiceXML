/*
 * File:    $RCSfile: JVoiceXmlTagStrategyFactory.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
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

package org.jvoicexml.interpreter.tagstrategy;

import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.interpreter.SsmlParsingStrategy;
import org.jvoicexml.interpreter.SsmlParsingStrategyFactory;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.vxml.Enumerate;
import org.jvoicexml.xml.vxml.Foreach;
import org.jvoicexml.xml.vxml.Value;

/**
 * Factory for {@link SsmlParsingStrategy}s.
 *
 * @see org.jvoicexml.interpreter.SsmlParsingStrategy
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class JvoiceXmlSsmlParsingStrategyFactory
        implements SsmlParsingStrategyFactory {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JvoiceXmlSsmlParsingStrategyFactory.class);

    /**
     * Known strategies. The known strategies are templates for the strategy to
     * be executed used by the {@link org.jvoicexml.interpreter.SsmlParser}.
     */
    private static final Map<String, SsmlParsingStrategy> STRATEGIES;

    static {
        STRATEGIES = new java.util.HashMap<String, SsmlParsingStrategy>();

        STRATEGIES.put(Audio.TAG_NAME, new AudioTagStrategy());
        STRATEGIES.put(Enumerate.TAG_NAME, new EnumerateTagStrategy());
        STRATEGIES.put(Foreach.TAG_NAME, new ForeachTagStrategy());
        STRATEGIES.put(Text.TAG_NAME, new TextStrategy());
        STRATEGIES.put(Value.TAG_NAME, new ValueStrategy());
    }

    /**
     * Construct a new object.
     */
    public JvoiceXmlSsmlParsingStrategyFactory() {
    }

    /**
     * {@inheritDoc}
     */
    public SsmlParsingStrategy getParsingStrategy(final VoiceXmlNode node) {
        if (node == null) {
            LOGGER.warn("cannot get strategy for null");

            return null;
        }

        final String tag = node.getTagName();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getting strategy for tag: '" + tag + "'");
        }

        final SsmlParsingStrategy strategy = STRATEGIES.get(tag);
        if (strategy == null) {
            LOGGER.warn("no suitable strategy for tag: '" + tag + "'");

            return null;
        }

        return (SsmlParsingStrategy) strategy.clone();
    }
}
