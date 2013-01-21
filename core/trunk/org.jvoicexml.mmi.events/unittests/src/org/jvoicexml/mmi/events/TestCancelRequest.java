package org.jvoicexml.mmi.events;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;

public class TestCancelRequest {

    @Test
    public void testCancelRequest() throws Exception {
        final CancelRequest request = new CancelRequest();
        request.setRequestID("request1");
        request.setSource("source1");
        request.setTarget("target1");
        request.setContext("context1");
        List data = new ArrayList();
        final Bar bar = new Bar();
        bar.setValue("hurz");
        final Foo foo = new Foo();
        List<Object> bars = new ArrayList<Object>();
        bars.add(bar);
        foo.setBars(bars);
        foo.setValue("lamm");
        data.add(foo);
        request.setData(data);
        final JAXBContext ctx = JAXBContext.newInstance(Mmi.class, Foo.class, Bar.class);
        final Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(request, System.out);
//        marshaller.marshal(foo, System.out);

    }

}
