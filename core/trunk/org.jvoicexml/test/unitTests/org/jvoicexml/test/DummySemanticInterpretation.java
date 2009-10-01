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
package org.jvoicexml.test;

import java.util.Collection;
import java.util.Map;

import org.jvoicexml.SemanticInterpretation;

/**
 * Dummy implementation for test cases.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 */
public final class DummySemanticInterpretation
        implements SemanticInterpretation {
    /** Result properties. */
    private final Map<String, Object> properties;

    /**
     * Constructs a new object.
     */
    public DummySemanticInterpretation() {
        properties = new java.util.HashMap<String, Object>();
    }

    /**
     * Adds the given result property.
     * @param name name of the result property
     * @param value value of the result property
     */
    public void addResultProperty(final String name, final Object value) {
        properties.put(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getResultProperties() {
        return properties.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(final String property) {
        return properties.get(property);
    }

}
