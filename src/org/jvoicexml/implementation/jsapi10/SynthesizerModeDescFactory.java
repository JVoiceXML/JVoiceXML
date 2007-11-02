/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/jsapi10/SynthesizerModeDescFactory.java $
 * Version: $LastChangedRevision: 535 $
 * Date:    $Date: 2007-11-01 09:36:48 +0100 (Do, 01 Nov 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.speech.synthesis.SynthesizerModeDesc;

/**
 * A factory for a {@link SynthesizerModeDesc}.
 *
 * <p>
 * Objects implementing this interface can be used to create a
 * {@link SynthesizerModeDesc} for the {@link org.jvoicexml.SynthesizedOuput}
 * objects that are created by the
 * {@link AbstractJsapi10SynthesizedOutputFactory}.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 535 $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public interface SynthesizerModeDescFactory {

    /**
     * Creates a new descriptor.
     * @return configured descriptor.
     */
    SynthesizerModeDesc createDescriptor();

}
