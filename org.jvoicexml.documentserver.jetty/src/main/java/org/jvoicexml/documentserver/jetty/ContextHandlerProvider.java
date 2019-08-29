package org.jvoicexml.documentserver.jetty;
/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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


import java.net.URI;
import java.util.Collection;

import org.eclipse.jetty.server.handler.ContextHandler;

/**
 * Plugin to the {@link JVoiceXmlWebServer} to provide additional
 * {@link ContextHandler}s.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public interface ContextHandlerProvider {
    /**
     * Retrieves the context handlers to use.
     * @return context handlers
     */
    Collection<ContextHandler> getContextHandlers();
    
    /**
     * Notifies the provider about the base URI of the web server.
     * @param uri base URI
     */
    void setServerUri(final URI uri);
}
