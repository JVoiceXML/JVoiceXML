/*
 * JVoiceXML VTP Plugin
 *
 * Copyright (C) 2006 Dirk Schnelle
 *
 * Copyright (c) 2006 Dirk Schnelle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jvoicexml.eclipse.debug.ui.launching.jvoicexml;

/**
 * Constants for the plugin used in the <code>JVoiceXmlBrowser</code>.
 *
 * @author Dirk Schnelle
 *
 * @see org.jvoicexml.eclipse.debug.ui.launching.jvoicexml.JVoiceXmlBrowser
 */
interface JVoiceXmlPluginConstants {
    /** Prefix of all constants. */
    String PREFIX = "org.eclipse.vtp.internal.jvoicexml.launching";

    /** Name of the application. */
    String APPLICATION_NAME = PREFIX + ".general.name";

    /** Location of the security policy. */
    String JNDI_POLICY = PREFIX + ".jndi.policy";

    /** JNDI Initial context factory. */
    String JNDI_CONTEXT_FACTORY = PREFIX + ".jndi.context.factory";

    /** RMI provider URL. */
    String JNDI_PROVIDER_URL = PREFIX + ".jndi.provider.url";

    /** Port of the logging receiver. */
    String LOGGING_PORT = PREFIX + ".logging.port";

    /** Level of the logger. */
    String LOGGING_LEVEL = PREFIX + ".logging.level";
    
    /** Text client port. */
    String TEXT_PORT = PREFIX + ".text.port";

}
