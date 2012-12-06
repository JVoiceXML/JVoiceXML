package org.jvoicexml.voicexmlunit.demo.input;


import static org.junit.Assert.*;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.voicexmlunit.Call;
import org.jvoicexml.voicexmlunit.Supervisor;
import org.jvoicexml.voicexmlunit.Voice;

import org.jvoicexml.xml.vxml.VoiceXmlDocument;


public class DtmfDemo {

	private Document demo = new Document();
	private Voice voice = new Voice();
	
	private VoiceXmlDocument document;
	private Call call;
	private Supervisor supervisor;
    
	@Before
	public void setUp() throws Exception {
		voice.setPolicy("etc/jvoicexml.policy");
		voice.lookupJVoiceXML();

		document = demo.create();
        if (document != null) {
	        @SuppressWarnings("unused")
			final String xml = demo.print(document);

	        call = new Call(demo.add(voice.getContext(), document));
	        call.setVoice(voice);
	        
	        supervisor = new Supervisor();
        }
	}

	@Test
	public void testDocument() {
		assertNotNull("JVoiceXML",voice.getJVoiceXml());

     	demo.interpret(supervisor,call);
    	
    	assertTrue(demo.inputSent());
	}

}
