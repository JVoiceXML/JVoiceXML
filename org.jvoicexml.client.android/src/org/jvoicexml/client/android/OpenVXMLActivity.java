package org.jvoicexml.client.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.jvoicexml.client.android.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.support.v4.app.NavUtils;

public class OpenVXMLActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_open_vxml);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void openVXMLURL(View view) {
		EditText urlText = (EditText) findViewById(R.id.vxmlURL);
		String urlAddress = urlText.getText().toString();
		MainJVXMLActivity.outputTextExternal(urlAddress);
		new Download(OpenVXMLActivity.this, urlAddress).execute();

		// clientGUI.getStartButton().setEnabled(true);

		this.finish();
	}

	private class Download extends AsyncTask<Void, Void, String> {
		ProgressDialog mProgressDialog;
		Context context;
		String surl;
		String outputFileName;

		public Download(Context context, String url) {
			this.context = context;
			this.surl = url;
		}

		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(context, "",
					"Please wait, Download...");
		}

		protected String doInBackground(Void... params) {

			try {

				URL url = new URL(surl);
				HttpURLConnection c = (HttpURLConnection) url.openConnection();
				c.setRequestMethod("GET");
				c.setDoOutput(true);
				c.connect();
				String[] path = url.getPath().split("/");
				String vxml = path[path.length - 1];

				String PATH = Environment.getExternalStorageDirectory()
						+ "/DownLoad/";
				Log.v("", "Path: " + PATH);
				File file = new File(PATH);
				file.mkdirs();

				String fileName = vxml;

				File outputFile = new File(file, fileName);
				FileOutputStream fos = new FileOutputStream(outputFile);
				InputStream is = c.getInputStream();

				byte[] buffer = new byte[1024];
				int len1 = 0;
				while ((len1 = is.read(buffer)) != -1) {

					fos.write(buffer, 0, len1);
				}
				MainJVXMLActivity.setDocumentVXML(outputFile);
				outputFileName = outputFile.getName();
				
				runOnUiThread(new Runnable() {
					public void run() {
						MainJVXMLActivity.outputTextExternal("Opened vxml: "
								+ outputFileName);
					}
				});
				fos.close();
				is.close();
			} catch (final MalformedURLException e) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    MainJVXMLActivity.outputTextExternal("IO fail");
                                    MainJVXMLActivity.outputTextExternal(e.toString());
                                }
                            });
			} catch (final IOException e) {
			        runOnUiThread(new Runnable() {
			            public void run() {
	                                MainJVXMLActivity.outputTextExternal("IO fail");
	                                MainJVXMLActivity.outputTextExternal(e.toString());
			            }
			        });
			}

			return "done";
		}

		protected void onPostExecute(String result) {
			if (result.equals("done")) {
				if (mProgressDialog.isShowing()) {
					try {
						mProgressDialog.dismiss();
					} catch (Exception ignore) {
					}
				}
			}
		}

	}

}
