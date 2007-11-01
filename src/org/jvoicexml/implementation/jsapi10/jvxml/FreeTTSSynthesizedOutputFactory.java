/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.speech.Central;
import javax.speech.EngineException;

import org.jvoicexml.SynthesizedOuput;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.jsapi10.AbstractJsapi10SynthesizedOutputFactory;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;

/**
 * {@link org.jvoicexml.implementation.ResourceFactory} for the
 * {@link SynthesizedOuput} based on
 * <a href="http://freetts.sourceforge.net">FreeTTS.</a>.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.5.5
 */
public final class FreeTTSSynthesizedOutputFactory
    extends AbstractJsapi10SynthesizedOutputFactory
    implements ResourceFactory<SynthesizedOuput> {
    /** Logger for this class. */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(FreeTTSSynthesizedOutputFactory.class);

    /**
     * Constructs a new object.
     */
    public FreeTTSSynthesizedOutputFactory() {
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
