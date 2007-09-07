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
import javax.media.protocol.SourceStream;
import javax.media.rtp.RTPControl;

import com.sun.media.protocol.BufferListener;
import com.sun.media.protocol.RTPSource;
import com.sun.media.rtp.RTPMediaLocator;

/**
 * A {@link javax.media.protocol.SourceStream} to send the data coming
 * from FreeTTS. This is in fact a general purpose
 * {@link javax.media.protocol.SourceStream} for any
 * {@link java.io.InputStream}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.6
 */
final class FreeTTSPullSourceStream implements PullSourceStream, RTPSource {
    /** The input stream to read data from. */
    private InputStream in;

    /** The RTP control instance. */
//    private FreeTTSRtpControl control;

    /** The number of bytes read so far. */
    private long numRead;

    /**
     * Constructs a new object.
     */
    public FreeTTSPullSourceStream() {
//        control = new FreeTTSRtpControl(this);
    }

    /**
     * Sets the input stream.
     * @param input the input stream.
     */
    public void setInstream(final InputStream input) {
        in = input;
    }

    /**
     * {@inheritDoc}
     */
    public int read(final byte[] bytes, final int start, final int offset)
        throws IOException {
        if (in == null) {
            return 0;
        }
        final int num = in.read(bytes, start, offset);

        numRead += num;

        return num;
    }

    /**
     * {@inheritDoc}
     */
    public boolean willReadBlock() {
        try {
            return in.available() > 0;
        } catch (IOException e) {
           return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean endOfStream() {
        try {
            return in.available() == 0;
        } catch (IOException e) {
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor(ContentDescriptor.RAW);
    }

    /**
     * {@inheritDoc}
     */
    public long getContentLength() {
        return SourceStream.LENGTH_UNKNOWN;
    }

    /**
     * {@inheritDoc}
     */
    public Object getControl(final String controlType) {
//        final Class cls;
//        try {
//            cls = Class.forName(controlType);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//        final Object[] controls = getControls();
//        for (int i = 0; i < controls.length; i++) {
//            final Object ctrl = controls[i];
//            if (cls.isInstance(ctrl)) {
//                return control;
//            }
//        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getControls() {
        return new RTPControl[0];
//        final RTPControl[] controls = new RTPControl[1];
//        controls[0] = control;
//        return controls;
    }

    /**
     * {@inheritDoc}
     */
    public void flush() {
    }

    /**
     * {@inheritDoc}
     */
    public String getCNAME() {
        return null;
//        return control.getCNAME();
    }

    /**
     * {@inheritDoc}
     */
    public int getSSRC() {
        return RTPMediaLocator.SSRC_UNDEFINED;
//        return control.getSSRC();
    }

    /**
     * {@inheritDoc}
     */
    public void prebuffer() {
    }

    /**
     * {@inheritDoc}
     */
    public void setBufferListener(final BufferListener listener) {

    }

    /**
     * Get the total number of bytes of media data that have been downloaded so
     * far.
     *
     * @return The number of bytes downloaded.
     */
    public long getContentProgress() {
        return numRead;
    }
}
