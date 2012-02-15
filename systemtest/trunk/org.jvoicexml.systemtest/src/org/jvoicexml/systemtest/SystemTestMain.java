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

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.JVoiceXmlMain;
import org.jvoicexml.JVoiceXmlMainListener;
import org.jvoicexml.config.JVoiceXmlConfiguration;
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
public final class SystemTestMain implements JVoiceXmlMainListener {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(SystemTestMain.class);

    /** Semaphore. */
    private final Object lock;

    /**
     * Construct a new object. never used.
     */
    private SystemTestMain() {
        lock = new Object();
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
        if (cm == null) {
            LOGGER.fatal("error loading the call manager");
            System.exit(-1);
        }
        final SystemTestMain test = new SystemTestMain();
        try {
            final JVoiceXml interpreter = test.startInterpreter();
            cm.setJVoiceXml(interpreter);
            cm.start();
            LOGGER.info("Waiting for JVoiceXML shutdown...");
            synchronized (test.lock) {
                test.lock.wait();
            }
            LOGGER.info("...JVoiceXML shutdown");
        } catch (InterruptedException e) {
            LOGGER.fatal(e.getMessage(), e);
        }
    }

    /**
     * Starts JVoiceXML.
     * @return reference to the JVoiceXML interpreter
     * @throws InterruptedException
     *         error waiting until JVoiceXML started
     */
    private JVoiceXml startInterpreter() throws InterruptedException {
        System.setProperty("jvoicexml.config", "../org.jvoicexml/config");
        final JVoiceXmlConfiguration config = new JVoiceXmlConfiguration();
        final JVoiceXmlMain jvxml = new JVoiceXmlMain(config);
        jvxml.addListener(this);
        jvxml.start();
        LOGGER.info("Waiting for JVoiceXML startup complete...");
        synchronized (lock) {
            lock.wait();
        }
        LOGGER.info("...JVoiceXML started");
        return jvxml;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void jvxmlStarted() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    @Override
    public void jvxmlTerminated() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
