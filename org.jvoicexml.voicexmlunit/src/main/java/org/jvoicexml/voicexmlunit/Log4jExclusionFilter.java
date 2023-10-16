/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2021 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.voicexmlunit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

/**
 * A filter to exclude namespaces that are used on the client.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
@Plugin(name = "ExclusionFilter", 
        category = Core.CATEGORY_NAME, 
        elementType = Appender.ELEMENT_TYPE)
public class Log4jExclusionFilter extends AbstractFilter {
    /** The name to exclude. */
    private final String name;
    
    /**
     * Constructs a new object. 
     * @param exclusionName the name to exclude
     */
    protected Log4jExclusionFilter(final String exclusionName) {
        super(null, null);
        name = exclusionName;
    }
    
    /**
     * Context Filter method. Remove all events from loggers with the name
     * to be excluded.
     * @param event The LogEvent.
     * @return The Result of filtering.
     */
    @Override
    public Result filter(final LogEvent event) {
        final String loggerName = event.getLoggerName();
        if (loggerName == null) {
            return Result.NEUTRAL;
        }
        if (loggerName.startsWith(name)) {
            return Result.DENY;
        }
        return Result.NEUTRAL;
    }
    
    /**
     * Context Filter method. Remove all events from loggers with the name
     * to be excluded.
     * @param logger the Logger.
     * @param level The logging Level.
     * @param marker The Marker, if any.
     * @param msg The message, if present.
     * @param t A throwable or null.
     * @return The Result of filtering.
     */
    @Override
    public Result filter(final Logger logger, final Level level, 
            final Marker marker, final Message msg, final Throwable t) {
        final String loggerName = logger.getName();
        if (loggerName == null) {
            return Result.NEUTRAL;
        }
        if (loggerName.startsWith(name)) {
            System.err.println("### denying " + loggerName);
            return Result.DENY;
        }
        return Result.NEUTRAL;
    }

    /**
     * Context Filter method. Remove all events from loggers with the name
     * to be excluded.
     * @param logger the Logger.
     * @param level The logging Level.
     * @param marker The Marker, if any.
     * @param msg The message, if present.
     * @param t A throwable or null.
     * @return The Result of filtering.
     */
    @Override
    public Result filter(final Logger logger, final Level level,
            final Marker marker, final Object msg, final Throwable t) {
        final String loggerName = logger.getName();
        if (loggerName == null) {
            return Result.NEUTRAL;
        }
        if (loggerName.startsWith(name)) {
            return Result.DENY;
        }
        return Result.NEUTRAL;
    }

    /**
     * Context Filter method. Remove all events from loggers with the name
     * to be excluded.
     * @param logger the Logger.
     * @param level The logging Level.
     * @param marker The Marker, if any.
     * @param msg The message, if present.
     * @param params An array of parameters or null.
     * @return The Result of filtering.
     */
    @Override
    public Result filter(final Logger logger, final Level level,
            final Marker marker, final String msg, final Object... params) {
        final String loggerName = logger.getName();
        if (loggerName == null) {
            return Result.NEUTRAL;
        }
        if (loggerName.startsWith(name)) {
            return Result.DENY;
        }
        return Result.NEUTRAL;
    }    

    /**
     * Factory method to create this filter.
     * @param name logger name to be excluded
     * @return new filter
     * @since 0.7.9
     */
    @PluginFactory
    public static Log4jExclusionFilter createFilter(
            @PluginAttribute("name") final String name) {
        return new Log4jExclusionFilter(name);
    }
}
