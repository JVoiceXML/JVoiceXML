package org.jvoicexml.eclipse.jst.server;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

import org.eclipse.wst.server.core.IServer;



/**
 * JVoiceXMLPingThread 
 *
 *	Ping the JVoiceXML server
 * 
 * @author Aurelian Maga
 * @version 0.1
 *
 */

public class JVoiceXMLPingThread {

	final int SLEEP = 5000;
	IServer server;
	private JVoiceXMLServerBehaviour behaviour;
	boolean check;
	Context context;
	
	public JVoiceXMLPingThread(IServer iserver,JVoiceXMLServerBehaviour jsb){
		behaviour = jsb;
		server = iserver;
		check = false;
		Hashtable env=new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.rmi.registry.RegistryContextFactory");
		env.put(Context.PROVIDER_URL,"rmi://localhost:1099");
			
		try {
			context = new InitialContext(env);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void stop(){
		check = false;
	}
	
	public void start(){
		check = true;
		Thread t = new Thread(){
			public void run(){
				while(check){
					check();
				}
			}
		};
		
		t.start();
	}
	
	private void check() {
		try{
			Thread.sleep(SLEEP);
		}catch(Exception ignore){}
		
		try{
			
			Object jvxml = context.lookup("JVoiceXml");
			
			behaviour.setStarted();
		
			jvxml = null;
			
		}catch(Exception ignore){
		}
	}
}
