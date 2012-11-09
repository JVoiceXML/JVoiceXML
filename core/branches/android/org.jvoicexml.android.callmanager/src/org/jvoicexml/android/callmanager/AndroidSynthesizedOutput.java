package org.jvoicexml.android.callmanager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SynthesisResult;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ObservableSynthesizedOutput;
import org.jvoicexml.implementation.OutputEndedEvent;
import org.jvoicexml.implementation.OutputStartedEvent;
import org.jvoicexml.implementation.OutputUpdateEvent;
import org.jvoicexml.implementation.QueueEmptyEvent;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

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
    private final Queue<SpeakableText> queuedSpeakables;

    /** <code>true</code> if the topmost speakable is currently processed. */
    private boolean processingSpeakable;

    /** Registered output listener. */
    private final Collection<SynthesizedOutputListener> outputListener;
    
    private TextToSpeech mTts;
    
    /**
     * Flag to indicate that TTS output and audio can be canceled.
     *
     * @todo Replace this by a solution that does not cancel output without
     *       bargein, if there is mixed output.
     */
    private boolean enableBargeIn;
    
    /** The current session id. */
    private String sessionId;
    
    /** Object lock for an empty queue. */
    private final Object emptyLock;
    
    /** <code>true</code> if the synthesizer supports SSML. */
    private boolean supportsMarkup;
    
    private int MY_DATA_CHECK_CODE=7007;
    
    private SpeakableText lastSpeakable;
    
    public AndroidSynthesizedOutput() {
    	queuedSpeakables = new java.util.concurrent.LinkedBlockingQueue<SpeakableText>();
        outputListener = new java.util.ArrayList<SynthesizedOutputListener>();
        emptyLock = new Object();
        supportsMarkup = false;
        enableBargeIn =true;
        
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
		
		mTts =new TextToSpeech(this, this);
        //wait until the Engine has been initialize
		synchronized (this) {
        	try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		mTts.setOnUtteranceCompletedListener(this);
	}

	@Override
	public void activate() throws NoresourceError {
		// TODO Auto-generated method stub

	}

	@Override
	public void passivate() throws NoresourceError {
		queuedSpeakables.clear();
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
		return !queuedSpeakables.isEmpty() || processingSpeakable || mTts.isSpeaking();
	}

	@Override
	public void connect(ConnectionInformation client) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect(ConnectionInformation client) {
		mTts.stop();
		queuedSpeakables.clear();

	}

	@Override
	public boolean supportsBargeIn() {
		return enableBargeIn;
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
        for (SpeakableText speakable : queuedSpeakables) {
            if (speakable.isBargeInEnabled()) {
                skipped.add(speakable);
            } else {
                break;
            }
        }
        queuedSpeakables.removeAll(skipped);
        if (queuedSpeakables.isEmpty()) {
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

//	@Override
//	public void queueSpeakable(SpeakableText speakable,
//			DocumentServer documentServer) throws NoresourceError,
//			BadFetchError {
//		// TODO Auto-generated method stub		
//		final Object o;
//	    if (speakable instanceof SpeakablePlainText) {
//	        SpeakablePlainText text = (SpeakablePlainText) speakable;
//	        o = text.getSpeakableText();
//	    } else {
//	        SpeakableSsmlText ssml = (SpeakableSsmlText) speakable;
//	        o = ssml.getDocument();
//	    }
//	
//	    if (LOGGER.isDebugEnabled()) {
//	        LOGGER.debug("queuing object " + o);
//	    }
//	    texts.add(speakable);
//
//	}
	/**
     * {@inheritDoc}
     *
     * Checks the type of the given speakable and forwards it either as for SSML
     * output or for plain text output.
     */
    @Override
    public void queueSpeakable(final SpeakableText speakable,
            final String sessId, final DocumentServer documentServer)
            throws NoresourceError, BadFetchError {
        if (mTts == null) {
            throw new NoresourceError("no synthesizer: cannot speak");
        }

        // Remember the new session id.
        sessionId = sessId;

        synchronized (queuedSpeakables) {
            queuedSpeakables.offer(speakable);
            // Do not process the speakable if there is some ongoing processing
            if (queuedSpeakables.size() > 1 || isBusy()) {
                return;
            }
        }

        // Otherwise process the added speakable asynchronous.
//        final Runnable runnable = new Runnable() {
            /**
             * {@inheritDoc}
             */
//            @Override
//            public void run() {
                try {
                    processNextSpeakable();
                } catch (NoresourceError e) {
                    notifyError(e);
                } catch (BadFetchError e) {
                    notifyError(e);
                }
            }
//        };
//        final Thread thread = new Thread(runnable);
//        thread.start();
//    }
    
    /**
     * Processes the next speakable in the queue.
     * @throws NoresourceError
     *         error processing the speakable.
     * @throws BadFetchError
     *         error processing the speakable.
     * @since 0.7.1
     */
    private synchronized void processNextSpeakable()
        throws NoresourceError, BadFetchError {
        // Check if there are more speakables to process
        final SpeakableText speakable;
        synchronized (queuedSpeakables) {
            if (queuedSpeakables.isEmpty()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("no more speakables to process");
                }
                fireQueueEmpty();
                synchronized (emptyLock) {
                    emptyLock.notifyAll();
                }
                return;
            }
            //poll removes the element from the queue but peek doesn't
            speakable = queuedSpeakables.peek();
//            speakable = queuedSpeakables.poll();
            lastSpeakable =speakable;
            processingSpeakable = true;
        }
        

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("processing next speakable: " + speakable);
        }

        // Really process the next speakable
        if (speakable instanceof SpeakablePlainText) {
            final SpeakablePlainText text = (SpeakablePlainText) speakable;
            fireOutputStarted(speakable);
            speakPlaintext(text);
        } else if (speakable instanceof SpeakableSsmlText) {
            final SpeakableSsmlText ssml = (SpeakableSsmlText) speakable;
            fireOutputStarted(speakable);
            speakSSML(ssml);
        } else {
            LOGGER.warn("unsupported speakable: " + speakable);
        }
    }
    /**
     * Speaks a plain text string.
     *
     * @param speakable
     *                speakable containing plain text to be spoken.
     * @exception NoresourceError
     *                    No synthesizer allocated.
     * @exception BadFetchError
     *                    Synthesizer in wrong state.
     */
    private void speakPlaintext(final SpeakablePlainText speakable)
        throws NoresourceError, BadFetchError {
        if (mTts == null) {
            throw new NoresourceError("no synthesizer: cannot speak");
        }
        final String text = speakable.getSpeakableText();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("speaking '" + text + "'...");
        }
//        try {
//        	mTts.resume();
//           int id = mTts.speak(text, this);
        
        	mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
//            queueIds.put(speakable, id);
//            if (LOGGER.isDebugEnabled()) {
//                LOGGER.debug("queued id " + id);
//            }
//        } catch (EngineStateException ese) {
//            throw new BadFetchError(ese);
//        }
    }
    
    /**
     * Queues the speakable SSML formatted text.
     *
     * @param ssmlText
     *                SSML formatted text.
     * @exception NoresourceError
     *                    The output resource is not available.
     * @exception BadFetchError
     *                    Error reading from the <code>AudioStream</code>.
     */
    private void speakSSML(final SpeakableSsmlText ssmlText)
        throws NoresourceError,
            BadFetchError {
        if (mTts == null) {
            throw new NoresourceError("no synthesizer: cannot speak");
        }

        final SsmlDocument document = ssmlText.getDocument();
        if (!supportsMarkup) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                 "synthesizer does not support markup. reducing to plain text");
            }
            final Speak speak = document.getSpeak();
            final String text = speak.getTextContent();
            final SpeakablePlainText speakable = new SpeakablePlainText(text);
            speakPlaintext(speakable);
            return;
        }
//        final String doc = document.toString();
//
//        if (LOGGER.isDebugEnabled()) {
//            LOGGER.debug("speaking SSML");
//            LOGGER.debug(doc);
//        }
//        enableBargeIn = ssmlText.isBargeInEnabled();
////        try {
//            synthesizer.resume();
//            int id = synthesizer.speakMarkup(doc, this);
//            mTts.speak(doc, TextToSpeech.QUEUE_ADD, null);
//            queueIds.put(ssmlText, id);
//            if (LOGGER.isDebugEnabled()) {
//                LOGGER.debug("queued id " + id);
//            }
//        } catch (IllegalArgumentException iae) {
//            throw new BadFetchError(iae);
//        } catch (EngineStateException ese) {
//            throw new BadFetchError(ese);
//        } catch (SpeakableException se) {
//            throw new BadFetchError(se);
//        }
    }

	

	@Override
	public void waitNonBargeInPlayed() {
		if (queuedSpeakables.isEmpty() && !mTts.isSpeaking()) {
            return;
        }
        do {
            final SpeakableText speakable = queuedSpeakables.peek();
            if (speakable.isBargeInEnabled()) {
                return;
            }
            synchronized (queuedSpeakables) {
                try {
                	queuedSpeakables.wait();
                } catch (InterruptedException e) {
                    return;
                }
            }
        } while (!queuedSpeakables.isEmpty());

	}

	@Override
	public void waitQueueEmpty() {
		while (isBusy()) {
            try {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("waiting for empty output queue...");
                }
                // Delay until the next text is removed.
                synchronized (queuedSpeakables) {
                	queuedSpeakables.wait();
                }
            } catch (InterruptedException e) {
                return;
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("output queue is empty");
        }

	}
//	/**
//     * The client disconnected from JVoiceXML.
//     * 
//     * @since 0.7.3
//     */
//    void disconnected() {
//        if (!isBusy()) {
//            return;
//        }
//        LOGGER.info("client disconnected. Aborting pending requests");
//        texts.clear();
//        processingSpeakable = false;
//        // Notify the listeners that the list has changed.
//        synchronized (texts) {
//            texts.notifyAll();
//        }
//    }
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
		synchronized (this) {
			this.notifyAll();			
		}
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
		try{
			processNextSpeakable();	
			fireOutputEnded(lastSpeakable);
		}
		catch(NoresourceError e)
		{
			return;			
		}
		catch(BadFetchError e)
		{
			return;			
		}
	}
	/**
     * Notifies all listeners that output has started.
     *
     * @param speakable
     *                the current speakable.
     */
    private void fireOutputStarted(final SpeakableText speakable) {
        final SynthesizedOutputEvent event = new OutputStartedEvent(this,
                sessionId, speakable);

        synchronized (outputListener) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>(outputListener);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all listeners that output has ended.
     *
     * @param speakable
     *                the current speakable.
     */
    private void fireOutputEnded(final SpeakableText speakable) {
        final SynthesizedOutputEvent event = new OutputEndedEvent(this,
                sessionId, speakable);

        synchronized (outputListener) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>(outputListener);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all listeners that output queue is empty.
     */
    private void fireQueueEmpty() {
        final SynthesizedOutputEvent event =
            new QueueEmptyEvent(this, sessionId);

        synchronized (outputListener) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>(outputListener);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }
    

    /**
     * Notifies all listeners that output has been updated.
     * @param synthesisResult
     *        the intermediate synthesis result
     */
    private void fireOutputUpdate(final SynthesisResult synthesisResult) {
        final SynthesizedOutputEvent event = new OutputUpdateEvent(this,
                sessionId, synthesisResult);

        synchronized (outputListener) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>(outputListener);
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
            speakable = queuedSpeakables.remove();
            processingSpeakable = true;
            fireOutputStarted(speakable);
        } catch (NoSuchElementException  e) {
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
        if (queuedSpeakables.isEmpty()) {
            fireQueueEmpty();

            // Notify the listeners that the list has changed.
            synchronized (queuedSpeakables) {
            	queuedSpeakables.notifyAll();
            }
        }
    }
    /**
     * Notifies all registered listeners about the given event.
     * @param error the error event
     * @since 0.7.4
     */
    private void notifyError(final ErrorEvent error) {
        synchronized (outputListener) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>();
            copy.addAll(outputListener);
            for (SynthesizedOutputListener current : copy) {
                current.outputError(error);
            }
        }
    }

}
