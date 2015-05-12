/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.implementation.mary/src/org/jvoicexml/implementation/mary/MaryAudioOutput.java $
 * Version: $LastChangedRevision: 3934 $
 * Date:    $Date: 2013-11-22 06:54:57 +0100 (Fri, 22 Nov 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $LastChangedDate $, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.implementation.mary;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import javax.sound.sampled.SourceDataLine;

/**
 * An {@link OutputStream} that writes to a {@link SourceDataLine}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3934 $
 * @since 0.7.7
 */
public final class LineOutputStream extends OutputStream
    implements Closeable {
    /** The source data line. */
    private final SourceDataLine line;

    /**
     * Constructs a new object.
     * @param source the line to write to.
     */
    public LineOutputStream(final SourceDataLine source) {
        line = source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final int b) throws IOException {
        byte[] bytes = new byte[1];
        write(bytes, 0, bytes.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final byte[] b, final int off, final int len)
        throws IOException {
        line.write(b, off, len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    /**
     * Stops the current output.
     */
    public void cancel() {
        line.stop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        if (line.isRunning()) {
            line.drain();
            line.stop();
        }
        line.close();
        super.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() throws IOException {
        line.drain();
        super.flush();
    }

}
