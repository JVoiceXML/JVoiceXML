/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.implementation.jsapi10/src/org/jvoicexml/implementation/jsapi10/speakstrategy/SpeakSpeakStrategy.java $
 * Version: $LastChangedRevision: 2583 $
 * Date:    $Date: 2011-02-16 23:02:39 +0700 (Wed, 16 Feb 2011) $
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

package org.jvoicexml.implementation.mobicents.speakstrategy;

import com.vnxtele.util.VNXLog;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.implementation.mobicents.MobicentsSynthesizedOutput;

/**
 * SSML strategy to play back a <code>&lt;speak&gt;</code> node.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2583 $
 * @since 0.5
 */
public final class SpeakSpeakStrategy
        extends SpeakStrategyBase {

    /**
     * Creates a new object.
     */
    public SpeakSpeakStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public void speak(final MobicentsSynthesizedOutput output,
            final SsmlNode node)
            throws NoresourceError, BadFetchError 
    {
        VNXLog.debug2("output:"+output);
        speakChildNodes(output, node);
        waitQueueEmpty(output);
    }
}
