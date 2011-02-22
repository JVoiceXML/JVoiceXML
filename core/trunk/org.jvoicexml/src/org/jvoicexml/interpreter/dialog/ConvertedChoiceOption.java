/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter.dialog;

import java.net.URI;
import java.util.Collection;

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.vxml.AcceptType;
import org.jvoicexml.xml.vxml.Field;

/**
 * A converted choice option.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.5
 */
public final class ConvertedChoiceOption implements Cloneable {
    /** The created field. */
    private final Field field;

    /** All possible accepted inputs. */
    private Collection<String> acceptedInputs;

    /** The text of the choice node. */
    private String text;

    /** The DTMF sequence of a choice node. */
    private String dtmf;

    /** The mode type. */
    private ModeType mode;

    /** The accept type. */
    private AcceptType accept;

    /** The created grammar. */
    private Grammar grammar;

    /** The URI of next dialog or document. */
    private URI next;

    /** An event to be thrown instead of a next. */
    private JVoiceXMLEvent event;

    /**
     * Constructs a new object.
     * @param fld the created field
     */
    public ConvertedChoiceOption(final Field fld) {
        field = fld;
    }

    /**
     * Sets the mode type.
     * @return the mode
     */
    public ModeType getMode() {
        return mode;
    }

    /**
     * Retrieves the mode type.
     * @param modeType the mode to set
     */
    public void setMode(final ModeType modeType) {
        mode = modeType;
    }

    /**
     * Retrieves the accepted inputs.
     * @return the acceptedInputs
     */
    public Collection<String> getAcceptedInputs() {
        return acceptedInputs;
    }

    /**
     * Set the accepted inputs of the grammar.
     * @param inputs the acceptedInputs to set
     */
    public void setAcceptedInputs(final Collection<String> inputs) {
        acceptedInputs = inputs;
    }

    /**
     * Adds the given input to the list of accepted inputs.
     * @param input the input to accept
     */
    public void addAcceptedInput(final String input) {
        if (acceptedInputs == null) {
            acceptedInputs = new java.util.ArrayList<String>();
        }
        acceptedInputs.add(input);
    }

    /**
     * Retrieves the accept type.
     * @return the accept
     */
    public AcceptType getAccept() {
        return accept;
    }

    /**
     * Sets the accept type.
     * @param acceptType the accept to set
     */
    public void setAccept(final AcceptType acceptType) {
        accept = acceptType;
    }

    /**
     * Retrieves the grammar object.
     * @return the grammar
     */
    public Grammar getGrammar() {
        return grammar;
    }

    /**
     * Sets the grammar object.
     * @param gram the grammar to set
     */
    public void setGrammar(final Grammar gram) {
        grammar = gram;
        field.appendChild(grammar);
    }

    /**
     * Creates an empty grammar node within the field.
     * @return the created grammar node
     * @since 0.7.5
     */
    public Grammar createGrammarNode() {
        return field.appendChild(Grammar.class);
    }

    /**
     * Retrieves the URI of next dialog or document.
     * @return the next
     */
    public URI getNext() {
        return next;
    }

    /**
     * Sets the URI of next dialog or document.
     * @param uri the next to set
     */
    public void setNext(final URI uri) {
        next = uri;
    }

    /**
     * Retrieves the event to be thrwon instead of a next.
     * @return the event
     */
    public JVoiceXMLEvent getEvent() {
        return event;
    }

    /**
     * Sets the event to be thrown instead of a next.
     * @param e the event to set
     */
    public void setEvent(final JVoiceXMLEvent e) {
        event = e;
    }

    /**
     * Retrieves the text within the choice node.
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text that is contained in the choice node.
     * @param txt the text to set
     */
    public void setText(final String txt) {
        text = txt;
    }

    /**
     * Retrieves the DTMF sequence of a choice node.
     * @return the DTMF sequence
     */
    public String getDtmf() {
        return dtmf;
    }

    /**
     * Sets the DTMF sequence that is set in the choice node.
     * @param value the DTMF sequence to set
     */
    public void setDtmf(final String value) {
        dtmf = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ConvertedChoiceOption clone() {
        try {
            return (ConvertedChoiceOption) super.clone();
        } catch (CloneNotSupportedException ignore) {
            return null;
        }
    }
}
