package org.jvoicexml.systemtest.script;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

public class ScriptDocNodeTest {
    /** Logger for this class. */
    final private static Logger LOGGER = Logger.getLogger(ScriptDocNodeTest.class);


    @Test
    public void scriptWrite() throws URISyntaxException, IOException {
        ScriptDocNode script = new ScriptDocNode();
        script.action.add(new DTMFAction());
        script.action.add(new SpeakAction());
        script.action.add(new NoInputAction());

        Writer writer = new OutputStreamWriter(System.out);

        Class[] names = new Class[4];
        int i = 0;
        names[i++] = ScriptDocNode.class;
        names[i++] = SpeakAction.class;
        names[i++] = NoInputAction.class;
        names[i++] = DTMFAction.class;
        Map<String, Object> prep = new HashMap<String, Object>();
        try {
            JAXBContext jc = JAXBContext.newInstance(names, prep);

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.setProperty(Marshaller.JAXB_FRAGMENT, true);
            m.marshal(script, writer);
        } catch (JAXBException e) {
            throw new IOException(e);
        }
        writer.close();
    }
}
