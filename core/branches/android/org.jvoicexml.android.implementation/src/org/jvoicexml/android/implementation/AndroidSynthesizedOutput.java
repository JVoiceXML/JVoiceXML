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
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ObservableSynthesizedOutput;
import org.jvoicexml.implementation.OutputEndedEvent;
import org.jvoicexml.implementation.OutputStartedEvent;
import org.jvoicexml.implementation.QueueEmptyEvent;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
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
		return "android";
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
		return !texts.isEmpty() || processingSpeakable || mTts.isSpeaking();
	}

	@Override
	public void connect(ConnectionInformation client) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect(ConnectionInformation client) {
		mTts.stop();
		texts.clear();

	}

	@Override
	public boolean supportsBargeIn() {
		return true;
	}

	@Override
	public void cancelOutput() throws NoresourceError {
		//stops the current utterance and clears the Android queue, 
		//which will never have more than one speakable.
		mTts.stop();
		
		//cancels all speakables until it finds one with no barge in
		if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("clearing all pending messages");
        }
        final Collection<SpeakableText> skipped =
            new java.util.ArrayList<SpeakableText>();
        for (SpeakableText speakable : texts) {
            if (speakable.isBargeInEnabled()) {
                skipped.add(speakable);
            } else {
                break;
            }
        }
        texts.removeAll(skipped);
        if (texts.isEmpty()) {
            fireQueueEmpty();
        }
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
		final Object o;
	    if (speakable instanceof SpeakablePlainText) {
	        SpeakablePlainText text = (SpeakablePlainText) speakable;
	        o = text.getSpeakableText();
	    } else {
	        SpeakableSsmlText ssml = (SpeakableSsmlText) speakable;
	        o = ssml.getDocument();
	    }
	
	    if (LOGGER.isDebugEnabled()) {
	        LOGGER.debug("queuing object " + o);
	    }
	    texts.add(speakable);

	}

	@Override
	public void waitNonBargeInPlayed() {
		if (texts.isEmpty() && !mTts.isSpeaking()) {
            return;
        }
        do {
            final SpeakableText speakable = texts.peek();
            if (speakable.isBargeInEnabled()) {
                return;
            }
            synchronized (texts) {
                try {
                    texts.wait();
                } catch (InterruptedException e) {
                    return;
                }
            }
        } while (!texts.isEmpty());

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
	/**
     * Notifies all listeners that output has started.
     * @param speakable the current speakable.
     */
    private void fireOutputStarted(final SpeakableText speakable) {
        final SynthesizedOutputEvent event =
            new OutputStartedEvent(this, speakable);
        fireOutputEvent(event);
    }

    /**
     * Notifies all listeners that output has ended.
     * @param speakable the current speakable.
     */
    private void fireOutputEnded(final SpeakableText speakable) {
        final SynthesizedOutputEvent event =
            new OutputEndedEvent(this, speakable);
        fireOutputEvent(event);
    }

    /**
     * Notifies all listeners that output queue is empty.
     */
    private void fireQueueEmpty() {
        final SynthesizedOutputEvent event = new QueueEmptyEvent(this);
        fireOutputEvent(event);
    }

    /**
     * Notifies all registered listeners about the given event.
     * @param event the event.
     * @since 0.6
     */
    private void fireOutputEvent(final SynthesizedOutputEvent event) {
        synchronized (outputListener) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>();
            copy.addAll(outputListener);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }
    /**
     * Reads the next text to send to the client.
     * @return next text, <code>null</code> if there is no next output.
     */
    SpeakableText getNextText() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("retrieving next output...");
        }
        final SpeakableText speakable;
        try {
            speakable = texts.take();
            processingSpeakable = true;
            fireOutputStarted(speakable);
        } catch (InterruptedException e) {
            return null;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("next output: " + speakable);
        }
        return speakable;
    }
    /**
     * Checks if the queue is empty after the retrieval of the given
     * speakable.
     * <p>
     * This method is a callback after the {@link TextTelephony} safely
     * obtained the speakable.
     * </p>
     * @param speakable the last retrieved speakable
     * @since 0.7.1
     */
    void checkEmptyQueue(final SpeakableText speakable) {
        fireOutputEnded(speakable);
        processingSpeakable = false;
        if (texts.isEmpty()) {
            fireQueueEmpty();

            // Notify the listeners that the list has changed.
            synchronized (texts) {
                texts.notifyAll();
            }
        }
    }

}
