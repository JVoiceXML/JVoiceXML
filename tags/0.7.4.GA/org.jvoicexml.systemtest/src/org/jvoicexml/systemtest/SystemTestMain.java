/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jvoicexml.systemtest;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Main class of the JVoiceXML System test.
 *
 * @author Zhang Nan
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class SystemTestMain {
    /** Maximum number of retries to lokk for JVoiceXML. */
    private static final int MAX_RETRIES = 60;

    /** Delay in msec between two lookups for JVoiceXML startup. */
    private static final int DELAY_STARTUP = 1000;

    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(SystemTestMain.class);

    /**
     * Construct a new object. never used.
     */
    private SystemTestMain() {

    }

    /**
     * The main method.
     *
     * @param args Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        LOGGER.info("Starting SystemTest for JVoiceXML...");

        final String filename = System.getProperty("systemtestconfig.config",
                "/systemtestconfig.xml");

        final SystemTestConfigLoader config =
            new SystemTestConfigLoader(filename);
        final SystemTestCallManager cm = config.loadObject(
                SystemTestCallManager.class, "callmanager");
        final JVoiceXml interpreter = findInterpreter();
        if (interpreter == null) {
            LOGGER.error("JVoiceXML not found, exit.");
            return;
        }
        cm.setJVoiceXml(interpreter);
        cm.start();
    }

    /**
     * Retrieves a reference to the JVoiceXML interpreter. If the reference can
     * not be retrieved this methods waits for a certain amount of time and
     * retries.
     * @return reference to the JVoiceXML interpreter <code>null</code> if
     * the reference could not be retrieved.
     */
    private static JVoiceXml findInterpreter() {
        final Context context;
        try {
            context = new InitialContext();
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error creating initial context", ne);
            return null;
        }

        LOGGER.info("Waiting until JVoiceXML started");
        int count = 0;
        do {
            try {
                return (JVoiceXml) context.lookup("JVoiceXml");
            } catch (javax.naming.NamingException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("unable to find the interpreter", e);
                }
            }
            ++count;
            try {
                Thread.sleep(DELAY_STARTUP);
            } catch (InterruptedException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("waiting interrupted", e);
                }
                return null;
            }
        } while (count < MAX_RETRIES);
        return null;
    }

    /**
     * Copy from org.jvoicexml.config.JVoiceXmlConfiguration if dirk modify
     * JVoiceXmlConfiguration(String file) method to public, there need not
     * create this class.
     */
    private static final class SystemTestConfigLoader {
        /** Logger for this class. */
        private static final Logger LOGGER =
            Logger.getLogger(SystemTestConfigLoader.class);

        /** The factory to retrieve configured objects. */
        private final XmlBeanFactory factory;

        /**
         * Construct a new object.
         * @param filename configuration file name.
         */
        public SystemTestConfigLoader(final String filename) {
            final Resource res = new ClassPathResource(filename);
            factory = new XmlBeanFactory(res);
        }

        /**
         * Loads the object which the class defined by the given key.
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
                LOGGER.error(be.getMessage(), be);
                return null;
            }

            return baseClass.cast(object);
        }
    }
}
