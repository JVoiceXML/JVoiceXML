package org.jvoicexml.android;

import org.jvoicexml.android.demoApplication.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class demoApplicationActivity extends Activity {
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
	/** Called when the activity is first created. */
	public final String sample = new String ("http://sites.google.com/site/komponiendo/vxml/sample.vxml");
	 /** Messenger for communicating with the service. */
    Messenger mService = null;

    /** Flag indicating whether we have called bind on the service. */
    boolean mBound;
    
    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
           		case 5:
           			Toast.makeText(getApplicationContext(),"El servicio me envió un OBJETO"+msg.getData().getString("str1"),1).show();            	
           		default:
                    super.handleMessage(msg);
            }
        }
    }
    
    /**
     * Target we publish for the callManager to send messages to demoApplication.
     */
    final Messenger callbackMessenger = new Messenger(new IncomingHandler());
    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = new Messenger(service);
            mBound = true;
            
         // We want to monitor the service for as long as we are
            // connected to it.
            try {
                Message msg = Message.obtain(null,
                        MSG_REGISTER_CLIENT);                
                msg.replyTo = callbackMessenger;
                mService.send(msg);               
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
        
            }
        }

        public void onServiceDisconnected(ComponentName className){
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mBound = false;
        }
    };
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final EditText documentEditText = (EditText) findViewById(R.id.editText1);      
       //interpret document button
        final Button startButton = (Button) findViewById(R.id.start_button);
        final Button stopButton = (Button) findViewById(R.id.stop_button);
        
        
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
            	Uri myUri=Uri.parse(sample);
            	
//            	Intent interpret =new Intent("org.jvoicexml.android.callmanager.INTERPRETVXML",myUri);
//            	String a=startService(interpret).toString();
            	
            	boolean a = bindService(new Intent("org.jvoicexml.android.callmanager.INTERPRETVXML", myUri), mConnection,
            	            Context.BIND_AUTO_CREATE);
            	
            	if(a)
            		Toast.makeText(getApplicationContext(),"demoApplications is binded",1).show();
            	else            		
            		Toast.makeText(getApplicationContext(), "demoApplications failed to bind",1).show();            	
            }
        });
        
        //stop interpreter button
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {                
            	//stopping interpreter code
            	if (!mBound) return;
                // Create and send a message to the service, using a supported 'what' value
                Message msg = Message.obtain(null, STOP_INTERPRETER, 0, 0);
                try {
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
    }
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }
    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }
}