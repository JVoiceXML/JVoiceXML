package voicexmlunit.demo.hello;


import java.io.File;

import org.junit.*;
import org.jvoicexml.client.text.TextServer;

import voicexmlunit.framework.*;

import junit.framework.TestCase;

public class HelloTest extends TestCase {

	private Supervisor supervisor;

	@Before
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty("java.security.policy","config/jvoicexml.policy");
		
		/*
		System.setProperty("java.naming.factory.initial","com.sun.jndi.rmi.registry.RegistryContextFactory");
		System.setProperty("java.naming.provider.url","rmi://localhost:1099");
		System.setProperty("java.naming.rmi.security.manager","true");
		*/
				
		supervisor = new Supervisor();
		supervisor.lookupVoice(new File("config/jndi.properties"));
	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void test() {
		final TextServer server = new TextServer(4242);
		final Conversation conversation = supervisor.init(server);

		conversation.addOutput("Hello World!");
		conversation.addOutput("Goodbye!");

		supervisor.process(new File("helloworld.vxml"));
	}

}
