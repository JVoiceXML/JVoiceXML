/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * @since 0.7
 */
public final class JVoiceXmlClassLoader extends URLClassLoader {
    /** Dynamically added URLs. */
    private final Collection<URL> urls;

    /** The repository that this class loader is responsible for. */
    private final String repository;

    /**
     * Constructs a new object.
     * @param parent the parent class loader.
     */
    public JVoiceXmlClassLoader(final ClassLoader parent) {
        this(parent, null);
    }

    /**
     * Constructs a new object.
     * @param parent the parent class loader.
     * @param repo the repository that this class loader is responsible for
     */
    public JVoiceXmlClassLoader(final ClassLoader parent, final String repo) {
        super(new URL[0], parent);
        urls = new java.util.ArrayList<URL>();
        repository = repo;
    }

    /**
     * Retrieves the used repository
     * @return the repository.
     * @since 0.7.9
     */
    public String getRepository() {
        return repository;
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
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append(getClass());
        str.append('[');
        str.append("repo=");
        str.append(repository);
        for (URL url : urls) {
            str.append(',');
            str.append(url);
        }
        str.append(",parent=");
        str.append(getParent());
        str.append(']');
        return str.toString();
    }
}
