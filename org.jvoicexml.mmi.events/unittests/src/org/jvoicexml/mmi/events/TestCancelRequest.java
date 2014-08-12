package org.jvoicexml.mmi.events;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;

/**
 * Test cases for {@link CancelRequest}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 *
 */
public class TestCancelRequest {

    /**
     * Tests the serialization of a {@link CancelRequest}.
     * @throws Exception
     *         test failed
     */
    @Test
    public void testCancelRequest() throws Exception {
        final Mmi mmi = new Mmi();
        final CancelRequest request = new CancelRequest();
        mmi.setCancelRequest(request);
        request.setRequestId("request1");
        request.setSource("source1");
        request.setTarget("target1");
        request.setContext("context1");
        List<Object> data = new ArrayList<Object>();
        final Bar bar = new Bar();
        bar.setValue("hurz");
        final Foo foo = new Foo();
        final AnyComplexType any = new AnyComplexType();
//        List<Object> bars = new ArrayList<Object>();
        any.getContent().add(bar);
        foo.setBars(any);
        foo.setValue("lamm");
        data.add(foo);
        request.setData(any);
        final JAXBContext ctx = JAXBContext.newInstance(Mmi.class, Foo.class,
                Bar.class);
        final Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(mmi, System.out);
//        marshaller.marshal(foo, System.out);

    }

}
