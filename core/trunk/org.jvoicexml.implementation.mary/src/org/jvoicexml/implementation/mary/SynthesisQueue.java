package org.jvoicexml.implementation.mary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Queue;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import marytts.client.MaryClient;

import org.apache.log4j.Logger;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.implementation.ObservableSynthesizedOutput;
import org.jvoicexml.implementation.OutputEndedEvent;
import org.jvoicexml.implementation.OutputStartedEvent;
import org.jvoicexml.implementation.QueueEmptyEvent;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;

/**SynthesisQueue extends Thead and is responsible for getting the speakables.
 * from the queue in which they are stored by MarySynthesizedOutput
 * and passes them to Mary server.After getting the processed data from the
 * server it calls queueAudio method of MaryAudioFileOutput to play the sound
 * */
public class SynthesisQueue extends Thread
    implements ObservableSynthesizedOutput {


    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(SynthesisQueue.class);

    /** The queue of speakables. */
    public final Queue<SpeakableText> queuedSpeakables;

    /** The system output listener. */
    private SynthesizedOutputListener listener;

    /** The MarySynthesized Output resource. */
    private final MarySynthesizedOutput marySynthesizedOutput;

    /** The MarySynthesized Output resource. */
    private final MaryAudioFileOutput maryAudioFileOutput;

    /** The Mary Client used to send requests to Mary server . */
    private MaryClient processor;

    /** The ByteArrayOutputStream used to store.
     * Mary server's response to synthesis request */
    private ByteArrayOutputStream out;

    /**Reference to the SpeakableText object that can be.
     * either a SpeakablePlainText or a SpeakableSsmlText */
    private SpeakableText queuedSpeakable;

    /**Flag that indicates whether the previous audio playing.
     * has completed
     *   */
    private boolean audioPlayed = false;


    /** Object lock.
     * The SynthesisQueue Thread waits on this object
     * until the previous audio playing has completed
     *  */
    private final Object audioPlayedLock;

    /**Id for TextProcessIOErrorEvent.*/
    private static final int TEXT_PROCESS_IOERROR = 5;

    /**Id for SSMLProcessIOErrorEvent.*/
    private static final int SSML_PROCESS_IOERROR = 6;

    /**Id for AudioPlayingIOErrorEvent.*/
    private static final int AUDIO_PLAYING_IOERROR = 7;

    /**Id for LineUnavailableErrorEvent.*/
    private static final int LINE_UNAVAILABLE_ERROR = 8;

    /**Id for UnsupportedAudioFileErrorEvent.*/
    private static final int UNSUPPORTED_AUDIOFILE_ERROR = 9;



    /**constructs a new SynthesisQueue object.
     * @param synthesizedOutput the MarySynthesizedOuput
     * .*/
    public SynthesisQueue(final MarySynthesizedOutput synthesizedOutput) {
        marySynthesizedOutput = synthesizedOutput;
        queuedSpeakables = new java.util.LinkedList<SpeakableText>();
        audioPlayedLock = new Object();
        maryAudioFileOutput = new MaryAudioFileOutput(audioPlayedLock);
        setDaemon(true);
    }

   /**Thread's run method:If the queue is Empty it fires a QueueEmpty Event
    to MarySynthesizedOutput and from there to the Voice Browser.
    Else it removes the first speakable and passes it to the Mary server.
    */

    @Override
    public final void run() {
        while (true) {
            synchronized (queuedSpeakables) {
                if (queuedSpeakables.isEmpty()) {
                    fireQueueEmpty();
                    try {
                        queuedSpeakables.wait();
                    } catch (InterruptedException e) {
                        return;
                    }

                }

                queuedSpeakable = queuedSpeakables.remove();
            }
                passSpeakableToMary(queuedSpeakable);
        }
    }


   /**The method that actually passes the speakable to Mary.
    * According to the speakable Type it calls the process method
    * of the MaryClient with inputType set to "TEXT" or "SSML"  as appropriate
    * It gets the answer from the server at
    *  ByteArrayOutputStream out and then it calls queueAudio method
    *  of MaryAudioFileOutput to play the audio.
    *  This method also fires the events
    *  OutputStarted and OutputEnded to MarySynthesizedOutput
    *  as well as error Events that inform the Browser
    *  that some exception occurred either at process or queueAudio processes
    *  This method does not return until the audio playing has completed
    *  during the process
    *  @param speakable the speakable to be passed to Mary server
    */

   public final void passSpeakableToMary(final SpeakableText speakable) {

       fireOutputStarted(speakable);

       out = new ByteArrayOutputStream();

       if (speakable instanceof SpeakablePlainText) {

           final String text = speakable.getSpeakableText();


           try {

               processor.process(text, "TEXT", "AUDIO",
                       "en_US", marySynthesizedOutput.audioType, null,
                       out, 5000);

           } catch (IOException e) {
               LOGGER.warn("I/O Error in plain text Process: "
                       + e.getMessage(), e);
               final SynthesizedOutputEvent textProcessIOErrorEvent =
                   new SynthesizedOutputEvent(this, TEXT_PROCESS_IOERROR);
               fireOutputEvent(textProcessIOErrorEvent);

           } finally {

               try {
                   out.flush();
                   out.close();
               } catch (IOException e) {
                   LOGGER.warn("error closng the output stream: "
                           + e.getMessage(), e);
               }
           }
       }


       if (speakable instanceof SpeakableSsmlText) {

           final SpeakableSsmlText ssml = (SpeakableSsmlText) speakable;

           String speakableText = ssml.getSpeakableText();

           try {

                processor.process(speakableText, "SSML",
                        "AUDIO", "en_US", marySynthesizedOutput.audioType, null,
                        out, 5000);

           } catch (IOException e) {
               LOGGER.warn("I/O Error in SSML Process: " + e.getMessage(), e);
               final SynthesizedOutputEvent sSMLProcessIOErrorEvent =
                   new SynthesizedOutputEvent(this, SSML_PROCESS_IOERROR);
               fireOutputEvent(sSMLProcessIOErrorEvent);
           } finally {
               try {
                out.flush();
                out.close();
               } catch (IOException e) {
                   LOGGER.warn("error closng the output stream:"
                           + e.getMessage(), e);
               }
           }
       }


       try {

               maryAudioFileOutput.queueAudio(
                       new ByteArrayInputStream(out.toByteArray()));

           waitAudioPlaying();

           fireOutputEnded(speakable);
       } catch (IOException e) {
           LOGGER.warn("I/O Error playing the audio", e);
           final SynthesizedOutputEvent audioPlayingIOErrorEvent =
               new SynthesizedOutputEvent(this, AUDIO_PLAYING_IOERROR);
           fireOutputEvent(audioPlayingIOErrorEvent);
       } catch (LineUnavailableException e) {
           LOGGER.warn("Line unavailable error", e);
           final SynthesizedOutputEvent lineUnavailableErrorEvent =
               new SynthesizedOutputEvent(this, LINE_UNAVAILABLE_ERROR);
           fireOutputEvent(lineUnavailableErrorEvent);
       } catch (UnsupportedAudioFileException e) {
           LOGGER.warn("Unsupported Audio File Error", e);
           final SynthesizedOutputEvent unsupportedAudioFileErrorEvent =
               new SynthesizedOutputEvent(this, UNSUPPORTED_AUDIOFILE_ERROR);
           fireOutputEvent(unsupportedAudioFileErrorEvent);
       }


   }


   /**All the notification events are passed initially.
   SynthesizedOutput and from there
   to VoiceBrowser
   @param outputListener the MarySynthesizedOutput object
   */

    public final void addListener(final SynthesizedOutputListener
                outputListener) {
       this.listener = outputListener;

    }

    /**
     * Removes the listener for system output events.
     * @param outputListener the MarySynthesizedOutput object to remove.
     *
     * @since 0.6
     */
    public final void removeListener(
            final SynthesizedOutputListener outputListener) {
        synchronized (listener) {
            this.listener = null;
            }
    }

    /**
     * Notifies the Listener that output queue is empty.
     */
    private void fireQueueEmpty() {
        final SynthesizedOutputEvent event = new QueueEmptyEvent(this);
        fireOutputEvent(event);
    }

    /**
     * Notifies the MarySynthesizedOutput about the given event.
     * @param event the event.
     */
    private void fireOutputEvent(final SynthesizedOutputEvent event) {
                listener.outputStatusChanged(event);
            }

    /**
     * Notifies the MarySynthesizedOutput that output has started.
     * @param speakable the current speakable.
     */
    private void fireOutputStarted(final SpeakableText speakable) {
        final SynthesizedOutputEvent event =
            new OutputStartedEvent(this, speakable);
        fireOutputEvent(event);
    }

    /**
     * Notifies the MarySynthesizedOutput that output has ended.
     * @param speakable the current speakable.
     */
    private void fireOutputEnded(final SpeakableText speakable) {
        final SynthesizedOutputEvent event =
            new OutputEndedEvent(this, speakable);
        fireOutputEvent(event);
    }

    /**
     * Sets the MaryClient object that will be used bu this Thread.
     * to send requests to Mary server
     * @param maryClient .
     */
    public final void setProcessor(final MaryClient maryClient) {

        processor = maryClient;

    }


    /**
     * Waits until the previous audio playing has completed.
     */
    private void waitAudioPlaying() {

        synchronized (audioPlayedLock) {
            if (!audioPlayed) {
                try {
                    LOGGER.info("waiting for end of audio");
                    audioPlayedLock.wait();
                } catch (InterruptedException e) {
                    return;
                }
            }
            audioPlayed = false;
        }

    }


}
