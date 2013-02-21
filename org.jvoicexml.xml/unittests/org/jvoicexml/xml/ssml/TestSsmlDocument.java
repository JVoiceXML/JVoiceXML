/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/unittests/org/jvoicexml/xml/ssml/TestSsmlDocument.java $
 * Version: $LastChangedRevision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.xml.ssml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;


/**
 * Test cases for {@link SsmlDocument}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2325 $
 * @since 0.7.3
 */
public final class TestSsmlDocument {
    /**
     * Test case for the serialization of an XML document.
     * @throws Exception
     *         test failed.
     */
    @Test
    public void testSerialize() throws Exception {
        System.setProperty("jvoicexml.xml.encoding", "UTF-8");
        final SsmlDocument doc = new SsmlDocument();
        final Speak speak = doc.getSpeak();
        speak.addText("Hello World!");
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectOutputStream oout = new ObjectOutputStream(out);
        oout.writeObject(doc);
        final ByteArrayInputStream in =
            new ByteArrayInputStream(out.toByteArray());
        final ObjectInputStream oin = new ObjectInputStream(in);
        final Object o = oin.readObject();
        Assert.assertEquals(doc.toString(), o.toString());

        final String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<speak><audio "
            + "src=\"http://localhost:8080/ivr.audios-0.0.1/a774.wav\"></audio>"
            + "</speak>";
        final StringReader reader = new StringReader(str);
        final InputSource source = new InputSource(reader);
        final SsmlDocument doc2 = new SsmlDocument(source);
        final ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        final ObjectOutputStream oout2 = new ObjectOutputStream(out2);
        oout2.writeObject(doc2);
        final ByteArrayInputStream in2 =
            new ByteArrayInputStream(out2.toByteArray());
        final ObjectInputStream oin2 = new ObjectInputStream(in2);
        final Object o2 = oin2.readObject();
        Assert.assertEquals(doc2.toString(), o2.toString());
    }

}
