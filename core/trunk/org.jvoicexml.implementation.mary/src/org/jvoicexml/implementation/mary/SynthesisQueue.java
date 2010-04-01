package org.jvoicexml.implementation.mary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.implementation.ObservableSynthesizedOutput;
import org.jvoicexml.implementation.OutputEndedEvent;
import org.jvoicexml.implementation.OutputStartedEvent;
import org.jvoicexml.implementation.QueueEmptyEvent;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;

public class SynthesisQueue extends Thread implements ObservableSynthesizedOutput {
    
    private static final Logger LOGGER =
        Logger.getLogger(SynthesisQueue.class);
    
    public final Queue<SpeakableText> queuedSpeakables;
    
    private SynthesizedOutputListener listener;
    
    private final MarySynthesizedOutput synthesizedOutput;
    
    private ByteArrayOutputStream out;
    
    
    
    public SynthesisQueue(MarySynthesizedOutput synthesizedOutput){
        this.synthesizedOutput=synthesizedOutput;
        queuedSpeakables = new java.util.LinkedList<SpeakableText>(); 
            
    }
   /*Thread's run method:If the queue is Empty it fires a QueueEmpty Event
    *to MarySynthesizedOutput and from there to the Voice Browser.
    Else it removes the first speakable from the queue and passes it to Mary 
    */
    
    @Override
    public void run(){
        while(true){
            synchronized(queuedSpeakables){
                while (queuedSpeakables.isEmpty()){
                    fireQueueEmpty();
                    try{
                        queuedSpeakables.wait();
                    }
                    catch(InterruptedException e){}    
                }
                
                passSpeakableToMary(queuedSpeakables.remove());
            }      
        }
    }   
    
   /*The method that actually passes the speakable to Mary.It gets the answer from the server at 
    *  ByteArrayOutputStream out and then it calls queueAudio method of MaryAudioFileOutput to play 
    *  the audio.This method also fires the events OutputStarted and OutputEnded to MarySynthesizedOutput
    */
        
   public void passSpeakableToMary(SpeakableText speakable){
       out=new ByteArrayOutputStream();
       final String text = speakable.getSpeakableText();
       fireOutputStarted(speakable);
       
       try {
       
            synthesizedOutput.processor.process(text, "TEXT", "AUDIO","en_US",synthesizedOutput.audioType,synthesizedOutput.voiceName, 
                       out,synthesizedOutput.serverTimeout);
          
            
            out.flush();
            out.close();
           
           //Wait for the previous sound to be played
           Thread.sleep(3000);
           
           synthesizedOutput.audioFileOutput.queueAudio(new ByteArrayInputStream(out.toByteArray())) ;
             
           fireOutputEnded(speakable); 
           
           
            }
       catch (BadFetchError e) {
                // TODO Auto-generated catch block
            e.printStackTrace();
       }
                
       catch (IOException e) {
            // TODO Auto-generated catch block
           e.printStackTrace();
       }
       catch (InterruptedException e) {
              // TODO Auto-generated catch block
           e.printStackTrace();
       } 
   
       
       
   }
    
   //All the notification events are passed initially to SynthesizedOutput and from there
   //to VoiceBrowser
   
    public void addListener(SynthesizedOutputListener outputListener) {
       this.listener=outputListener;

    }  
    
    private void fireQueueEmpty() {
        final SynthesizedOutputEvent event = new QueueEmptyEvent(this);
        fireOutputEvent(event);
    }
    
    private void fireOutputEvent(final SynthesizedOutputEvent event) {
                listener.outputStatusChanged(event);
            }
    @Override
    public void removeListener(SynthesizedOutputListener listener) {
        
    }
        
    private void fireOutputStarted(final SpeakableText speakable) {
        final SynthesizedOutputEvent event =
            new OutputStartedEvent(this, speakable);
        fireOutputEvent(event);
    }

    private void fireOutputEnded(final SpeakableText speakable) {
        final SynthesizedOutputEvent event =
            new OutputEndedEvent(this, speakable);
        fireOutputEvent(event);
    }
    
    
}
