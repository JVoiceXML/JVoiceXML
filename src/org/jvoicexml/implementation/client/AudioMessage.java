/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date: $
 * Author:  $java.LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Audio message that contains data to be delivered to the client's audio
 * device.
 *
 * <p>
 * An audio message maintains a buffer for audio data. The whole audio
 * is embraced by a {@link AudioStartMessage} and a {@link AudioEndMessage}
 * and may consist of multiple {@link AudioMessage}s.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.6
 *
 * @see AudioStartMessage
 * @see AudioEndMessage
 */
public class AudioMessage
        implements Serializable {
    /** The serial version UID. */
    private static final long serialVersionUID = -5746762080896440563L;

    /** The growing byte buffer. */
    private transient ByteArrayOutputStream buffer;

    /**
     * Constructs a new object.
     */
    public AudioMessage() {
    }

    /**
     * Appends the given bytes to the buffer.
     * @param bytes Bytes to copy.
     * @param start Start copying at this position.
     * @param end End copying at this position.
     */
    public void write(final byte[] bytes, final int start, final int end) {
        if (buffer == null) {
            buffer = new ByteArrayOutputStream();
        }

        buffer.write(bytes, start, end);
    }

    /**
     * Resets the buffer in this audio message.
     */
    public void reset() {
        if (buffer == null) {
            return;
        }

        buffer.reset();
    }

    /**
     * Retrieves the pure audio data.
     * @return Audio data.
     */
    public byte[] getBuffer() {
        if (buffer == null) {
            return null;
        }

        return buffer.toByteArray();
    }

    /**
     * Writes this object to the stream.
     * @param out The stream to write to.
     * @throws IOException
     *         Error accessing the stream.
     */
    private void writeObject(final ObjectOutputStream out)
            throws IOException {
        final byte[] bytes = buffer.toByteArray();
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    /**
     * Reads this object from the stream.
     * @param in The stream to read from.
     * @throws IOException
     *         Error accessing the stream.
     * @throws java.lang.ClassNotFoundException
     *         Error instantiating an object from the stream.K
     */
    private void readObject(final ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        if (buffer == null) {
            buffer = new ByteArrayOutputStream();
        } else {
            buffer.reset();
        }

        final int length = in.readInt();
        final byte[] bytes = new byte[length];
        in.read(bytes, 0, length);

        buffer.write(bytes);
    }

}
