package org.jvoicexml.android;

import org.jvoicexml.android.demoApplication.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class demoApplicationActivity extends Activity {
    /** Called when the activity is first created. */
	public final String sample = new String ("http://sites.google.com/site/komponiendo/vxml/sample.vxml");
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final EditText documentEditText = (EditText) findViewById(R.id.editText1);        
        
        
       //interpret document button
        final Button startButton = (Button) findViewById(R.id.start_button);
        
        
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
            	Uri myUri=Uri.parse(sample);
            	
            	Intent interpret =new Intent("org.jvoicexml.android.callmanager.INTERPRETVXML",myUri);
            	String a=startService(interpret).toString();
            	if(a!=null)
            		Toast.makeText(getApplicationContext(),"el servicio se inicia. Demo",1).show();
            	else            		
            		Toast.makeText(getApplicationContext(), "ha salido null",1).show();            	
            	
            	Toast.makeText(getApplicationContext(),"el servicio se inicia bien. Demo",1).show();
            	
            }
        });
        
        //stop interpreter button
        final Button stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
            	//stopping interpreter code
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