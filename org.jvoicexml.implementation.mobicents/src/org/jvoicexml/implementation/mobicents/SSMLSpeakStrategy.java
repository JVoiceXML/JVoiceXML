/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.implementation.jsapi10/src/org/jvoicexml/implementation/jsapi10/SSMLSpeakStrategy.java $
 * Version: $LastChangedRevision: 2583 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.xml.SsmlNode;

/**
 * Strategy to play back a node of a SSML document via Mobicents.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2583 $
 * @since 0.5
 */
public interface SSMLSpeakStrategy {
    /**
     * Speaks the given node.
     * @param output the synthesizer to use.
     * @param node The node to speak.
     * @exception NoresourceError
     *            No recognizer allocated.
     * @exception BadFetchError
     *            Recognizer in wrong state.
     */
    void speak(final MobicentsSynthesizedOutput output, final SsmlNode node)
            throws NoresourceError, BadFetchError;
}
