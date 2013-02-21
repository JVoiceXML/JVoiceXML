/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/Log4jHandler.java $
 * Version: $LastChangedRevision: 2917 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

import java.text.MessageFormat;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Handler to route <code>java.util.logging</code> messages to log4.
 *
 * <p>
 * Some libraries tend to use the Java built in logging. Since we want to be
 * able to configure everything using log4j, we simply forward all logging
 * requests to Log4j.
 * </p>
 * <p>
 * Therefore, all other logging handlers are removed and this one is
 * left as the only remaining logging handler. Furthermore, the log level
 * is set to root.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @since 0.6
*/
public final class Log4jHandler extends Handler {
    /**
     * Constructs a new object.
     */
    public Log4jHandler() {
        configure();
    }

    /**
     * Initializes the logging environment to work solely with this handler.
     *
     * <p>
     * Removes all registered handlers and registers this handler as the
     * only logging handler for the root namespace.
     * </p>
     */
    private void configure() {
        setLevel(java.util.logging.Level.ALL);
        final java.util.logging.Logger rootLogger =
            java.util.logging.Logger.getLogger("");
        java.util.logging.Handler[] handlers = rootLogger.getHandlers();
        for (java.util.logging.Handler current : handlers) {
            rootLogger.removeHandler(current);
        }
        rootLogger.setLevel(java.util.logging.Level.ALL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(final LogRecord record) {
        final Logger logger = Logger.getLogger(record.getLoggerName());
        final Level level = decode(record.getLevel());
        if (level.isGreaterOrEqual(logger.getEffectiveLevel())) {
            final String message = record.getMessage();
            final Object[] parameters = record.getParameters();
            final  String logMessage = getMessage(message, parameters);
            final Throwable t = record.getThrown();
            final long timestamp = record.getMillis();
            final String fqcn = record.getSourceClassName();
            final LoggingEvent event =
                new LoggingEvent(fqcn, logger, timestamp, level, logMessage, t);
            logger.callAppenders(event);
        }
    }

    /**
     * Retrieves the final logging message including the parameters.
     * @param message the message
     * @param parameters potential parameters
     * @return expanded message
     * @since 0.7.5
     */
    private String getMessage(final String message, final Object[] parameters) {
        if ((parameters == null) || (parameters.length == 0)) {
            return message;
        }
        return MessageFormat.format(message, parameters);
    }

    /**
     * Decodes the given java logging {@link java.util.logging.Level} into a
     * log4j {@link Level}.
     * @param level the level to decode.
     * @return decoded level.
     */
    private Level decode(final java.util.logging.Level level) {
        int i = level.intValue();
        if (i <= java.util.logging.Level.FINEST.intValue()) {
            return Level.TRACE;
        } else if (i <= java.util.logging.Level.FINER.intValue()) {
            return Level.TRACE;
        } else if (i <= java.util.logging.Level.FINE.intValue()) {
            return Level.DEBUG;
        } else if (i <= java.util.logging.Level.INFO.intValue()) {
            return Level.INFO;
        } else if (i <= java.util.logging.Level.WARNING.intValue()) {
            return Level.WARN;
        } else if (i <= java.util.logging.Level.SEVERE.intValue()) {
            return Level.ERROR;
        }
        return Level.INFO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
    }

}
