package org.jvoicexml.client.android.debug;

import java.io.ByteArrayOutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.util.Log;

public class MainDebug extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_debug);
		org.apache.log4j.BasicConfigurator.configure();
		new Debug().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_debug, menu);
		return true;
	}

	private class Debug extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			final Result result = new StreamResult(buffer);
			try {
				final TransformerFactory transFact = TransformerFactory
						.newInstance();
				final Transformer transformer = transFact.newTransformer();
				final SsmlDocument ssmldoc = new SsmlDocument();
				final String encoding = System.getProperty(
						"jvoicexml.xml.encoding", "UTF-8");
				transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
				final Speak speak = ssmldoc.getSpeak();
				speak.addText("Hello World!");
				final Source source = new DOMSource(speak);
				transformer.transform(source, result);

			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			// Post results to main thread
		}
	}
}
