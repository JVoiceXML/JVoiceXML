package org.jvoicexml.systemtest;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.systemtest.testcase.IRTestCase;
import org.jvoicexml.systemtest.testcase.IRTestCaseLibrary;

public class CallManagerTest {

	IRTestCaseLibrary lib = null;

	String docBase = "http://localhost:8080/irtest/irtests/";

	String docURI = docBase + "manifest.xml";

	@Before
	public void setUp() throws Exception {
		boolean remote = true;
		URL url = null;
		if (remote) {
			url = new URL(docURI);
		} else {
			File f = new File(
					"/home/lancer/works/nsjvxml/xxjas/xxjas-vxml/vxml/irtests/manifest.xml");
			Assert.assertTrue(f.exists());
			url = f.toURI().toURL();
		}
		lib = new IRTestCaseLibrary(url);
	}

	@Test
	public void listFatch() throws Exception {
		
		SystemTestCallManager cm = new SystemTestCallManager();

		Assert.assertTrue(lib.size() > 0);
		List<IRTestCase> list;

		list = cm.getJobs(lib, "345");
		Assert.assertEquals(1, list.size());
		IRTestCase tc = list.get(0);
		Assert.assertEquals(345, tc.getId());

		list = cm.getJobs(lib, "345,346, 1, 2 , 24 ");
		Assert.assertEquals(5, list.size());
		int i = 0;
		Assert.assertEquals(345, list.get(i++).getId());
		Assert.assertEquals(346, list.get(i++).getId());
		Assert.assertEquals(1, list.get(i++).getId());
		Assert.assertEquals(2, list.get(i++).getId());
		Assert.assertEquals(24, list.get(i++).getId());

		list = cm.getJobs(lib, "5");
		Assert.assertEquals(0, list.size());
	}

}
