package org.eclipse.vtp.launching;
/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * LaunchConfigurationDelegate implementation for VoiceXML Execution
 * 
 * @author Brent D. Metz
 */
public class VoiceXMLLaunchConfigurationDelegate implements
		ILaunchConfigurationDelegate {

	/**
	 * Produces a shortened version of a URL.
	 * 
	 * @param url The URL to convert.
	 * @return A string representing a shortened version of a URL.
	 */
	protected String createURLShortname(String url) {
		try { 
			URL u = new URL(url);
			return u.getFile();
		} catch (Exception e) {
			
		}
		return url;
	}
	/**
	 * Instantitate and start specified browser. 
	 */
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		if (mode == null || mode == ILaunchManager.DEBUG_MODE) {
			return; // Debug not supported
		}
		
		String url = configuration.getAttribute(IVoiceXMLBrowserConstants.LAUNCH_URL, ""); //$NON-NLS-1$
		if (url == null || url.trim().length() == 0) {
			return;
		}
		String browserID = configuration.getAttribute(IVoiceXMLBrowserConstants.LAUNCH_BROWSER_ID, ""); //$NON-NLS-1$
		if (browserID == null || browserID.trim().length() == 0) {
			return;
		}
		
		IVoiceXMLBrowser browser = null;
		String browserLabel = createURLShortname(url);
		
		IExtensionPoint iep = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.vtp.launching.voiceXMLBrowser"); //$NON-NLS-1$
		if (iep != null) {
			IExtension[] extensions = iep.getExtensions();

			for (int i = 0; browser==null && i < extensions.length; i++) {
				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
				if (elements == null) {
					continue;
				}
				for (int j = 0; browser==null && j < elements.length; j++) {
					String id = elements[j].getAttributeAsIs("id"); //$NON-NLS-1$
					if (id != null && id.equals(browserID)) {
						Object o = elements[j].createExecutableExtension("class"); //$NON-NLS-1$
						if (o != null && o instanceof IVoiceXMLBrowser) {
							browser = (IVoiceXMLBrowser)o;
						}
						String launchInputView = elements[j].getAttributeAsIs("launchInputView"); //$NON-NLS-1$
						if (launchInputView != null && launchInputView.trim().toLowerCase().equals("true")) { //$NON-NLS-1$
							try {
								Display.getDefault().syncExec(new Runnable() {
									public void run() {
										try {
											PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.vtp.debug.ui.inputView", null, IWorkbenchPage.VIEW_ACTIVATE); //$NON-NLS-1$
										} catch (PartInitException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								});
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		
		if (browser == null) {
			return;
		}
		
		// Set browser properties
		Map attributes = configuration.getAttributes();
		Iterator I = attributes.keySet().iterator();
		while (I.hasNext()) {
			Object o = I.next();
			if (!(o instanceof String)) {
				continue;
			}
			String key = (String)o;
			browser.setProperty(key, attributes.get(key));
		}

		VoiceXMLBrowserProcess proc = new VoiceXMLBrowserProcess(launch, browser);
		proc.setLabel(browserLabel);
		launch.addProcess(proc);
		
		browser.setProcess(proc);
		
		DebugEvent[] eventSet = new DebugEvent[2];
		eventSet[0] = new DebugEvent(proc, DebugEvent.CREATE);
		eventSet[1] = new DebugEvent(launch, DebugEvent.CHANGE);
		DebugPlugin.getDefault().fireDebugEventSet(eventSet);
		
		browser.start();
	}

}
