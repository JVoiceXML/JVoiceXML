package org.jvoicexml.android.implementation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DtmfRecognizerProperties;
import org.jvoicexml.SpeechRecognizerProperties;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.ObservableSpokenInput;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.vxml.BargeInType;

public class AndroidSpokenInput implements SpokenInput, ObservableSpokenInput {

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void open() throws NoresourceError {
		// TODO Auto-generated method stub

	}

	@Override
	public void activate() throws NoresourceError {
		// TODO Auto-generated method stub

	}

	@Override
	public void passivate() throws NoresourceError {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isBusy() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void connect(ConnectionInformation client) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect(ConnectionInformation client) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startRecognition(SpeechRecognizerProperties speech,
			DtmfRecognizerProperties dtmf) throws NoresourceError,
			BadFetchError {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopRecognition() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addListener(SpokenInputListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeListener(SpokenInputListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<GrammarType> getSupportedGrammarTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void activateGrammars(Collection<GrammarImplementation<?>> grammars)
			throws BadFetchError, UnsupportedLanguageError,
			UnsupportedFormatError, NoresourceError {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivateGrammars(Collection<GrammarImplementation<?>> grammars)
			throws NoresourceError, BadFetchError {
		// TODO Auto-generated method stub

	}

	@Override
	public GrammarImplementation<?> loadGrammar(Reader reader, GrammarType type)
			throws NoresourceError, BadFetchError, UnsupportedFormatError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<BargeInType> getSupportedBargeInTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void record(OutputStream out) throws NoresourceError {
		// TODO Auto-generated method stub

	}

	@Override
	public URI getUriForNextSpokenInput() throws NoresourceError,
			URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

}
