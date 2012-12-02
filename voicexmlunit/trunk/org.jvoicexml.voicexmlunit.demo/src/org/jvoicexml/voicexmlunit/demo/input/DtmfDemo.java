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
	
	private Supervisor supervisor;
    
	@Before
	public void setUp() throws Exception {
		voice.setPolicy("etc/jvoicexml.policy");

		document = demo.createDocument();
        if (document != null) {
	        @SuppressWarnings("unused")
			final String xml = demo.printDocument(document);

	        supervisor = new Supervisor();
        }
	}

	@Test
	public void testDocument() {
		assertNotNull("JVoiceXML",voice.getJVoiceXml());
		
		assertNotNull(document);
        URI uri = demo.addDocument(voice.getContext(), document);
		assertNotNull(uri);
		
    	final Call call = new Call(uri);
    	demo.interpretDocument(supervisor,call);
    	
    	assertTrue(demo.inputSent());
	}

}
