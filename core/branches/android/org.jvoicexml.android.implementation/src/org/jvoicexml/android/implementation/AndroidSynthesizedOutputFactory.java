package org.jvoicexml.android.implementation;

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SynthesizedOutput;

public class AndroidSynthesizedOutputFactory implements
		ResourceFactory<SynthesizedOutput> {
	
	private int instances;

	@Override
	public Class<SynthesizedOutput> getResourceType() {
		return SynthesizedOutput.class;
	}

	@Override
	public SynthesizedOutput createResource() throws NoresourceError {
		return new AndroidSynthesizedOutput();	
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
