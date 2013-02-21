/*
 * File:    $RCSfile: JVoiceXmlStub.java,v $
 * Version: $Revision: 1.8 $
 * Date:    $Date: 2006/05/19 09:33:19 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.jndi;

import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.event.error.ErrorEvent;
import org.jvoicexml.implementation.CallControl;

/**
 * Stub for <code>JVoiceXml</code>.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.8 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
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
    protected Class getLocalClass() {
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
    public Session createSession(final CallControl call, final String id)
            throws ErrorEvent {
        final RemoteJVoiceXml jvxml = getSkeleton();

        Session session;

        try {
            session = jvxml.createSession(call, id);
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();
            session = null;

            final ErrorEvent event = getErrorEvent(re);
            if (event == null) {
                re.printStackTrace();
            } else {
                throw event;
            }
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
