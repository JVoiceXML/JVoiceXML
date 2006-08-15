/*
 * File:    $RCSfile: InitializationTagStrategyFactory.java,v $
 * Version: $Revision: 1.3 $
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

package org.jvoicexml.interpreter.tagstrategy;

import java.util.Map;

import org.jvoicexml.interpreter.TagStrategy;
import org.jvoicexml.interpreter.TagStrategyFactory;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.vxml.Property;
import org.jvoicexml.xml.vxml.Script;
import org.jvoicexml.xml.vxml.Var;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * Factory for initialization tag strategies.
 *
 * @see org.jvoicexml.interpreter.TagStrategy
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.3 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.5
 */
public final class InitializationTagStrategyFactory
        implements TagStrategyFactory {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(InitializationTagStrategyFactory.class);

    /**
     * Known strategies. The known strategies are templates for the strategy to
     * be executed by the <code>ForminterpreteationAlgorithm</code>.
     */
    private static final Map<String, TagStrategy> STRATEGIES;

    static {
        STRATEGIES = new java.util.HashMap<String, TagStrategy>();

        STRATEGIES.put(Property.TAG_NAME, new PropertyStrategy());
        STRATEGIES.put(Script.TAG_NAME, new ScriptStrategy());
        STRATEGIES.put(Var.TAG_NAME, new VarStrategy());
    }

    /**
     * Constructs a new object.
     */
    public InitializationTagStrategyFactory() {
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

        final TagStrategy strategy = STRATEGIES.get(tag);
        if (strategy == null) {
            LOGGER.warn("no suitable strategy for tag: '" + tag + "'");

            return null;
        }

        return strategy.newInstance();
    }
}
