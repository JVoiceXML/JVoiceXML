package org.jvoicexml.android.callmanager;

import org.jvoicexml.CallControl;
import org.jvoicexml.CallControlProperties;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.ImplementationPlatformFactory;
import org.jvoicexml.Session;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.EventObserver;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;


import android.content.Context;
import android.util.Log;

public class AndroidImplementationPlatformFactory 
	implements ImplementationPlatformFactory, ImplementationPlatform {
	
	private Session session;
	private EventObserver observer;
	private long timeout;
	private Context context;
	private AndroidSpokenInput androidSpokenInput;
	private AndroidSynthesizedOutput androidSynthesizedOutput;
	private AndroidSynthesizedOutputFactory androidSynthesizedOutputFactory;
	
	
	
	@Override
	public void setPromptTimeout(long timeout) {
		this.timeout=timeout;
	}

	@Override
	public void queuePrompt(SpeakableText speakable) {
		// TODO Auto-generated method stub
		//here I have to add the code to queue the Prompt into the Android TTS Engine
		//I'm not passing any document server, later will see if there's need for it
		try {
			androidSynthesizedOutput.queueSpeakable(speakable, session.getSessionID(), null);
		} catch (NoresourceError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadFetchError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public SystemOutput getSystemOutput() throws NoresourceError,
			ConnectionDisconnectHangupEvent {
		return null;
	}

	@Override
	public void waitOutputQueueEmpty() {

	}

	@Override
	public void waitNonBargeInPlayed() {

	}

	@Override
	public boolean hasUserInput() {
		if (androidSpokenInput!=null)
			return true;
		else
			return false;
	}

	@Override
	public UserInput getUserInput() throws NoresourceError,
			ConnectionDisconnectHangupEvent {
		return androidSpokenInput;
	}

	@Override
	public CharacterInput getCharacterInput() throws NoresourceError,
			ConnectionDisconnectHangupEvent {
		return null;
	}

	@Override
	public CallControl getCallControl() throws NoresourceError,
			ConnectionDisconnectHangupEvent {
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEventHandler(EventObserver observer) {
		this.observer=observer;
		return;
	}

	@Override
	public void setSession(Session session) {
		this.session = session;
		return;
	}

	public void setContext(Context context) {
		this.context = context;
		return;
	}
	public void init(Configuration config) throws ConfigurationException
	{
		AndroidConfiguration androidConfig = (AndroidConfiguration) config;
		setContext(androidConfig.getContext());
		androidSynthesizedOutputFactory= new AndroidSynthesizedOutputFactory();
		androidSynthesizedOutputFactory.setContext(androidConfig.getContext());
		
		
		
		try {
			androidSynthesizedOutput= (AndroidSynthesizedOutput) androidSynthesizedOutputFactory.createResource();
		} catch (NoresourceError e) {
			Log.e("AndroidImplementationPlatformFactory","There's not an androidSynthesizedOutput available");
			e.printStackTrace();
		}
		androidSpokenInput = new AndroidSpokenInput();
		return;
	}

	@Override
	public void renderPrompts(String sessionId, DocumentServer server,
			CallControlProperties callProps) throws BadFetchError,
			NoresourceError, ConnectionDisconnectHangupEvent {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ImplementationPlatform getImplementationPlatform(
			ConnectionInformation info) throws NoresourceError {
		return this;
	}

}
