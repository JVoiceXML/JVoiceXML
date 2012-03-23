package org.jvoicexml.android.implementation;

import java.io.IOException;

import android.app.Activity;
import android.content.pm.PackageManager;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.ArrayAdapter;

public class AndroidSpokenInput extends Activity implements SpokenInput, ObservableSpokenInput {
	
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "android";
	}

	@Override
	public void open() throws NoresourceError {
		// Check to see if a recognition activity is present
		PackageManager pm = getPackageManager();
		List activities = pm.queryIntentActivities(
		  new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() != 0) {
		  return;
		} else {
			return;
		  //throw NoresourceError;
		}

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
		/**
	     * Fire an intent to start the speech recognition activity.
	     */	    
	        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

	        // Specify the calling package to identify your application
	        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

	        // Display an hint to the user about what he should say.
	        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");

	        // Given an hint to the recognizer about what the user is going to say
	        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

	        // Specify how many results you want to receive. The results will be sorted
	        // where the first result is the one with higher confidence.
	        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

	        // Specify the recognition language. This parameter has to be specified only if the
	        // recognition has to be done in a specific language and not the default one (i.e., the
	        // system locale). Most of the applications do not have to set this parameter.
	        if (!mSupportedLanguageView.getSelectedItem().toString().equals("Default")) {
	            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
	                    mSupportedLanguageView.getSelectedItem().toString());
	        }

	        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);   

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

//	@Override
//	public void record(OutputStream out) throws NoresourceError {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public URI getUriForNextSpokenInput() throws NoresourceError,
			URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}
	protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
           //what to do here?
        }

    }

}
