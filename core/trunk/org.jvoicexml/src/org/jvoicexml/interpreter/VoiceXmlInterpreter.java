/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jvoicexml.Configurable;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * VoiceXML interpreter that process VoiceXML documents retrieved from a
 * document server.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class VoiceXmlInterpreter implements Configurable {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(VoiceXmlInterpreter.class);

    /** Reference to the VoiceXML interpreter context. */
    private final VoiceXmlInterpreterContext context;

    /** The current FIA. */
    private FormInterpretationAlgorithm fia;

    /** Current VoiceXML document. */
    private VoiceXmlDocument document;

    /** The executable dialogs of the current VoiceXML document. */
    private Collection<Dialog> dialogs;

    /**
     * The next dialog to process, <code>null</code> if there is no next dialog.
     */
    private Dialog nextDialog;

    /** The interpreter state. */
    private InterpreterState state;

    /**
     * The interpreter has entered the final processing state.
     *
     * <p>
     * Since this is not a valid interpreter state, this state has to be
     * handeled seperately.
     * </p>
     */
    private boolean finalProcessingState;

    /** The tag initialization factory. */
    private InitializationTagStrategyFactory initTagFactory;

    /**
     * Constructs a new object.
     *
     * @param ctx
     *        The VoiceXML interpreter context.
     * @exception ConfigurationException
     *        error configuring.
     */
    public VoiceXmlInterpreter(final VoiceXmlInterpreterContext ctx) {
        context = ctx;
    }

    /**
     * {@inheritDoc}
     * 
     * Loads the {@link InitializationTagStrategyFactory}.
     */
    @Override
    public void init(final Configuration configuration)
        throws ConfigurationException {
        initTagFactory = configuration.loadObject(
                InitializationTagStrategyFactory.class);
    }

    /**
     * Retrieves the current FIA.
     * @return the FIA while the interpreter is processing, <code>null</code>
     *         otherwise.
     * @since 0.7
     */
    public FormInterpretationAlgorithm getFormInterpretationAlgorithm() {
        return fia;
    }

    /**
     * Retrieves the interpreter state.
     * @return the interpreter state.
     * @since 0.6
     */
    public InterpreterState getState() {
        return state;
    }

    /**
     * Sets the interpreter state.
     * @param newState the new interpreter state.
     * @since 0.6
     */
    public void setState(final InterpreterState newState) {
        if (state == newState) {
            return;
        }

        state = newState;

        LOGGER.info("entered state " + state);
    }

    /**
     * Sets the next VoiceXML document, to be interpreted and initializes the
     * interpreter.
     *
     * <p>
     * This method also analyzes the dialogs of the given document and
     * determines the first dialog to process according to the document order.
     * </p>
     *
     * @param doc
     *        the next VoiceXML document.
     * @param startDialog
     *        the dialog where to start interpretation.
     */
    void setDocument(final VoiceXmlDocument doc, final String startDialog) {
        document = doc;
        if (document == null) {
            return;
        }

        final Vxml vxml = document.getVxml();
        if (vxml == null) {
            return;
        }

        // Determine all dialogs of the current document.
        final DialogFactory factory =
                new org.jvoicexml.interpreter.dialog.JVoiceXmlDialogFactory();
        dialogs = factory.getDialogs(vxml);

        if (startDialog == null) {
            // Check if there is at least one dialog and take this one as the
            // next dialog.
            final Iterator<Dialog> iterator = dialogs.iterator();
            if (iterator.hasNext()) {
                nextDialog = iterator.next();
            }
        } else {
            nextDialog = getDialog(startDialog);
        }
        if (nextDialog == null) {
            if (startDialog == null) {
                LOGGER.info("the document does not contain any dialogs");
            } else {
                LOGGER.warn("start dialog '" + startDialog
                        + "' not found in document!");
            }
        } else {
            LOGGER.info("interpreter starts with dialog '" + nextDialog.getId()
                    + "'");
        }
    }

    /**
     * Retrieves the next dialog to process.
     *
     * @return next dialog to be processed, <code>null</code> if there is no
     *         next dialog to process.
     */
    public Dialog getNextDialog() {
        return nextDialog;
    }

    /**
     * Retrieves the dialog with the given id.
     * @param id Id of the form to find.
     * @return form with the given id, <code>null</code> if there is no dialog
     * with that id.
     *
     * @since 0.3
     */
    public Dialog getDialog(final String id) {
        if (id == null) {
            LOGGER.warn("unable to get form with a null id");
            return null;
        }
        if (dialogs == null) {
            LOGGER.warn(
                    "dialogs not initialized. can not determine dialog with "
                    + "id '" + id + "'");
            return null;
        }

        for (Dialog dialog : dialogs) {
            final String currentId = dialog.getId();
            if (id.equalsIgnoreCase(currentId)) {
                return dialog;
            }
        }

        return null;
    }

    /**
     * Process the given dialog.
     *
     * @param dialog
     *        the dialog to be processed.
     * @exception JVoiceXMLEvent
     *            Error or event processing the dialog.
     */
    public void process(final Dialog dialog)
            throws JVoiceXMLEvent {
        // There is no next dialog by default.
        nextDialog = null;
        fia =  new FormInterpretationAlgorithm(context, this, dialog);

        // Collect dialog level catches.
        final EventHandler eventHandler = context.getEventHandler();
        eventHandler.collect(context, this, dialog);

        // Start the fia.
        try {
            try {
                fia.initialize(initTagFactory);
                context.finalizedInitialization();
            } catch (JVoiceXMLEvent event) {
                fia.processEvent(event);
            }
            fia.mainLoop();
        } finally {
            fia = null;
        }
    }

    /**
     * Under certain circumstances (in particular, while the VoiceXML
     * interpreter is processing a disconnect event) the interpreter may
     * continue executing in the final processing state after there is no
     * longer a connection to allow the interpreter to interact with the end
     * user. The purpose of this state is to allow the VoiceXML application to
     * perform any necessary final cleanup, such as submitting information to
     * the application server.
     */
    public void enterFinalProcessingState() {
        LOGGER.info("entered final processing state");

        finalProcessingState = true;
    }

    /**
     * Checks, if the interpreter is in the final processing state.
     * @return <code>true</code> if the interpreter is in the final processing
     * state.
     */
    public boolean isInFinalProcessingState() {
        return finalProcessingState;
    }

    /**
     * Retrieves the language of the current VoiceXML document.
     * <p>
     * If the language is not set, the system's default locale is used
     * by calling <code>Locale.getDefault()</code>.
     * </p>
     * @return language of the VoiceXML document, <code>null</code> if
     * there is no document.
     * @since 0.7.3
     */
    public Locale getLanguage() {
        if (document == null) {
            return null;
        }
        final Vxml vxml = document.getVxml();
        final Locale locale = vxml.getXmlLangObject();
        if (locale != null) {
            return locale;
        }
        return Locale.getDefault();
    }
}
