/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.config/src/org/jvoicexml/config/ExtendedConfiguration.java $
 * Version: $LastChangedRevision: 2605 $
 * Date:    $Date: 2011-02-20 04:38:38 -0600 (dom, 20 feb 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.config;

import java.io.File;

/**
 * An extended configuration is able to read other configuration data from the
 * configuration file.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2605 $
 * @since 0.7.4
 */
public interface ExtendedConfiguration {
    /**
     * Sets the configuration file.
     * @param config the configuration file
     */
    void setConfigurationFile(final File config);
}
