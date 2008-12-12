/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * The <em>JVoiceXMLConfiguration</em> is the base class to retrieve
 * custom configuration settings for each component.
 *
 * <p>
 * This is the central for all configuration settings. It offers
 * access to custom configurations for each component:
 * <ul>
 * <li>implementation</li>
 * <li>interpreter</li>
 * <li>document server</li>
 * </ul>
 *
 * The configuration is structured as spring beans to enable
 * configuration by injection.
 * </p>
 *
 * <p>
 * Access to the configuration can be obtained via<br>
 * <br>
 * <code>
 * JVoiceXmlConfiguration config = JVoiceXmlConfiguration.getInstance()
 * </code><br>
 * <br>
 * Then, the references <code>config</code> can be used to load the
 * beans via:<br>
 * <code>
 * T object = config.loadObject(T.class, &lt;key&gt;);
 * </code>
 * </p>
 *
 * @author Arindam Das
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision$
 */
public final class JVoiceXmlConfiguration {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(JVoiceXmlConfiguration.class);;

    /** The singleton. */
    private static final JVoiceXmlConfiguration CONFIGURATION;

    /** The factory to retrieve configured objects. */
    private XmlBeanFactory factory;

    static {
        final String filename =
                System.getProperty("jvoicexml.config", "/jvoicexml.xml");

        CONFIGURATION = new JVoiceXmlConfiguration(filename);
    }

    /**
     * Do not create from outside.
     *
     * <p>
     * Loads the configuration.
     * </p>
     *
     * @param filename
     *        Location of the configuration file.
     */
    private JVoiceXmlConfiguration(final String filename) {
        final Resource res = new ClassPathResource(filename);
        try {
            factory = new XmlBeanFactory(res);
        } catch (BeansException e) {
            LOGGER.error("unable to load configuration", e);
            factory = null;
        }
    }


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
     */
    public <T extends Object> T loadObject(final Class<T> baseClass,
                                           final String key) {
        if (!factory.containsBean(key)) {
            LOGGER.warn("unable to load object: key '" + key + "' not found");
            return null;
        }
        final Object object;
        try {
            object = factory.getBean(key, baseClass);
        } catch (org.springframework.beans.BeansException e) {
            LOGGER.error("error loading bean '" + key + "'", e);

            return null;
        }

        return baseClass.cast(object);
    }

    /**
     * Get a reference to the singleton.
     *
     * @return The only JVoiceXML configuration instance.
     */
    public static JVoiceXmlConfiguration getInstance() {
        return CONFIGURATION;
    }
}
