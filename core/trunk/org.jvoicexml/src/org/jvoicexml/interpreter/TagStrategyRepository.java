/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.interpreter;

import java.net.URI;

import org.jvoicexml.Configuration;
import org.w3c.dom.Node;


/**
 * {@link TagStrategy} repository that knows several {@link TagStrategyFactory}s
 * for different namespaces.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.4
 */
public interface TagStrategyRepository {
    /**
     * Initializes this tag strategy repository.
     * @param configuration the configuration to use.
     * @exception Exception
     *            error initializing the repository
     * @since 0.7
     */
    void init(final Configuration configuration) throws Exception;

    /**
     * Factory method to get a strategy to process the given node in the given
     * namespace.
     *
     * @param node
     *        the node to process
     * @param namespace
     *        the namespace of the node
     * @return Strategy, to process the given node, <code>null</code> if there
     *         is no suitable strategy.
     */
    TagStrategy getTagStrategy(final Node node, final URI namespace);
}
