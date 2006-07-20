/*
 * File:    $RCSfile: Sphinx4EngineCentral.java,v $
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

import javax.speech.EngineCentral;
import javax.speech.EngineList;
import javax.speech.EngineModeDesc;

import org.apache.log4j.Logger;

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
public final class Sphinx4EngineCentral
        implements EngineCentral {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(Sphinx4EngineCentral.class);

    /** The recognizer engine mode descriptor. */
    private final EngineModeDesc desc;

    /**
     * Construct a new object.
     */
    public Sphinx4EngineCentral() {
        desc = new Sphinx4RecognizerModeDesc();
    }

    /**
     * Create an EngineList containing an EngineModeDesc for each mode of
     * operation of a speech engine that matches a set of required features.
     * Each object in the list must be a sub-class of either
     * <code>RecognizerModeDesc</code> or <code>SynthesizerModeDesc</code> and
     * must implement the EngineCreate interface.
     *
     * <p>
     * The Central class ensures that the require parameter is an instance of
     * either <code>RecognizerModeDesc</code> or
     * <code>SynthesizerModeDesc</code>. This enables the EngineCentral to
     * optimize its search for either recognizers or synthesizers.
     * </p>
     *
     * <p>
     * Returns <code>null</code> if no engines are available or if none meet
     * the specified requirements.
     * </p>
     *
     * <p>
     * The returned list should indicate the list of modes available at the
     * time of the call (the list may change over time). The engine can create
     * the list at the time of the call or it may be pre-stored.
     * </p>
     *
     * @param engineModeDesc Descriptor with search parameters.
     * @return List with matching engines.
     * @throws SecurityException
     *         iIf the caller does not have accessEngineModeDesc permission.
     */
    public EngineList createEngineList(final EngineModeDesc engineModeDesc)
            throws SecurityException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating engine list for '" + engineModeDesc + "'");
        }

        if (engineModeDesc == null) {
            final EngineList engines = new EngineList();
            engines.add(desc);

            return engines;
        }

        return null;
    }
}
