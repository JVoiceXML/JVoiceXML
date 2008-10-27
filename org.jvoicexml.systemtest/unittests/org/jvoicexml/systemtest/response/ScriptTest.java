package org.jvoicexml.systemtest.response;

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
import org.jvoicexml.systemtest.Action;

public class ScriptTest {
    /** Logger for this class. */
    final private static Logger LOGGER = Logger.getLogger(ScriptTest.class);

    @Test
    public void scriptLoad() throws Exception {
        URL file = ScriptTest.class.getResource("example.script.xml");
        Script script = new Script(file);

        Assert.assertEquals(3, script.getActions().size());
        Iterator<Action> iterator = script.getActions().iterator();
        
        Action a = iterator.next();
        Assert.assertTrue(a instanceof WaitAction);
        Assert.assertEquals(1000, ((WaitAction)a).timeout);
        
        a = iterator.next();
        Assert.assertTrue(a instanceof WaitAction);
        Assert.assertEquals(2000, ((WaitAction)a).timeout);
        
        a = iterator.next();
        Assert.assertTrue(a instanceof AnswerAction);
        Assert.assertEquals("That is OK.", ((AnswerAction)a).speak);
    }

    @Test
    public void scriptWrite() throws URISyntaxException, IOException {
        ScriptXMLDocument script = new ScriptXMLDocument();
        script.action.add(new WaitAction());
        script.action.add(new AnswerAction());

        Writer writer = new OutputStreamWriter(System.out);

        List<Class> names = new ArrayList<Class>();
        names.add(ScriptXMLDocument.class);
        names.add(WaitAction.class);
        names.add(AnswerAction.class);
        Map<String, Object> prep = new HashMap<String, Object>();
        try {
            JAXBContext jc = JAXBContext.newInstance(names.toArray(new Class[names.size()]), prep);

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
