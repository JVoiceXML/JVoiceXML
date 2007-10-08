/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

/**
 * Defintion of the type of the grammar.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5.5
 */
public enum GrammarType {
    /**
     * JSGF formatted grammar.
     */
    JSGF("application/x-jsgf"),

    /**
     * SRGS grammar with ABNF format.
     */
    SRGS_ABNF("application/srgs"),

    /**
     * SRGS grammar in XML format.
     */
    SRGS_XML("application/srgs+xml");

    /** Name of the grammar type. */
    private final String type;

    /**
     * Do not create from outside.
     * @param name name of the grammar type.
     */
    private GrammarType(final String name) {
        type = name;
    }

    /**
     * Retrieves the name of this grammar type.
     * @return Name of this type.
     */
    public String getType() {
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
    public static GrammarType valueOfAttribute(final String attribute) {
        if (JSGF.getType().equals(attribute)) {
            return JSGF;
        }
        if (SRGS_ABNF.getType().equals(attribute)) {
            return SRGS_ABNF;
        }
        if (SRGS_XML.getType().equals(attribute)) {
            return SRGS_XML;
        }
        throw new IllegalArgumentException("Unksupported grammar type '"
                + attribute + "'");
    }
}
