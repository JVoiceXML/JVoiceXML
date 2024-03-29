/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/tagstrategy/JVoiceXmlTagStrategyFactory.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.profile.mmi;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.profile.TagStrategy;
import org.jvoicexml.profile.TagStrategyFactory;
import org.jvoicexml.xml.vxml.Vxml;
import org.w3c.dom.Node;

/**
 * Factory for tag strategies of the MMI profile.
 *
 * @see org.jvoicexml.profile.TagStrategy
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4080 $
 */
public final class MmiTagStrategyFactory implements TagStrategyFactory {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(MmiTagStrategyFactory.class);

    /** The profile. */
    private MmiProfile profile;

    /**
     * Known strategies. The known strategies are templates for the strategy to
     * be executed by the <code>ForminterpreteationAlgorithm</code>.
     */
    private Map<String, TagStrategy> strategies;

    /**
     * Construct a new object.
     */
    public MmiTagStrategyFactory() {
    }

    /**
     * Sets the profile.
     * @param value the profile
     */
    public void setProfle(final MmiProfile value) {
        profile = value;
    }

    /**
     * Adds the given tag strategies.
     * 
     * @param values
     *            the tag strategies to add
     */
    public void setTagStrategies(final Map<String, TagStrategy> values) {
        strategies = values;
        if (LOGGER.isDebugEnabled()) {
            for (String name : values.keySet()) {
                final TagStrategy strategy = values.get(name);
                LOGGER.debug("added tag strategy '" + strategy.getClass()
                        + "' for tag '" + name + "'");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public TagStrategy getTagStrategy(final Node node) {
        if (node == null) {
            LOGGER.warn("cannot get strategy for null");
            return null;
        }

        final String tagName = node.getLocalName(); // namespace aware
        if (tagName == null) {
            // for #text etc.s
            final String nodeName = node.getNodeName();
            return getTagStrategy(nodeName);
        }
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

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("getting strategy for tag: '" + tag + "'");
        }
        final TagStrategy strategy = strategies.get(tag);
        if (strategy == null) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.warn("no suitable strategy for tag: '" + tag + "'");
            }

            return null;
        }

        final TagStrategy tagstrategy = strategy.newInstance();
        if (tagstrategy instanceof ProfileAwareTagStrategy) {
            final ProfileAwareTagStrategy profileAwareTagStrategy =
                    (ProfileAwareTagStrategy) tagstrategy;
            profileAwareTagStrategy.setProfile(profile);
        }
        return tagstrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getTagNamespace() throws URISyntaxException {
        return new URI(Vxml.DEFAULT_XMLNS);
    }
}
