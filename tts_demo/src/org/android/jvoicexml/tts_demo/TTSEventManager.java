package org.android.jvoicexml.tts_demo;

import java.util.Locale;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;

import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

public class TTSEventManager extends Thread {

private AndroidSynthesizedOutput androidSynthesizedOutput;	
private Object renderLock; 

	public TTSEventManager(AndroidSynthesizedOutput androidSynthesizedOutput,Object renderLock)
	{
		this.androidSynthesizedOutput = androidSynthesizedOutput;
		this.renderLock= renderLock;
		android.os.Debug.waitForDebugger();
	}
	
	public void run()
	{
		androidSynthesizedOutput.start();
		synchronized (renderLock) {
			try {
				renderLock.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
	}
	
   
}
	


