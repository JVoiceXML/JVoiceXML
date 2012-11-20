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
package org.jvoicexml.test;

import org.jvoicexml.Configuration;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.Session;
import org.jvoicexml.documentserver.JVoiceXmlDocumentServer;
import org.jvoicexml.documentserver.schemestrategy.FileSchemeStrategy;
import org.jvoicexml.documentserver.schemestrategy.HttpSchemeStrategy;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentStrategy;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.interpreter.GrammarProcessor;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.interpreter.grammar.GrammarIdentifierCentral;
import org.jvoicexml.interpreter.grammar.JVoiceXmlGrammarProcessor;
import org.jvoicexml.interpreter.grammar.identifier.SrgsXmlGrammarIdentifier;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;

/**
 * This class provides a dummy implementation for {@link JVoiceXmlCore}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class DummyJvoiceXmlCore implements JVoiceXmlCore {
    /** The document server. */
    private JVoiceXmlDocumentServer documentServer;

    /** The grammar processor. */
    private GrammarProcessor grammarProcessor;

    /**
     * {@inheritDoc}
     */
    public DocumentServer getDocumentServer() {
        if (documentServer == null) {
            documentServer = new JVoiceXmlDocumentServer();
            documentServer.addSchemeStrategy(new MappedDocumentStrategy());
            documentServer.addSchemeStrategy(new FileSchemeStrategy());
            documentServer.addSchemeStrategy(new HttpSchemeStrategy());
        }

        return documentServer;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarProcessor getGrammarProcessor() {
        if (grammarProcessor == null) {
            final JVoiceXmlGrammarProcessor processor =
                new JVoiceXmlGrammarProcessor();
            final GrammarIdentifierCentral identifier =
                new GrammarIdentifierCentral();
            identifier.addIdentifier(new SrgsXmlGrammarIdentifier());
            processor.setGrammaridentifier(identifier);
            grammarProcessor = processor;
        }

        return grammarProcessor;
    }

    /**
     * {@inheritDoc}
     */
    public Session createSession(final ConnectionInformation info)
        throws ErrorEvent {
        final ImplementationPlatform platform =
                new DummyImplementationPlatform();
        return new JVoiceXmlSession(platform, this, info);
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration getConfiguration() {
        return null;
    }

}
