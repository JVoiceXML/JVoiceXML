/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.implementation.jsapi10/src/org/jvoicexml/implementation/jsapi10/jvxml/Sphinx4SpokenInputFactory.java $
 * Version: $LastChangedRevision: 2520 $
 * Date:    $LastChangedDate: 2011-01-19 16:26:12 +0700 (Wed, 19 Jan 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import edu.cmu.sphinx.jsapi.SphinxEngineCentral;
import org.jvoicexml.implementation.mobicents.MobicentsSpokenInputFactory;

/**
 * Demo implementation of a
 * {@link org.jvoicexml.implementation.ResourceFactory} for the
 * {@link org.jvoicexml.implementation.SpokenInput} based on JSAPI 1.0.
 *
 * @author Dirk Schnelle
 * @version $Revision: 2520 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.5.5
 */
public final class MobicentsSphinx4SpokenInputFactory
    extends MobicentsSpokenInputFactory {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(MobicentsSphinx4SpokenInputFactory.class);

    /**
     * Constructs a new object.
     */
    public MobicentsSphinx4SpokenInputFactory() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerEngineCentral() throws EngineException {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("registering sphinx4 engine central...");
            }
            Central.registerEngineCentral(SphinxEngineCentral.class.getName());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("...registered sphinx4 engine central");
            }
        } catch (EngineException ee) {
            LOGGER.error("error registering engine central", ee);
        }
    }
}
