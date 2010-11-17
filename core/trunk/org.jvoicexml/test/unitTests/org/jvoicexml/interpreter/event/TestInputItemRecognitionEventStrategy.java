package org.jvoicexml.interpreter.event;


import static org.junit.Assert.fail;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.DummyRecognitionResult;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
import org.jvoicexml.xml.ccxml.Disconnect;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Filled;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Nomatch;
import org.jvoicexml.xml.vxml.Property;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

public class TestInputItemRecognitionEventStrategy {
    /** The VoiceXML interpreter context. */
    private VoiceXmlInterpreterContext context;

    /** The VoiceXML interpreter. */
    private VoiceXmlInterpreter interpreter;
    
    /** The recognized result */
    private DummyRecognitionResult result;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        final ImplementationPlatform platform =
            new DummyImplementationPlatform();
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final JVoiceXmlSession session =
            new JVoiceXmlSession(platform, jvxml, null);
        context = new VoiceXmlInterpreterContext(session);
        interpreter = new VoiceXmlInterpreter(context);
        
        /**
         * Set up Dummy result
         * utterance: "hello world"
         * confidence: 0.55
         */
        result = new DummyRecognitionResult();
        result.setUtterance("hello world");
        result.setConfidence(0.55f);
        result.setAccepted(true);
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testHandleEvent() throws ParserConfigurationException, IOException {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        
        final Field field = form.appendChild(Field.class);
        field.setName("field");
        
        final Property property = field.appendChild(Property.class);
        property.setAttribute("name", "confidencelevel");
        property.setAttribute("value", "0.8");
        
        final Grammar grammar = field.appendChild(Grammar.class);
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId("rootRule");
        grammar.setRoot(rule);
        final Item item = rule.appendChild(Item.class);
        item.setTextContent("hello world");
        
        final Nomatch nomatch = field.appendChild(Nomatch.class);
        
        final Filled filled = field.appendChild(Filled.class);
        filled.appendChild(Disconnect.class);
        
        System.out.println(document.toXml());
        
        //TODO
        fail("This is just a test skeleton");
    }

    
    
    
}
