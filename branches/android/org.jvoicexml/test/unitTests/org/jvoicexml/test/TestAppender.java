/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/test/TestAppender.java $
 * Version: $LastChangedRevision: 2416 $
 * Date:    $Date: 2010-12-08 03:58:48 -0600 (mié, 08 dic 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.test;

import java.util.Collection;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Test appender to monitor behavior.
 *
 * This appender can be used e.g. in unit tests to check if a certain output
 * has been logged. In order to use this appender in a unit test add the
 * following code snippet:
 * <pre>
 * @BeforeClass
 * public static void init() {
 *     final Logger logger = Logger.getRootLogger();
 *     logger.addAppender(new TestAppender());
 * }
 * </pre>
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2416 $
 * @since 0.7
 */
public final class TestAppender implements Appender {
    /** Collected messages. */
    private static Collection<String> messages =
        new java.util.ArrayList<String>();;

    /** Name of the appender. */
    private String name;

    /** A filter. */
    private Filter filter;

    /**
     * {@inheritDoc}
     */
    public void addFilter(final Filter value) {
        filter = value;
    }

    /**
     * {@inheritDoc}
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * {@inheritDoc}
     */
    public void clearFilters() {
        filter = null;
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        messages.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void doAppend(final LoggingEvent event) {
        final String message = event.getMessage().toString();
        if (!message.startsWith("test: ")) {
            return;
        }
        messages.add(message);
    }

    /**
     * {@inheritDoc}
     */
    public void setErrorHandler(final ErrorHandler handler) {
    }

    /**
     * {@inheritDoc}
     */
    public ErrorHandler getErrorHandler() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setLayout(final Layout layout) {
    }

    /**
     * {@inheritDoc}
     */
    public Layout getLayout() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean requiresLayout() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(final String value) {
        name = value;
    }

    /**
     * Checks if the given message is contained in the list of messages.
     * @param message the message to look for.
     * @return <code>true</code> if the message is contained.
     */
    public static boolean containsMessage(final String message) {
        return messages.contains(message);
    }

    /**
     * Clears all previously stored messages.
     * 
     * @since 0.7.4
     */
    public static void clear() {
        messages.clear();
    }
}
