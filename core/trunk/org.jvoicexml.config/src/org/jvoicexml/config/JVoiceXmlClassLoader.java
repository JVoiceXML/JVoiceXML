/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/config/JVoiceXmlClassLoader.java $
 * Version: $LastChangedRevision: 2129 $
 * Date:    $LastChangedDate: 2010-04-09 11:33:10 +0200 (Fr, 09 Apr 2010) $
 * Author:  $LastChangedBy: schnelle $
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
import java.util.Collection;

/**
 * A class loader to allow for loading of other jars that are added as a
 * URL.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2129 $
 * @since 0.7
 */
public final class JVoiceXmlClassLoader extends URLClassLoader {
    /** Dynamically added URLs. */
    private final Collection<URL> urls;

    /**
     * Constructs a new object.
     * @param parent the parent class loader.
     */
    public JVoiceXmlClassLoader(final ClassLoader parent) {
        super(new URL[0], parent);
        urls = new java.util.ArrayList<URL>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addURL(final URL url) {
        if (urls.contains(url)) {
            return;
        }

        urls.add(url);
        super.addURL(url);
    }

    /**
     * Adds the given URLs to the classpath.
     * @param additions URLs to add.
     */
    public void addURLs(final URL[] additions) {
        for (URL url : additions) {
            addURL(url);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> loadClass(final String name)
        throws ClassNotFoundException {
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass == null) {
            try {
                if (name.startsWith("java.")) {
                    loadedClass = Class.forName(name);
                } else {
                    loadedClass = findClass(name);
                }
            } catch (ClassNotFoundException e) {
                // Swallow exception
                // does not exist locally
            }
            if (loadedClass == null) {
                if (name.startsWith("java.")) {
                    final ClassLoader systemLoader =
                        ClassLoader.getSystemClassLoader();
                    loadedClass = systemLoader.loadClass(name);
                } else {
                    loadedClass = super.loadClass(name);
                }
            }
        }
        return loadedClass;
    }
}
