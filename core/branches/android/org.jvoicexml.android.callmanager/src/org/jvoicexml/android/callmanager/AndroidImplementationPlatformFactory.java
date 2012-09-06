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
import org.jvoicexml.android.implementation.AndroidSpokenInput;
import org.jvoicexml.event.EventObserver;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;

public class AndroidImplementationPlatformFactory 
	implements ImplementationPlatformFactory, ImplementationPlatform {
	
	private Session session;
	private EventObserver observer;
	private long timeout;
	private AndroidSpokenInput androidSpokenInput;
	
	
	@Override
	public void setPromptTimeout(long timeout) {
		this.timeout=timeout;
	}

	@Override
	public void queuePrompt(SpeakableText speakable) {
		// TODO Auto-generated method stub

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
	}

	@Override
	public void setSession(Session session) {
		this.session = session;
	}
	public void init(Configuration config) throws ConfigurationException
	{
		androidSpokenInput = new AndroidSpokenInput();
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
