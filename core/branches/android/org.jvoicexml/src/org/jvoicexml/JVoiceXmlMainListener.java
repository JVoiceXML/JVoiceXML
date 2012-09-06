/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/JVoiceXmlMainListener.java $
 * Version: $LastChangedRevision: 2650 $
 * Date:    $Date: 2011-05-11 08:21:24 -0500 (mié, 11 may 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml;

/**
 * A listener to monitor the states of JVoiceXmlMain.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2650 $
 * @since 0.7.5
 */
public interface JVoiceXmlMainListener {
    /**
     * JVoiceXml has been started.
     */
    void jvxmlStarted();

    /**
     * JVoiceXml has been stopped.
     */
    void jvxmlTerminated();
}
