/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.documentserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.BadFetchError;
import org.mozilla.intl.chardet.nsDetector;

/**
 * A simple buffer.
 * @author Dirk Schnell-Walka
 * @version $Revision: $
 * @since 0.7.5
 */
class ReadBuffer {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(ReadBuffer.class);

    /** Size of the read buffer when reading objects. */
    private static final int READ_BUFFER_SIZE = 1024;

    /** The buffered bytes. */
    private final ByteArrayOutputStream buffer;

    /** <code>true</code> if the contents of {@link #buffer} is plain text. */
    private boolean isAscii;

    /** The detected charset. */
    private String charset;

    /**
     * Constructs a new object.
     */
    public ReadBuffer() {
        buffer = new ByteArrayOutputStream();
        isAscii = true;
    }

    /**
     * Reads a {@link String} from the given {@link InputStream}.
     * @param input the input stream to use.
     * @throws BadFetchError
     *         Error reading.
     */
    public void read(final InputStream input) throws IOException {
        final nsDetector detector = new nsDetector();
        final JVoiceXmlCharsetDetectionObserver observer =
            new JVoiceXmlCharsetDetectionObserver();
        detector.Init(observer);
        boolean done = false;
        // Read from the input
        final byte[] readBuffer = new byte[READ_BUFFER_SIZE];
        int num;
        do {
            num = input.read(readBuffer);
            if (num >= 0) {
                buffer.write(readBuffer, 0, num);
                if (isAscii) {
                    isAscii = detector.isAscii(readBuffer, num);
                }

                // Do character set detection if non-ascii and not done yet.
                if (!isAscii && !done) {
                    done = detector.DoIt(readBuffer, num, false);
                }
            }
        } while(num >= 0);
        detector.DataEnd();
        final String charset = observer.getCharset();
        if (charset != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("detected charset '" + charset + "'");
            }
        }
    }

    /**
     * Checks if the contents of the read buffer was plain text.
     * @return <code>true</code> if the contents was plain text.
     */
    public boolean isAscii() {
        return isAscii;
    }

    /**
     * Retrieves the determined charset if the contents of the buffer is
     * plain text.
     * @return detected charset, <code>null</code> if no charset was detected.
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Retrieves the read buffer.
     * @return the buffer contents
     */
    public byte[] getBytes() {
        return buffer.toByteArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final byte[] bytes = getBytes();
        if (isAscii) {
            if (charset == null) {
                return new String(bytes);
            } else {
                try {
                    return new String(bytes, charset);
                } catch (UnsupportedEncodingException e) {
                    return super.toString();
                }
            }
        } else {
            return super.toString();
        }
    }
}
