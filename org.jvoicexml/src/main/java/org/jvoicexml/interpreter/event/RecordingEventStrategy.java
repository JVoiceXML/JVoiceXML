/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.event;

import java.io.ByteArrayInputStream;
import java.net.URI;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.CallControl;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.event.EventBus;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.event.plain.implementation.RecordingStoppedEvent;
import org.jvoicexml.event.plain.jvxml.RecordingEvent;
import org.jvoicexml.interpreter.EventStrategy;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.RecordFormItem;

/**
 * Event strategy to handle the end of a recording.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.6
 */
final class RecordingEventStrategy
        extends AbstractInputItemEventStrategy<RecordFormItem>
        implements EventStrategyPrototype {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(RecordingEventStrategy.class);

    /** Audio format to use for recording. */
    private final AudioFormat format;

    /**
     * Constructs a new object.
     */
    RecordingEventStrategy() {
        format = null;
    }

    /**
     * Constructs a new object.
     *
     * @param ctx
     *            The VoiceXML interpreter context.
     * @param interpreter
     *            The VoiceXML interpreter.
     * @param algorithm
     *            The FIA.
     * @param formItem
     *            The current form item.
     * @param recordingFormat
     *            audio format to use for recording.
     */
    public RecordingEventStrategy(final VoiceXmlInterpreterContext ctx,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm algorithm,
            final FormItem formItem, final AudioFormat recordingFormat) {
        super(ctx, interpreter, algorithm, formItem, RecordingEvent.EVENT_TYPE);
        format = recordingFormat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean handleEvent(final RecordFormItem record,
            final JVoiceXMLEvent event) throws JVoiceXMLEvent {
        final RecordingEvent recordingEvent = (RecordingEvent) event;
        final byte[] buffer = recordingEvent.getRecordingBuffer();
        final ByteArrayInputStream in = new ByteArrayInputStream(buffer);
        final VoiceXmlInterpreterContext context =
                getVoiceXmlInterpreterContext();
        final DocumentServer server = context.getDocumentServer();

        // Store the recording.
        final long length = buffer.length / format.getFrameSize();
        final AudioInputStream ain = new AudioInputStream(in, format, length);
        final URI result = server.storeAudio(ain);

        // Save the URI in the event for later retrieval.
        recordingEvent.setInputResult(result);

        // Notify that recording has stopped.
        final Session session = context.getSession();
        final SessionIdentifier id = session.getSessionId();
        final RecordingStoppedEvent stopped = new RecordingStoppedEvent(
                id.getId(), result);
        final EventBus bus = context.getEventBus();
        bus.publish(stopped);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventStrategy newInstance(final VoiceXmlInterpreterContext ctx,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem item) {
        final ImplementationPlatform platform = ctx.getImplementationPlatform();
        CallControl call = null;
        try {
            call = platform.getCallControl();
        } catch (NoresourceError e) {
            LOGGER.error("unable to obtain call control", e);
        } catch (ConnectionDisconnectHangupEvent e) {
            LOGGER.error("unable to obtain call control", e);
        }
        final AudioFormat audioFormat;
        if (call == null) {
            LOGGER.warn("no active call control. can not set audio format");
            audioFormat = null;
        } else {
            audioFormat = call.getRecordingAudioFormat();
        }
        final RecordFormItem record = (RecordFormItem) item;
        return new RecordingEventStrategy(ctx, interpreter, fia, record,
                audioFormat);
    }

}
