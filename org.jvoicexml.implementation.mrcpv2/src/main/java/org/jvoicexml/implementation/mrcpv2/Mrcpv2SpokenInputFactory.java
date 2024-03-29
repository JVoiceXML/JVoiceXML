/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.mrcpv2;

import java.util.List;

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.grammar.GrammarParser;
import org.jvoicexml.xml.srgs.GrammarType;
import org.speechforge.cairo.client.SessionManager;

/**
 * Implementation of a {@link org.jvoicexml.implementation.ResourceFactory} for
 * the {@link SpokenInput} based on MRCPv2.
 * 
 * @author Spencer Lord
 * @author Dirk Schnelle-Walka
 * @author Patrick L. Lange
 * @since 0.7
 */
public final class Mrcpv2SpokenInputFactory
        implements ResourceFactory<SpokenInput> {
    /** Number of instances that this factory will create. */
    private int instances;

    /** The configured grammar parser. */
    private List<GrammarType> grammarTypes;

    /** The type of resource that this factory will create. */
    private final String type;

    /** SIP Service used for MRCP channel config and control. */
    private SessionManager sessionManager;

    /**
     * Constructs a new object.
     */
    public Mrcpv2SpokenInputFactory() {
        type = "mrcpv2";
    }

    /**
     * {@inheritDoc}
     */
    public SpokenInput createResource() throws NoresourceError {
        final Mrcpv2SpokenInput input = new Mrcpv2SpokenInput();
        input.setSessionManager(sessionManager);
        input.setGrammarTypes(grammarTypes);

        return input;
    }

    /**
     * Sets the number of instances that this factory will create.
     * 
     * @param number
     *            Number of instances to create.
     */
    public void setInstances(final int number) {
        instances = number;
    }

    /**
     * {@inheritDoc}
     */
    public int getInstances() {
        return instances;
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public Class<SpokenInput> getResourceType() {
        return SpokenInput.class;
    }

    /**
     * @return the sipService
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * Sets the session manager.
     * 
     * @param manager
     *            the session manager
     */
    public void setSessionManager(final SessionManager manager) {
        sessionManager = manager;
    }

    /**
     * Sets the grammar types that are supported by this factory.
     * @param types the grammar types to set
     */
    public void setGrammarTypes(final List<GrammarType> types) {
        grammarTypes = types;
    }
}
