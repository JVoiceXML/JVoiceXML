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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for TextMessageReader.
 * @author Raphael Groner
 * @version $Revision$
 * @since 0.7.6
 */
public class TestTextMessageReader {
    
    private class MessageStream {

        private ByteArrayOutputStream out;
        private TextMessageReader reader;
        private InputStream in;
        
        public MessageStream() {
            clearBuffer();
        }
        
        public TextMessage writeMessage() throws IOException {
            final ObjectOutputStream obj = new ObjectOutputStream(out);
            final TextMessage message = new TextMessage();
            obj.writeObject(message);
            return message;
        }
        
        public void writeVoid() {
            out.write(0);            
        }
        
        public boolean isAvailable() throws IOException {
            flush();
            return reader.isStreamAvailable();
        }
        
        /**
         * Reads a message from the stream buffer.
         * @return the message from buffer
         * @throws IOException stream error
         * @since 0.7.6
         */
        private TextMessage readMessage() throws IOException {
            flush();
            return reader.getNextMessage();
        }

        /**
         * Serializes a streamed message.
         * Updates the internal buffer used by the reader.
         * @since 0.7.6
         */
        public void flush() {
            in = new ByteArrayInputStream(out.toByteArray());
            clearBuffer();
        }

        /**
         * Clears the stream buffer.
         * @since 0.7.6
         */
        private void clearBuffer() {
            reader = new TextMessageReader(in);
            out = new ByteArrayOutputStream(); // we need a new empty object
        }
        
        /**
         * Closes the stream and all related resources.
         * @throws IOException
         * @since 0.7.6
         */
        public void close() throws IOException {
            out.close();
            reader.close();
        }
    }

    private MessageStream stream;

    /**
     * @throws java.lang.Exception
     * @since 0.7.6
     */
    @Before
    public void setUp() throws Exception {
        stream = new MessageStream();        
    }

    /**
     * @throws java.lang.Exception
     * @since 0.7.6
     */
    @After
    public void tearDown() throws Exception {
        stream.close();
    }

    /**
     * Testmethode für {@link org.jvoicexml.client.text.TextMessageReader#isStreamAvailable()}.
     * @throws IOException stream error
     */
    @Test
    public void testIsStreamAvailable() throws IOException {
        Assert.assertFalse("not available", stream.isAvailable());
        stream.writeMessage();
        Assert.assertTrue("available", stream.isAvailable());
        stream.flush();
        Assert.assertFalse("flushed", stream.isAvailable());
    }

    /**
     * Testmethode für {@link org.jvoicexml.client.text.TextMessageReader#getNextMessage()}.
     * @throws IOException stream error
     */
    @Test
    public void testGetNextMessage() throws IOException {
        Assert.assertNull("empty stream", stream.readMessage());
        final TextMessage message = stream.writeMessage();
        Assert.assertEquals(message, stream.readMessage());
        Assert.assertNull("message read", stream.readMessage());
        stream.writeVoid();
        Assert.assertNull("invalid message", stream.readMessage());
       
    }
}
