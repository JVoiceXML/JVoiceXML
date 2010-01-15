/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/jndi/client/JVoiceXmlStub.java $
 * Version: $LastChangedRevision: 202 $
 * Date:    $Date: 2007-02-01 10:09:55 +0100 (Do, 01 Feb 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.client.jndi;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;

import org.jvoicexml.JVoiceXml;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.Session;
import org.jvoicexml.client.BasicRemoteClient;
import org.jvoicexml.client.TcpUriFactory;
import org.jvoicexml.event.ErrorEvent;

/**
 * Stub for <code>JVoiceXml</code>.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 202 $
 * @since 0.4
 * @see org.jvoicexml.JVoiceXml
 */
public final class JVoiceXmlStub
        extends AbstractStub<RemoteJVoiceXml>
        implements JVoiceXml {
    /**
     * Constructs a new object.
     */
    public JVoiceXmlStub() {
    }

    /**
     * Constructs a new object.
     * @param context The context to use.
     * @since 0.6
     */
    public JVoiceXmlStub(final Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    public String getStubName() {
        return JVoiceXml.class.getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<RemoteJVoiceXml> getRemoteClass() {
        return RemoteJVoiceXml.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getLocalClass() {
        return JVoiceXml.class;
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        final RemoteJVoiceXml jvxml = getSkeleton();
        try {
            return jvxml.getVersion();
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();

            re.printStackTrace();

            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Session createSession(final RemoteClient client)
            throws ErrorEvent {
        final RemoteJVoiceXml jvxml = getSkeleton();

        Session session;
        try {
            if (client instanceof BasicRemoteClient) {
                final Context context = getContext();
                final Map<?, ?> env = context.getEnvironment();
                final BasicRemoteClient basic = (BasicRemoteClient) client;
                if (basic.getCalledDevice() == null) {
                    final Object prov = env.get(Context.PROVIDER_URL);
                    basic.setCalledDevice(new URI(prov.toString()));
                }
                if (basic.getCallingDevice() == null) {
                    final InetAddress localhost = InetAddress.getLocalHost();
                    final URI uri = TcpUriFactory.createUri(localhost);
                    basic.setCallingDevice(uri);
                }
                if (basic.getProtocolName() == null) {
                    basic.setProtocolName("rmi");
                }
            }
            session = jvxml.createSession(client);
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();
            session = null;

            final ErrorEvent event = getErrorEvent(re);
            if (event == null) {
                re.printStackTrace();
            } else {
                throw event;
            }
        } catch (NamingException e) {
            clearSkeleton();
            session = null;
            e.printStackTrace();
        } catch (URISyntaxException e) {
            clearSkeleton();
            session = null;
            e.printStackTrace();
        } catch (UnknownHostException e) {
            clearSkeleton();
            session = null;
            e.printStackTrace();
        }

        // Reuse the context on the client.
        if (session instanceof SessionStub) {
            final SessionStub sessionStub = (SessionStub) session;
            final Context context = getContext();
            sessionStub.setContext(context);
        }

        return session;
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        final RemoteJVoiceXml jvxml = getSkeleton();

        try {
            jvxml.shutdown();
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();

            re.printStackTrace();
        }
    }
}
