/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/variables/ConnectionLocalVarContainer.java $
 * Version: $LastChangedRevision: 2476 $
 * Date:    $Date: 2010-12-23 05:36:01 -0600 (jue, 23 dic 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.variables;

import java.net.URI;

import org.mozilla.javascript.ScriptableObject;

/**
 * A variable container to hold the local connection information.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2476 $
 * @since 0.7
 */
public final class ConnectionLocalVarContainer extends ScriptableObject {
    /** The serial version UID. */
    private static final long serialVersionUID = -1072897636114590939L;

    /** The local interpreter context device. */
    private final String uri;

    /**
     * Constructs a new object.
     * @param localUri local interpreter context device
     */
    public ConnectionLocalVarContainer(final URI localUri) {
        if (localUri == null) {
            uri = null;
        } else {
            uri = localUri.toString();
        }
        defineProperty("uri", ConnectionLocalVarContainer.class,
                READONLY);
    }

    /**
     * This method is a callback for rhino which gets called on instantiation.
     * (virtual js constructor)
     */
    public void jsContructor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassName() {
        return ConnectionLocalVarContainer.class.getSimpleName();
    }

    /**
     * Retrieves the URI.
     * @return the URI.
     */
    public String getUri() {
        return uri;
    }
}
