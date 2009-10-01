/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

package org.jvoicexml;

import java.util.Collection;

/**
 * This is a place holder to store the information of a semantic interpretation
 * of a recognition result according to
 * <a href="http://www.w3.org/TR/semantic-interpretation/">
 * http://www.w3.org/TR/semantic-interpretation/</a>.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */

public interface SemanticInterpretation {
    /**
     * Returns the top-level result properties.
     * @return the result properties
     * @since 0.7.2
     */
    Collection<String> getResultProperties();

    /**
     * Retrieves the value of the given property.
     * @param property name of the property
     * @return value of the given property.
     * @since 0.7.2
     */
    Object getValue(final String property);
}
