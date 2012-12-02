package org.jvoicexml.voicexmlunit;


import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.voicexmlunit.Voice;


public class TestVoice {
	private Voice voice;

	@Before
	public void setUp() throws Exception {
		voice = new Voice();
		voice.setPolicy("unittests/etc/jvoicexml.policy");
		voice.loadConfiguration("unittests/etc/jndi.properties");
	}
	
	@Test
	public void testLookup() {		
		// NOTICE: JVoiceXml has to run for test success!!
		Assert.assertNotNull(voice.getJVoiceXml());
	}

}
