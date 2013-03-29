package org.jvoicexml.android.callmanager;


import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXmlMainListener;
import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;


	  
public class CallManager extends Service implements JVoiceXmlMainListener{

	
	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	Messenger mClient; 
	/**
     * Command to the service to register a client, receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client where callbacks should be sent.
     */
    static final int MSG_REGISTER_CLIENT = 1;

    /**
     * Command to the service to unregister a client, ot stop receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client as previously given with MSG_REGISTER_CLIENT.
     */
    static final int MSG_UNREGISTER_CLIENT = 2;
    /**
     * Command to the service to stop the Interpreter
     */
    public static final int STOP_INTERPRETER = -1;	
    
	public static final Logger LOGGER = Logger.getLogger(Service.class);
	private Interpreter interpreter;
	
	/**
	 * Handler of incoming messages from clients.
	 */
    class IncomingHandler extends Handler {
    	@Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClient= msg.replyTo;
					try {
						Message message= Message.obtain(null,5);
						Bundle b= new Bundle();
						b.putString("str1", "testingConnection");					
						message.setData(b);
						mClient.send(message);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    break;
                case MSG_UNREGISTER_CLIENT:
                	mClient=null;
                    break;
                case STOP_INTERPRETER:
                    Toast.makeText(getApplicationContext(),"stopping interpreter!",Toast.LENGTH_SHORT).show();
                    interpreter.finish();
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
		this.notifyAll();
	}
}