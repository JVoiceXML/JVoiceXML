/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jvoicexml.event.plain.jvxml;

import java.io.OutputStream;

/**
 *
 * @author piri
 */
public final class RecordingEvent extends AbstractInputEvent {
    /** The detail message. */
    public static final String EVENT_TYPE = RecordingEvent.class.getName();

    /** The output buffer of the recording. */
    private final byte[] buffer;

    /** The input result. */
    private Object result;

    /**
     * Constructs a new object.
     * @param recordingBuffer output buffer of the recording.
     */
    public RecordingEvent(final byte[] recordingBuffer) {
        buffer = recordingBuffer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }

    /**
     * Sets the input result.
     * @param inputResult
     */
    public void setInputResult(final Object inputResult) {
        result = inputResult;
        
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getInputResult() {
        return result;
    }
    
    /**
     * Retrieves teh buffer of the recording.
     * @return buffer of the recording.
     */
    public byte[] getRecordingBuffer() {
        return buffer;
    }
}
