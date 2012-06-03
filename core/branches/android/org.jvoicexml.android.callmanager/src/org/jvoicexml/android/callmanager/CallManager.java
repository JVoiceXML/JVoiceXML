package org.jvoicexml.android.callmanager;

import java.io.File;
import java.net.URI;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXmlMainListener;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import org.jvoicexml.JVoiceXmlMain;
import org.jvoicexml.JVoiceXmlMainListener;
import org.jvoicexml.Session;
import org.jvoicexml.client.BasicConnectionInformation;
//import org.jvoicexml.config.JVoiceXmlConfiguration;


	  
public class CallManager extends Service implements JVoiceXmlMainListener{

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	public static final Logger LOGGER = Logger.getLogger(Service.class);
	public static final int STOP_INTERPRETER = -1;	
	private Interpreter interpreter;
	
	/**
	 * Handler of incoming messages from clients.
	 */
    class IncomingHandler extends Handler {

		@Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STOP_INTERPRETER:
                    Toast.makeText(getApplicationContext(),"stopping interpreter!",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        Log.e("intent action", intent.getAction());
        if(intent.getAction().compareTo("org.jvoicexml.android.callmanager.INTERPRETVXML")==0)
        {
        	startInterpreter(intent);		  
        }
        return mMessenger.getBinder();
    }
	
  
  /** Logger for this class. */
  
  public CallManager() {
	    super();
	}
  
  
  @Override
  public void onCreate() {
    // Start up the thread running the service.  Note that we create a
    // separate thread because the service normally runs in the process's
    // main thread, which we don't want to block.  We also make it
    // background priority so CPU-intensive work will not disrupt our UI.
	  System.setProperty("jvoicexml.config", "../org.jvoicexml/config");
	  android.os.Debug.waitForDebugger();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
      return START_STICKY;
  }
  
  @Override
  public void onDestroy() {
    Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show(); 
  }
  
  public void startInterpreter(Intent intent)
  {
	  
	  this.interpreter = new Interpreter(intent.getData(),this);
	  this.interpreter.start();
	  
  }
  
	@Override
	public synchronized void jvxmlStarted() {
		// TODO Auto-generated method stub
		this.notifyAll();
	}
	
	@Override
	public void jvxmlTerminated() {
		// TODO Auto-generated method stub
		
	}
}