/*
 * File:    $RCSfile: Sphinx4RecognizerModeDesc.java,v $
 * Version: $Revision: 1.2 $
 * Date:    $Date: 2006/05/17 08:20:22 $
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


package org.jvoicexml.implementation.jsapi10.jvxml;

import java.util.Locale;

import javax.speech.Engine;
import javax.speech.EngineCreate;
import javax.speech.EngineException;
import javax.speech.recognition.RecognizerModeDesc;

/**
 * JSAPI wrapper for sphinx4.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.2 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class Sphinx4RecognizerModeDesc
        extends RecognizerModeDesc implements EngineCreate {
    /**
     * Construct a new object.
     */
    public Sphinx4RecognizerModeDesc() {
        super("sphinx4", null, Locale.US, true, true, null);
    }

    /**
     * Create an engine with the properties specified by this object. A new
     * engine should be created in the <code>DEALLOCATED</code> state.
     *
     * @return The created engine.
     *
     * @throws IllegalArgumentException
     *         The properties of the EngineModeDesc do not refer to a known
     *         engine or engine mode
     * @throws EngineException
     *         The engine defined by this EngineModeDesc could not be properly
     *         created.
     * @throws SecurityException
     *         If the caller does not have createRecognizer permission but is
     *         attempting to create a Recognizer
     */
    public Engine createEngine()
            throws IllegalArgumentException, EngineException,
            SecurityException {
        return new Sphinx4Recognizer();
    }
}
