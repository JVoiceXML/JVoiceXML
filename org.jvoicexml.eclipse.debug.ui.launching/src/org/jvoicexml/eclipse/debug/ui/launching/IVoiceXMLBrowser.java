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
/**
 * Interface to a VoiceXML Browser utilized by execution environment to control starting,
 * stopping and feed events such as DTMF keypressed and simulation of recognized text. 
 * 
 * @author Brent D. Metz
 */
public interface IVoiceXMLBrowser {

	/**
	 * This capability indicates that this VoiceXML browser is capable of responding to requests to sendInput().
	 */
	public static final String CAPABILITY_INTERACTIVE = "org.jvoicexml.eclipse.debug.ui.launching.capability_interactive"; //$NON-NLS-1$

	/**
	 * This capability indicates that this VoiceXML browser is capable of processing DTMF events.
	 */
	public static final String CAPABILITY_DTMF = "org.jvoicexml.eclipse.debug.ui.launching.capability_dtmf"; //$NON-NLS-1$

	/**
	 * This capability indicates that this VoiceXML browser sends log events.
	 */
	public static final String CAPABILITY_LOG_EVENT = "org.jvoicexml.eclipse.debug.ui.launching.capability_log_event"; //$NON-NLS-1$

	/**
	 * Starts the VoiceXML Browser.
	 *
	 */
	public void start();

	/**
	 * Stops the VoiceXML Browser immediately. (Note: No hangup events are thrown).
	 * 
	 */
	public void stop();

	/**
	 * Sends a hangup request.
	 */
	public void hangup();

	/**
	 * Sends input to the VoiceXML browser for processing. This should only be called on browsers
	 *   where hasCapability(CAPABILITY_INTERACTIVE) returns true.
	 * 
	 * @param input The input to send to the browser.
	 */
	public void sendInput(VoiceXMLBrowserInput input);

	/**
	 * Sets the VoiceXMLBrowserProcess associated with this browser, if available. This object
	 *   contains the necessary references for a VoiceXML Browser to throw Debug Events.
	 * 
	 * @param process The VoiceXMLBrowserProcess associated with this browser.
	 */
	public void setProcess(VoiceXMLBrowserProcess process);
	
	/**
	 * Returns the VoiceXMLBrowserProcess associated with this browser, if available.
	 * 
	 * @return The VoiceXMLBrowserProcess object associated with this browser or null if none is has been set.
	 */
	public VoiceXMLBrowserProcess getProcess();
	
	/**
	 * Sets a browser property. This method should be called before calling start().
	 * 
	 * @param propertyname The name of the property to set.
	 * @param value The value of the property to set.
	 */
	public void setProperty(String propertyname, Object value);

	/**
	 * Returns whether or not the browser supports the given capability. A list of standard
	 *   capabilities is present in IVoiceXMLBrowser.
	 * 
	 * @param capability The capability to check for.
	 * @return true if the browser has the capability; false otherwise
	 */
	public boolean hasCapability(String capability);
}
