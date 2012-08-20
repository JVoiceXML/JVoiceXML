/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.interpreter.InitializationTagStrategyFactory;
import org.jvoicexml.interpreter.TagStrategy;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * Factory for initialization tag strategies. Initialization tag strategies
 * are used in the initialization phase of the
 * {@link org.jvoicexml.interpreter.FormInterpretationAlgorithm}.
 *
 * @see org.jvoicexml.interpreter.TagStrategy
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.5
 */
public final class JVoiceXmlInitializationTagStrategyFactory
        implements InitializationTagStrategyFactory {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlInitializationTagStrategyFactory.class);

    /**
     * Known strategies. The known strategies are templates for the strategy to
     * be executed by the
     * {@link org.jvoicexml.interpreter.FormInterpretationAlgorithm}.
     */
    private Map<String, TagStrategy> strategies;

    /**
     * Constructs a new object.
     */
    public JVoiceXmlInitializationTagStrategyFactory() {
        // Initialize woth an empty set of strategies
        strategies = new java.util.HashMap<String, TagStrategy>();
    }

    /**
     * Adds the given tag strategies.
     * @param values the tag strategies to add
     * @since 0.7.4
     */
    public void setTagStrategies(final Map<String, TagStrategy> values) {
        strategies = values;
        // Output of the received strategies for debugging
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
        // First check if a strategy can be obtained in general
        if (tag == null) {
            LOGGER.warn("cannot get strategy for null");
            return null;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getting strategy for tag: '" + tag + "'");
        }

        // Retrieve the strategy
        final TagStrategy strategy = strategies.get(tag);
        if (strategy == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("no suitable strategy for tag: '" + tag + "'");
            }
            return null;
        }

        return strategy.newInstance();
    }
}
