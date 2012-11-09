/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/vxml/RequestMethod.java $
 * Version: $LastChangedRevision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.vxml;

/**
 * Type of the request method.
 * @author Dirk Schnelle
 * @version $Revision: 2325 $
 * @since 0.7
 */
public enum RequestMethod {
    /** Request method post. */
    POST("post"),

    /** Request method get. */
    GET("get");

    /** Name of the request method. */
    private final String method;

    /**
     * Do not create from outside.
     * @param name name of the request method
     */
    private RequestMethod(final String name) {
        method = name;
    }

    /**
     * Retrieves the name of this request method.
     * @return Name of this method.
     */
    public String getMethod() {
        return method;
    }

}
