/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

import java.util.Locale;

import javax.speech.recognition.RecognizerModeDesc;

import org.apache.log4j.Logger;

/**
 * A {@link RecognizerModeDesc} that can be read by the configuration.
 *
 * <p>
 * This is mainly a storage for the settings that can be passed to the
 * constructor of a {@link RecognizerModeDesc} which can be retrieved
 * by the factory method {@link #createDescriptor()}.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class JVoiceXmlRecognizerModeDescFactory
    implements RecognizerModeDescFactory {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlRecognizerModeDescFactory.class);

    /** The desired engine name. */
    private String engineName;

    /** The mode name. */
    private String modeName;

    /** The locale. */
    private Locale locale;

    /**
     * {@inheritDoc}
     */
    public RecognizerModeDesc createDescriptor() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating recognizer mode desc (" + engineName
                    + ", " + modeName + ", " + locale + ")");
        }
        return new RecognizerModeDesc(engineName, modeName, locale, null,
                null, null);
    }

    /**
     * Sets the engine name.
     * @param name the engine name to set
     */
    public void setEngineName(final String name) {
        engineName = name;
    }

    /**
     * Sets the locale.
     * @param loc the locale to set.
     */
    public void setLocale(final Locale loc) {
        locale = loc;
    }

    /**
     * Sets the mode name.
     * @param name the mode name to set
     */
    public void setModeName(final String name) {
        modeName = name;
    }
}
