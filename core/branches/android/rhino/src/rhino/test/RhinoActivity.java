package rhino.test;



import android.app.Activity;
import android.content.Context;

import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.mozilla.javascript.ScriptableObject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class RhinoActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ScriptingEngine rhino = new ScriptingEngine(null);
        Context context = getApplicationContext();
        CharSequence text = "Si funciona!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        
        
        try {
        	toast.show();
			Object result = rhino.eval("var a = 7;");
			String json = rhino.toJSON((ScriptableObject) result);
			
			
		} catch (SemanticError e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
    }
}