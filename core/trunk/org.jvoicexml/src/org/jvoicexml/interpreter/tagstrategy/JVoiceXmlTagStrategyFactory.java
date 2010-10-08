/*
 * File:    $RCSfile: JVoiceXmlTagStrategyFactory.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.interpreter.TagStrategy;
import org.jvoicexml.interpreter.TagStrategyFactory;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * Factory for tag strategies.
 *
 * @see org.jvoicexml.interpreter.TagStrategy
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class JVoiceXmlTagStrategyFactory
        implements TagStrategyFactory {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlTagStrategyFactory.class);

    /**
     * Known strategies. The known strategies are templates for the strategy to
     * be executed by the <code>ForminterpreteationAlgorithm</code>.
     */
    private Map<String, TagStrategy> strategies;

    /**
     * Construct a new object.
     */
    public JVoiceXmlTagStrategyFactory() {
    }

    public void setTagStrategies(final Map<String, TagStrategy> values) {
        strategies = values;
        if (LOGGER.isDebugEnabled()) {
            for (String name : values.keySet()) {
                final TagStrategy strategy = values.get(name);
                LOGGER.debug("added tag strategy '"
                        + strategy.getClass() + "' for tag '" + name + "'");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public TagStrategy getTagStrategy(final VoiceXmlNode node) {
        if (node == null) {
            LOGGER.warn("cannot get strategy for null");

            return null;
        }

        final String tagName = node.getTagName();

        return getTagStrategy(tagName);
    }

    /**
     * {@inheritDoc}
     */
    public TagStrategy getTagStrategy(final String tag) {
        if (tag == null) {
            LOGGER.warn("cannot get strategy for null");

            return null;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getting strategy for tag: '" + tag + "'");
        }
        final TagStrategy strategy = strategies.get(tag);
        if (strategy == null) {
            LOGGER.warn("no suitable strategy for tag: '" + tag + "'");

            return null;
        }

        return strategy.newInstance();
    }
}
