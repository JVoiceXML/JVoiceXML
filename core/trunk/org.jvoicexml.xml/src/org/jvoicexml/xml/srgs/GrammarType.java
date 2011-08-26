/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

/**
 * Defintion of the type of the grammar.
 *
 * <p>
 * In order to define custom grammar types this class must be derived. In
 * addition it is required to implement a custom {@link GrammarTypeFactory} to
 * be able to obtain the added grammar type for the added type. The
 * {@link GrammarTypeFactory} is looked up using the service locator
 * mechanism. Therefore, the jar containing the extra grammar type must be
 * in the classpath at startup time.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.5.5
 */
public class GrammarType {
    /**
     * JSGF formatted grammar.
     */
    public static final GrammarType JSGF =
        new GrammarType("application/x-jsgf", false);

    /**
     * SRGS grammar with ABNF format.
     */
    public static final GrammarType SRGS_ABNF =
        new GrammarType("application/srgs", false);

    /**
     * SRGS grammar in XML format.
     */
    public static final GrammarType SRGS_XML =
        new GrammarType("application/srgs+xml", true);

    /**
     * Nuance GSL grammar format as defined at
     * <a href="http://cafe.bevocal.com/docs/grammar/gsl.html#198142">http://cafe.bevocal.com/docs/grammar/gsl.html#198142</a>.
     */
    public static final GrammarType GSL =
        new GrammarType("application/x-nuance-gsl", true);

    /**
     * Binary Nuance GSL grammar format as defined at
     * <a href="http://cafe.bevocal.com/docs/grammar/define.html#195253">http://cafe.bevocal.com/docs/grammar/define.html#195253</a>.
     */
    public static final GrammarType GSL_BINARY =
        new GrammarType("application/x-nuance-dynagram-binary", false);
    
    /** Name of the grammar type. */
    private final String type;

    /** <code>true</code> if the grammar is XML formatted. */
    private final boolean isXmlFormat;

    /**
     * Do not create from outside.
     * @param name name of the grammar type.
     * @param isXml <code>true</code> if the grammar is XML formatted
     */
    protected GrammarType(final String name, final boolean isXml) {
        type = name;
        isXmlFormat = isXml;
    }

    /**
     * Retrieves the name of this grammar type.
     * @return Name of this type.
     */
    public final String getType() {
        return type;
    }

    /**
     * Checks if this grammar type is XML formatted.
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
        return type;
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
    public static final GrammarType valueOfAttribute(final String attribute) {
        final ServiceLoader<GrammarTypeFactory> factories =
            ServiceLoader.load(GrammarTypeFactory.class);
        for (GrammarTypeFactory factory : factories) {
            final GrammarType type = factory.getGrammarType(attribute);
            if (type != null) {
                return type;
            }
        }
        final JVoiceXmlGrammarTypeFactory factory =
            new JVoiceXmlGrammarTypeFactory();
        final GrammarType type = factory.getGrammarType(attribute);
        if (type != null) {
            return type;
        }
        throw new IllegalArgumentException("Unable to determine the grammar"
                + " type for '" + attribute + "'");
    }
}
