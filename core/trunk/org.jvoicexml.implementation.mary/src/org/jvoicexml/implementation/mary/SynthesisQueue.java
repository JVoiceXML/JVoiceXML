package org.jvoicexml.implementation.mary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Queue;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.parsers.ParserConfigurationException;

import marytts.client.MaryClient;

import org.apache.log4j.Logger;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ObservableSynthesizedOutput;
import org.jvoicexml.implementation.OutputEndedEvent;
import org.jvoicexml.implementation.OutputStartedEvent;
import org.jvoicexml.implementation.QueueEmptyEvent;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**SynthesisQueue extends Thead and is responsible for getting the speakables.
 * from the queue in which they are stored by MarySynthesizedOutput
 * and passes them to Mary server and to TextOutput if text output is enabled
 * After getting the processed data from the
 * server it calls queueAudio method of MaryAudioFileOutput to play the sound
 * 
 * @author Dirk Schnelle-Walka
 * @author Giannis Assiouras
 * 
 * */
public class SynthesisQueue extends Thread
    implements ObservableSynthesizedOutput {


    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(SynthesisQueue.class);

    /** The queue of speakables. */
    private final Queue<SpeakableText> queuedSpeakables;

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
    public static boolean audioPlayed = false;

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

    /**The HashTable that contains Mary synthesis request parameters.
     * e.g audioType,voiceName,voiceEffects and their value
     */
    private Hashtable maryRequestParameters;

    /**
     * Flag to indicate that TTS output and audio of the current speakable.
     * can be canceled.
     */
    private boolean enableBargeIn;
    
    
    PrintWriter output = null;
    
    /**Flag that indicates if text output is required*/
    private boolean textOutputEnabled;

    private int textOutputPort;
    
    /**constructs a new SynthesisQueue object.
     * @param synthesizedOutput the MarySynthesizedOuput
     * .*/
    public SynthesisQueue(final MarySynthesizedOutput synthesizedOutput) {
        marySynthesizedOutput = synthesizedOutput;
        queuedSpeakables = new java.util.LinkedList<SpeakableText>();
        audioPlayedLock = new Object();
        maryAudioFileOutput = new MaryAudioFileOutput(audioPlayedLock);
        setDaemon(true);
        setName("SynthesisQueueThread");
        
        
    }

   /**Thread's run method:If the queue is Empty it fires a QueueEmpty Event
    to MarySynthesizedOutput and from there to the Voice Browser.
    Else it removes the first speakable and passes it to the Mary server and to TextOuptut.
    */

    @Override
    public final void run() {
      if(textOutputEnabled){
        try {
            
            ServerSocket serverSocket=new ServerSocket(textOutputPort);
            output = new PrintWriter(serverSocket.accept().getOutputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
      } 
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
              
            
            if(textOutputEnabled){
                textOutput(queuedSpeakable);
                
            } 
            if(processor!=null)
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
       
       enableBargeIn = speakable.isBargeInEnabled();
       out = new ByteArrayOutputStream();

       if (speakable instanceof SpeakablePlainText) {

           final String text = speakable.getSpeakableText();


           try {

               processor.process(text, "TEXT", "AUDIO",
                       "en_US",(String) maryRequestParameters.get("audioType"),
                       (String) maryRequestParameters.get("voiceName"),out, 5000);

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
                   LOGGER.warn("error closing the output stream: "
                           + e.getMessage(), e);
               }
           }
       }


       if (speakable instanceof SpeakableSsmlText) {
          
           final SpeakableSsmlText ssml = (SpeakableSsmlText) speakable;

           String speakableText = ssml.getSpeakableText();
           
           try {

                processor.process(speakableText, "SSML",
                        "AUDIO", "en_US",(String) maryRequestParameters.get("audioType"),
                        (String) maryRequestParameters.get("voiceName"),out, 5000);
             
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
                   System.out.println("TTTTTTTTTTTTTTTTTTTTTt");
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
           LOGGER.warn("I/O Error playing the audio"+e.getMessage(),e);
           final SynthesizedOutputEvent audioPlayingIOErrorEvent =
               new SynthesizedOutputEvent(this, AUDIO_PLAYING_IOERROR);
           fireOutputEvent(audioPlayingIOErrorEvent);
       } catch (LineUnavailableException e) {
           LOGGER.warn("Line unavailable error"+e.getMessage(), e);
           final SynthesizedOutputEvent lineUnavailableErrorEvent =
               new SynthesizedOutputEvent(this, LINE_UNAVAILABLE_ERROR);
           fireOutputEvent(lineUnavailableErrorEvent);
       } catch (UnsupportedAudioFileException e) {
           LOGGER.warn("Unsupported Audio File Error"+e.getMessage(), e);
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
                    
                    if(LOGGER.isDebugEnabled()){
                    LOGGER.debug("waiting for end of audio");
                    }
                    
                    audioPlayedLock.wait();
                } catch (InterruptedException e) {
                    return;
                }
            }
            audioPlayed = false;
        }

    }

    /**The queueSpeakable method simply offers a speakable to the queue.
     *it notifies the synthesisQueue Thread and then it returns*/
    public void queueSpeakables(SpeakableText speakable){
        
        
        synchronized (queuedSpeakables) {
            queuedSpeakables.offer(speakable);
            queuedSpeakables.notify();

        }
        
        
    }
    
    /**Removes all the speakables from the queue*/ 
    public void clearQueue(){
      synchronized(queuedSpeakables){
        queuedSpeakables.clear();
      }  
    }
    
    
    /**
     * Stops the currently playing output if barge-in is enabled and.
     * Removes from the queue the speakables for which barge-in is enabled*/
    public void cancelOutput(){
        
        if (!enableBargeIn) {
            return;
        }
            
       try {
        maryAudioFileOutput.cancelOutput();
    } catch (NoresourceError e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
      synchronized(queuedSpeakables){
        final Collection<SpeakableText> skipped =
            new java.util.ArrayList<SpeakableText>();
        for (SpeakableText speakable : queuedSpeakables) {
            System.out.println("in queue"+speakable);
            if (speakable.isBargeInEnabled()) {
                
                System.out.println("canceled:"+speakable);
                skipped.add(speakable);
            } else {
                break;
            }
        }
        queuedSpeakables.removeAll(skipped);
      } 
       
         
    }
    
    
    /**Stops the currently playing output if barge-in is enabled.*/
    public void cancelAudioOutput(){
        
        if (!enableBargeIn) {
            return;
        }
        
        try {
         maryAudioFileOutput.cancelOutput();
     } catch (NoresourceError e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
     }
        
    }
    
    
    
    /**Sets the parameters e.g AudioType,VoiceName,VoiceEffects 
     * required by MaryClient to make a synthesis request to MaryServer
     * @param parameters The HashTable that contains synthesis
     *  parameters and their values 
     */
    public void setRequestParameters(Hashtable parameters){
       
        maryRequestParameters=parameters;
       
    }
    
    /**Sends the speakable to text output*/
    public void textOutput(SpeakableText speakable){
     
        String speakText = null;
        if (speakable instanceof SpeakableSsmlText) {
            InputStream is = null; 
            String temp = speakable.getSpeakableText(); 
            byte[] b = temp.getBytes();
            is = new ByteArrayInputStream(b);
            InputSource src = new InputSource(is);
            SsmlDocument ssml = null;
            try {
                ssml = new SsmlDocument(src);
            } catch (ParserConfigurationException e) {
                
                LOGGER.warn("Error Occured in TextOutput"+e.getMessage(),e);    
             
            } catch (SAXException e) {
                
                LOGGER.warn("Error Occured in TextOutput"+e.getMessage(),e); 
                
            } catch (IOException e) {
                
                LOGGER.warn("Error Occured in TextOutput"+e.getMessage(),e); 
            }
            speakText = ssml.getSpeak().getTextContent();
         } else if (speakable instanceof SpeakablePlainText) {
             speakText = speakable.getSpeakableText();
         }
        
        
        output.println(speakText);
        output.flush();
        
       }
    
    
    /**Enables text output*/
    public final void enableTextOutput(boolean enableTextOutput){
        
        textOutputEnabled=enableTextOutput;
    }
    
    /**Sets the text output port*/
    public final void setTextOutputPort(int port){
        
        textOutputPort=port;
        
    }
}
