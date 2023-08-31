/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010-2021 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter;

import java.net.URI;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.event.EventBus;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.error.jvxml.ExceptionWrapper;
import org.jvoicexml.event.plain.jvxml.ReturnEvent;
import org.jvoicexml.event.plain.jvxml.SubdialogResultEvent;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.scope.Scope;

/**
 * Asynchronous execution of a subdialog.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.4
 */
final class SubdialogExecutorThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(SubdialogExecutorThread.class);

    /** The URI of the subdialog. */
    private final URI uri;

    /** The context of the subdialog. */
    private final VoiceXmlInterpreterContext context;

    /** The current application. */
    private final Application application;

    /** The event bus to propagate errors and results. */
    private final EventBus eventbus;

    /** Parameters of the subdialog call. */
    private final Map<String, Object> parameters;

    final DataModel sourceModel;
    
    /**
     * Constructs a new object.
     * 
     * @param subdialogUri
     *            the URI of the subdialog
     * @param subdialogContext
     *            the context of the subdialog
     * @param appl
     *            the current application
     * @param params
     *            parameters of the subdialog call
     * @param bus
     *            the event bus of the calling context to correctly propagate
     *            messages
     */
    SubdialogExecutorThread(final URI subdialogUri,
            final VoiceXmlInterpreterContext subdialogContext,
            final Application appl, final Map<String, Object> params,
            final EventBus bus, final DataModel model) {
        uri = subdialogUri;
        context = subdialogContext;
        application = appl;
        eventbus = bus;
        parameters = params;
        sourceModel = model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        final ReturnEventHandler handler = registerReturnEventHandler();
        final DataModel model = context.getDataModel();
        try {
            // We need to copy the values here to ensure that the contexts are
            // associated with this thread.
            sourceModel.copyValues(model);
            model.createScope(Scope.DIALOG);
            final DocumentDescriptor descriptor = new DocumentDescriptor(uri,
                    DocumentDescriptor.MIME_TYPE_XML);
            context.processSubdialog(application, descriptor, parameters);
            final ReturnEvent event = handler.getReturnEvent();
            final Object result;
            try {
                result = getReturnObject(event);
            } catch (SemanticError sematicerror) {
                eventbus.publish(sematicerror);
                return;
            }
            final ImplementationPlatform platform = context
                    .getImplementationPlatform();
            platform.setEventBus(eventbus);
            final DocumentServer server = context.getDocumentServer();
            // TODO use the correct call properties
            platform.playPrompts(server, null);
            final SubdialogResultEvent resultEvent =
                    new SubdialogResultEvent(result);
            eventbus.publish(resultEvent);
        } catch (JVoiceXMLEvent e) {
            LOGGER.warn("Caught JVoiceXMLEvent in subdialog", e);
            eventbus.publish(e);
        } catch (Exception e) {
            LOGGER.error("Caught error in subdialog", e);
            final ExceptionWrapper wrapper = 
                    new ExceptionWrapper(e.getMessage(), e);
            eventbus.publish(wrapper);
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("abandonned subdialog application");
            }
            unregisterReturnEventHandler(handler);
            model.deleteScope(Scope.DIALOG);
        }
    }

    /**
     * Registers a return event handler.
     * @return the registered handler
     * @since 0.7.9
     */
    private ReturnEventHandler registerReturnEventHandler() {
        final ReturnEventHandler handler = new ReturnEventHandler();
        final EventBus bus = context.getEventBus();
        bus.subscribe(ReturnEvent.EVENT_TYPE, handler);
        return handler;
    }

    /**
     * Unregisters a return event handler.
     * @return the handler to unregister
     * @since 0.7.9
     */
    private void unregisterReturnEventHandler(final ReturnEventHandler handler) {
        final EventBus bus = context.getEventBus();
        bus.unsubscribe(ReturnEvent.EVENT_TYPE, handler);
    }
    
    /**
     * Creates the value for the returned result.
     * 
     * @param event
     *            caught event.
     * @return return result.
     * @throws SemanticError
     *             if a variable could not be evaluated
     */
    private Object getReturnObject(final ReturnEvent event)
            throws SemanticError {
        final StringBuilder str = new StringBuilder();
        str.append("var out = new Object();");
        final Map<String, Object> variables = event.getVariables();
        for (String name : variables.keySet()) {
            str.append("out.");
            str.append(name);
            str.append(" = ");
            final Object value = variables.get(name);
            if (value instanceof String) {
                str.append("\"");
                str.append(value);
                str.append("\"");
            } else {
                str.append(value);
            }
            str.append(";");
        }
        final DataModel model = context.getDataModel();
        final String expr = str.toString();
        final String unescapedExpr = StringEscapeUtils.unescapeXml(expr);
        model.evaluateExpression(unescapedExpr, Object.class);
        return model.readVariable("out", Object.class);
    }
}
