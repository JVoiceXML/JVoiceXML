/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.mock;

import java.io.Serializable;
import java.util.Collection;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;

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
 * Messages that are kept in this appender <b>must</b> begin with
 * {@link #TEST_PREFIX}. All other messages are filtered.
 * @author Dirk Schnelle-Walka
 * @since 0.7
 */
public final class TestAppender implements Appender {
    /** Prefix of messages that are kept in this appender. */
    public static final String TEST_PREFIX = "test: ";

    /** Collected messages. */
    private static Collection<String> messages =
        new java.util.ArrayList<String>();;

    /** Name of the appender. */
    private final String name;

    /** Flag if the appender has been started. */
    private boolean started;
    
    /**
     * Constructs a new object.
     */
    public TestAppender() {
        name = "test";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public State getState() {
        if (started) {
            return State.STARTED;
        } else {
            return State.STOPPED;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        started = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        messages.clear();
        started = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStarted() {
        return started;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStopped() {
        return !started;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void append(LogEvent event) {
      final Message message = event.getMessage();
      final String formattedMessage = message.getFormattedMessage();
      if (!formattedMessage.startsWith(TEST_PREFIX)) {
          return;
      }
      messages.add(formattedMessage);
    }

    /**
     * Checks if the given message is contained in the list of messages.
     * 
     * @param message
     *            the message to look for.
     * @return <code>true</code> if the message is contained.
     */
    public static boolean containsMessage(final String message) {
        return messages.contains(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Layout<? extends Serializable> getLayout() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean ignoreExceptions() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorHandler getHandler() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHandler(ErrorHandler handler) {
    }
}
