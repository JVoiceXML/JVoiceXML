/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date: $, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.systemtest.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * An appender to catch log4j messages. 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class SystemTestAppender extends AppenderSkeleton {
    /** Collected messages. */
    private final List<LoggingEvent> events =
        new java.util.ArrayList<LoggingEvent>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresLayout() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void append(final LoggingEvent event) {
        events.add(event);
    };

    /**
     * Retrieves all logging events.
     * @return all logging events.
     */
    public List<LoggingEvent> getEvents() {
        return events;
    }

    /**
     * Writes the logs to the specified filename using the given layout.
     * @param layout the layout to use.
     * @param file the file where to write the messages.
     * @throws IOException
     *         error writing
     */
    public void writeToFile(final Layout layout, final File file)
            throws IOException {
        final FileWriter writer = new FileWriter(file);
        try {
            for (LoggingEvent event : events) {
                final String message = layout.format(event);
                writer.write(message);
            }
        } finally {
            writer.close();
        }
    }

    /**
     * Clears the list of recorded logging events.
     * 
     * @since 0.7.4
     */
    public void clear() {
        events.clear();
    }
}
