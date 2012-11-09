package org.android.jvoicexml.tts_demo;

import java.util.ArrayList;

import org.jvoicexml.ConfigurationException;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.implementation.SynthesizedOutput;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Tts_demoActivity extends Activity
{
    /** Called when the activity is first created. */
	
	private SpeakablePlainText speakablePlainText;
	private AndroidImplementationPlatformFactory androidImplementationPlatformFactory;
	private AndroidSynthesizedOutput synthesizedOutput;
	private SpeakableText lastSpeakable;
	private ArrayList<String> strings= new ArrayList<String>();
	private boolean encolar;
	private TextToSpeech mTts;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //comprobamos que tenga los paquetes instalados
        Intent checkIntent = new Intent(); 
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA); 
        startActivityForResult(checkIntent, RESULT_OK); 
        
        encolar=true;
        androidImplementationPlatformFactory=new AndroidImplementationPlatformFactory(this);
        try {
			androidImplementationPlatformFactory.init();
		} catch (ConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        final Button encolarHabla = (Button) findViewById(R.id.encolar);
        
        strings.add("Estoy");
        strings.add("empezando");
        strings.add("a hablar");
        
        encolarHabla.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            if(encolar=true){
            	SpeakablePlainText speakablePlainText1=new SpeakablePlainText(strings.get(0));    
	            SpeakablePlainText speakablePlainText2=new SpeakablePlainText(strings.get(1));    
	            SpeakablePlainText speakablePlainText3=new SpeakablePlainText(strings.get(2)); 
	            
	            androidImplementationPlatformFactory.queuePrompt(speakablePlainText1);
	            androidImplementationPlatformFactory.queuePrompt(speakablePlainText2);
	            androidImplementationPlatformFactory.queuePrompt(speakablePlainText3);
      
            	try {
        			androidImplementationPlatformFactory.renderPrompts("aloha", null, null);
        		} catch (BadFetchError e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} catch (NoresourceError e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} catch (ConnectionDisconnectHangupEvent e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		encolar = true;
            }
            
            }
        });
        
        
//        speakablePlainText = new SpeakablePlainText("estoy hablando");
//        synthesizedOutput = new AndroidSynthesizedOutput(this);
//        
//		if(synthesizedOutput.mTts== null){
//			synthesizedOutput.start(this);
//			lastSpeakable = (SpeakablePlainText)speakablePlainText;
//		}
//		else
//		{
//			try {
//				synthesizedOutput.queueSpeakable(speakablePlainText, "aloha", null);
//			} catch (NoresourceError e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (BadFetchError e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
        
        }
    

   
}