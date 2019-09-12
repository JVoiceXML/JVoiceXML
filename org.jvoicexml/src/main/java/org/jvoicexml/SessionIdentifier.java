/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.Serializable;

/**
 * An identifier for a {@link org.jvoicexml.Session} within JVoiceXML.
 * 
 * This instance is as unique as the session and may be used to distinguish
 * multiple sessions. It is insufficient if a call to {@link #getId()} returns
 * different ids.
 * 
 * Note, that it may be helpful to also override the {@code #toString()} method
 * to have something meaningful in log statements.
 *  
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public interface SessionIdentifier extends Serializable {
    /**
     * Retrieves the session identifier. Subsequent calls to this method
     * must always return the same value for this object.
     * @return String representation of the session identifier
     * @since 0.7.9
     */
    String getId();
}
