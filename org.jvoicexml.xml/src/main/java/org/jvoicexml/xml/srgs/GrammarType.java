/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.srgs;

import java.util.ServiceLoader;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

/**
 * Definition of the type of the grammar.
 *
 * <p>
 * In order to define custom grammar types this class must be derived. In
 * addition it is required to implement a custom {@link GrammarTypeFactory} to
 * be able to obtain the added grammar type for the added type. The
 * {@link GrammarTypeFactory} is looked up using the service locator mechanism.
 * Therefore, the jar containing the extra grammar type must be in the classpath
 * at startup time.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @since 0.5.5
 */
public class GrammarType {
    /**
     * JSGF formatted grammar.
     */
    public static final GrammarType JSGF = new GrammarType("application",
            "x-jsgf", false);

    /**
     * SRGS grammar with ABNF format.
     */
    public static final GrammarType SRGS_ABNF = new GrammarType("application",
            "srgs", false);

    /**
     * SRGS grammar in XML format.
     */
    public static final GrammarType SRGS_XML = new GrammarType("application",
            "srgs+xml", true);

    /**
     * Nuance GSL grammar format as defined at <a href=
     * "http://cafe.bevocal.com/docs/grammar/gsl.html#198142">http://cafe.bevocal.com/docs/grammar/gsl.html#198142</a>.
     */
    public static final GrammarType GSL = new GrammarType("application",
            "x-nuance-gsl", true);

    /**
     * Binary Nuance GSL grammar format as defined at <a href=
     * "http://cafe.bevocal.com/docs/grammar/define.html#195253">http://cafe.bevocal.com/docs/grammar/define.html#195253</a>.
     */
    public static final GrammarType GSL_BINARY = new GrammarType("application",
            "x-nuance-dynagram-binary", false);

    /** Name of the grammar type. */
    private MimeType type;

    /** <code>true</code> if the grammar is XML formatted. */
    private final boolean isXmlFormat;

    /**
     * Do not create from outside.
     * 
     * @param primary
     *            the primary type, typically {@code application}
     * @param sub
     *            the grammar sub type
     * @param isXml
     *            <code>true</code> if the grammar is XML formatted
     * @exception IllegalArgumentException if the type does not denote a mime type
     */
    protected GrammarType(final String primary, final String sub,
            final boolean isXml) {
        try {
            type = new MimeType(primary, sub);
        } catch (MimeTypeParseException e) {
            throw new IllegalArgumentException(
                    "unable to parse mime type:" + e.getMessage(), e);
        }
        isXmlFormat = isXml;
    }

    /**
     * Do not create from outside.
     * 
     * @param mimeType
     *            the type
     * @param isXml
     *            <code>true</code> if the grammar is XML formatted
     */
    protected GrammarType(final MimeType mimeType, final boolean isXml) {
        type = mimeType;
        isXmlFormat = isXml;
    }

    
    /**
     * Retrieves the mime of this grammar type.
     * 
     * @return mime type of this type.
     */
    public final MimeType getType() {
        return type;
    }

    /**
     * Checks if this grammar type is XML formatted.
     * 
     * @return <code>true</code> if the grammar is XML formatted.
     * @since 0.7.5
     */
    public final boolean isXmlFormat() {
        return isXmlFormat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return type.toString();
    }

    /**
     * Converts the given value of the attribute into a <code>GrammarType</code>
     * object. If the attribute can not be resolved, an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param attribute
     *            Value of the attribute as it is specified in a {@link Grammar}
     *            type.
     * @return corresponding <code>GrammarType</code> object.
     * @since 0.6
     */
    public static final GrammarType valueOfAttribute(final String attribute) {
        // First, check if there is an externally defined grammar
        final ServiceLoader<GrammarTypeFactory> factories = ServiceLoader
                .load(GrammarTypeFactory.class);
        for (GrammarTypeFactory factory : factories) {
            final GrammarType type = factory.getGrammarType(attribute);
            if (type != null) {
                return type;
            }
        }

        // If there is none, try it with internal grammars
        final JVoiceXmlGrammarTypeFactory factory = new JVoiceXmlGrammarTypeFactory();
        final GrammarType type = factory.getGrammarType(attribute);
        if (type != null) {
            return type;
        }
        throw new IllegalArgumentException("Unable to determine the grammar"
                + " type for '" + attribute + "'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isXmlFormat ? 1231 : 1237);
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GrammarType other = (GrammarType) obj;
        if (isXmlFormat != other.isXmlFormat) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

}
