/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/xml/vxml/BargeInType.java $
 * Version: $LastChangedRevision: 208 $
 * Date:    $Date: 2007-02-02 09:16:14 +0100 (Fr, 02 Feb 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * The mode of a grammar indicates the type of input that the user agent should
 * be detecting.
 *
 * @author Dirk Schnelle
 * @version $Revision: 208 $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public enum ModeType {
    /**
     * Speech recognition grammar.
     */
    VOICE("voice"),

    /**
     * DTMF grammar.
     */
    DTMF("dtmf");

    /** Name of the mode. */
    private final String mode;

    /**
     * Do not create from outside.
     * @param name name of the mode.
     */
    private ModeType(final String name) {
        mode = name;
    }

    /**
     * Retrieves the name of this barge-in type.
     * @return Name of this type.
     */
    public String getMode() {
        return mode;
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
    public static ModeType valueOfAttribute(final String attribute) {
        if (VOICE.getMode().equals(attribute)) {
            return VOICE;
        }
        if (DTMF.getMode().equals(attribute)) {
            return DTMF;
        }
        throw new IllegalArgumentException("Unksupported mode type '"
                + attribute + "'");
    }
}
