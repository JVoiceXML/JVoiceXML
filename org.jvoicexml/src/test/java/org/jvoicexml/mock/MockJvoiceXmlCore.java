/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.mock;

import org.jvoicexml.Configuration;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.ImplementationPlatformFactory;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.documentserver.JVoiceXmlDocumentServer;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentStrategy;
import org.jvoicexml.documentserver.schemestrategy.ResourceDocumentStrategy;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.interpreter.GrammarProcessor;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.interpreter.grammar.GrammarIdentifierCentral;
import org.jvoicexml.interpreter.grammar.JVoiceXmlGrammarProcessor;
import org.jvoicexml.interpreter.grammar.identifier.SrgsXmlGrammarIdentifier;
import org.jvoicexml.mock.implementation.MockImplementationPlatform;
import org.jvoicexml.profile.Profile;
import org.jvoicexml.profile.SsmlParsingStrategyFactory;
import org.mockito.Mockito;

/**
 * This class provides a dummy implementation for {@link JVoiceXmlCore}.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.6
 */
public final class MockJvoiceXmlCore implements JVoiceXmlCore {
    /** The document server. */
    private DocumentServer documentServer;

    /** The grammar processor. */
    private GrammarProcessor grammarProcessor;

    /**
     * Sets the document server.
     * @param server the document server
     * @since 0.7.9
     */
    public void setDocumentServer(final DocumentServer server) {
        documentServer = server;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DocumentServer getDocumentServer() {
        if (documentServer == null) {
            documentServer = new JVoiceXmlDocumentServer();
            ((JVoiceXmlDocumentServer)documentServer).addSchemeStrategy(
                    new MappedDocumentStrategy());
            ((JVoiceXmlDocumentServer)documentServer).addSchemeStrategy(
                    new ResourceDocumentStrategy());
            try {
                documentServer.start();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return documentServer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarProcessor getGrammarProcessor() {
        if (grammarProcessor == null) {
            final JVoiceXmlGrammarProcessor processor = new JVoiceXmlGrammarProcessor();
            final GrammarIdentifierCentral identifier = new GrammarIdentifierCentral();
            identifier.addIdentifier(new SrgsXmlGrammarIdentifier());
            processor.setGrammaridentifier(identifier);
            grammarProcessor = processor;
        }

        return grammarProcessor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session createSession(final ConnectionInformation info,
            final SessionIdentifier id)
            throws ErrorEvent {
        final ImplementationPlatform platform = new MockImplementationPlatform();
        final Profile profile = Mockito.mock(Profile.class);
        final SsmlParsingStrategyFactory factory = Mockito
                .mock(SsmlParsingStrategyFactory.class);
        Mockito.when(profile.getSsmlParsingStrategyFactory()).thenReturn(
                factory);

        return new JVoiceXmlSession(platform, this, info, profile, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVersion() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration getConfiguration() {
        return null;
    }

    @Override
    public ImplementationPlatformFactory getImplementationPlatformFactory() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public Session createSession(ConnectionInformation info,
            ImplementationPlatform platform, SessionIdentifier id)
            throws ErrorEvent {
        // TODO Auto-generated method stub
        return null;
    }

}
