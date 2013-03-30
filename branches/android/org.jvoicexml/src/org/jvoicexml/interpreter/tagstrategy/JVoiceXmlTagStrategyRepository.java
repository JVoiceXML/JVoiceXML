/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/tagstrategy/JVoiceXmlTagStrategyRepository.java $
 * Version: $LastChangedRevision: 2509 $
 * Date:    $Date: 2011-01-16 07:40:14 -0600 (dom, 16 ene 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.interpreter.TagStrategy;
import org.jvoicexml.interpreter.TagStrategyFactory;
import org.jvoicexml.interpreter.TagStrategyRepository;
import org.jvoicexml.xml.vxml.Vxml;
import org.w3c.dom.Node;

/**
 * Basic implementation of a {@link TagStrategyRepository}.
 * <p>
 * This implementation simply stores the available {@link TagStrategyFactory}s
 * for the supported namespaces in a map.
 * </p>
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2509 $
 * @since 0.7.4
 */
public final class JVoiceXmlTagStrategyRepository
    implements TagStrategyRepository {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlTagStrategyRepository.class);

    /** Known {@link TagStrategyFactory}s. */
    private static final  Map<URI, TagStrategyFactory> FACTORIES;

    /** Default tag factory. */
    private TagStrategyFactory vxmlFactory;

    static {
        FACTORIES = new java.util.HashMap<URI, TagStrategyFactory>();
    }

    /**
     * {@inheritDoc}
     * This implementation loads the {@link TagStrategyFactory}s. They can also
     * be added manually by {@link #addTagStrategyFactory(TagStrategyFactory)}.
     */
    @Override
    public void init(final Configuration configuration)
        throws ConfigurationException {
        final Collection<TagStrategyFactory> factories =
            configuration.loadObjects(TagStrategyFactory.class, "tagsupport");
        for (TagStrategyFactory factory : factories) {
            try {
                addTagStrategyFactory(factory);
            } catch (URISyntaxException e) {
                throw new ConfigurationException(e.getMessage(), e);
            }
        }
    }

    /**
     * Adds the given {@link TagStrategyFactory}.
     * @param factory the factory to add.
     * @throws URISyntaxException
     *         if the namespace could not be retrieved
     */
    public void addTagStrategyFactory(final TagStrategyFactory factory)
        throws URISyntaxException {
        final URI namespace = factory.getTagNamespace();
        FACTORIES.put(namespace, factory);
        LOGGER.info("added tag strategy factory '" + factory.getClass()
                + "' for namespace '" + namespace + "'");
        if (namespace.toString().equals(Vxml.DEFAULT_XMLNS)) {
            vxmlFactory = factory;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TagStrategy getTagStrategy(final Node node, final URI namespace) {
        // If there is no namespace, try to use the default factory.
        if (namespace == null) {
            return vxmlFactory.getTagStrategy(node);
        }
        final TagStrategyFactory factory = FACTORIES.get(namespace);
        if (factory == null) {
            return null;
        }
        return factory.getTagStrategy(node);
    }


    
}
