package org.jvoicexml.documentserver.schemestrategy;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;


public class ProxyHttpDelegate {
	
	
	 HttpClient client =null;
	 GetMethod method = null;
	 private boolean saveCookies = false;
	 private Cookie[] cookies;
	 
	 public ProxyHttpDelegate(boolean cookies){
		 super();
		 setSaveCookies(cookies);
	 }
	

	public InputStream getHtmlAsInputStream() throws URISyntaxException, FileNotFoundException{
	
		
		String html = getData();
    
	    
	    //we must now check, if this is an html document and not vxml
	    //then we must get the <anchor> tag since the VoicePortal is 
		//really whacked and returns an html document on the 
		//first interaction
		//IE
		/*<?xml version="1.0" encoding="UTF-8"?>
			<HTML xmlns="http://www.w3.org/2001/vxml">
			  <HEAD>
			    <TITLE>Answer Document</TITLE>
			  </HEAD>
			  <BODY>
			    <form name="inputform">
			      <a href="http://localhost:8080/VoicePortal/FNBCARD_1.vxml">http://localhost:8080/VoicePortal/FNBCARD_1.vxml</a>
			    </form>
			  </BODY>
			</HTML>
		 * 
		 * 
		 */
		while(htmlDocument(html)){
			//we must get the vxml
			String vxmlAnchor = getVXMLAnchorFromHTML(html);
			setUri(new URI(vxmlAnchor));
			html = getData();
		}
		
        //create a temp file
		
		File temp = null;
		   try {
		        // Create temp file.
		        temp = File.createTempFile("vxmlDocHttpDelegate"+System.currentTimeMillis(), ".vxml");
     	        // Write to temp file
		        BufferedWriter out = new BufferedWriter(new FileWriter(temp));
		        out.write(html);
		        out.close();
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		    
		    InputStream is = new FileInputStream(temp);
		    temp.deleteOnExit();
		  	return is;
	}	
	
	
	
	
	private boolean htmlDocument(String html) {
		// TODO Auto-generated method stub
		return html.toUpperCase().indexOf("<HTML")>-1 || html.toUpperCase().indexOf("</HTML>")>-1 ;
	}




	private String getData() {
		String html = null;
	    client = new HttpClient();
	    
	    // Create a method instance.
	    method = new GetMethod(getUri().toString());
      // Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
	    
	    
	      //if we must load cookies
	      if(isSaveCookies()){
	    	  if(cookies!=null){
	    	     client.getState().addCookies(cookies);
	    	  }
	      }
	    
         try {
	      // Execute the method.
	      int statusCode = client.executeMethod(method);
          if (statusCode != HttpStatus.SC_OK) {
	        System.err.println("Method failed: " + method.getStatusLine());
	      }
	      // Read the response body.
	      byte[] responseBody = method.getResponseBody();
	      html = new String(responseBody);
	      // Deal with the response.
	      // Use caution: ensure correct character encoding and is not binary data
	      //System.out.println(html);
	      printCookies();
	      
	      //if we must save cookies
	      if(isSaveCookies()){
	    	  cookies =  client.getState().getCookies();
	      }
	      
	    } catch (Exception e) {
	      System.err.println("Fatal protocol violation: " + e.getMessage());
	      e.printStackTrace();
	    }	
	
	    return html;
	}




	private void printCookies() {
		// TODO Auto-generated method stub
		Cookie[] cookies = client.getState().getCookies();
		
		for(int i=0;i<cookies.length;i++){
			Cookie cookie = cookies[i];
			System.out.println("HttpDelegate.Cookie["+i+"] = "+cookie.toString());
		}
	}


	
	public void release(){
		method.releaseConnection();
	}

	private boolean isSaveCookies() {
		return saveCookies;
	}

	private void setSaveCookies(boolean saveCookies) {
		this.saveCookies = saveCookies;
	}

	private URI uri;

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	
	private String getVXMLAnchorFromHTML(String html) {
		// TODO Auto-generated method stub
		
		String aStart = "<a href=\"";		
		int start = html.indexOf(aStart);
		
		//get rid of everything before the start
		html = html.substring(start+aStart.length(),html.length());
		
		//now find the next index of ">
		String aEnd = "\">";
		int end = html.indexOf(aEnd);
		
		//substring again
		
		
		return html.substring(0,end);
		
	}

	
	
	
	
	
	
	

}
