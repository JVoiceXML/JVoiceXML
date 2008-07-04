/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/org.jvoicexml/src/org/jvoicexml/implementation/jsapi10/speakstrategy/SpeakStratgeyFactory.java $
 * Version: $LastChangedRevision: 769 $
 * Date:    $Date: 2008-04-15 09:33:17 +0200 (Di, 15 Apr 2008) $
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

package org.jvoicexml.implementation.jsapi10;

import org.jvoicexml.xml.SsmlNode;

/**
 * Factory for {@link SSMLSpeakStrategy}s.
 *
 * @author Dirk Schnelle
 * @version $Revision: 769 $
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
