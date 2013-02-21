/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.net.URISyntaxException;

import org.w3c.dom.Node;


/**
 * Factory for {@link TagStrategy}s from the namespace returned by
 * {@link #getTagNamespace()} that can be executed while interpreting
 * the form items.
 *
 * @see org.jvoicexml.interpreter.TagStrategy
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public interface TagStrategyFactory {
    /**
     * Retrieves the namespace of the tags that are returned by this factory.
     * @return supported namespace
     * @exception URISyntaxException
     *            error creating the URI for the namespace
     * @since 0.7.4
     */
    URI getTagNamespace() throws URISyntaxException;

    /**
     * Factory method to get a strategy to process the given node.
     *
     * @param node
     *        The node to process.
     * @return Strategy, to process the given node, <code>null</code> if there
     *         is no suitable strategy.
     */
    TagStrategy getTagStrategy(final Node node);

    /**
     * Factory method to get a strategy to process a node with the given name.
     *
     * @param tag
     *        Tagname of the node to process.
     * @return Strategy, to process a node with the given name,
     *         <code>null</code> if there is no suitable strategy.
     */
    TagStrategy getTagStrategy(final String tag);
}
