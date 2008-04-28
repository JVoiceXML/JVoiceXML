package org.eclipse.vtp.internal.debug.ui.launcher;
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
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

/**
 * Tab group for VoiceXML Launch Configuration Dialog
 * 
 * @author Brent D. Metz
 */
public class VoiceXMLTabGroup extends AbstractLaunchConfigurationTabGroup {

	public VoiceXMLTabGroup() {
		super();
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new CommonVoiceXMLBrowserTab(),
				new CommonTab()
		};
		setTabs(tabs);		
	}
}
