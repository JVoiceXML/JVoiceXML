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
import java.lang.reflect.Type;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.mmi.events.AnyComplexType;
import org.jvoicexml.mmi.events.Bar;
import org.jvoicexml.mmi.events.CancelRequest;
import org.jvoicexml.mmi.events.CancelResponse;
import org.jvoicexml.mmi.events.Foo;
import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.StatusType;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * Test cases for [{@link JsonMmi}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class JsonMmiTest {

    /**
     * Retrieves the contents of the specified resource as a string.
     * @param resource path of the resource
     * @return contents of the resource
     * @throws IOException
     *          if the resource could not be found
     */
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
    public void testToJsonCancelRequest() throws IOException {
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

    @Test
    public void testFromJsonCancelRequestNoData() throws IOException {
        final String json = getResourceAsString("/CancelRequestNoData.json");
        final JsonMmi mmi = JsonMmi.fromJson(json);
        final LifeCycleEvent event = mmi.getLifeCycleEvent();
        Assert.assertTrue(event instanceof CancelRequest);
        final CancelRequest cancelRequest = (CancelRequest) event;
        Assert.assertEquals("context1", cancelRequest.getContext());
        Assert.assertEquals("source1", cancelRequest.getSource());
        Assert.assertEquals("request1", cancelRequest.getRequestId());
        Assert.assertEquals("target1", cancelRequest.getTarget());
        Assert.assertNull(cancelRequest.getData());
    }

    class FooDeserializer implements JsonDeserializer<Foo> {
        @Override
        public Foo deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {
            final JsonObject object = json.getAsJsonObject();
            final JsonElement valueElement = object.get("value");
            final String value = valueElement.getAsString();
            final Foo foo = new Foo();
            foo.setValue(value);
            if (object.has("bars")) {
                final AnyComplexType any = new AnyComplexType();
                final JsonElement dataElement = object.get("bars");
                final JsonArray data = dataElement.getAsJsonArray();
                for (int i=0; i< data.size(); i++) {
                    final JsonElement current = data.get(i);
                    final Bar bar = context.deserialize(current, Bar.class);
                    any.addContent(bar);
                }
                foo.setBars(any);
            }
            
            return foo;
        }
        
    }
    
    @Test
    public void testFromJsonCancelRequest() throws IOException {
        final String json = getResourceAsString("/CancelRequest.json");
        final JsonDeserializerConfiguration foodeser = new JsonDeserializerConfiguration(Foo.class, new FooDeserializer());
        final JsonMmi mmi = JsonMmi.fromJson(json, Foo.class, Object.class, 
                foodeser);
        final JsonMmi otherMmi = new JsonMmi();
        final CancelRequest request = new CancelRequest();
        otherMmi.setLifeCycleEvent(request);
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
        Assert.assertEquals(otherMmi, mmi);
    }

    /**
     * Test case for {@link JsonMmi#toJson()}.
     * @throws IOException
     *          test failed
     */
    @Test
    public void testToJsonCancelResponse() throws IOException {
        final JsonMmi mmi = new JsonMmi();
        final CancelResponse response = new CancelResponse();
        mmi.setLifeCycleEvent(response);
        response.setRequestId("request1");
        response.setSource("source1");
        response.setTarget("target1");
        response.setContext("context1");
        final Bar bar = new Bar();
        bar.setValue("hurz2");
        final Foo foo = new Foo();
        final AnyComplexType any = new AnyComplexType();
        any.addContent(bar);
        foo.setBars(any);
        foo.setValue("lamm2");
        final AnyComplexType data = new AnyComplexType();
        data.addContent(foo);
        response.setData(data);
        response.setStatus(StatusType.SUCCESS);
        response.addStatusInfo("ois is guat");
        final String json = mmi.toJson();
        final String ref = getResourceAsString("/CancelResponse.json");
        Assert.assertEquals(ref, json);
    }

    @Test
    public void testFromJsonCancelResponse() throws IOException {
        final String json = getResourceAsString("/CancelResponse.json");
        final JsonDeserializerConfiguration foodeser = new JsonDeserializerConfiguration(Foo.class, new FooDeserializer());
        final JsonMmi mmi = JsonMmi.fromJson(json, Foo.class, String.class, 
                foodeser);
        final JsonMmi otherMmi = new JsonMmi();
        final CancelResponse response = new CancelResponse();
        otherMmi.setLifeCycleEvent(response);
        response.setRequestId("request1");
        response.setSource("source1");
        response.setTarget("target1");
        response.setContext("context1");
        final Bar bar = new Bar();
        bar.setValue("hurz2");
        final Foo foo = new Foo();
        final AnyComplexType any = new AnyComplexType();
        any.addContent(bar);
        foo.setBars(any);
        foo.setValue("lamm2");
        final AnyComplexType data = new AnyComplexType();
        data.addContent(foo);
        response.setData(data);
        response.setStatus(StatusType.SUCCESS);
        response.addStatusInfo("ois is guat");
        Assert.assertEquals(otherMmi, mmi);
    }
}
