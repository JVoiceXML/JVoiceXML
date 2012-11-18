package org.jvoicexml.voicexmlunit.test;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.jvoicexml.voicexmlunit.Voice;

public class VoiceTest {

	@Test
	public void test() {
		System.setProperty("java.security.policy","test/jvoicexml.policy");
		
		// NOTICE: JVoiceXml has to run for test success!!
		final String path = "test/jndi.properties";
		final File configuration = new File(path);
		Assert.assertNotNull(Voice.lookup(configuration));
	}

}
