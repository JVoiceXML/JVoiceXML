package org.jvoicexml.android.callmanager;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXmlMain;
import org.jvoicexml.JVoiceXmlMainListener;
import org.jvoicexml.Session;
import org.jvoicexml.client.BasicConnectionInformation;
import org.jvoicexml.event.ErrorEvent;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class Interpreter extends Thread implements JVoiceXmlMainListener
{
	private JVoiceXmlMain jvxml;
	
	private URI voiceXmlDocument;
	
	private Context callManagerContext;
	  
	  /** Logger for this class. */
	public static final Logger LOGGER = Logger.getLogger(Interpreter.class);
	
	public Interpreter(final Uri uri, Context context)
	{
		System.setProperty("jvoicexml.config", "../org.jvoicexml/config");
		
		try {
			this.voiceXmlDocument= new URI(uri.toString());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		android.os.Debug.waitForDebugger();
		this.callManagerContext=context; 
		
	}
	public void run()
	{
		AndroidConfiguration config = new AndroidConfiguration();
		config.setContext(callManagerContext);
		jvxml = new JVoiceXmlMain(config);
		jvxml.addListener(this);
		jvxml.start();
		
		
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}	
		Log.e("el Interprete salio del wait","VICTORIA!");
	
//		while(true);
		
		final ConnectionInformation client = new BasicConnectionInformation("dummy", "android", "android");
		Session session = null;
		try {
			session = jvxml.createSession(client);
		} catch (ErrorEvent e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			session.call(voiceXmlDocument);
		} catch (ErrorEvent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			session.waitSessionEnd();
		} catch (ErrorEvent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		session.hangup();
	}
	@Override
	public synchronized void jvxmlStarted() {
		this.notifyAll();		
	}
	@Override
	public synchronized void jvxmlTerminated() {
		// TODO Auto-generated method stub
		this.notifyAll();
	}
	
	public void finish()
	{
		jvxml.shutdown();
		jvxml.waitShutdownComplete();
		this.stop();
	}
	
}
