package org.eclipse.vtp.internal.debug.ui.launcher;
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
import java.net.URI;
import java.net.URL;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.debug.ui.BrowserConfigurationUIListener;
import org.eclipse.vtp.debug.ui.IBrowserConfigurationUI;
import org.eclipse.vtp.internal.debug.ui.Messages;
import org.eclipse.vtp.launching.IVoiceXMLBrowserConstants;

/**
 * Tab group for VoiceXML browser settings. May not be subclassed.
 * 
 * @author Brent D. Metz
 */
public class CommonVoiceXMLBrowserTab extends AbstractLaunchConfigurationTab {

	protected Image icon = null;
	
	protected String browserIds[] = null;
	protected IConfigurationElement browserElements[] = null;
	
	protected Text urlText;

	protected Combo browserCombo;
	
	protected Composite customPane=null;
	
	protected Composite mainComposite=null;
	
	protected ILaunchConfiguration launchConfiguration = null;
	
	protected IBrowserConfigurationUI currentBrowserUI = null;
	
	protected int defaultBrowser = -1;
	
	public void createControl(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		mainComposite=c;
		setControl(c);
		
		GridLayout gl = new GridLayout();
		gl.numColumns=2;
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace=true;
		gd.grabExcessVerticalSpace=true;
		gd.verticalIndent=10;
		
		c.setLayout(gl);
		c.setLayoutData(gd);
		
		Label l = new Label(c, SWT.NONE);
		l.setText(Messages.BrowserWithColon);
		l.setLayoutData(new GridData());
		
		Vector names = new Vector();
		Vector ids = new Vector();
		Vector browserExtensions = new Vector();
		
		IExtensionPoint iep = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.vtp.launching.voiceXMLBrowser"); //$NON-NLS-1$
		if (iep != null) {
			IExtension[] extensions = iep.getExtensions();

			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
				if (elements == null) {
					continue;
				}
				for (int j = 0; j < elements.length; j++) {
					String id = elements[j].getAttributeAsIs("id"); //$NON-NLS-1$
					String name = elements[j].getAttributeAsIs("name"); //$NON-NLS-1$
					if (new Boolean(elements[j].getAttributeAsIs("default")).equals(Boolean.TRUE)) { //$NON-NLS-1$
						defaultBrowser=i;
					}
					if (id == null || name == null) {
						continue;
					}
					ids.add(id);
					names.add(name);
					browserExtensions.add(elements[j]);
				}
			}
		}
		String browsers[] = (String[])names.toArray(new String[0]);
		browserIds = (String[])ids.toArray(new String[0]);
		
		// Keep extension points objects around so the UI class can be instantiated if necessary
		browserElements = (IConfigurationElement[])browserExtensions.toArray(new IConfigurationElement[0]);
		
		browserCombo = new Combo(c, SWT.READ_ONLY);
		browserCombo.setItems(browsers);


		browserCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				refreshActiveConfigurationPane();
			}
		});
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace=true;
		browserCombo.setLayoutData(gd);
		
		l = new Label(c, SWT.NONE);
		l.setText(Messages.URL);
		l.setLayoutData(new GridData());
	 
		urlText = new Text(c, SWT.SINGLE|SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace=true;
		urlText.setLayoutData(gd);
		urlText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		
		if (browsers != null && browsers.length > 0) {
			browserCombo.select(0);
			browserCombo.select(defaultBrowser);
			refreshActiveConfigurationPane();
		}
	}
	
	/**
	 * Loads the correct configuration pane for the currently selected browser
	 */
	private void refreshActiveConfigurationPane() {
		if (customPane != null) {
			customPane.dispose();
		}
		currentBrowserUI=null;
		
		int idx = browserCombo.getSelectionIndex();
		if (idx == -1 || browserIds == null || browserIds.length < (idx-1)) {
			return;
		}
		
		try {
			Object o = browserElements[idx].createExecutableExtension("configurationUIClass"); //$NON-NLS-1$
			if (o == null || !(o instanceof IBrowserConfigurationUI)) {
				return;
			}
			
			customPane = new Composite(mainComposite, SWT.NONE);
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.horizontalSpan=2;
			gd.grabExcessHorizontalSpace=true;
			gd.grabExcessVerticalSpace=true;
			customPane.setLayoutData(gd);
			customPane.setLayout(new GridLayout());
			
			currentBrowserUI = (IBrowserConfigurationUI)o;
			currentBrowserUI.drawConfigurationUI(customPane, new BrowserConfigurationUIListener() {
				public void contentsChanged() {
					setDirty(true);
					updateLaunchConfigurationDialog();
				}
			});
			if (launchConfiguration != null) {
				currentBrowserUI.initializeFrom(launchConfiguration);
			}
			mainComposite.layout(true, true);
			

		} catch (CoreException e) {
			return;
		}
		
	}
	
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		IExtensionPoint iep = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.vtp.launching.voiceXMLBrowser"); //$NON-NLS-1$
		if (iep != null) {
			IExtension[] extensions = iep.getExtensions();

			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
				if (elements == null) {
					continue;
				}
				for (int j = 0; j < elements.length; j++) {
					if (new Boolean(elements[j].getAttributeAsIs("default")).equals(Boolean.TRUE)) { //$NON-NLS-1$
						defaultBrowser=i;
					}
				}
			}
		}
		
		if (urlText != null) {
			urlText.setText("");//$NON-NLS-1$
		}
		if (browserCombo != null) {
			browserCombo.select(0);
			browserCombo.select(defaultBrowser);
			refreshActiveConfigurationPane();
			if (currentBrowserUI != null) {
			   currentBrowserUI.setDefaults(configuration);
			}
		}
	}
	
	public boolean isValid(ILaunchConfiguration launchConfig) {
		try {
			String url = launchConfig.getAttribute(IVoiceXMLBrowserConstants.LAUNCH_URL, ""); //$NON-NLS-1$
			if (url.trim().length() == 0) {
				return false;
			}
			
			new URI(url.trim());
			
			String browser = launchConfig.getAttribute(IVoiceXMLBrowserConstants.LAUNCH_BROWSER_ID, ""); //$NON-NLS-1$
			
			if (browser.trim().length() == 0) {
				return false;
			}
			if (currentBrowserUI != null) {
				return currentBrowserUI.isValid(launchConfig);
			}
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			launchConfiguration = configuration;
			if (browserCombo != null) {
				browserCombo.select(defaultBrowser);
			}
			
			String url = configuration.getAttribute(IVoiceXMLBrowserConstants.LAUNCH_URL, ""); //$NON-NLS-1$
			urlText.setText(url);
			
			String browser = configuration.getAttribute(IVoiceXMLBrowserConstants.LAUNCH_BROWSER_ID, ""); //$NON-NLS-1$
			if (browser.trim().length() != 0 && browserIds != null) {
				for (int i=0;i<browserIds.length;i++) {
					if (browserIds[i] == null) {
						continue;
					}
					if (browserIds[i].equals(browser)) {
						browserCombo.select(i);
						refreshActiveConfigurationPane();
					}
				}
			}
		} catch (CoreException e) {
		}
		
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(IVoiceXMLBrowserConstants.LAUNCH_URL, urlText.getText().trim());
		int idx = browserCombo.getSelectionIndex();
		if (idx == -1 || browserIds == null || browserIds.length < (idx-1)) {
			return;
		}
		configuration.setAttribute(IVoiceXMLBrowserConstants.LAUNCH_BROWSER_ID, browserIds[idx]);
		if (currentBrowserUI != null) {
			currentBrowserUI.performApply(configuration);
		}
	}

	public String getName() {
		return Messages.Browser;
	}
	
	public Image getImage() {
		try {
			if (icon != null) {
				return icon;
			}
			URL imageURL = new URL("platform:/plugin/org.eclipse.vtp.debug.ui/icons/cview16/VoiceXMLFile.gif"); //$NON-NLS-1$
			ImageDescriptor id = ImageDescriptor.createFromURL(imageURL);
			icon = id.createImage();
			return icon;
		} catch (Exception e) {
			return super.getImage();
		}
	}

	public void dispose() {
		if (icon != null) {
			try {
				icon.dispose();
				icon=null;
			} catch (Exception e) {
			}
		}
		if (currentBrowserUI != null) {
			currentBrowserUI.dispose();
		}
		super.dispose();
	}
}
