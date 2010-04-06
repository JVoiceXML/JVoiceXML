package org.jvoicexml.implementation.mary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Queue;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;
import org.jvoicexml.SpeakableText;
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
    
    private SpeakableText speakable;
    
    public boolean audioPlayed=true;
    
    public Object audioPlayedLock;

    
    
    public SynthesisQueue(MarySynthesizedOutput synthesizedOutput){
        this.synthesizedOutput=synthesizedOutput;
        queuedSpeakables = new java.util.LinkedList<SpeakableText>(); 
        audioPlayedLock=new Object();
            
    }
    
   /*Thread's run method:If the queue is Empty it fires a QueueEmpty Event
    to MarySynthesizedOutput and from there to the Voice Browser.
    Else it removes the first speakable from the queue and if previous audio playing
    has finished it passes the speakable to Mary server. 
    */
    
    @Override
    public void run(){
        while(true){
            synchronized(queuedSpeakables){
                if(queuedSpeakables.isEmpty()){
                    fireQueueEmpty();
                    try{
                        queuedSpeakables.wait();
                    }
                    catch(InterruptedException e){
                        return;
                    }  
                    
                }
                speakable=queuedSpeakables.remove();
            }     
            
            synchronized(audioPlayedLock){
                while(!audioPlayed){
                    try{
                        audioPlayedLock.wait();
                    }
                    catch(InterruptedException e){
                        return;
                    }    
                }
                        
                }
                audioPlayed=false;
                passSpeakableToMary(speakable);
            }      
    }
      
    
   /*The method that actually passes the speakable to Mary.It gets the answer from the server at 
    *  ByteArrayOutputStream out and then it calls queueAudio method of MaryAudioFileOutput to play 
    *  the audio.This method also fires the events OutputStarted and OutputEnded to MarySynthesizedOutput
    *  as well as error Events that inform the Browser that some exception occured 
    *  during the process
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
           
            
       }
       
       catch (IOException e) {
           LOGGER.info("I/O Error in Process");
           final SynthesizedOutputEvent ProcessIOErrorEvent =
               new SynthesizedOutputEvent(this,5);
           fireOutputEvent(ProcessIOErrorEvent);
       }
       
            
       try{ 
           
           synthesizedOutput.audioFileOutput.queueAudio(new ByteArrayInputStream(out.toByteArray())) ;
             
           fireOutputEnded(speakable); 
           
           }
    
                
       catch (IOException e) {
           LOGGER.info("I/O Error playing the audio");
           final SynthesizedOutputEvent AudioPlayingIOErrorEvent =
               new SynthesizedOutputEvent(this,6);
           fireOutputEvent(AudioPlayingIOErrorEvent);
       } 
       catch (LineUnavailableException e) {
           LOGGER.info("Line unavailable error");
           final SynthesizedOutputEvent LineUnavailableErrorEvent =
               new SynthesizedOutputEvent(this,7);
           fireOutputEvent(LineUnavailableErrorEvent);
       }  
       catch (UnsupportedAudioFileException e) {
           LOGGER.info("Unsupported Audio File Error");
           final SynthesizedOutputEvent UnsupportedAudioFileErrorEvent=
               new SynthesizedOutputEvent(this,8);
           fireOutputEvent(UnsupportedAudioFileErrorEvent);
       } 
        
   }
    
   
   //All the notification events are passed initially to SynthesizedOutput and from there
   //to VoiceBrowser
   
    public void addListener(final SynthesizedOutputListener outputListener) {
       this.listener=outputListener;

    }  
    
    @Override
    public void removeListener(final SynthesizedOutputListener listener) {
        synchronized (listener) {
            this.listener=null;
            }
    }
    
    private void fireQueueEmpty() {
        final SynthesizedOutputEvent event = new QueueEmptyEvent(this);
        fireOutputEvent(event);
    }
    
    private void fireOutputEvent(final SynthesizedOutputEvent event) {
                listener.outputStatusChanged(event);
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
