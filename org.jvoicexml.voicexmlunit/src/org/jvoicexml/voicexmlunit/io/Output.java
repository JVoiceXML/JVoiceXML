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

package org.jvoicexml.voicexmlunit.io;

import org.junit.Assert;

import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

public class Output implements OutputMessage {
    private String message;
    private SsmlDocument document;
    /**
     * Constructs a new object.
     * @param text the text of this output.
     */
    public Output(final String text) {
        message = text;
    }

    /**
     * Constructs a new object.
     * @param doc document of the output
     */
    public Output(final SsmlDocument doc) {
        document = doc;
    }

    /**
     * Retrieves the received SSML document.
     * @return the received SSML dcument
     */
    public final SsmlDocument getDocument() {
        return document;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (document != null) {
            final Speak speak = document.getSpeak();
            return speak.getTextContent();
        }
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void receive(SsmlDocument actual) {
        final Speak speak = actual.getSpeak();
        final String text = speak.getTextContent();
        receive(text);
    }

    public void receive(final String actual) {
        final String expect = toString();
        Assert.assertEquals(getClass().getSimpleName(), expect, actual);
    }
}
