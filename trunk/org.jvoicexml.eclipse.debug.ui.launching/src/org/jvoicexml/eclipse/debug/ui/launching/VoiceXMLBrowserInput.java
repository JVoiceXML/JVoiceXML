package org.jvoicexml.eclipse.debug.ui.launching;
/*******************************************************************************
 * Copyright (c) 2005,2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

/**
 * Defines the input that may be sent to a IVoiceXMLBrowser. Contains support for DTMF, Recognized Speech, and Events.
 *   May be subclassed to add additional communication to the browser.
 * 
 * @see IVoiceXMLBrowser
 * @author Brent D. Metz
 */
public class VoiceXMLBrowserInput {
	/**
	 * Indicates this input is a DTMF keypress or keypresses. The input object should be a single character in a String.
	 */
	public static final int TYPE_DTMF = 1;
	
	/**
	 * Indicates this input is recognized speech. The input object should be a String.
	 */
	public static final int TYPE_VOICE = 2;
	
	/**
	 * Indicates this input is an event being thrown (such as nomatch, noinput, or application defined events).
	 *   The input object should be a String.
	 */
	public static final int TYPE_EVENT = 3;
	
	/**
	 * Indicates this input is vendor specific. The implementation should provide its own object to pass to setInput().
	 */
	public static final int TYPE_VENDOR_SPECIFIC = 4;
	
	/**
	 * Type of input
	 */
	protected int type=0;
	
	/**
	 * Input value
	 */
	protected Object input=null;
	
	/**
	 * Confidence score, if a recognized utterance
	 */
	protected double confidence=0.0;
	
	/**
	 * Semantic Interpretations in XML Fragment, as defined by W3C Semantic Interpretation for Speech Recognition section 7.1
	 */
	protected String interpretations=null;

	/**
	 * Sets the kind of event this object represents (eg, TYPE_DTMF, TYPE_VOICE, TYPE_EVENT)
	 * 
	 * @param eventType The type of event this object represents.
	 */
	public void setInputType(int eventType) {
		this.type=eventType;
	}
	
	/**
	 * Gets the kind of event this object represents.
	 */
	public int getInputType() {
		return type;
	}
	
	/**
	 * Sets the type-specific input to send to a browser. In the case of TYPE_DTMF, the string should be a string of one digits 
	 * representing the DTMF Key Pressed. In the case of TYPE_VOICE, the string should contain the utterance recognized. In the
	 * case of TYPE_EVENT, the string should contain the event to throw. In the case of TYPE_VENDOR_SPECIFIC, a
	 * vendor-specific object should be specified.
	 * 
	 * @param input The input to send to the browser.
	 */
	public void setInput(Object input) {
		this.input=input;
	}
	
	/**
	 * Gets the type-specific input.
	 * 
	 * @see #setInput(String)
	 * @return The input for the browser to interpret.
	 */
	public Object getInput() {
		return input;
	}
	
	/**
	 * Sets the confidence score of a speech input. Only valid if setInputType(TYPE_VOICE) is used. Values
	 *   must be in the range 0.0 to 1.0.
	 *   
	 * @param confidence The confidence score to use for this input.
	 */
	public void setConfidence(double confidence) {
		this.confidence=confidence;
	}
	
	/**
	 * Returns the confidence score. Note that this value is only relevant if getInputType() is equal to TYPE_VOICE.
	 * 
	 * @return The confidence score of the recognized input.
	 */
	public double getConfidence() {
		return confidence;
	}

	/**
	 * Returns the XML fragment representing the semantic interpretations associated with this input. See
	 *   the W3C document "Semantic Interpretation for Speech Recognition" section 7.1 for more details.
	 * 
	 * @return The semantic interpretation or null if none defined.
	 */
	public String getInterpretations() {
		return interpretations;
	}

	/**
	 * Sets the XML fragment version of the semantic interpretation for this input or null if none is to be
	 *   specified. See The W3C document "Semantic Interpretation for Speech Recognition" section 7.1 for more details
	 *   on the content of the fragment.
	 * @param interpretations The semantic interpretation or null if none defined.
	 */
	public void setInterpretations(String interpretations) {
		this.interpretations = interpretations;
	}
	
}
