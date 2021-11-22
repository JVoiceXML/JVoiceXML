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

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

/**
 * An {@link org.apache.logging.log4j.core.Appender} to capture 
 * {@link LogEvent}s in a list.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
@Plugin(name = "Log4jListAppender", 
        category = Core.CATEGORY_NAME, 
        elementType = Appender.ELEMENT_TYPE)
public class Log4jListAppender extends AbstractAppender {
    /** Captured log events. */
    private final List<LogEvent> events;

    /**
     * Creates a new object.
     * @param name The Appender name.
     * @param filter The Filter to associate with the Appender.
     * @param layout The layout to use to format the event.
     * @param ignoreExceptions If true, exceptions will be logged and
     *          suppressed. If false errors will be logged and
     *          then passed to the application.
     */
    protected Log4jListAppender(final String name, final Filter filter,
            final Layout<? extends Serializable> layout) {
        super(name, filter, layout, true, Property.EMPTY_ARRAY);
        events = new java.util.ArrayList<LogEvent>();
        System.err.println("*** created");
    }
    
    @PluginFactory
    public static Log4jListAppender createAppender(
      @PluginAttribute("name") String name, 
      @PluginElement("Layout") Layout<String> layout,
      @PluginElement("Filter") Filter filter) {
        return new Log4jListAppender(name, filter, layout);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void append(final LogEvent event) {
        events.add(event);
        System.err.println("*** " + event.getMessage().getFormattedMessage());
    }

}
