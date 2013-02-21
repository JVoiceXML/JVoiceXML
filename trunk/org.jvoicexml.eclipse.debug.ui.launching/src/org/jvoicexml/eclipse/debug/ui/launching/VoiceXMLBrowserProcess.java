package org.jvoicexml.eclipse.debug.ui.launching;
/*******************************************************************************
 * Copyright (c) 2005,2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

/**
 * Process object implementation for dealing with the IVoiceXMLBrowser object.
 * 
 * @author Brent D. Metz
 */
public class VoiceXMLBrowserProcess implements IProcess {
	private IVoiceXMLBrowser browser = null;
	private ILaunch launch=null;
	private boolean running=true;
	protected String label = Messages.VoiceXMLBrowser;
	
	public VoiceXMLBrowserProcess(ILaunch launch, IVoiceXMLBrowser browser) {
		this.launch=launch;
		this.browser=browser;
		running=true;
	}
	
	public IVoiceXMLBrowser getVoiceXMLBrowser() {
		return browser;
	}
	
	public String getLabel() {
		return label;
	}

	public ILaunch getLaunch() {
		return launch;
	}

	public IStreamsProxy getStreamsProxy() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAttribute(String key, String value) {
	}
	
	/**
	 * Sets whether or not the browser process is terminated. Should be called by implementations of
	 *   IVoiceXMLBrowser after they have finished shutting down.
	 * 
	 * @param isTerminated true is the browser is terminated; false otherwise.
	 */
	public void setTerminated(boolean isTerminated) {
		running=!isTerminated;
	}

	public String getAttribute(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getExitValue() throws DebugException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object getAdapter(Class adapter) {
		if (adapter==ILaunch.class) {
			return launch;
		}
		return null;
	}

	public boolean canTerminate() {
		return running;
	}

	public boolean isTerminated() {
		return !running;
	}

	public void terminate() throws DebugException {
		if (running) {
			if (browser != null) {
				browser.stop();
			}
			running=false;
			DebugEvent[] eventSet = new DebugEvent[2];
			eventSet[0] = new DebugEvent(this, DebugEvent.TERMINATE);
			eventSet[1] = new DebugEvent(launch, DebugEvent.CHANGE);
			DebugPlugin.getDefault().fireDebugEventSet(eventSet);
		}
	}
	
	/**
	 * Sets the displayed label representing this browser session.
	 * 
	 * @param browserLabel The label to display.
	 */
	public void setLabel(String browserLabel) {
		this.label=browserLabel;
	}

}
