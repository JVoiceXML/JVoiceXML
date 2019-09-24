/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.mmi.events.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.mmi.events.AnyComplexType;
import org.jvoicexml.mmi.events.Bar;
import org.jvoicexml.mmi.events.CancelRequest;
import org.jvoicexml.mmi.events.Foo;

/**
 * Test cases for [{@link JsonMmi}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class JsonMmiTest {

    final String getResourceAsString(final String resource) throws IOException {
        final InputStream in = JsonMmiTest.class.getResourceAsStream(resource);
        if (in == null) {
            throw new IOException("Resource '" + resource + "' not found");
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] readBuffer = new byte[1024];
        int num;
        do {
            num = in.read(readBuffer);
            if (num >= 0) {
                out.write(readBuffer, 0, num);
            }
        } while(num >= 0);
        return out.toString();
    }

    /**
     * Test case for {@link JsonMmi#toJson()}.
     * @throws IOException
     *          test failed
     */
    @Test
    public void testToJson() throws IOException {
        final JsonMmi mmi = new JsonMmi();
        final CancelRequest request = new CancelRequest();
        mmi.setLifeCycleEvent(request);
        request.setRequestId("request1");
        request.setSource("source1");
        request.setTarget("target1");
        request.setContext("context1");
        final Bar bar = new Bar();
        bar.setValue("hurz");
        final Foo foo = new Foo();
        final AnyComplexType any = new AnyComplexType();
        any.addContent(bar);
        foo.setBars(any);
        foo.setValue("lamm");
        final AnyComplexType data = new AnyComplexType();
        data.addContent(foo);
        request.setData(data);
        final String json = mmi.toJson();
        final String ref = getResourceAsString("/CancelRequest.json");
        Assert.assertEquals(ref, json);
    }

}
