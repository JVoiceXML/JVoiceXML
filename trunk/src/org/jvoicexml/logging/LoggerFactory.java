/*
 * File:    $RCSfile: LoggerFactory.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
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

package org.jvoicexml.logging;


import org.jvoicexml.logging.jvxml.JVoiceXmlLogger;

/**
 * This class returns the appropriately-configured logging
 * implementation instance.
 *
 * <p>The user should only have to place a logging configuration XML
 * file into their CLASSPATH for the logging to pick it up
 * and use the appropriate logging tool.</p>
 *
 * @author Shaun Childers
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class LoggerFactory {
    /** The logging configuration to use. */
    private static LoggingConfiguration config;

    /**
     * Do not allow the no-arg constructor.
     */
    private LoggerFactory() {
    }

    /**
     * Get a new instance of the logging implementation.
     * @param clazz The class to attach the logger to.
     * @return The Logger implementation.
     */
    public static Logger getLogger(final Class clazz) {
        // Get the logger from configuration
        final LoggingConfiguration configuration = getConfiguration();
        final Class logClass = config.getLoggingImplementation();
        // Create an instance of the logger
        Logger logger = null;
        try {
            logger = (Logger) logClass.newInstance();
        } catch (Exception e) {
            // Catch any exception here
            logger = new JVoiceXmlLogger();
        }
        logger.seed(clazz);

        return logger;
    }

    /**
     * Load the logging configuration.
     * @return LoggingConfiguration
     */
    private  static synchronized LoggingConfiguration getConfiguration() {
        if (config != null) {
            return config;
        }

//        final JVoiceXmlConfiguration configuration =
//                JVoiceXmlConfiguration.getInstance();
//
//        config = configuration.loadObject(LoggingConfiguration.class,
//                                          LoggingConfiguration.CONFIG_KEY);

        /** @todo Find a solution to bootstrap logging configuration. */
        config = new LoggingConfiguration();
        config.setLogger(org.jvoicexml.logging.jvxml.JVoiceXmlLogger.class.
                         getName());

        return config;
    }
}
