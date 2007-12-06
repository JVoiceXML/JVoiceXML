/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
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

package org.jvoicexml.client.rtp;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.URI;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.jlibrtp.DataFrame;
import org.jlibrtp.Participant;
import org.jlibrtp.RTPAppIntf;
import org.jlibrtp.RTPSession;

/**
 * RTP player for playing the output on the client side.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class RtpPlayer implements RTPAppIntf {
    /** URI of the RTP source stream. */
    private final URI uri;

    /** The RTP session. */
    private RTPSession session;

    /** The line to play back the data. */
    private SourceDataLine line;

    /**
     * Constructs a new object.
     *
     * @param rtpUri
     *            RTP source stream.
     */
    public RtpPlayer(final URI rtpUri) {
        uri = rtpUri;
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws IOException {
        DatagramSocket rtpSocket = new DatagramSocket(4242);

        session = new RTPSession(rtpSocket, null);
        session.naivePktReception(true);
        session.registerRTPSession(this, null, null);

        AudioFormat.Encoding encoding =  new AudioFormat.Encoding("PCM_SIGNED");
        AudioFormat format = new AudioFormat(encoding,((float) 8000.0), 16, 1,
                2, ((float) 8000.0) ,false);
        System.out.println(format.toString());
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
                return;
        } catch (Exception e) {
                e.printStackTrace();
                return;
        }


        line.start();
    }

    /**
     * Stops the current output.
     */
    public void close() {
        session.endSession();
        line.drain();
        line.close();
    }

    /**
     * {@inheritDoc}
     */
    public int frameSize(final int size) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public void receiveData(final DataFrame frame,
            final Participant participant) {
        if (line != null) {
            final byte[] data = frame.getConcatenatedData();
            line.write(data, 0, data.length);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void userEvent(final int type, final Participant[] participants) {
    }
}
