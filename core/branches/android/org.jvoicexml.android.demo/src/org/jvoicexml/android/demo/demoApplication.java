package org.jvoicexml.android.demo;

import java.io.File;
import java.net.URI;






import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class demoApplication extends Activity {
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
                
            	//Uri myUri=Uri.parse(documentEditText.getText().toString());
            	Uri myUri=Uri.parse(sample);
            	File dialog= new File(myUri.toString());         	
            	startJVoiceXML(dialog.toURI());
            	
            	//Intent interpret =new Intent("org.jvoicexml.android.callmanager.INTERPRETVXML",myUri);
//            	String a=startService(interpret).toString();
//            	if(a!=null)
//            		Toast.makeText(getApplicationContext(),"el servicio se inicia. Demo",1).show();
//            	else            		
//            		Toast.makeText(getApplicationContext(), "ha salido null",1).show();            	
            	
            	//File dialog= new File(documentEditText.getText().toString()           	
            	//startJVoiceXML(dialog.toURI());
            	
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
      
   


public void startJVoiceXML(URI uri)
{
	
    final SimpleVoiceXML demo = new SimpleVoiceXML();  
    //public static final Logger LOGGER = Logger.getLogger(SimpleVoiceXML.class);
    
    		//URI uri= null;
    		//try {
    			//File dialog = new File("hello.vxml");
    			//uri = dialog.toURI();
    		//} catch (RuntimeException e) {
    			//e.printStackTrace();
    			//return;
    		//}
    		try {        		
    			demo.interpretDocument(uri);
    		} catch (org.jvoicexml.event.JVoiceXMLEvent e) {
    			SimpleVoiceXML.LOGGER.error("error processing the document", e);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
}
}