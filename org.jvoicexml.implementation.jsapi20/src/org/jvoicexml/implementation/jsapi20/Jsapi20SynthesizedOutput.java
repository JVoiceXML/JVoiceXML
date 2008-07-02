/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.implementation.jsapi20;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.speech.AudioException;
import javax.speech.EngineException;
import javax.speech.EngineManager;
import javax.speech.EngineStateException;
import javax.speech.synthesis.SpeakableEvent;
import javax.speech.synthesis.SpeakableListener;
import javax.speech.synthesis.SpeakableException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerEvent;
import javax.speech.synthesis.SynthesizerListener;
import javax.speech.synthesis.SynthesizerMode;
import javax.speech.synthesis.Voice;

import org.apache.log4j.Logger;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioFileOutput;
import org.jvoicexml.implementation.ObservableSynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.SynthesisResult;
import javax.speech.synthesis.PhoneInfo;
import org.jvoicexml.SpeakablePhoneInfo;


/**
 * Audio output that uses the JSAPI 2.0 to address the TTS engine.
 *
 * <p>
 * Handle all JSAPI calls to the TTS engine to make JSAPI transparent to the
 * interpreter.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class Jsapi20SynthesizedOutput
        implements SynthesizedOutput, ObservableSynthesizedOutput,
            SpeakableListener, SynthesizerListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(Jsapi20SynthesizedOutput.class);

    /** The used synthesizer. */
    private Synthesizer synthesizer;

    /** Reference to the audio file output. */
    private AudioFileOutput audioFileOutput;

    /** The default synthesizer mode descriptor. */
    private final SynthesizerMode desc;

    /** The system output listener. */
    private Collection<SynthesizedOutputListener> listeners;

    /** Name of the voice to use. */
    private String voiceName;

    /** Type of this resources. */
    private String type;

    /** Reference to a remote client configuration data. */
    private RemoteClient client;

    private String mediaLocator;

    /**
     * Flag to indicate that TTS output and audio can be canceled.
     *
     * @todo Replace this by a solution that does not cancel output without
     *       bargein, if there is mixed output.
     */
    private boolean enableBargeIn;

    /** Queued speakables. */
    private final List<SpeakableText> queuedSpeakables;

    private Semaphore resumePauseSemaphore;

    private boolean hasSentPhones;

    /**
     * Constructs a new audio output.
     *
     * @param defaultDescriptor
     *            the default synthesizer mode descriptor.
     */
    public Jsapi20SynthesizedOutput(
            final SynthesizerMode defaultDescriptor, final String mediaLocator) {
        desc = defaultDescriptor;
        this.mediaLocator = mediaLocator;
        listeners = new java.util.ArrayList<SynthesizedOutputListener>();
        queuedSpeakables = new java.util.ArrayList<SpeakableText>();

        resumePauseSemaphore = new Semaphore(1, true);
        hasSentPhones = false;
    }

    /**
     * {@inheritDoc}
     */
    public void open()
            throws NoresourceError {
        try {
            synthesizer = (Synthesizer) EngineManager.createEngine(desc);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("allocating synthesizer...");
            }

            try {
                synthesizer.getAudioManager().setMediaLocator(mediaLocator);
                synthesizer.allocate();
                synthesizer.setSpeakableMask(synthesizer.getSpeakableMask() | SpeakableEvent.PHONEME_STARTED);
                synthesizer.setSpeechEventExecutor(new SynchronousSpeechEventExecutor());
                synthesizer.addSynthesizerListener(this);
            } catch (EngineStateException ex) {
                ex.printStackTrace();
            } catch (EngineException ex) {
                ex.printStackTrace();
            } catch (AudioException ex) {
                ex.printStackTrace();
            }

            if (voiceName != null) {
                setVoice(voiceName);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("...synthesizer allocated");
            }
        } catch (EngineException ee) {
            throw new NoresourceError(ee);
        } catch (PropertyVetoException e) {
            throw new NoresourceError(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        if (synthesizer == null) {
            LOGGER.warn("no synthesizer: cannot deallocate");
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("closing audio output...");
        }

        waitQueueEmpty();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("deallocating synthesizer...");
        }

        try {
            synthesizer.deallocate();
        } catch (AudioException ex) {
        } catch (EngineStateException ee) {
        } catch (EngineException ee) {
            LOGGER.warn("error deallocating synthesizer", ee);
        } finally {
            synthesizer = null;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("audio output closed");
        }
    }

    /**
       * {@inheritDoc}
       */
      public void addListener(
              final SynthesizedOutputListener outputListener) {
          synchronized (listeners) {
              listeners.add(outputListener);
          }
      }

      /**
       * {@inheritDoc}
       */
      public void removeListener(
              final SynthesizedOutputListener outputListener) {
          synchronized (listeners) {
              listeners.remove(outputListener);
          }
    }

    /**
     * {@inheritDoc}
     *
     * Checks the type of the given speakable and forwards it either as for SSML
     * output or for plain text output.
     */
    public void queueSpeakable(final SpeakableText speakable,
                               final boolean bargein,
                               final DocumentServer documentServer)
            throws NoresourceError, BadFetchError {
        if (synthesizer == null) {
            throw new NoresourceError("no synthesizer: cannot speak");
        }


        ////////////////////////////fireOutputStarted(speakable);
        enableBargeIn = bargein;

        if (speakable instanceof SpeakablePlainText) {
            final String text = speakable.getSpeakableText();

            queuePlaintext(text);
        } else if (speakable instanceof SpeakableSsmlText) {
            final SpeakableSsmlText ssml = (SpeakableSsmlText) speakable;

            queueSpeakableMessage(ssml, documentServer);
        } else {
            LOGGER.warn("unsupported speakable: " + speakable);
        }
    }

    /**
     * Queues the speakable SSML formatted text.
     *
     * @param text
     *            SSML formatted text.
     * @param documentServer
     *            The DocumentServer to use.
     * @exception NoresourceError
     *                The output resource is not available.
     * @exception BadFetchError
     *                Error reading from the <code>AudioStream</code>.
     */
    private void queueSpeakableMessage(final SpeakableSsmlText ssmlText,
                                       final DocumentServer documentServer)
            throws NoresourceError, BadFetchError {

        if (synthesizer == null) {
            LOGGER.warn("no synthesizer: cannot speak");
            throw new NoresourceError("no synthesizer: cannot speak");
        }

        final SsmlDocument document = ssmlText.getDocument();
        final String doc = document.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("speaking SSML");
            LOGGER.debug(doc);
        }

        synchronized (queuedSpeakables) {
            queuedSpeakables.add(ssmlText);
        }

        try {
            synthesizer.speakMarkup(doc, this);
        } catch (IllegalArgumentException iae) {
            throw new BadFetchError(iae);
        } catch (EngineStateException ese) {
            throw new BadFetchError(ese);
        } catch (SpeakableException se) {
            throw new BadFetchError(se);
        }

        try {
            System.err.println("Acquiring 1 permit  @queueSsml. waiting: "+resumePauseSemaphore.getQueueLength());
            resumePauseSemaphore.acquire(1);
            System.err.println("Acquired new permit @queueSsml to: "+document.toString());
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (synthesizer != null) {
            try {
                synthesizer.resume();
            } catch (EngineStateException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Notifies all listeners that output has started.
     * @param speakable the current speakable.
     */
    private void fireOutputStarted(final SpeakableText speakable) {
        final SynthesizedOutputEvent event =
            new SynthesizedOutputEvent(this,
                    SynthesizedOutputEvent.OUTPUT_STARTED, speakable);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>(listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all listeners that the given marker has been reached.
     * @param mark the reached marker.
     */
    private void fireMarkerReached(final String mark) {
        final SynthesizedOutputEvent event =
            new SynthesizedOutputEvent(this,
                    SynthesizedOutputEvent.MARKER_REACHED, mark);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>(listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all listeners that output has started.
     * @param speakable the current speakable.
     */
    private void fireOutputEnded(final SpeakableText speakable) {
        final SynthesizedOutputEvent event =
            new SynthesizedOutputEvent(this,
                    SynthesizedOutputEvent.OUTPUT_ENDED, speakable);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>(listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all listeners that output queue us empty.
     */
    private void fireQueueEmpty() {
        final SynthesizedOutputEvent event =
            new SynthesizedOutputEvent(this,
                    SynthesizedOutputEvent.QUEUE_EMPTY);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>(listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    private void fireOutputUpdate(final SynthesisResult synthesisResult) {
      final SynthesizedOutputEvent event =
          new SynthesizedOutputEvent(this,
                                     SynthesizedOutputEvent.OUTPUT_UPDATE, synthesisResult);

      synchronized (listeners) {
        final Collection<SynthesizedOutputListener> copy =
            new java.util.ArrayList<SynthesizedOutputListener>(listeners);
        for (SynthesizedOutputListener current : copy) {
          current.outputStatusChanged(event);
        }
      }
    }


    /**
     * Speaks a plain text string.
     *
     * @param text
     *            String contains plain text to be spoken.
     * @exception NoresourceError
     *                No recognizer allocated.
     * @exception BadFetchError
     *                Recognizer in wrong state.
     */
    public void queuePlaintext(final String text)
            throws NoresourceError, BadFetchError {

        if (synthesizer == null) {
            LOGGER.warn("no synthesizer: cannot speak");
            throw new NoresourceError("no synthesizer: cannot speak");
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("speaking '" + text + "'...");
        }

        synchronized (queuedSpeakables) {
            queuedSpeakables.add(new SpeakablePlainText(text));
        }

        try {
            synthesizer.speak(text, this);
        } catch (EngineStateException ese) {
            throw new BadFetchError(ese);
        }

        try {
            System.err.println("Acquiring 1 permit  @queuePlainText. waiting: "+resumePauseSemaphore.getQueueLength());
            resumePauseSemaphore.acquire(1);
            System.err.println("Acquired new permit @queuePlainText to: "+text);

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (synthesizer != null) {
            try {
                synthesizer.resume();
            } catch (EngineStateException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void cancelOutput()
            throws NoresourceError {
        if (synthesizer == null) {
            throw new NoresourceError("no synthesizer: cannot queue audio");
        }

        if (!enableBargeIn) {
            return;
        }

        try {
            synthesizer.cancelAll();
        } catch (EngineStateException ee) {
            throw new NoresourceError(ee);
        }
    }

    /**
     * Blocks the calling thread until the Engine is in a specified state.
     * <p>
     * All state bits specified in the state parameter must be set in order for
     * the method to return, as defined for the testEngineState method. If the
     * state parameter defines an unreachable state (e.g. PAUSED | RESUMED) an
     * exception is thrown.
     * </p>
     * <p>
     * The waitEngineState method can be called successfully in any Engine
     * state.
     * </p>
     *
     * @param state
     *            State to wait for.
     * @exception java.lang.InterruptedException
     *                If another thread has interrupted this thread.
     */
    public void waitEngineState(final long state)
            throws java.lang.InterruptedException {
        if (synthesizer == null) {
            LOGGER.warn("no synthesizer: cannot wait for engine state");
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("waiting for synthesizer engine state " + state);
        }

        final long current = synthesizer.getEngineState();
        if (current != state) {
            synthesizer.waitEngineState(state);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("reached engine state " + state);
        }
    }

    /**
     * Convenient method to wait until all output is being played.
     */
    public void waitQueueEmpty() {
        try {
            waitEngineState(Synthesizer.QUEUE_EMPTY);
        } catch (InterruptedException ie) {
            LOGGER.error("error waiting for empty queue", ie);
        }
    }

    /**
     * A mark in an SSML output has been reached.
     *
     * @param mark
     *            Name of the mark.
     */
    public void reachedMark(final String mark) {
        if (listeners == null) {
            return;
        }

        fireMarkerReached(mark);
    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("activating output..."+queuedSpeakables.size());
        }

        //System.err.println("Releasing 1 permit @A. waiting: "+resumePauseSemaphore.getQueueLength());
        //resumePauseSemaphore.release();


    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("passivating output..."+queuedSpeakables.size());
        }



       /* try {
            System.err.println("Acquiring 1 permit @P. waiting: "+resumePauseSemaphore.getQueueLength());
            resumePauseSemaphore.acquire(1);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }*/


      /*  if (synthesizer != null) {
            try {
                synthesizer.pause();
            } catch (EngineStateException ex) {
                ex.printStackTrace();
            }
        }*/
        listeners.clear();
        ///////////////////////////////queuedSpeakables.clear();
        client = null;


        //System.err.println("Releasing 1 permit @P. waiting: "+resumePauseSemaphore.getQueueLength());
        //resumePauseSemaphore.release();




        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...passivated output");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient remoteClient)
            throws IOException {

        client = remoteClient;
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient remoteClient) {

       client = null;
   }

   /**
    * {@inheritDoc}
    */
   public String getType() {
       return type;
   }

   /**
    * Sets the type of this resource.
    * @param resourceType type of the resource
    */
   public void setType(final String resourceType) {
       type = resourceType;
   }

   /**
    * {@inheritDoc}
    */
   public void setAudioFileOutput(final AudioFileOutput fileOutput) {
       audioFileOutput = fileOutput;
   }

   /**
     * Use the given voice for the synthesizer.
     *
     * @param name
     *            Name of the voice to use
     * @throws PropertyVetoException
     *             Error in assigning the voice.
     */
    public void setVoice(final String name)
            throws PropertyVetoException {
        if (synthesizer == null) {
            LOGGER.warn("no synthesizer: cannot set voice");

            voiceName = name;

            return;
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("setting voice to '" + voiceName + "'...");
        }

        final Voice voice = findVoice(voiceName);

        synthesizer.getSynthesizerProperties().setVoice(voice);
    }

    /**
     * Find the voice with the given name.
     *
     * @param name
     *            name of the voice to find.
     * @return Voice with the given name, or <code>null</code> if there is no
     *         voice with that name.
     */
    private Voice findVoice(final String name) {
        final SynthesizerMode currentDesc =
                (SynthesizerMode) synthesizer
                .getEngineMode();
        final Voice[] voices = currentDesc.getVoices();

        for (int i = 0; i < voices.length; i++) {
            final Voice currentVoice = voices[i];
            final String currentVoiceName = currentVoice.getName();

            if (name.equals(currentVoiceName)) {
                return currentVoice;
            }
        }

        LOGGER.warn("could not find voice '" + name + "'");

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public URI getUriForNextSynthesisizedOutput() throws NoresourceError {
        if (synthesizer != null) {
            try {
                URI uri = new URI(synthesizer.getAudioManager().getMediaLocator());
                if (uri.getQuery() != null) {
                    String[] parametersString = uri.getQuery().split("\\&");
                    String newParameters = "";
                    String participantUri = "";
                    for (String part : parametersString) {
                        String[] queryElement = part.split("\\=");
                        if (queryElement[0].equals("participant")) {
                            participantUri = uri.getScheme();
                            participantUri += "://";
                            participantUri += queryElement[1];
                            participantUri += "/audio";
                        }  else {
                            if (newParameters.equals("")) {
                                newParameters += "?";
                            } else {
                                newParameters += "&";
                            }
                            newParameters += queryElement[0];
                            newParameters += "=";
                            newParameters += queryElement[1];
                        }
                    }
                    if (!participantUri.equals("")) {
                        participantUri += newParameters;
                    }

                    return new URI(participantUri);
                }
                return uri;
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        final boolean busy;

        synchronized (queuedSpeakables) {
            busy = !queuedSpeakables.isEmpty();
        }
        return busy;
    }

    public void speakableUpdate(SpeakableEvent speakableEvent) {
        //System.err.println("speakableUpdate: " + speakableEvent.paramString() + "@" + System.currentTimeMillis() + " - "+queuedSpeakables.size() + " " + speakableEvent.getSource());
        int type = speakableEvent.getId();
        SpeakableText speakableText = null;
        if (type == SpeakableEvent.SPEAKABLE_STARTED) {
          hasSentPhones = false;

          //Request phones list (if available)
          /////////synthesizer.setSpeakableMask(synthesizer.getSpeakableMask() | SpeakableEvent.PHONEME_STARTED);



           /* try {
                System.err.println("Acquiring 1 permit @SS. waiting: "+resumePauseSemaphore.getQueueLength());
                resumePauseSemaphore.acquire(1);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }*/

           synchronized (queuedSpeakables) {
               speakableText = queuedSpeakables.get(0);
           }
           assert(speakableText != null);
           fireOutputStarted(speakableText);
        } else if (type == SpeakableEvent.SPEAKABLE_ENDED) {

            try {
                synthesizer.pause();
            } catch (EngineStateException ex) {
                ex.printStackTrace();
            }

            synchronized (queuedSpeakables) {
                speakableText = queuedSpeakables.remove(0);
            }

            fireOutputEnded(speakableText);
        } else if ((type == SpeakableEvent.PHONEME_STARTED) && (hasSentPhones == false)) {


          //Cancel receiving phones (only care about the list of phones)
          ///////////////////////////synthesizer.setSpeakableMask(synthesizer.getSpeakableMask() ^ SpeakableEvent.PHONEME_STARTED);

          //Get speakable text that produced this output event
          synchronized (queuedSpeakables) {
            speakableText = queuedSpeakables.get(0);
          }

          //Convert phones object types (Jsapi20 -> JVXML)
          final PhoneInfo[] phoneInfos = speakableEvent.getPhones();
          SpeakablePhoneInfo[] speakablePhones = null;
          if ((phoneInfos != null) && (phoneInfos.length > 0)) {
            speakablePhones = new SpeakablePhoneInfo[phoneInfos.length];

            for (int i = 0; i < phoneInfos.length; i++) {
              final SpeakablePhoneInfo spi = new SpeakablePhoneInfo(phoneInfos[i].getPhoneme(), phoneInfos[i].getDuration());

              speakablePhones[i] = spi;
            }
          }

          //Make the object that holds the phones
          final Jsapi20SynthesisResult jsapi20SpeakableResult = new Jsapi20SynthesisResult(speakableText, speakablePhones);

          fireOutputUpdate(jsapi20SpeakableResult);
          hasSentPhones = true;
        }
  }

    public void synthesizerUpdate(SynthesizerEvent synthesizerEvent) {
        //System.err.println("synthesizerUpdate: " + synthesizerEvent.paramString() + "@" + System.currentTimeMillis());
        int type = synthesizerEvent.getId();
        if (type == SynthesizerEvent.QUEUE_EMPTIED) {
            //synchronized (queuedSpeakables) {
               if (queuedSpeakables.size() != 0) {
                   //System.err.println("Received a QUEUE_EMPTIED but local queue is not empty: "+queuedSpeakables.size());
                   //////////////////////////////////////////////queuedSpeakables.clear();
               }
           //}
           fireQueueEmpty();


           //System.err.println("Releasing 1 permit @QE. waiting: "+resumePauseSemaphore.getQueueLength());
           ////////////resumePauseSemaphore.release(1);
        }
        else if (type == SynthesizerEvent.ENGINE_PAUSED) {
            System.err.println("Releasing 1 permit @EnginePaused. waiting: "+resumePauseSemaphore.getQueueLength());
            resumePauseSemaphore.release(1);

        }
    }

}
