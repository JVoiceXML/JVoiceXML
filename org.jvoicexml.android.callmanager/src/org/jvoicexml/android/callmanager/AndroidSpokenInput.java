package org.jvoicexml.android.callmanager;

import java.io.IOException;

import android.app.Activity;
import android.content.pm.PackageManager;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DtmfRecognizerProperties;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.SpeechRecognizerProperties;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.ExternalResource;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.ObservableSpokenInput;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.implementation.SrgsXmlGrammarImplementation;
import org.jvoicexml.implementation.grammar.transformer.XsltGrammarTransformer;
import org.jvoicexml.processor.srgs.GrammarChecker;
import org.jvoicexml.processor.srgs.GrammarGraph;
import org.jvoicexml.processor.srgs.SrgsXmlGrammarParser;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AndroidSpokenInput extends Activity implements UserInput, ObservableSpokenInput,ExternalResource {
	
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
//	private Spinner mSupportedLanguageView;
	 /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(AndroidSpokenInput.class);

    private XsltGrammarTransformer transformer;
    
    /** Supported barge-in types. */
    private static final Collection<BargeInType> BARGE_IN_TYPES;

    /** Supported grammar types. */
    private static final Collection<GrammarType> GRAMMAR_TYPES;

    /**Reference to the SrgsXmlGrammarParser.*/
    private final SrgsXmlGrammarParser parser;
    
    /** Active grammar checkers.*/
    private final Map<SrgsXmlGrammarImplementation, GrammarChecker>
        grammarCheckers;

    static {
        BARGE_IN_TYPES = new java.util.ArrayList<BargeInType>();
        BARGE_IN_TYPES.add(BargeInType.SPEECH);
        BARGE_IN_TYPES.add(BargeInType.HOTWORD);

        GRAMMAR_TYPES = new java.util.ArrayList<GrammarType>();
        GRAMMAR_TYPES.add(GrammarType.SRGS_XML);
    }

    /** Registered listener for input events. */
    private final Collection<SpokenInputListener> listener;

    /** Flag, if recognition is turned on. */
    private boolean recognizing;
    
    public AndroidSpokenInput()
    {

    	listener = new java.util.ArrayList<SpokenInputListener>();
        grammarCheckers = new java.util.HashMap<SrgsXmlGrammarImplementation,
            GrammarChecker>();
        parser = new SrgsXmlGrammarParser();
    }

	@Override
	public String getType() {
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
		listener.clear();
        grammarCheckers.clear();
        recognizing = false;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isBusy() {
		return recognizing;
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
//	        if (!mSupportedLanguageView.getSelectedItem().toString().equals("Default")) {
//	            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
//	                    mSupportedLanguageView.getSelectedItem().toString());
//	        }

	        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);   

	}

	@Override
	public void stopRecognition() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addListener(SpokenInputListener listener) {
		this.listener.add(listener);
	}

	@Override
	public void removeListener(SpokenInputListener listener) {
		this.listener.remove(listener);

	}

	@Override
	public Collection<GrammarType> getSupportedGrammarTypes(final ModeType mode) {
		 return GRAMMAR_TYPES;	
		 }

	@Override
	public void activateGrammars(final Collection<GrammarDocument> grammars)
	            throws BadFetchError, UnsupportedLanguageError, NoresourceError,
	                UnsupportedFormatError{
		  for (GrammarDocument grammar : grammars) {
			  SrgsXmlGrammarImplementation impl=null;
				try {
					impl = (SrgsXmlGrammarImplementation) transformer.transformGrammar(this, grammar);
				} catch (UnsupportedFormatError e) {
					e.printStackTrace();
				}
	            if (!grammarCheckers.containsKey(impl)) {
	                final SrgsXmlDocument doc = impl.getGrammar();
	                final GrammarGraph graph = parser.parse(doc);
	                if (graph != null) {
	                    final GrammarChecker checker = new GrammarChecker(graph);
	                    grammarCheckers.put(impl, checker);
	                } else {
	                    if (LOGGER.isDebugEnabled()) {
	                        LOGGER.warn("Cannot create a grammar graph "
	                                + "from the grammar file");
	                    }
	                }
	            }
	        }
		
	}

	@Override
	public void deactivateGrammars(final Collection<GrammarDocument> grammars)
    		throws NoresourceError, BadFetchError {
		for (GrammarDocument grammar : grammars) {
            SrgsXmlGrammarImplementation impl=null;
			try {
				impl = (SrgsXmlGrammarImplementation) transformer.transformGrammar(this, grammar);
			} catch (UnsupportedFormatError e) {
				e.printStackTrace();
			}
            if (grammarCheckers.containsKey(impl)) {
                grammarCheckers.remove(impl);
            }
        }
	}
	@Override
	public GrammarImplementation<?> loadGrammar(Reader reader, GrammarType type)
			throws NoresourceError, BadFetchError, UnsupportedFormatError {
		if (type != GrammarType.SRGS_XML) {
            throw new UnsupportedFormatError("Only SRGS XML is supported!");
        }

        final InputSource inputSource = new InputSource(reader);
        final SrgsXmlDocument doc;
        try {
            doc = new SrgsXmlDocument(inputSource);
        } catch (ParserConfigurationException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (SAXException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (IOException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
        return new SrgsXmlGrammarImplementation(doc);
	}

	@Override
	public Collection<BargeInType> getSupportedBargeInTypes() {
		return BARGE_IN_TYPES;
	}

//	@Override
//	public void record(OutputStream out) throws NoresourceError {
//		// TODO Auto-generated method stub
//
//	}

	
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
