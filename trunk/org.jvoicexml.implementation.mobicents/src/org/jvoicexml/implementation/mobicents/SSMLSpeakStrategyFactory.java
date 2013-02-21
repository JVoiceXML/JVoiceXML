/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.implementation.jsapi10/src/org/jvoicexml/implementation/jsapi10/SSMLSpeakStrategyFactory.java $
 * Version: $LastChangedRevision: 2355 $
 * Date:    $Date: 2010-10-08 01:28:03 +0700 (Fri, 08 Oct 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.mobicents;

import org.jvoicexml.xml.SsmlNode;

/**
 * Factory for {@link SSMLSpeakStrategy}s.
 *
 * @author Dirk Schnelle
 * @version $Revision: 2355 $
 * @since 0.7
 */
public interface SSMLSpeakStrategyFactory {
    /**
     * Retrieves the strategy to play back the given node.
     * @param node The SSML node to play back.
     * @return Strategy to play back the node, <code>null</code> if there
     * is none.
     */
    SSMLSpeakStrategy getSpeakStrategy(final SsmlNode node);
}
