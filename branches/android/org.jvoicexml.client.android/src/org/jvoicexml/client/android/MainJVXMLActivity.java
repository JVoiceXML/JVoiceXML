package org.jvoicexml.client.android;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;

import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.JVoiceXmlMain;
import org.jvoicexml.Session;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.client.android.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Main user interface class with JVoiceXML
 * 
 * @author macinos
 * 
 */
public class MainJVXMLActivity extends Activity implements TextListener {

	private static Object lock = new Object();
	private static TextServer server;
	private JVoiceXml jvxml;
	private Session session;
	private boolean connected;
	private boolean serverStarted;
	private static File documentVXML;

	private EditText textIn;
	private static TextView textOut;
	private Button startButton;
	private Button openVXMLButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_jvxml);
		textIn = (EditText) findViewById(R.id.textIn);
		textOut = (TextView) findViewById(R.id.textOut);
		startButton = (Button) findViewById(R.id.startButton);
		openVXMLButton = (Button) findViewById(R.id.openVXMLButton);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main_jvxml, menu);
		return true;
	}

	@Override
	public void connected(InetSocketAddress arg0) {
		connected = true;
	}

	@Override
	public void disconnected() {
		session.hangup();
		connected = false;
		startButton.setEnabled(true);
		startButton.requestFocus();
		textIn.setEnabled(false);
		textOut.append("--Session ended.\n");
	}

	@Override
	public void outputSsml(SsmlDocument doc) {
		synchronized (textOut) {
			textOut.append(doc.toString() + "\n");
		}
	}

	/**
	 * Used outside this class.
	 * 
	 * @param outTextExt
	 */
	public static void outputTextExternal(String outTextExt) {
		synchronized (textOut) {
			textOut.append(outTextExt + "\n");
		}
	}

	@Override
	public void started() {
		synchronized (lock) {
			lock.notifyAll();
		}
		serverStarted = true;
		//manipulation with UI elements has to be done on the original (UI) thread
		runOnUiThread(new Runnable() {
			public void run() {
				textIn.setEnabled(true);
				textIn.requestFocus();
				startButton.setEnabled(false);
				textOut.append("--Session started.\n");
			}
		});

	}

	/**
	 * Sends user input to the interpreter. (Not used yet)
	 */
	private void inputText() {
		if (connected) {
			try {
				synchronized (textOut) {
					textOut.append(textIn.getText() + "\n");
				}
				server.sendInput(textIn.getText().toString());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		textIn.setText("");
	}

	/**
	 * Set VXML document for this GUI.
	 * 
	 * @param documentVXML
	 */
	public static void setDocumentVXML(File newDocumentVXML) {
		documentVXML = newDocumentVXML;
	}

	public Button getStartButton() {
		return startButton;
	}

	/**
	 * Starts JVXML session to process the VXML document.
	 */
	public void startSession() {
		AndroidTextConfiguration config = new AndroidTextConfiguration();
		JVoiceXmlMain jvxmlmain = new JVoiceXmlMain(config);
		jvxmlmain.start();
		jvxml = jvxmlmain;
		
		if (jvxml == null) {
			textOut.append("--JVXML not found!\n");
			startButton.setEnabled(true);
			return;
		}

		if (!serverStarted) {
			server = new TextServer(4242);
			server.addTextListener(this);
			server.start();
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			startButton.setEnabled(false);
			textIn.setEnabled(true);
			textIn.requestFocus();
			textOut.append("--Session started.\n");
		}

		// do interpretation on separate thread
		new ConnectOnBackground().execute();

	}

	/**
	 * Class for running time expensive methods from startSession() on
	 * background thread (Android standard).
	 * 
	 * @author macinos
	 * 
	 */
	private class ConnectOnBackground extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			final ConnectionInformation info;
			final URI uri;

			if (documentVXML != null && documentVXML.canRead()) {
				try {

					info = server.getConnectionInformation();
					session = jvxml.createSession(info);
					uri = documentVXML.toURI();
					session.call(uri);
					session.waitSessionEnd();
					session.hangup();

				} catch (UnknownHostException uhex) {
					uhex.printStackTrace();
				} catch (ErrorEvent eex) {
					eex.printStackTrace();
				}
			} else {
				startButton.setEnabled(true);
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			// Post results to main thread
		}
	}

	/**
	 * VXML Opening dialog after clicking Open VXML button.
	 * 
	 * @param view
	 */
	public void openVXML(View view) {
		Intent intent = new Intent(this, OpenVXMLActivity.class);
		startActivity(intent);
	}

	/**
	 * Method run when pressing the Start button in the interface.
	 * 
	 * @param view
	 */
	public void startInterpreter(View view) {
		startSession();
	}

	@Override
	public void expectingInput() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputClosed() {
		// TODO Auto-generated method stub
		
	}

}
