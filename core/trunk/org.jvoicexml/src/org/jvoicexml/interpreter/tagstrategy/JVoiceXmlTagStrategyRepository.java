/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.interpreter.tagstrategy;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.interpreter.TagStrategy;
import org.jvoicexml.interpreter.TagStrategyFactory;
import org.jvoicexml.interpreter.TagStrategyRepository;
import org.w3c.dom.Node;

/**
 * Basic implementation of a {@link TagStrategyRepository}.
 * <p>
 * This implementation simply stores the available {@link TagStrategyFactory}s
 * for the supported namespaces in a map.
 * </p>
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.4
 */
public class JVoiceXmlTagStrategyRepository implements TagStrategyRepository {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlTagStrategyRepository.class);

    /** Known {@link TagStrategyFactory}s. */
    final static Map<URI, TagStrategyFactory> FACTORIES;

    static {
        FACTORIES = new java.util.HashMap<URI, TagStrategyFactory>();
        TagStrategyFactory vxmlTagStrategyFacory =
            new JVoiceXmlTagStrategyFactory();
        try {
            FACTORIES.put(vxmlTagStrategyFacory.getTagNamespace(),
                    vxmlTagStrategyFacory);
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TagStrategy getTagStrategy(final Node node, final URI namespace) {
        final TagStrategyFactory factory = FACTORIES.get(namespace);
        if (factory == null) {
            return null;
        }
        return factory.getTagStrategy(node);
    }

}
