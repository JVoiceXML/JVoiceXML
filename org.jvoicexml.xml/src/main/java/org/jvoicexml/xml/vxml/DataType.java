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

package org.jvoicexml.xml.vxml;

import java.util.ServiceLoader;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.jvoicexml.xml.srgs.Grammar;

/**
 * Definition of the type of the data referenced by the {@link Data} tag.
 *
 * <p>
 * In order to define custom grammar types this class must be derived. In
 * addition it is required to implement a custom {@link DataTypeFactory} to
 * be able to obtain the added grammar type for the added type. The
 * {@link DataTypeFactory} is looked up using the service locator
 * mechanism. Therefore, the jar containing the extra data type must be
 * in the classpath at startup time.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class DataType {
    /**
     * XML formatted data.
     */
    public static final DataType XML =
        new DataType("application", "xml");

    /**
     * JSON formatted data.
     */
    public static final DataType JSON =
        new DataType("application", "json");

    /** Name of the grammar type. */
    private final MimeType type;

    /**
     * Do not create from outside.
     * 
     * @param primary
     *            the primary type, typically {@code application}
     * @param sub
     *            the grammar sub type
     * @exception IllegalArgumentException if the type does not denote a mime type
     */
    protected DataType(final String primary, final String sub) {
        try {
            type = new MimeType(primary, sub);
        } catch (MimeTypeParseException e) {
            throw new IllegalArgumentException(
                    "unable to parse mime type:" + e.getMessage(), e);
        }
    }

    /**
     * Do not create from outside.
     * @param mimeType type of the data type.
     */
    protected DataType(final MimeType mimeType) {
        type = mimeType;
    }
    
    /**
     * Retrieves the name of this grammar type.
     * @return Name of this type.
     */
    public final MimeType getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return type.toString();
    }

    /**
     * Converts the given value of the attribute into a
     * <code>GrammarType</code> object. If the attribute can not be
     * resolved, an {@link IllegalArgumentException} is thrown.
     *
     * @param attribute Value of the attribute as it is specified in
     *        a {@link Grammar} type.
     * @return corresponding <code>GrammarType</code> object.
     * @since 0.6
     */
    public static final DataType valueOfAttribute(final String attribute) {
        // First, check if there is an externally defined grammar
        final ServiceLoader<DataTypeFactory> factories =
            ServiceLoader.load(DataTypeFactory.class);
        for (DataTypeFactory factory : factories) {
            final DataType type = factory.getDataType(attribute);
            if (type != null) {
                return type;
            }
        }
        
        // If there is none, try it with internal grammars
        final JVoiceXmlDataTypeFactory factory =
            new JVoiceXmlDataTypeFactory();
        final DataType type = factory.getDataType(attribute);
        if (type != null) {
            return type;
        }
        throw new IllegalArgumentException("Unable to determine the data"
                + " type for '" + attribute + "'");
    }
}
