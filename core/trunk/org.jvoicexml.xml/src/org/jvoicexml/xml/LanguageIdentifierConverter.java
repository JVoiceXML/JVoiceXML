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
package org.jvoicexml.xml;

import java.util.Locale;

/**
 * A language identifier labels information content as being of a particular
 * human language variant. Following the XML specification for language
 * identification, a legal language identifier is identified by an
 * <a href="http://www.ietf.org/rfc/rfc3066.txt">RFC3066</a> code. A language
 * code is required by RFC 3066. A country code or other subtag identifier is
 * optional by RFC 3066. This converter is able to transform such a
 * language identifier to a {@link Locale} and vice versa.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.1
 */
public final class LanguageIdentifierConverter {
    /**
     * Prevent construction.
     */
    private LanguageIdentifierConverter() {
    }

    /**
     * Converts a locale to a language identifier.
     * @param locale the locale to convert.
     * @return converted language identifier
     */
    public static String toLanguageIdentifier(final Locale locale) {
        if (locale == null) {
            return null;
        }
        final StringBuilder str = new StringBuilder();
        str.append(locale.getLanguage());
        final String country = locale.getCountry();
        if (!country.isEmpty()) {
            str.append('-');
            str.append(country);
        }
        return str.toString();
    }

    /**
     * Converts a language identifier to a locale. Throws a 
     * {@link IllegalArgumentException} if the language identifier can not be
     * converted.
     * @param identifier language identifier to convert.
     * @return converted locale
     */
    public static Locale toLocale(final String identifier) {
        if ((identifier == null) || identifier.isEmpty()) {
            return null;
        }
        final String[] parts = identifier.split("-");
        final String language = parts[0];
        if (language.length() != 2) {
            throw new IllegalArgumentException(
                    "Language must be a 2-letter code!");
        }
        if (parts.length == 1) {
            return new Locale(language.toLowerCase());
        }
        final String country = parts[1];
        if (country.length() != 2) {
            throw new IllegalArgumentException(
                    "Country must be a 2-letter code!");
        }
        return new Locale(language.toLowerCase(), country.toUpperCase());
    }
}
