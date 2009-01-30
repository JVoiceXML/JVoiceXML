/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.config;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * A class loader to allow for loading of other jars that are added as a
 * URL.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class JVoiceXmlClassLoader extends URLClassLoader {
    /**
     * Constructs a new object.
     * @param urls Array of URLs to add to the current classpath.
     * @param parent the parent class laoder.
     */
    public JVoiceXmlClassLoader(final URL[] urls, final ClassLoader parent) {
        super(urls, parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> loadClass(final String name)
        throws ClassNotFoundException {
        @SuppressWarnings("unchecked")
        Class loadedClass = findLoadedClass(name);
        if (loadedClass == null) {
            try {
                loadedClass = findClass(name);
            } catch (ClassNotFoundException e) {
                // Swallow exception
                // does not exist locally
            }
            if (loadedClass == null) {
                loadedClass = super.loadClass(name);
            }
        }
        return loadedClass;
    }
}
