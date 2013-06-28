/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.client.text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.apache.log4j.Logger;

/**
 * 
 * @author Raphael Groner
 * @version $Revision$
 * @since 0.7.6
 */
public final class TextMessageReader {

    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(TextMessageReader.class);
    
    /** the buffer for partial objects.  */
    private final BufferedInputStream stream;
    
    /** minimal bytes size of a serialized TextMessage object. */
    private static final int MESSAGE_SIZE_MIN = 114;
    
    /**
     * Creates a new reader object.
     * @param in the stream to read from
     */
    public TextMessageReader(final InputStream in) {
        stream = new BufferedInputStream(in);
    }

    /**
     * Checks available and completed object in a stream.
     * @return <code>true</code> if a valid object is in the stream buffer
     * @throws IOException stream error
     * @since 0.7.6
     */
    public boolean isStreamAvailable() throws IOException {
        if (stream == null) {
            return false;
        }
        int available = stream.available();
        // should find at least one complete object
        if (available < MESSAGE_SIZE_MIN) {
            return false;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("stream available: " + available);
        }
        return true;
     }
    
    /**
     * Gets the next message available from the stream.
     * If no object is available currently, the caller 
     * may wait till one is available.
     * @return next message from the stream, or
     *         <code>null</code> if there's none available yet
     * @throws IOException stream error
     * @since 0.7.6
     */
    public TextMessage getNextMessage() throws IOException {
        if (isStreamAvailable()) {
            try {
                ObjectInputStream obj = new ObjectInputStream(stream);
                return (TextMessage) obj.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException("unable to instantiate the read object",
                        e);
            }
        } else {
            return null;
        }
    }

    /**
     * Closes the stream and discards all not processed buffered data.
     * @throws IOException stream error
     * @since 0.7.6
     */
    public void close() throws IOException {
        stream.close();
    }
}
