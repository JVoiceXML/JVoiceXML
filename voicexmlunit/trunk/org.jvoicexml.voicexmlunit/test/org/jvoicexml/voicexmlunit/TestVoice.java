package org.jvoicexml.voicexmlunit;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.jvoicexml.voicexmlunit.Voice;

public class TestVoice {

	@Test
	public void test() {
		System.setProperty("java.security.policy","test/etc/jvoicexml.policy");
		
		// NOTICE: JVoiceXml has to run for test success!!
		final String path = "test/etc/jndi.properties";
		final File configuration = new File(path);
		Assert.assertNotNull(Voice.lookup(configuration));
	}

}
