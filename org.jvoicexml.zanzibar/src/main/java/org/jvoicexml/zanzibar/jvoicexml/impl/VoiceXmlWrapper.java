package org.jvoicexml.zanzibar.jvoicexml.impl;

import org.jvoicexml.JVoiceXmlMain;
import org.jvoicexml.JVoiceXmlMainListener;
import org.jvoicexml.config.JVoiceXmlConfiguration;

public class VoiceXmlWrapper implements JVoiceXmlMainListener {

	private JVoiceXmlMain jvxml;



	public VoiceXmlWrapper() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public void  startUp() {
		JVoiceXmlConfiguration config = new JVoiceXmlConfiguration();
		jvxml = new JVoiceXmlMain(config);
		jvxml.addListener(this);
		jvxml.start();
		
		//this.wait();
	}


	public void jvxmlStarted() {
		// TODO Auto-generated method stub
		
	}


	public void jvxmlTerminated() {
		// TODO Auto-generated method stub
		
	}
	

	public JVoiceXmlMain getJvxml() {
		return jvxml;
	}


	public void setJvxml(JVoiceXmlMain jvxml) {
		this.jvxml = jvxml;
	}


	@Override
	public void jvxmlStartupError(Throwable exception) {
		// TODO Auto-generated method stub
		
	}


}
