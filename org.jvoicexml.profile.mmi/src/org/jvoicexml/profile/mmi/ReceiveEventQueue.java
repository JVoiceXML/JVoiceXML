/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/tagstrategy/AssignStrategy.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $LastChangedDate: 2013-12-17 09:46:17 +0100 (Tue, 17 Dec 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.profile.mmi;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jvoicexml.event.EventBus;
import org.jvoicexml.event.EventSubscriber;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.mmi.events.AnyComplexType;
import org.jvoicexml.mmi.events.ExtensionNotification;
import org.jvoicexml.mmi.events.Mmi;
import org.w3c.dom.Node;

/**
 * The event queue that stores MMI events until they are pulled from executing a
 * receive tag.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public class ReceiveEventQueue implements EventSubscriber {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(ReceiveEventQueue.class);

    /** The queue. */
    private final BlockingQueue<LastMessage> queue;

    /** The current VoiceXmlInterpreter context. */
    private final VoiceXmlInterpreterContext context;

    /**
     * Constructs a new object.
     * 
     * @param ctx
     *            the current VoiceXML interpreter context
     */
    public ReceiveEventQueue(final VoiceXmlInterpreterContext ctx) {
        queue = new LinkedBlockingDeque<LastMessage>();
        context = ctx;
    }

    /**
     * Evaluates the given name to boolean.
     * 
     * @param name
     *            the name of the variable to check
     * @return boolean value of the variable
     */
    private boolean evaluate(final String name) {
        final ScriptingEngine scripting = context.getScriptingEngine();
        try {
            final Object object = scripting.eval(name + ";");
            if (object instanceof Boolean) {
                final Boolean value = (Boolean) object;
                return value.booleanValue();
            }
        } catch (SemanticError e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Converts the received message into a last message.
     * 
     * @param event
     *            the event to convert
     * @return the converted message
     * @since 0.7.7
     */
    private LastMessage toLastMessage(
            final IncomingExtensionNotificationJVoiceXmlEvent event) {
        final Mmi mmi = event.getExtensionNotification();
        final ExtensionNotification notification = mmi
                .getExtensionNotification();
        if (notification == null) {
            LOGGER.warn("unable to convert event into a last message: "
                    + "no extension notifiaction");
            return null;
        }
        final String name = notification.getName();
        final String contentType;
        final Object content;
        final AnyComplexType any = notification.getData();
        if (any != null) {
            final List<Object> list = any.getContent();
            if (!list.isEmpty()) {
                final Object firstItem = list.get(0);
                if (firstItem instanceof String) {
                    contentType = "text/plain";
                    content = firstItem;
                } else if (firstItem instanceof Node) {
                    contentType = "text/xml";
                    content = firstItem;
                } else {
                    LOGGER.warn("unable to convert event into a last message: "
                            + "unsupported content type of " + firstItem);
                    return null;
                }
            } else {
                contentType = null;
                content = ScriptingEngine.getUndefinedValue();
            }
        } else {
            contentType = null;
            content = ScriptingEngine.getUndefinedValue();
        }
        return new LastMessage(contentType, name, content);
    }

    /**
     * Retrieves the next message.
     * 
     * @param timeout
     *            timeout to wait for the next message.
     * @return next message, {@code null} if no message was received
     */
    public LastMessage getNextLastMessage(final long timeout) {
        try {
            return queue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEvent(final JVoiceXMLEvent event) {
        if (!(event instanceof IncomingExtensionNotificationJVoiceXmlEvent)) {
            return;
        }
        final IncomingExtensionNotificationJVoiceXmlEvent ext = (IncomingExtensionNotificationJVoiceXmlEvent) event;
        boolean externalEventsEnabled = evaluate("externalevents.enable");
        final LastMessage message = toLastMessage(ext);
        final EventBus bus = context.getEventBus();
        if (externalEventsEnabled) {
            LOGGER.info("asynchronously delivering external message '"
                    + message.getEventType() + "'");
            bus.publish(message);
        } else {
            boolean externalEventsQueued = evaluate("externalevents.queue");
            if (!externalEventsQueued) {
                LOGGER.info("queueing of external events not enabled: "
                        + "discarding event '" + message.getEventType() + "'");
            } else {
                LOGGER.info("queueing of external event '"
                        + message.getEventType() + "'");
                queue.add(message);
            }
        }
    }
}
