/*
 * File:    $RCSfile: TagStrategyFactory.java,v $
 * Version: $Revision: 3839 $
 * Date:    $Date: 2013-07-17 09:37:34 +0200 (Wed, 17 Jul 2013) $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.xml.VoiceXmlNode;

/**
 * Factory for {@link SsmlParsingStrategy}s.
 *
 * @see org.jvoicexml.interpreter.SsmlParsingStrategy
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3839 $
 * @since 0.6
 */
public interface SsmlParsingStrategyFactory {
    /**
     * Factory method to get a strategy to clone the given node.
     *
     * @param node
     *        The node to clone.
     * @return Strategy, to clone the given node, <code>null</code> if there
     *         is no suitable strategy.
     */
    SsmlParsingStrategy getParsingStrategy(final VoiceXmlNode node);
}
