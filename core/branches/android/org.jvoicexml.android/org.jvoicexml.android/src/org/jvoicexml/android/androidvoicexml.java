package org.jvoicexml.android;


import java.io.File;
import java.net.URI;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;


public class androidvoicexml extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startJVoiceXML();
       
    }
    
    public void startJVoiceXML()
    {
    	Toast.makeText(this, "Starting JVoiceXML",2).show();
        final SimpleVoiceXML demo = new SimpleVoiceXML();        
        
        		URI uri= null;
        		try {
        			File dialog = new File("hello.vxml");
        			uri = dialog.toURI();
        		} catch (RuntimeException e) {
        			e.printStackTrace();
        			return;
        		}
        		try {        		
        			demo.interpretDocument(uri);
        		} catch (org.jvoicexml.event.JVoiceXMLEvent e) {
        			SimpleVoiceXML.LOGGER.error("error processing the document", e);
        		} catch (InterruptedException e) {
        			e.printStackTrace();
        		}
    }
}