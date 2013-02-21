/*
 * File:    $RCSfile: RecognitionEngine.java,v $
 * Version: $Revision: 1.1 $
 * Date:    $Date: 2006/03/27 17:23:04 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.speech.EngineModeDesc;

/**
 * Interace to the JSAPI 1.0 compliant recognition engine.
 *
 * <p>
 * Classes that implement this interface provide an interface to the speech
 * recognizer to use.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.1 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net"
 * >http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public interface RecognitionEngine {
    /**
     * Registers the speech engines with the Central class for use by
     * JVoiceXML.
     *
     * @see javax.speech.EngineCentral
     */
    void registerEngines();

    /**
     * Retrieves the required engine properties.
     * @return Required engine properties or <code>null</code> for default
     * engine selection
     */
    EngineModeDesc getEngineProperties();
}
