/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.implementation.jsapi10/src/org/jvoicexml/implementation/jsapi10/RecognizerModeDescFactory.java $
 * Version: $LastChangedRevision: 2686 $
 * Date:    $Date: 2011-05-31 17:11:26 +0700 (Tue, 31 May 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.speech.recognition.RecognizerModeDesc;

/**
 * A factory for a {@link RecognizerModeDesc}.
 *
 * <p>
 * Objects implementing this interface can be used to create a
 * {@link RecognizerModeDesc} for the {@link org.jvoicexml.implementation.SpokenInput}
 * objects that are created by the
 * {@link Jsapi10SynthesizedOutputFactory}.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2686 $
 * @since 0.6
 */
public interface RecognizerModeDescFactory {

    /**
     * Creates a new descriptor.
     * @return configured descriptor.
     */
    RecognizerModeDesc createDescriptor();
}
