package org.jvoicexml.eclipse.debug.ui.launcher;

import org.eclipse.osgi.util.NLS;

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
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jvoicexml.eclipse.debug.ui.launcher.messages"; //$NON-NLS-1$

	private Messages() {
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String BrowserWithColon;

	public static String URL;

	public static String Browser;
}
