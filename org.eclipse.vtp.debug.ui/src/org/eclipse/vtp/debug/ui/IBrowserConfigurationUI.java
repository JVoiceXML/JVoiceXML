package org.eclipse.vtp.debug.ui;
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
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.widgets.Composite;

/**
 * Defines a standard UI to contribute custom configuration UI to the VTP VoiceXML Browser
 * Launch Configuration Dialog Tabs. Implementers must ensure that they save and load
 * configuration values properly into the launch configuration.
 * 
 *   @since 1.0
 *   @author Brent D. Metz
 */
public interface IBrowserConfigurationUI {
	/**
	 * Initializes the given launch configuration with default values for this UI. This method is called when a new launch configuration is created such that the configuration can be initialized with meaningful values. This method may be called before this tab's control is created. 
	 *
	 * @param configuration Launch Configuration
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration); 

	/**
	 * Copies values from this tab into the given launch configuration.
	 * 
	 * @param configuration Launch Configuration
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration); 
	
	/**
	 * Initializes this UI's controls with values from the given launch configuration. This method is called when a configuration is selected to view or edit, after this UI's control has been created. 
	 * @param configuration
	 */
	public void initializeFrom(ILaunchConfiguration configuration); 
	
	/**
	 * Renders the configuration UI.
	 * 
	 * @param UI The composite to draw any configuration items into.
	 * @param listener A listener for events thrown by the UI elements, such as when they change.
	 */
	public void drawConfigurationUI(Composite UI, BrowserConfigurationUIListener listener);
	
	/**
	 * Checks that required values are present in the given launch configuration.
	 * 
	 * @param configuration
	 */
	public boolean isValid(ILaunchConfiguration configuration);
	
	/**
	 * Disposes of controls in the browser UI after new browser gets selected.
	 * 
	 */
	public void dispose();
	
}
