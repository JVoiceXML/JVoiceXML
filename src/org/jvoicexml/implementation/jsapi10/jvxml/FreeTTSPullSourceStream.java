/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/jsapi10/jvxml/SystemOutputFactory.java $
 * Version: $LastChangedRevision: 172 $
 * Date:    $LastChangedDate: 2006-12-14 09:35:30 +0100 (Do, 14 Dez 2006) $
 * Author:  $LastChangedBy: schnelle $
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

/**
 * A {@link javax.media.protocol.SourceStream} to send the data coming
 * from FreeTTS. This is in fact a general purpose
 * {@link javax.media.protocol.SourceStream} for any
 * {@link java.io.InputStream}.
 *
 * @author Dirk Schnelle
 * @version $Revision: 172 $
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.6
 */
public final class FreeTTSPullSourceStream implements PullSourceStream {
    /** No controls aloowed. */
    private static final Object[] EMPTY_OBJECT_ARRAY = {};

    /** The input stream to read data from. */
    private InputStream in;

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
        return in.read(bytes, start, offset);
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
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getControls() {
        return EMPTY_OBJECT_ARRAY;
    }
}
