/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.mmi.events/src/org/jvoicexml/mmi/events/ExtensionNotification.java $
 * Version: $LastChangedRevision: 3474 $
 * Date:    $Date: 2013-01-21 14:14:00 +0100 (Mo, 21 Jan 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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


package org.jvoicexml.mmi.events;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Base class for MMI lifecycle responses.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3607 $
 * @since 0.7.6
 *
 */
public class LifeCycleResponse extends LifeCycleEvent {
    /** The context identifier of this response. */ 
    private String context;

    /** Status response code of the request. */
    private StatusType status;

    /** Additional status info. */
    private AnyComplexType statusInfo;

    /**
     * Constructs a new object.
     */
    public LifeCycleResponse() {
        super();
    }

    /**
     * Gets the value of the context property.
     * 
     * @return possible object is {@link String }
     * 
     */
    @XmlAttribute(name = "Context",
            namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
    public final String getContext() {
        return context;
    }

    /**
     * Sets the value of the context property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public final void setContext(final String value) {
        context = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return possible object is {@link StatusType }
     * 
     */
    @XmlAttribute(name = "Status",
            namespace = "http://www.w3.org/2008/04/mmi-arch", required = true)
    public final StatusType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *            allowed object is {@link StatusType }
     * 
     */
    public final void setStatus(final StatusType value) {
        status = value;
    }

    /**
     * Retrieves the status info.
     * @return status info
     */
    @XmlElement(name = "StatusInfo",
            namespace = "http://www.w3.org/2008/04/mmi-arch")
    public final AnyComplexType getStatusInfo() {
        return statusInfo;
    }

    /**
     * Sets the status info.
     * @param value new value of the status info
     */
    public final void setStatusInfo(final AnyComplexType value) {
        statusInfo = value;
    }

    /**
     * Adds the value of the status info property to the given text message.
     * @param text the status message to set
     */
    public final void addStatusInfo(final String text) {
        if (statusInfo == null) {
            statusInfo = new AnyComplexType();
        }
        final List<Object> infos = statusInfo.getContent();
        infos.add(infos);
    }
}
