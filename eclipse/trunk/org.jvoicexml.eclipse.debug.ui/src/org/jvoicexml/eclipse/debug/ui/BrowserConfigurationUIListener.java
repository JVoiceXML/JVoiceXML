package org.jvoicexml.eclipse.debug.ui;
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
 * Interface to signal the outer VoiceXML Configuration Tab that the browser-specific
 *   pane has changes and needs to refresh the tab's apply/defaults buttons.
 *   
 *   @author Brent D. Metz <bdmetz@us.ibm.com>
 *   @since 1.0
 */
public interface BrowserConfigurationUIListener {
	/**
	 * Signals that the contents of the UI have changed. Typically called after the user changes
	 *  a control and the UI has to be revalidated.
	 *  
	 *  @see IBrowserConfigurationUI
	 */
	public void contentsChanged();
}
