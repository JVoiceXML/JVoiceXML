package org.jvoicexml.android.callmanager;

import java.io.File;
import java.net.URI;

import org.apache.log4j.Logger;


import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;
	  
public class CallManager extends Service {
	//private Looper mServiceLooper;	  
	//final SimpleVoiceXML demo =new SimpleVoiceXML();

  // Handler that receives messages from the thread
  @Override
  public void onCreate() {
    // Start up the thread running the service.  Note that we create a
    // separate thread because the service normally runs in the process's
    // main thread, which we don't want to block.  We also make it
    // background priority so CPU-intensive work will not disrupt our UI.
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
	  if(intent.getAction()=="INTERPRETVXML"){
		  Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();  
	      startJVoiceXML(intent.getData());
	      // If we get killed, after returing from here, restart
	  }
      return START_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
      // We don't provide binding, so return null
      return null;
  }
  
  @Override
  public void onDestroy() {
    Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show(); 
  }
  public void startJVoiceXML(Uri uri)
  {
  	  
	File dialog= new File(uri.toString());         	
	final SimpleVoiceXML demo = new SimpleVoiceXML();  
	//final Logger LOGGER = Logger.getLogger(SimpleVoiceXML.class);
      
      		//URI uri= null;
      		//try {
      			//File dialog = new File("hello.vxml");
      			//uri = dialog.toURI();
      		//} catch (RuntimeException e) {
      			//e.printStackTrace();
      			//return;
      		//}
      		try {        		
      			demo.interpretDocument(dialog.toURI());
      		} catch (org.jvoicexml.event.JVoiceXMLEvent e) {
      			SimpleVoiceXML.LOGGER.error("error processing the document", e);
      		} catch (InterruptedException e) {
      			e.printStackTrace();
      		}
  }
}