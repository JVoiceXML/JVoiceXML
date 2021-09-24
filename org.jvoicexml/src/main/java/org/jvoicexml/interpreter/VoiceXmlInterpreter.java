/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2018 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.event.EventBus;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.event.plain.jvxml.GotoNextDocumentEvent;
import org.jvoicexml.event.plain.jvxml.GotoNextFormEvent;
import org.jvoicexml.event.plain.jvxml.SubmitEvent;
import org.jvoicexml.profile.Profile;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * VoiceXML interpreter that process VoiceXML documents retrieved from a
 * document server.
 *
 * @author Dirk Schnelle-Walka
 */
public final class VoiceXmlInterpreter {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(VoiceXmlInterpreter.class);

    /** Reference to the VoiceXML interpreter context. */
    private final VoiceXmlInterpreterContext context;

    /** The current FIA. */
    private FormInterpretationAlgorithm fia;

    /** Current VoiceXML document. */
    private VoiceXmlDocument document;

    /** The default locale of the document. */
    private Locale documentLocale;

    /** The executable dialogs of the current VoiceXML document. */
    private Collection<Dialog> dialogs;

    /**
     * The next dialog to process, <code>null</code> if there is no next dialog.
     */
    private Dialog nextDialog;

    /** The interpreter state. */
    private InterpreterState state;

    /**
     * Constructs a new object.
     *
     * @param ctx
     *            The VoiceXML interpreter context.
     */
    public VoiceXmlInterpreter(final VoiceXmlInterpreterContext ctx) {
        context = ctx;
    }

    /**
     * Retrieves the current FIA.
     * 
     * @return the FIA while the interpreter is processing, <code>null</code>
     *         otherwise.
     * @since 0.7
     */
    public FormInterpretationAlgorithm getFormInterpretationAlgorithm() {
        return fia;
    }

    /**
     * Retrieves the interpreter state.
     * 
     * @return the interpreter state.
     * @since 0.6
     */
    public InterpreterState getState() {
        return state;
    }

    /**
     * Sets the interpreter state.
     * 
     * @param newState
     *            the new interpreter state.
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
     *            the next VoiceXML document.
     * @param startDialog
     *            the dialog where to start interpretation
     * @param configuration
     *            the configuration to use to load further components
     * @throws ConfigurationException
     *             if the needed configuration parts could not be loaded
     */
    void setDocument(final VoiceXmlDocument doc, final String startDialog,
            final Configuration configuration) throws ConfigurationException {
        if (doc == null) {
            document = null;
            return;
        }
        document = doc;

        final Vxml vxml = document.getVxml();
        if (vxml == null) {
            LOGGER.warn("no vxml tag found in '" + doc + "'");
            return;
        }

        // Determine all dialogs of the current document.
        final DialogFactory factory = configuration
                .loadObject(DialogFactory.class);
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
     * 
     * @param id
     *            Id of the form to find.
     * @return form with the given id, <code>null</code> if there is no dialog
     *         with that id.
     *
     * @since 0.3
     */
    public Dialog getDialog(final String id) {
        if (id == null) {
            LOGGER.warn("unable to get form with a null id");
            return null;
        }
        if (dialogs == null) {
            LOGGER.warn("dialogs not initialized. can not determine dialog"
                    + " with id '" + id + "'");
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
     *            the dialog to be processed.
     * @param parameters
     *            passed parameters when executing this dialog
     * @exception JVoiceXMLEvent
     *                Error or event processing the dialog.
     */
    public void process(final Dialog dialog,
            final Map<String, Object> parameters) throws JVoiceXMLEvent {
        // There is no next dialog by default.
        nextDialog = null;
        fia = new FormInterpretationAlgorithm(context, this, dialog);
        final EventBus eventbus = context.getEventBus();
        final HangupEventHandler hangupHandler = new HangupEventHandler(fia);
        eventbus.subscribe(ConnectionDisconnectHangupEvent.EVENT_TYPE,
                hangupHandler);

        // Collect dialog level catches.
        final EventHandler eventHandler = context.getEventHandler();
        eventHandler.collect(context, this, dialog);

        final JVoiceXmlSession session = (JVoiceXmlSession) context
                .getSession();
        final Profile profile = session.getProfile();
        // Start the fia.
        try {
            try {
                fia.initialize(profile, parameters);
                fia.mainLoop();
            } catch (GotoNextFormEvent | GotoNextDocumentEvent | SubmitEvent e) {
                throw e;
            } catch (JVoiceXMLEvent event) {
                eventbus.publish(event);
            }
        } finally {
            fia = null;
            eventbus.subscribe(ConnectionDisconnectHangupEvent.EVENT_TYPE,
                    hangupHandler);
        }
    }

    /**
     * Checks, if the interpreter is in the final processing state.
     * 
     * @return <code>true</code> if the interpreter is in the final processing
     *         state.
     */
    public boolean isInFinalProcessingState() {
        return state == InterpreterState.FINALPROCESSING;
    }

    /**
     * Retrieves the language of the current VoiceXML document.
     * <p>
     * If the language is not set, the system's default locale is used by
     * calling <code>Locale.getDefault()</code>.
     * </p>
     * 
     * @return language of the VoiceXML document, <code>null</code> if there is
     *         no document.
     * @since 0.7.3
     */
    public Locale getLanguage() {
        if (documentLocale != null) {
            return documentLocale;
        }
        if (document == null) {
            return null;
        }
        final Vxml vxml = document.getVxml();
        final Locale locale = vxml.getXmlLangObject();
        if (locale != null) {
            documentLocale = locale;
        } else {
            documentLocale = Locale.getDefault();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("document default language is " + documentLocale);
        }
        return documentLocale;
    }
}
