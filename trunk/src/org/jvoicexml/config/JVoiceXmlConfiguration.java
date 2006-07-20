/*
 * File:    $RCSfile: JVoiceXmlConfiguration.java,v $
 * Version: $Revision: 1.23 $
 * Date:    $Date: 2006/06/07 07:40:49 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * The <em>JVoiceXMLConfiguration</em> is the base class to retrieve
 * custom configuration settings for each component.
 *
 * <p>
 * This is the central for all configuration settings. It offerss
 * access to custom configurations for each component:
 * <ul>
 * <li>implementation</li>
 * <li>interpreter</li>
 * <li>document server</li>
 * </ul>
 *
 * The configuration is structured accordingly:<br>
 * <br>
 * <code>
 * &lt;jvoicexml&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;implementation&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/implementation&gt;<br>
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;interpreter&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/interpreter&gt;<br>
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;documentserver&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/documentserver&gt;<br>
 * &lt;/jvoicexml&gt;<br>
 * </code> <br>
 * The configuration settings are accessable by wrapper classes as an
 * abstraction to the real configuration. Thus, the access to the
 * configuration files and the usedmechanism stays transparent to the
 * modules.
 * </p>
 *
 * <p>
 * Access to the configuration can be obtained via<br>
 * <br>
 * <code>
 * JVoiceXmlConfiguration config = JVoiceXmlConfiguration.getInstance()
 * </code><br>
 * <br>
 * Then, the references <code>config</code> can be used to obtain
 * the configuration wrapper for the component of interest.
 * </p>
 *
 * @author Arindam Das
 * @author Dirk Schnelle
 * @version $Revision: 1.23 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/ </a>
 * </p>
 */
public final class JVoiceXmlConfiguration {
    /** The singleton. */
    private static final JVoiceXmlConfiguration CONFIGURATION;

    /** The factory to retrieve configured objects. */
    private final XmlBeanFactory factory;

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
        factory = new XmlBeanFactory(res);
    }


    /**
     * Loads the object whith the class defined by the given key.
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
        final Object object;

        try {
            object = factory.getBean(key, baseClass);
        } catch (org.springframework.beans.BeansException be) {
            be.printStackTrace();

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
