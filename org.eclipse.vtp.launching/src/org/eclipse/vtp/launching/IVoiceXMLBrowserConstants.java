package org.eclipse.vtp.launching;
/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

/**
 * Constants relating to VoiceXML Browser execution. Defines event types and properties passed into
 *  an IVoiceXMLBrowser instance.
 *  
 *  @see IVoiceXMLBrowser
 */
public interface IVoiceXMLBrowserConstants {

	/**
	 * Event type defining that a resource was loaded by the browser.
	 *   The DebugEvent getData() should contain a java.net.URI class.
	 */
	public static final int EVENT_RESOURCE_LOADED = 1;
	
	/**
	 * Event type defining that input in the form of a platform event such as noinput/nomatch or
	 *   DTMF or a recognized utterance. The DebugEvent getData() should contain a
	 *   VoiceXMLBrowserInput object.
	 */
	public static final int EVENT_INPUT_RECEIVED = 2;
	
	/**
	 * Event type defining a log message contained in a VoiceXML <log> tag was recieved from the
	 *   browser. The DebugEvent getData() should contain a VoiceXMLLogMessage object with log tag contents.
     *
     * @see VoiceXMLLogMessage
	 */
	public static final int EVENT_LOG_MESSAGE = 3;
	
	/**
	 * Event type defining entry by the VoiceXML Browser into a form. The DebugEvent getData() should
	 *   contain a java.net.URI identifying the form.
	 */
	public static final int EVENT_ENTERED_FORM = 4;

	/**
	 * Event type defining exiting by the VoiceXML Browser of a form. The DebugEvent getData() should
	 *   contain a java.net.URI identifying the form.
	 */
	public static final int EVENT_EXITED_FORM = 5;
	
	/**
	 * Event type defining entry by the VoiceXML Browser into a field. The DebugEvent getData() should
	 *   contain a String identifying the field entered. A EVENT_ENTERED_FORM event should be thrown
	 *   before this event.
	 */
	public static final int EVENT_ENTERED_FIELD = 6;
	
	/**
	 * Event type defining exit by the VoiceXML Browser of a field. The DebugEvent getData() should
	 *   contain a String identifying the field entered. 
	 */
	public static final int EVENT_EXITED_FIELD = 7;
	
	/**
	 * Initial URL for the VoiceXML Browser to execute.
	 */
	public static final int PROPERTY_URL = 1;

	/**
	 * Property for Launch Configuration to specify the ID of the browser to instantiate for
	 *   this launch.
	 */
	public static final String LAUNCH_BROWSER_ID = "org.eclipse.vtp.launching.browser_id"; //$NON-NLS-1$
	
	/**
	 * Property for Launch Configuration to specify what URL to connect to
	 */
	public static final String LAUNCH_URL = "org.eclipse.vtp.launching.url"; //$NON-NLS-1$
}
