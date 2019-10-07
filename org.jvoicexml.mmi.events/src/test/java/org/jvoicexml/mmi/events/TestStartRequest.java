/**
 * 
 */
package org.jvoicexml.mmi.events;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

/**
 * @author dirk
 * 
 */
public class TestStartRequest {
    /**
     * Tests the serialization of a {@link StartRequest}.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void testStartRequest() throws Exception {
        final Mmi mmi = new Mmi();
        final StartRequest request = new StartRequest();
        mmi.setStartRequest(request);
        request.setRequestId("request1");
        request.setSource("source1");
        request.setTarget("target1");
        request.setContext("context1");
        final ContentURLType url = new ContentURLType();
        url.setHref("http://nowhere");
        request.setContentURL(url);
        final JAXBContext ctx = JAXBContext.newInstance(Mmi.class);
        final Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(mmi, out);
        System.out.println("1*" + out);
        final Unmarshaller unmarshaller = ctx.createUnmarshaller();
        final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        final Object o = unmarshaller.unmarshal(in);
        System.out.println("2*" + o);
    }

    @Test
    public void testReadFromFile() throws Exception {
        final Mmi mmi = new Mmi();
        final StartRequest request = new StartRequest();
        mmi.setStartRequest(request);
        request.setRequestId("request1");
        request.setSource("source1");
        request.setTarget("target1");
        request.setContext("context1");
        final ContentURLType url = new ContentURLType();
        url.setHref("http://nowhere");
        request.setContentURL(url);
        final JAXBContext ctx = JAXBContext.newInstance(Mmi.class);
        final Marshaller marshaller = ctx.createMarshaller();
//        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        final FileOutputStream out = new FileOutputStream("testrequest.xml");
        marshaller.marshal(mmi, out);
        out.close();
        final FileInputStream in = new FileInputStream("testrequest.xml");
        final Unmarshaller unmarshaller = ctx.createUnmarshaller();
        final Object o = unmarshaller.unmarshal(in);
        System.out.println(o);
    }
}
