package org.jvoicexml.android.implementation;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.ImplementationPlatformFactory;
import org.jvoicexml.event.error.NoresourceError;


public class AndroidImplementationPlatformFactory implements
		ImplementationPlatformFactory {

	@Override
	public void init(Configuration configuration) throws ConfigurationException {
		// TODO Auto-generated method stub

	}

	@Override
	public ImplementationPlatform getImplementationPlatform(
			ConnectionInformation info) throws NoresourceError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
