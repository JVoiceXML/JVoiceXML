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
package org.jvoicexml.implementation.marc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.UUID;

import org.junit.Test;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;

/**
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.5
 */
public class TestMarcFeedback implements SynthesizedOutputListener {

    /**
     * Test method for {@link org.jvoicexml.implementation.marc.MarcFeedback#run()}.
     * @exception Exception test failed
     * @exception JVoiceXMLEvent test failed
     */
    @Test
    public void testRun() throws Exception, JVoiceXMLEvent {
        final MarcSynthesizedOutput output = new MarcSynthesizedOutput();
        output.connect(null);
        output.addListener(this);
        final SpeakableText speakable = new SpeakablePlainText("test");
        final String sessionId = UUID.randomUUID().toString();
        output.queueSpeakable(speakable, sessionId, null);
        final MarcFeedback feedback = new MarcFeedback(output, 4011);
        final DatagramSocket server = new DatagramSocket(4012);
        feedback.start();
        Thread.sleep(1000);
        final String msg = "<event id=\"JVoiceXMLTrack:end\"/>";
        final byte[] buf = msg.getBytes();
        final InetAddress address = Inet4Address.getLocalHost();
        final DatagramPacket packet = new DatagramPacket(buf, buf.length,
                address, 4011);
        server.send(packet);
        final String msg2 = "<event id=\"SpeechCommand:end\"/>";
        final byte[] buf2 = msg2.getBytes();
        final DatagramPacket packet2 = new DatagramPacket(buf2, buf2.length,
                address, 4011);
        server.send(packet2);
        Thread.sleep(1000);
    }

    @Override
    public void outputStatusChanged(final SynthesizedOutputEvent event) {
        System.out.println(event);
    }

    @Override
    public void outputError(final ErrorEvent error) {
        // TODO Auto-generated method stub
        
    }

}
