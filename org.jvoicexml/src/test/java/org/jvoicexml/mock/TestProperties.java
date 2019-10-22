/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.mock;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Settings for the unit tests.
 * <p>
 * It mimics the the behavior of gradle to first load the settings in
 * {@code $PROJECT_ROOT/gradle.properties} and override these with the local
 * copy in {@code $HOME/.gradle}.
 * </p>
 * @author Dirk Schnelle-Walka
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
                "../gradle.properties");
        settings.load(globalin);
        final String home = System.getProperty("user.home");
        try {
            final InputStream localin = new FileInputStream(
                home + "/.gradle/gradle.properties");
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
