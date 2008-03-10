/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jvoicexml.interpreter.event;

import java.io.ByteArrayInputStream;
import java.net.URI;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import org.apache.log4j.Logger;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.jvxml.RecordingEvent;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.AbstractFormItem;
import org.jvoicexml.interpreter.formitem.RecordFormItem;

/**
 *
 * @author piri
 */
public final class RecordingEventStrategy
        extends AbstractInputItemEventStrategy<RecordFormItem> {

    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(RecordingEventStrategy.class);

    /**
     * Construct a new object.
     *
     * @param ctx
     *        The VoiceXML interpreter context.
     * @param interpreter
     *        The VoiceXML interpreter.
     * @param algorithm
     *        The FIA.
     * @param formItem
     *        The current form item.
     */
    public RecordingEventStrategy(final VoiceXmlInterpreterContext ctx,
                                    final VoiceXmlInterpreter interpreter,
                                    final FormInterpretationAlgorithm algorithm,
                                    final AbstractFormItem formItem) {
        super(ctx, interpreter, algorithm, formItem,
                RecordingEvent.EVENT_TYPE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean handleEvent(final RecordFormItem record,
            final JVoiceXMLEvent event)
            throws JVoiceXMLEvent {
        final RecordingEvent recordingEvent = (RecordingEvent) event;
        final byte[] buffer = recordingEvent.getRecordingBuffer();
        final ByteArrayInputStream in = new ByteArrayInputStream(buffer);
        final VoiceXmlInterpreterContext context =
                getVoiceXmlInterpreterContext();
        final DocumentServer server = context.getDocumentServer();

        // Store the recording.
        final AudioFormat.Encoding encoding =
                new AudioFormat.Encoding("PCM_SIGNED");
        final AudioFormat format =
                new AudioFormat(encoding,((float) 8000.0), 16, 1, 2,
                ((float) 8000.0) ,false);
        final long length = buffer.length / format.getFrameSize();
        final AudioInputStream ain = new AudioInputStream(in, format, length);
        final URI result = server.storeAudio(ain);

        // Save the URI in the event for later retrieval.
        recordingEvent.setInputResult(result);

        return true;
    }

}
