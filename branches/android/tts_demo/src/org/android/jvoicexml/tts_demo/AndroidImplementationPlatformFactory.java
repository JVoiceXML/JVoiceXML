package org.android.jvoicexml.tts_demo;

import java.util.Locale;

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
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.EventObserver;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;

import android.app.Activity;
import android.content.Context;

public class AndroidImplementationPlatformFactory extends Activity
	implements ImplementationPlatformFactory, ImplementationPlatform{
	
	private Session session;
	private EventObserver observer;
	private long timeout;
	private AndroidSpokenInput androidSpokenInput;
	private AndroidSynthesizedOutput androidSynthesizedOutput;
	private Context context;
	private Object renderLock;
	private TTSEventManager ttsEventManager;
	
	public AndroidImplementationPlatformFactory(Context context){
		this.context=context;
		renderLock= new Object();
		androidSpokenInput =new AndroidSpokenInput();
		androidSynthesizedOutput= new AndroidSynthesizedOutput(context,renderLock);
		ttsEventManager = new TTSEventManager(androidSynthesizedOutput, context);
		
	}
	
	
	@Override
	public void setPromptTimeout(long timeout) {
		this.timeout=timeout;
	}

	@Override
	public void queuePrompt(SpeakableText speakable) {
		
		try {
			androidSynthesizedOutput.queueSpeakable(speakable, null, null);
		} catch (NoresourceError e) {
			e.printStackTrace();
		} catch (BadFetchError e) {
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
	public void init(Configuration config) throws ConfigurationException
	{
		return;
	}
	public void init() throws ConfigurationException
	{
		return;
	}

	@Override
	public void renderPrompts(String sessionId, DocumentServer server,
			CallControlProperties callProps) throws BadFetchError,
			NoresourceError, ConnectionDisconnectHangupEvent {
		
		if(androidSynthesizedOutput.ttsInitialized!=true)
			androidSynthesizedOutput.start();
		else 
			androidSynthesizedOutput.processNextSpeakable();
		
	}

	@Override
	public ImplementationPlatform getImplementationPlatform(
			ConnectionInformation info) throws NoresourceError {
		return this;
	}


	
}
