package org.jvoicexml.client.android;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;

import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.JVoiceXmlMain;
import org.jvoicexml.JVoiceXmlMainListener;
import org.jvoicexml.Session;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.client.android.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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
public class MainJVXMLActivity extends Activity
        implements TextListener, JVoiceXmlMainListener {

    private static Object lock = new Object();
    private Object jvxmlMonitor = new Object();
    private static TextServer server;
    private JVoiceXml jvxml;
    private Session session;
    private boolean connected;
    private boolean serverStarted;
    private static File documentVXML;

    private EditText textIn;
    private static TextView textOut;
    private static Button startButton;
    private Button openVXMLButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_jvxml);
        textIn = (EditText) findViewById(R.id.textIn);
        textOut = (TextView) findViewById(R.id.textOut);
        startButton = (Button) findViewById(R.id.startButton);
        openVXMLButton = (Button) findViewById(R.id.openVXMLButton);
        //allow the text screen to scroll vertically
        textOut.setMovementMethod(new ScrollingMovementMethod());
        org.apache.log4j.BasicConfigurator.configure();
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
        runOnUiThread(new Runnable() {
            public void run() {
                startButton.setEnabled(true);
                startButton.requestFocus();
                textIn.setEnabled(false);
                appendTextToTextOut("--Session ended.");
            }
        });
    }

    @Override
    public void outputSsml(SsmlDocument doc) {
        Speak speak = doc.getSpeak();
        final String text = speak.getTextContent();
        runOnUiThread(new Runnable() {
            public void run() {
                synchronized (textOut) {                   
                    appendTextToTextOut(text);
                }
            }
        });

    }

    /**
     * Used outside this class.
     * 
     * @param outTextExt
     */
    public static void outputTextExternal(String outTextExt) {
        synchronized (textOut) {
            appendTextToTextOut(outTextExt);
        }
    }
    
    /**
     * Method for adding text to the main TextView, plus auto scroll.
     * 
     * @param text Text to be showed.
     */
    public static void appendTextToTextOut(String text) {
        if(textOut != null){
            textOut.append(text + "\n");
            final Layout layout = textOut.getLayout();
            if(layout != null){
                int scrollDelta = layout.getLineBottom(textOut.getLineCount() - 1) 
                    - textOut.getScrollY() - textOut.getHeight();
                if(scrollDelta > 0)
                    textOut.scrollBy(0, scrollDelta);
            }
        }
    }

    @Override
    public void started() {
        synchronized (lock) {
            lock.notifyAll();
        }
        serverStarted = true;
        // manipulation with UI elements has to be done on the original (UI)
        // thread
        runOnUiThread(new Runnable() {
            public void run() {
                textIn.setEnabled(true);
                textIn.requestFocus();
                startButton.setEnabled(false);
                appendTextToTextOut("--Session started.");
                appendTextToTextOut("--Interpreting...");
            }
        });

    }

    /**
     * Sends user input to the interpreter.
     */
    private void inputText() {
        if (connected) {
            try {
                synchronized (textOut) {
                    appendTextToTextOut(textIn.getText().toString());
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

    public static Button getStartButton() {
        return startButton;
    }

    /**
     * Starts JVXML session to process the VXML document.
     */
    public void startSession() {
        AndroidTextConfiguration config = new AndroidTextConfiguration();
        JVoiceXmlMain jvxmlmain = new JVoiceXmlMain(config);
        jvxmlmain.addListener(this);
        jvxmlmain.start();
        synchronized (jvxmlMonitor) {
            try {
                jvxmlMonitor.wait();
            } catch (InterruptedException e) {
                appendTextToTextOut("Jvxml not started: " + e.getMessage());
                return;
            }
        }

        jvxml = jvxmlmain;

        if (jvxml == null) {
            appendTextToTextOut("--JVXML not found!");
            startButton.setEnabled(true);
            return;
        }

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
        //commented out, because the text server cannot be used 
        //again when interpreting another document after first 
        //one with this if/else statement, thus getting error
/*        if (!serverStarted) {
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
            appendTextToTextOut("--Session started.");
        }*/

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
    
    /**
     * Sends input to text server if prompted, when pressing send button.
     * 
     * @param view
     */
    public void sendTextInput(View view) {
        inputText();
    }

    @Override
    public void expectingInput() {
        // TODO Auto-generated method stub

    }

    @Override
    public void inputClosed() {
        // TODO Auto-generated method stub

    }

    @Override
    public void jvxmlStartupError(Throwable e) {
        System.out.println("jvxml startup error:" + e.getMessage());
        runOnUiThread(new Runnable() {
            public void run() {
                appendTextToTextOut("Jvxml startup error.");
            }
        });
        synchronized (jvxmlMonitor) {
            jvxmlMonitor.notifyAll();
        }
    }

    @Override
    public void jvxmlStarted() {
        synchronized (jvxmlMonitor) {
            jvxmlMonitor.notifyAll();
        }
    }

    @Override
    public void jvxmlTerminated() {
        // TODO Auto-generated method stub

    }

}
