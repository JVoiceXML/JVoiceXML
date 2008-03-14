/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/AudioFileOutput.java $
 * Version: $LastChangedRevision: 699 $
 * Date:    $Date: 2008-02-20 09:32:15 +0100 (Mi, 20 Feb 2008) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.DocumentServer;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.jvxml.RecordingEvent;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.AbstractFormItem;
import org.jvoicexml.interpreter.formitem.RecordFormItem;

/**
 * Event strategy to handle the end of a recording.
 *
 * @author Dirk Schnelle
 * @version $Revision: 737 $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class RecordingEventStrategy
        extends AbstractInputItemEventStrategy<RecordFormItem> {
    /** Audio format to use for recording. */
    private final AudioFormat format;

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
     * @param recordingFormat
     *        audio format to use for recording.
     */
    public RecordingEventStrategy(final VoiceXmlInterpreterContext ctx,
                                    final VoiceXmlInterpreter interpreter,
                                    final FormInterpretationAlgorithm algorithm,
                                    final AbstractFormItem formItem,
                                    final AudioFormat recordingFormat) {
        super(ctx, interpreter, algorithm, formItem,
                RecordingEvent.EVENT_TYPE);
        format = recordingFormat;
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
        final long length = buffer.length / format.getFrameSize();
        final AudioInputStream ain = new AudioInputStream(in, format, length);
        final URI result = server.storeAudio(ain);

        // Save the URI in the event for later retrieval.
        recordingEvent.setInputResult(result);
        return true;
    }

}
