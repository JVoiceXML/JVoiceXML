/*
 * File:    $RCSfile: VoiceXmlInterpreter.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * VoiceXML interpreter that process VoiceXML documents retrieved from a
 * document server.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class VoiceXmlInterpreter {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(VoiceXmlInterpreter.class);

    /** Reference to the VoiceXML interpreter context. */
    private final VoiceXmlInterpreterContext context;

    /** Current VoiceXML document. */
    private VoiceXmlDocument document;

    /** The executable forms of the current VoiceXML document. */
    private Collection<ExecutableForm> forms;

    /** The next form to process. */
    private ExecutableForm nextForm;

    /**
     * Construct a new object.
     *
     * @param ctx
     *        The VoiceXML interpreter context.
     */
    public VoiceXmlInterpreter(final VoiceXmlInterpreterContext ctx) {
        context = ctx;
    }

    /**
     * Parse the given VoiceXML document.
     *
     * @param xml
     *        VoiceXML document to be parsed.
     * @return Parsed VoiceXML document.
     * @throws ParserConfigurationException
     *         Error creating the document builder.
     * @throws SAXException
     *         Error parsing the input source.
     * @throws IOException
     *         Error reading the input source.
     */
    public VoiceXmlDocument parse(final String xml)
            throws ParserConfigurationException, SAXException, IOException {
        final StringReader reader = new StringReader(xml);
        final InputSource source = new InputSource(reader);

        return new VoiceXmlDocument(source);
    }

    /**
     * Set the next VoiceXML document, to be interpreted. In general, this will
     * be the result of a <code>parse(String)</code> call.
     *
     * @param doc
     *        Next VoiceXML document.
     */
    void setDocument(final VoiceXmlDocument doc) {
        if (doc == null) {
            document = null;

            return;
        }

        document = doc;
        final Vxml vxml = document.getVxml();
        if (vxml == null) {
            return;
        }

        final ExecutableFormFactory formFactory =
                new org.jvoicexml.interpreter.form.
                JVoiceXmlExecutableFormFactory();

        forms = formFactory.getExecutableForms(vxml);

        final Iterator<ExecutableForm> iterator = forms.iterator();
        if (iterator.hasNext()) {
            nextForm = iterator.next();
        }
    }

    /**
     * Get the next form to process.
     *
     * @return Next form to be processed, <code>null</code> if there is no
     *         next form to process.
     */
    public ExecutableForm getNextForm() {
        return nextForm;
    }

    /**
     * Get the form with the given id.
     * @param id Id of the form to find.
     * @return Form with the given id, <code>null</code> if there is no form
     * with that id.
     *
     * @since 0.3
     */
    public ExecutableForm getForm(final String id) {
        for (ExecutableForm form : forms) {
            final String currentId = form.getId();
            if (id.equalsIgnoreCase(currentId)) {
                return form;
            }
        }

        return null;
    }

    /**
     * Process the given form.
     *
     * @param form
     *        The form to be processed.
     * @exception JVoiceXMLEvent
     *            Error or event processing the form.
     */
    public void processForm(final ExecutableForm form)
            throws JVoiceXMLEvent {
        // There is no next form and no next document by default.
        nextForm = null;

        final FormInterpretationAlgorithm fia =
                new FormInterpretationAlgorithm(context, this, form);

        fia.initialize();
        fia.mainLoop();
    }
}
