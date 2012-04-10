package org.jvoicexml.android;

import org.jvoicexml.CallControl;
import org.jvoicexml.CallControlProperties;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.Configuration;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.Session;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.EventObserver;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;

public class AndroidImplementationPlatformFactory implements
		ImplementationPlatform {

	@Override
	public void setPromptTimeout(long timeout) {
		// TODO Auto-generated method stub

	}

	@Override
	public void queuePrompt(SpeakableText speakable) {
		// TODO Auto-generated method stub

	}

	

	@Override
	public SystemOutput getSystemOutput() throws NoresourceError,
			ConnectionDisconnectHangupEvent {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void waitOutputQueueEmpty() {
		// TODO Auto-generated method stub

	}

	@Override
	public void waitNonBargeInPlayed() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasUserInput() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public UserInput getUserInput() throws NoresourceError,
			ConnectionDisconnectHangupEvent {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharacterInput getCharacterInput() throws NoresourceError,
			ConnectionDisconnectHangupEvent {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CallControl getCallControl() throws NoresourceError,
			ConnectionDisconnectHangupEvent {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEventHandler(EventObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSession(Session session) {
		// TODO Auto-generated method stub

	}
	public void init(Configuration config)
	{
		
	}

	@Override
	public void renderPrompts(String sessionId, DocumentServer server,
			CallControlProperties callProps) throws BadFetchError,
			NoresourceError, ConnectionDisconnectHangupEvent {
		// TODO Auto-generated method stub
		
	}

}
