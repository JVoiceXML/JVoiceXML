package org.jvoicexml.android.implementation;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ObservableSynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputListener;

import android.app.Activity;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

public class AndroidSynthesizedOutput extends Activity implements SynthesizedOutput,
		ObservableSynthesizedOutput,OnInitListener,OnUtteranceCompletedListener  {

	 /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(AndroidSynthesizedOutput.class);

    /** Queued texts. */
    private final BlockingQueue<SpeakableText> texts;

    /** <code>true</code> if the topmost speakable is currently processed. */
    private boolean processingSpeakable;

    /** Registered output listener. */
    private final Collection<SynthesizedOutputListener> outputListener;
    
    private TextToSpeech mTts;
    
    private int MY_DATA_CHECK_CODE=7007;
    
    public AndroidSynthesizedOutput() {
        texts = new java.util.concurrent.LinkedBlockingQueue<SpeakableText>();
        outputListener = new java.util.ArrayList<SynthesizedOutputListener>();
    }
	
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void open() throws NoresourceError {
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
	}

	@Override
	public void activate() throws NoresourceError {
		// TODO Auto-generated method stub

	}

	@Override
	public void passivate() throws NoresourceError {
		texts.clear();
        outputListener.clear();
        //this call flushes the queue and stops the current utterance from being played or recorded
        //returns SUCCESS (0) or ERROR (-1)
        mTts.stop();
	}

	@Override
	public void close() {
		//frees the resources
		 mTts.shutdown();
	}

	@Override
	public boolean isBusy() {
		//return !texts.isEmpty() || processingSpeakable;
		return mTts.isSpeaking();
	}

	@Override
	public void connect(ConnectionInformation client) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect(ConnectionInformation client) {
		texts.clear();
		mTts.stop();

	}

	@Override
	public boolean supportsBargeIn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void cancelOutput() throws NoresourceError {
		// TODO Auto-generated method stub

	}

	@Override
	public void addListener(SynthesizedOutputListener listener) {
		synchronized (outputListener) {
            outputListener.add(listener);
        }

	}

	@Override
	public void removeListener(SynthesizedOutputListener listener) {
		synchronized (outputListener) {
            outputListener.remove(listener);
        }

	}

	@Override
	public URI getUriForNextSynthesisizedOutput() throws NoresourceError,
			URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void queueSpeakable(SpeakableText speakable,
			DocumentServer documentServer) throws NoresourceError,
			BadFetchError {
		// TODO Auto-generated method stub

	}

	@Override
	public void waitNonBargeInPlayed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void waitQueueEmpty() {
		while (isBusy()) {
            try {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("waiting for empty output queue...");
                }
                // Delay until the next text is removed.
                synchronized (texts) {
                    texts.wait();
                }
            } catch (InterruptedException e) {
                return;
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("output queue is empty");
        }

	}
	/**
     * The client disconnected from JVoiceXML.
     * 
     * @since 0.7.3
     */
    void disconnected() {
        if (!isBusy()) {
            return;
        }
        LOGGER.info("client disconnected. Aborting pending requests");
        texts.clear();
        processingSpeakable = false;
        // Notify the listeners that the list has changed.
        synchronized (texts) {
            texts.notifyAll();
        }
    }
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                mTts = new TextToSpeech(this, this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                    TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		
	}
	public int isLanguageAvailable(Locale language)
	{
		return mTts.isLanguageAvailable(language);
	}
	public boolean setLanguage(Locale language)
	{
		int available=isLanguageAvailable(language);
		if(available== TextToSpeech.LANG_AVAILABLE ||available==TextToSpeech.LANG_COUNTRY_AVAILABLE){
			mTts.setLanguage(language);
			return true;
		}
		else 
			return false;
	}
	public Locale getLanguage(){
		return mTts.getLanguage();
	} 

	@Override
	public void onUtteranceCompleted(String uttId) {
		// TODO Auto-generated method stub
		
	}

}
