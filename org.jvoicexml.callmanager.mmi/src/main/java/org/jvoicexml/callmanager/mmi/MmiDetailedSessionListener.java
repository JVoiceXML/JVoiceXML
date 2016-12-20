/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.callmanager.mmi;

import java.io.IOException;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.jvoicexml.Session;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.HelpEvent;
import org.jvoicexml.event.plain.NoinputEvent;
import org.jvoicexml.event.plain.implementation.InputStartedEvent;
import org.jvoicexml.event.plain.implementation.NomatchEvent;
import org.jvoicexml.event.plain.implementation.OutputEndedEvent;
import org.jvoicexml.event.plain.implementation.OutputStartedEvent;
import org.jvoicexml.event.plain.implementation.QueueEmptyEvent;
import org.jvoicexml.event.plain.implementation.RecognitionEvent;
import org.jvoicexml.event.plain.implementation.RecognitionStartedEvent;
import org.jvoicexml.event.plain.implementation.RecognitionStoppedEvent;
import org.jvoicexml.event.plain.implementation.RecordingStartedEvent;
import org.jvoicexml.event.plain.implementation.RecordingStoppedEvent;
import org.jvoicexml.event.plain.implementation.SpokenInputEvent;
import org.jvoicexml.event.plain.implementation.SynthesizedOutputEvent;
import org.jvoicexml.interpreter.DetailedSessionListener;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.mmi.events.AnyComplexType;
import org.jvoicexml.mmi.events.ExtensionNotification;
import org.jvoicexml.mmi.events.Mmi;
import org.jvoicexml.profile.mmi.OutgoingExtensionNotificationJVoiceXmlEvent;

/**
 * A detailed session listener that sends out extension notifications.
 * 
 * The following events will be mapped to extension notifications:
 * <dl>
 * <dt>{@link OutputStartedEvent}</dt>
 * <dd>{@code vxml.output.start} The data tag will contain the SSML to speak.</dd>
 * <dt>{@link OutputEndedEvent}</dt>
 * <dd>{@code vxml.output.end} The data tag will contain the spoken SSML.</dd>
 * <dt>{@link QueueEmptyEvent}</dt>
 * <dd>{@code vxml.output.emptyQueue}</dd>
 * <dt>{@link RecognitionStartedEvent}</dt>
 * <dd>{@code vxml.input.start}</dd>
 * <dt>{@link InputStartedEvent}</dt>
 * <dd>{@code vxml.input.speech.start}</dd>
 * <dt>{@link RecognitionStoppedEvent}</dt>
 * <dd>{@code vxml.input}</dd>
 * <dt>{@link RecognitionEvent}</dt>
 * <dd>{@code vxml.input.end} The data tag will contain the recognition result.</dd>
 * <dt>{@link NomatchEvent}</dt>
 * <dd>{@code vxml.input.nomatch}</dd>
 * <dt>{@link NoinputEvent}</dt>
 * <dd>{@code vxml.input.noinput}</dd>
 * <dt>{@link HelpEvent}</dt>
 * <dd>{@code vxml.input.help}</dd>
 * <dt>{@link RecordingStartedEvent}</dt>
 * <dd>{@code vxml.record.start}</dd>
 * <dt>{@link RecordingStoppedEvent}</dt>
 * <dd>{@code vxml.record.end} The data tag will contain the URI of the recorded
 * audio file</dd>
 * <dt>{@link ErrorEvent}</dt>
 * <dd>{@code "vxml" + error.getEventType()} The data tag will detail the error</dd>
 * </dl>
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.7
 */
public final class MmiDetailedSessionListener
    implements DetailedSessionListener {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(MmiDetailedSessionListener.class);

    /** The ETL protocol adapter to send MMI events. */
    private final ETLProtocolAdapter adapter;

    /** The MMI context. */
    private final MMIContext context;

    /** The extension notification converter. */
    private final ExtensionNotificationDataConverter converter;

    /**
     * Constructs a new object.
     * 
     * @param ctx the MMI context
     * @param conv the converter for MMI events
     * @param protocolAdapter
     *            the adapter to send events
     */
    public MmiDetailedSessionListener(final ETLProtocolAdapter protocolAdapter,
            final MMIContext ctx,
            final ExtensionNotificationDataConverter conv) {
        adapter = protocolAdapter;
        context = ctx;
        converter = conv;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionStarted(final Session session) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionEvent(final Session session,
            final JVoiceXMLEvent event) {
        final DataModel model = ((JVoiceXmlSession) session).getDataModel();
        final Mmi mmi = convertJVoiceXMLEvent(model, event);
        try {
            final Object channel = context.getChannel();
            adapter.sendMMIEvent(channel, mmi);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Converts the given event into an extension notification.
     * 
     * @param model the employed data model
     * @param event
     *            the received event.
     * @return extension notification
     * @since 0.7.7
     */
    private Mmi convertJVoiceXMLEvent(final DataModel model,
            final JVoiceXMLEvent event) {
        // Simply retrieve an encapsulated event to handle a send tag.
        if (event instanceof OutgoingExtensionNotificationJVoiceXmlEvent) {
            final OutgoingExtensionNotificationJVoiceXmlEvent ext =
                    (OutgoingExtensionNotificationJVoiceXmlEvent) event;
            return ext.getExtensionNotification();
        }

        // Convert it into something appropriate
        final Mmi mmi = new Mmi();
        final ExtensionNotification notification = new ExtensionNotification();
        mmi.setExtensionNotification(notification);
        notification.setContext(context.getContextId());
        notification.setRequestId(UUID.randomUUID().toString());
        notification.setTarget(context.getTarget());
        final String name = getExtensionNotificationName(event);
        if (name == null) {
            LOGGER.warn("no name for event " + event + " not sending");
            return null;
        }
        notification.setName(name);
        Object data = null;
        if (event instanceof SynthesizedOutputEvent) {
            final SynthesizedOutputEvent output =
                    (SynthesizedOutputEvent) event;
            try {
                data = converter.convertSynthesizedOutputEvent(output);
            } catch (ConversionException e) {
                LOGGER.error(e.getMessage(), e);
            }
        } else if (event instanceof RecognitionEvent) {
            final RecognitionEvent input = (RecognitionEvent) event;
            try {
                data = converter.convertRecognitionEvent(model, input);
            } catch (ConversionException e) {
                LOGGER.error(e.getMessage(), e);
                return null;
            }
        } else if (event instanceof SpokenInputEvent) {
            final SpokenInputEvent input = (SpokenInputEvent) event;
            try {
                data = converter.convertSpokenInputEvent(input);
            } catch (ConversionException e) {
                LOGGER.error(e.getMessage(), e);
                return null;
            }
        } else if (event instanceof RecordingStoppedEvent) {
            final RecordingStoppedEvent stopped = (RecordingStoppedEvent) event;
            data = stopped.getUri().toString();
        } else if (event instanceof ErrorEvent) {
            final ErrorEvent error = (ErrorEvent) event;
            data = error.getMessage();
        }
        if (data != null) {
            final AnyComplexType any = new AnyComplexType();
            any.addContent(data);
            notification.setData(any);
        }
        return mmi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionEnded(final Session session) {
        // Remove this instance asynchronously to avoid a concurrent
        // modification exception
        final DetailedSessionListener listener = this;
        final Thread thread = new Thread() {
            @Override
            public void run() {
                final JVoiceXmlSession jvxmlSession =
                        (JVoiceXmlSession) session;
                jvxmlSession.removeSessionListener(listener);
            }
        };
        thread.start();
    }

    /**
     * Retrieves the name for the extension notification for the given event
     * type.
     * 
     * @param event
     *            the event
     * @return name for the extension notification, {@code null} if there is no
     *         matching name
     */
    private String getExtensionNotificationName(final JVoiceXMLEvent event) {
        if (event instanceof OutputStartedEvent) {
            return "vxml.output.start";
        } else if (event instanceof OutputEndedEvent) {
            return "vxml.output.end";
        } else if (event instanceof QueueEmptyEvent) {
            return "vxml.output.emptyQueue";
        } else if (event instanceof InputStartedEvent) {
            return "vxml.input.speech.start";
        } else if (event instanceof RecognitionStartedEvent) {
            return "vxml.input.start";
        } else if (event instanceof RecognitionEvent) {
            return "vxml.input";
        } else if (event instanceof RecognitionStoppedEvent) {
            return "vxml.input.end";
        } else if (event instanceof NomatchEvent) {
            return "vxml.input.nomatch";
        } else if (event instanceof NoinputEvent) {
            return "vxml.input.noinput";
        } else if (event instanceof HelpEvent) {
            return "vxml.input.help";
        } else if (event instanceof RecordingStartedEvent) {
            return "vxml.record.start";
        } else if (event instanceof RecordingStoppedEvent) {
            return "vxml.record.end";
        } else if (event instanceof ErrorEvent) {
            return "vxml" + event.getEventType();
        }

        return null;
    }
}
