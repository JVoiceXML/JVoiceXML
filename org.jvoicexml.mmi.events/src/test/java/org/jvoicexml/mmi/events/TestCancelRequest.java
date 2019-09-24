package org.jvoicexml.mmi.events;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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
        final Bar bar = new Bar();
        bar.setValue("hurz");
        final AnyComplexType anyBars = new AnyComplexType();
        anyBars.addContent(bar);
        final Foo foo = new Foo();
        foo.setBars(anyBars);
        foo.setValue("lamm");
        final AnyComplexType data = new AnyComplexType();
        data.addContent(foo);
        request.setData(data);
        final JAXBContext ctx = JAXBContext.newInstance(Mmi.class, Foo.class,
                Bar.class);
        final Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(mmi, System.out);
    }

    @Test
    public void testRead() throws Exception {
//        final String str = "<?xml version=\"1.0\"?>"
//                + "<mmi:mmi xmlns:mmi=\"http://www.w3.org/2008/04/mmi-arch\">"
//                + "<mmi:StartRequest mmi:Context=\"da43976d-9861-469c-88af-cb71f5995542\""
//                + " mmi:RequestID=\"f7fb8a39-870b-4368-8012-cd065d55679e\""
//                + " mmi:Source=\"http://s1451.dyn.hrz.tu-darmstadt.de:4344/mmi\""
//                + " mmi:Target=\"http://jvoicexml/mmi\">"
//                + "<mmi:ContentURL "
//                + " mmi:href=\"https://hello.vxml\""
//                + " />"
//                + "</mmi:StartRequest>"
//                + "</mmi:mmi>";
        final String str =  "<?xml version=\"1.0\"?>"
                + "<mmi:mmi xmlns:mmi=\"http://www.w3.org/2008/04/mmi-arch\">"
                + "<mmi:StartRequest mmi:Context=\"context1\" mmi:RequestID=\"request1\" mmi:Source=\"source1\" mmi:Target=\"target1\">"
                + "<mmi:ContentURL mmi:href=\"http://nowhere\"/>"
                + "</mmi:StartRequest>"
                + "</mmi:mmi>";
        final StringReader reader = new StringReader(str);
        final InputStream in = TestCancelRequest.class.getResourceAsStream("StartRequest.xml");
        final JAXBContext ctx = JAXBContext.newInstance(Mmi.class);
        final Unmarshaller unmarshaller = ctx.createUnmarshaller();
        final Object o = unmarshaller.unmarshal(in);
        System.out.println(o);
    }
}
