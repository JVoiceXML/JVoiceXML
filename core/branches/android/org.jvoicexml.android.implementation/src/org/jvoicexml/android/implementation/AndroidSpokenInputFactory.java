/**
 * 
 */
package org.jvoicexml.android.implementation;

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SpokenInput;

/**
 * @author Yo
 *
 */
public class AndroidSpokenInputFactory implements ResourceFactory<SpokenInput> {
	private int instances;
	@Override
	public Class<SpokenInput> getResourceType() {
		return SpokenInput.class;
		
	}

	@Override
	public SpokenInput createResource() throws NoresourceError {
		return new AndroidSpokenInput();
	}

	@Override
	public int getInstances() {
		return instances;
	}
	
	public void setInstances(int number) {
		instances = number;
	}
	

	@Override
	public String getType() {
		return "android";
	}

}
