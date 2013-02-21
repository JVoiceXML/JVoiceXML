/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10.jvxml;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.jvoicexml.implementation.jsapi10.StreamableSpokenInput;

import edu.cmu.sphinx.frontend.Data;
import edu.cmu.sphinx.frontend.DataEndSignal;
import edu.cmu.sphinx.frontend.DataProcessingException;
import edu.cmu.sphinx.frontend.DataStartSignal;
import edu.cmu.sphinx.frontend.DoubleData;
import edu.cmu.sphinx.frontend.util.DataUtil;
import edu.cmu.sphinx.frontend.util.Microphone;

/**
 * Implementation of an microphone based on streams for the sphinx4 frontend
 * to be used in RTP scenarios.
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */

public final class StreamableMicrophone extends Microphone
    implements StreamableSpokenInput {
    /** Read data. */
    private final BlockingQueue<Data> data;

    /** Flag if we are recording. */
    private boolean recording;

    /**
     * Constructs a new object.
     */
    public StreamableMicrophone() {
        data = new java.util.concurrent.LinkedBlockingQueue<Data>();
    }

    /**
     * {@inheritDoc}
     */
    public void writeRecognizerStream(final byte[] buffer, final int offset,
            final int length) throws IOException {
        long collectTime = System.currentTimeMillis();
        int sampleSizeInBytes = 16;
        int sampleRate = 8000;
        double[] samples = DataUtil.bytesToValues(buffer, offset, length,
                sampleSizeInBytes, false);
        final Data currentData =
            new DoubleData(samples, sampleRate, collectTime, 0);
        data.add(currentData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean startRecording() {
        if (recording) {
            return false;
        }
        recording = true;
        final Data start = new DataStartSignal(8000);
        data.add(start);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void stopRecording() {
        long duration = 42;
        final Data end = new DataEndSignal(duration);
        data.add(end);
        recording = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Data getData() throws DataProcessingException {
        try {
            return data.take();
        } catch (InterruptedException e) {
            throw new DataProcessingException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRecording() {
        return recording;
    }
}
