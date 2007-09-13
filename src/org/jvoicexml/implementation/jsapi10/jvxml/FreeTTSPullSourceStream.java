/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10.jvxml;

import java.io.IOException;
import java.io.InputStream;

import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullSourceStream;

/**
 * A {@link javax.media.protocol.SourceStream} to send the data coming from
 * FreeTTS. This is in fact a general purpose
 * {@link javax.media.protocol.SourceStream} for any {@link java.io.InputStream}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.6
 */
final class FreeTTSPullSourceStream implements PullSourceStream {
    /** No controls allowed. */
    private static final Object[] EMPTY_OBJECT_ARRAY = {};

    /** The input stream to read data from. */
    private InputStream in;

    /** The number of bytes read so far. */
    private int num = 0;

    /** Maximum number of bytes to read. */
    private int max;

    /** Wait lock to wait for the end of the stream. */
    private Integer waitLock = new Integer(0);

    /**
     * Sets the input stream.
     *
     * @param input
     *            the input stream.
     */
    public void setInstream(final InputStream input) {
        waitEndOfStream();

        in = input;
        num = 0;
        try {
            max = in.available();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public int read(final byte[] bytes, final int start, final int length)
            throws IOException {
        if (in == null) {
            return 0;
        }

        int readBytes = in.read(bytes, start, length);

        num += length;
        if (num == max) {
            synchronized (waitLock) {
                waitLock.notifyAll();
            }
        }

        return readBytes;
    }

    /**
     * {@inheritDoc}
     */
    public boolean willReadBlock() {
        if (in == null) {
            return true;
        }

        try {
            return in.available() > 0;
        } catch (IOException e) {
            return true;
        }
    }

    /**
     * Wait until the end of stream has been reached.
     */
    public void waitEndOfStream() {
        synchronized (waitLock) {
            if (in == null) {
                return;
            }

            try {
                waitLock.wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }

            in = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean endOfStream() {
        return max == num;
    }

    /**
     * {@inheritDoc}
     */
    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor(ContentDescriptor.RAW_RTP);
    }

    /**
     * {@inheritDoc}
     */
    public long getContentLength() {
        return max;
    }

    /**
     * {@inheritDoc}
     */
    public Object getControl(final String controlType) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getControls() {
        return EMPTY_OBJECT_ARRAY;
    }
}
