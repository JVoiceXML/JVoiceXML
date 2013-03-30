/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/Configuration.java $
 * Version: $LastChangedRevision: 2509 $
 * Date:    $Date: 2011-01-16 07:40:14 -0600 (dom, 16 ene 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

import java.util.Collection;

/**
 * JVoiceXML configuration.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2509 $
 * @since 0.7.4
 */
public interface Configuration {
    /**
     * Loads all objects extending the <code>baseClass</code> from all
     * files with the given configuration base.
     * @param <T> type of class to loads
     * @param baseClass base class of the return type.
     * @param root name of the root element.
     * @return list of objects extending with the given root.
     * @exception ConfigurationException error loading the configuration
     * @since 0.7
     */
    <T extends Object> Collection<T> loadObjects(final Class<T> baseClass,
            final String root) throws ConfigurationException;

    /**
     * Loads the object with the class defined by the given key.
     *
     * @param <T>
     *        Type of the object to load.
     * @param baseClass
     *        Base class of the return type.
     * @param key
     *        Key of the object to load.
     * @return Instance of the class, <code>null</code> if the
     *         object could not be loaded.
     * @exception ConfigurationException error loading the configuration
     */
    <T extends Object> T loadObject(final Class<T> baseClass, final String key)
        throws ConfigurationException;

    /**
     * Loads the object with the class.
     *
     * @param <T>
     *        Type of the object to load.
     * @param baseClass
     *        Base class of the return type.
     * @return Instance of the class, <code>null</code> if the
     *         object could not be loaded.
     * @exception ConfigurationException error loading the configuration
     * @since 0.7
     */
    <T extends Object> T loadObject(final Class<T> baseClass)
        throws ConfigurationException;
}
