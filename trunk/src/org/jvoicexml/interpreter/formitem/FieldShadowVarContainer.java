/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.interpreter.formitem;

import org.mozilla.javascript.ScriptableObject;

/**
 * Component that provides a container for the shadowed variables of a field.
 * See <a href="http://www.w3.org/TR/voicexml20/#dml2.3.1">
 * http://www.w3.org/TR/voicexml20/#dml2.3.1</a> for details.
 *
 * @author Torben Hardt
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.3.1
 */
@SuppressWarnings("serial")
public final class FieldShadowVarContainer
        extends ScriptableObject {
    /** The field's utterance. */
    private String utterance;

    /** The field's input mode. */
    private String inputmode;

    /** The field's interpretation. */
    private String interpretation;

    /** The confidence. */
    private String confidence;

    /** The name of the mark last executed by the SSML processor. */
    private String markname;

    /**
     * Constructs a new object.
     */
    public FieldShadowVarContainer() {
        defineProperty("utterance", FieldShadowVarContainer.class, READONLY);
        defineProperty("confidence", FieldShadowVarContainer.class, READONLY);
        defineProperty("markname", FieldShadowVarContainer.class, READONLY);
        defineProperty("inputmode", FieldShadowVarContainer.class, PERMANENT);
        defineProperty("interpretation", FieldShadowVarContainer.class,
                PERMANENT);
    }

    /**
     * This method is a callback for rhino which gets called on instantiation.
     * (virtual js constructor)
     */
    public void jsContructor() {
    }

    /**
     * Gets the current confidence.
     * @return the current confidence
     */
    public String getConfidence() {
        return confidence;
    }

    /**
     * Gets the current inputmode.
     * @return the current inputmode
     */
    public String getInputmode() {
        return inputmode;
    }

    /**
     * Sets the current inputmode.
     * @param mode the input mode.
     */
    public void setInputmode(final String mode) {
        inputmode = mode;
    }

    /**
     * Gets the current interpretation.
     * @return the current interpretation
     */
    public String getInterpretation() {
        return interpretation;
    }

    /**
     * Gets the current utterance.
     * @return the current utterance
     */
    public String getUtterance() {
        return utterance;
    }

    /**
     * Sets the confidence.
     * @param conf the new confidence
     */
    public void setConfidence(final String conf) {
        confidence = conf;
    }

    /**
     * Sets the interpretation.
     * @param inter the new interpretation
     */
    public void setInterpretation(final String inter) {
        interpretation = inter;
    }

    /**
     * Sets the utterance.
     * @param utter the new utterance
     */
    public void setUtterance(final String utter) {
        utterance = utter;
    }

    /**
     * Sets the markname.
     * @return The name of the mark.
     *
     * @since 0.6
     */
    public String getMarkname() {
        return markname;
    }

    /**
     * Sets the markname.
     * @param mark The name of the mark.
     *
     * @since 0.5
     */
    public void setMarkname(final String mark) {
        markname = mark;
    }

    /**
     * Return the name of the class. This is typically the same name as the
     * constructor.
     * @return Name of the class.
     */
    public String getClassName() {
        return FieldShadowVarContainer.class.getSimpleName();
    }
}
