package org.jvoicexml.documentserver.schemestrategy;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.jvoicexml.documentserver.SchemeStrategy;
import org.jvoicexml.event.error.BadFetchError;


public class ProxyHttpClientSchemeStrategy implements SchemeStrategy {

	public static final String SCHEME_NAME = "http";
	ProxyHttpDelegate delegate  = new ProxyHttpDelegate(true);
	static Logger log = Logger.getLogger( ProxyHttpClientSchemeStrategy.class );
	
	public InputStream getInputStream(URI arg0) throws BadFetchError {
		// TODO Auto-generated method stub
		log.debug("Returning InputStream.....");
		delegate.setUri(arg0);
		try {
			return delegate.getHtmlAsInputStream();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log.debug("Could not return input stream");
		return null;
		
	}

	public String getScheme() {
		// TODO Auto-generated method stub
		return SCHEME_NAME;
	}
	
	
	
	
	

}
