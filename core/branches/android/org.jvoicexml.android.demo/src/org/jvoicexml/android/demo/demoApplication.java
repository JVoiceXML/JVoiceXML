package org.jvoicexml.android.demo;

import java.io.File;
import java.net.URI;



import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class demoApplication extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final EditText documentEditText = (EditText) findViewById(R.id.editText1);
       
        
        
       //interpret document button
        final Button button1 = (Button) findViewById(R.id.start_button);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
            	File dialog= new File(documentEditText.getText().toString());            	
            	startJVoiceXML(dialog.toURI());
            	
            }
        });
        
        //stop interpreter button
        final Button button2 = (Button) findViewById(R.id.stop_button);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
            	//stopping interpret code
            	
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