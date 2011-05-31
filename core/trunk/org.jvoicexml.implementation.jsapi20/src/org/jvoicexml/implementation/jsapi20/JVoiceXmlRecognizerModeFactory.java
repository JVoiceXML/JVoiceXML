/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.implementation.jsapi10/src/org/jvoicexml/implementation/jsapi10/JVoiceXmlRecognizerModeDescFactory.java $
 * Version: $LastChangedRevision: 2355 $
 * Date:    $Date: 2010-10-07 20:28:03 +0200 (Do, 07 Okt 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.jsapi20;

import javax.speech.SpeechLocale;
import javax.speech.recognition.RecognizerMode;

import org.apache.log4j.Logger;

/**
 * A {@link RecognizerMode} that can be read by the configuration.
 *
 * <p>
 * This is mainly a storage for the settings that can be passed to the
 * constructor of a {@link RecognizerMode} which can be retrieved
 * by the factory method {@link #createDescriptor()}.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2355 $
 * @since 0.7.5
 */
public final class JVoiceXmlRecognizerModeFactory
    implements RecognizerModeFactory {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlRecognizerModeFactory.class);

    /** The desired engine name. */
    private String engineName;

    /** The mode name. */
    private String modeName;

    /** The locale. */
    private SpeechLocale locale;

    /**
     * {@inheritDoc}
     */
    public RecognizerMode createDescriptor() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating recognizer mode desc (" + engineName
                    + ", " + modeName + ", " + locale + ")");
        }
        final SpeechLocale[] locales;
        if (locale == null) {
            locales = null;
        } else {
            locales = new SpeechLocale[] {locale};
        }
        return new RecognizerMode(engineName, modeName, null, null, null, null,
                locales, null);
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
    public void setLocale(final String loc) {
        locale = new SpeechLocale(loc);
    }

    /**
     * Sets the mode name.
     * @param name the mode name to set
     */
    public void setModeName(final String name) {
        modeName = name;
    }
}
