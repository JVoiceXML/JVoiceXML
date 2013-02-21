/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.implementation.jsapi10/src/org/jvoicexml/implementation/jsapi10/jvxml/FreeTTSSynthesizedOutputFactory.java $
 * Version: $LastChangedRevision: 2355 $
 * Date:    $LastChangedDate: 2010-10-08 01:28:03 +0700 (Fri, 08 Oct 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.mobicents.jvxml;

import javax.speech.Central;
import javax.speech.EngineException;

import org.apache.log4j.Logger;

import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;
import org.jvoicexml.implementation.mobicents.MobicentsSynthesizedOutputFactory;

/**
 * {@link org.jvoicexml.implementation.ResourceFactory} for the
 * {@link org.jvoicexml.implementation.SynthesizedOutput} based on
 * <a href="http://freetts.sourceforge.net">FreeTTS.</a>.
 *
 * @author Dirk Schnelle
 * @version $Revision: 2355 $
 *
 * <p>
 * Copyright &copy; 2006-2008 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.5.5
 */
public final class MobicentsXIVRSynthesizedOutputFactory
    extends MobicentsSynthesizedOutputFactory {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(MobicentsSynthesizedOutputFactory.class);

    /**
     * Constructs a new object.
     */
    public MobicentsXIVRSynthesizedOutputFactory() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerEngineCentral() throws EngineException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("registering FreeTTS engine central...");
        }
        Central.registerEngineCentral(FreeTTSEngineCentral.class.getName());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...registered FreeTTS engine central");
        }
    }
}
