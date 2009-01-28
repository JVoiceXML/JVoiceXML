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
 * Component that provides a cointainer for the shadowed variables of a field.
 * Currently the shadowed vars are hardcoded into this class as attributes,
 * this is going to be changed.
 * See http://www.w3.org/TR/voicexml20/#dml2.3.1 for details.
 *
 * @author Torben Hardt
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.3.1
 */
public final class FieldShadowVarContainer
        extends ScriptableObject {

    /** needed for the interface ScriptableObject. */
    static final long serialVersionUID = 6459275291786741159L;

    /** The field's utterance. */
    private String utterance;

    /** The field's inputmode. */
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
    }

    /**
     * this method is a callback for rhino which gets called on instantiation.
     * (virtual js constructor)
     */
    public void jsContructor() {
    }

    /**
     * js (rhino) callback to provide javabean-like interface.
     * @return the current utterance
     */
    public String jsGet_utterance() {
        return utterance;
    }

    /**
     * js (rhino) callback to provice javabean-like interface.
     * @return the current confidence
     */
    public String jsGet_confidence() {
        return confidence;
    }

    /**
     * js (rhino) callback to provide javabean-like interface.
     * @return the current utterance
     *
     * @since 0.5
     */
    public String jsGet_markname() {
        return markname;
    }

    /**
     * js (rhino) callback to provide javabean-like interface.
     * @return the current inputmode
     */
    public String jsGet_inputmode() {
        return inputmode;
    }

    /**
     * js (rhino) callback to provide javabean-like interface.
     * sets the inputmode
     * @param input the new inputmode
     */
    public void jsSet_inputmode(final String input) {
        inputmode = input;
    }

    /**
     * js (rhino) callback to provide javabean-like interface.
     * @return the current interpretation
     */
    public String jsSet_interpretation() {
        return interpretation;
    }

    /**
     * js (rhino) callback to provide javabean-like interface.
     * sets the interpretation
     * @param inter the new interpretation
     */
    public void jsSet_interpretation(final String inter) {
        interpretation = inter;
    }

    /**
     * gets the current confidence.
     * @return the current confidence
     */
    public String getConfidence() {
        return confidence;
    }

    /**
     * gets the current inputmode.
     * @return the current inputmode
     */
    public String getInputmode() {
        return inputmode;
    }

    /**
     * gets the current interpretation.
     * @return the current interpretation
     */
    public String getInterpretation() {
        return interpretation;
    }

    /**
     * gets the current utterance.
     * @return the current utterance
     */
    public String getUtterance() {
        return utterance;
    }

    /**
     * sets the confidence.
     * @param conf the new confidence
     */
    public void setConfidence(final String conf) {
        confidence = conf;
    }

    /**
     * sets the interpretation.
     * @param inter the new interpretation
     */
    public void setInterpretation(final String inter) {
        interpretation = inter;
    }

    /**
     * sets the utterance.
     * @param utter the new utterance
     */
    public void setUtterance(final String utter) {
        utterance = utter;
    }

    /**
     * sets the markname.
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
