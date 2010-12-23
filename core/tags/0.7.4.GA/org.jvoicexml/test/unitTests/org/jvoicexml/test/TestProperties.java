/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date $, Dirk Schnelle-Walka, project lead
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Settings for the unit tests.
 * <p>
 * Property files <code>test.properties</code> are expected in the folders
 * <code>config-props</code> and <code>personal-props</code> of the core
 * project. The settings in the personal properties override the global
 * settings.
 * </p>
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.3
 */
public final class TestProperties {
    /** The settings. */
    private final Properties settings;

    /**
     * Constructs a new object.
     * @throws IOException if the test properties file could not be
     *           loaded
     */
    public TestProperties() throws IOException {
        settings = new Properties();
        final InputStream globalin = new FileInputStream(
                "../org.jvoicexml/config-props/test.properties");
        settings.load(globalin);
        try {
            final InputStream localin = new FileInputStream(
                "../org.jvoicexml/personal-props/test.properties");
            settings.load(localin);
        } catch (IOException ignore) {
        }
    }

    /**
     * Retrieves the setting for the given key.
     * @param key the key for which to obtain the value
     * @return associated value
     */
    public String get(final String key) {
        return settings.getProperty(key);
    }
}
