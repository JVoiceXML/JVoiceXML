package com.example.org.jvoicexml.android.exceptiontestproject;


import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 final Button startButton = (Button) findViewById(R.id.start_button);
	        
	        
	        startButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                
	            	
	            	
	            }
	        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
