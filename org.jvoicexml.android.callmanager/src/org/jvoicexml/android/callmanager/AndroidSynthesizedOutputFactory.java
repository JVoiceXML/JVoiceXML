package org.jvoicexml.android.callmanager;

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SynthesizedOutput;

import android.content.Context;

public class AndroidSynthesizedOutputFactory implements
		ResourceFactory<SynthesizedOutput> {
	
	private int instances;
	private Context callManagerContext;

	@Override
	public Class<SynthesizedOutput> getResourceType() {
		return SynthesizedOutput.class;
	}

	@Override
	public SynthesizedOutput createResource() throws NoresourceError {
		 AndroidSynthesizedOutput androidSynthesizedOutput = new AndroidSynthesizedOutput();
		 androidSynthesizedOutput.setContext(this.callManagerContext);
		 androidSynthesizedOutput.open();
		 instances+=1;
		 
		 return androidSynthesizedOutput;
		
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
	
	public void setContext(Context context){
		this.callManagerContext=context;
	}
	
	//retrieve the Android Context
	public Context getContext(){
		return callManagerContext;
	}

}
