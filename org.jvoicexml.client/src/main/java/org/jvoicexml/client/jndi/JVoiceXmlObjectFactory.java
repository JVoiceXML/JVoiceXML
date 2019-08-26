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

package org.jvoicexml.client.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * Object factory to create the stubs on the client side.
 * 
 * <p>
 * The stubs are those objects that are known in the local JNDI space. They
 * enable a remote connection via RMI to the skeleton objects.
 * </p>
 * 
 * @author Dirk Schnelle-Walka
 * 
 * @since 0.4
 */
public final class JVoiceXmlObjectFactory implements ObjectFactory {
    /**
     * Constructs a new object.
     */
    public JVoiceXmlObjectFactory() {
    }

    /**
     * {@inheritDoc}
     */
    public Object getObjectInstance(final Object obj, final Name name,
            final Context context, final Hashtable<?, ?> environment)
            throws Exception {
        if (obj instanceof Reference) {
            final Reference ref = (Reference) obj;
            final String className = ref.getClassName();

            return resolveReference(className, context);
        }

        return null;
    }

    /**
     * Retrieves an object referencing the given class name.
     * 
     * @param className
     *            name of the class to resolve.
     * @param context
     *            the context to use.
     * @return Resolved stub, <code>null</code> if the name does not match a
     *         known stub.
     * 
     * @since 0.5
     */
    private Object resolveReference(final String className,
            final Context context) {
        if (className.equals(RemoteMappedDocumentRepository.class.getName())) {
            if (context == null) {
                return new MappedDocumentRepositoryStub();
            } else {
                return new MappedDocumentRepositoryStub(context);
            }
        } else if (className.equals(RemoteJVoiceXml.class.getName())) {
            if (context == null) {
                return new JVoiceXmlStub();
            } else {
                return new JVoiceXmlStub(context);
            }
        } else if (className.equals(RemoteSession.class.getName())) {
            if (context == null) {
                return new SessionStub();
            } else {
                return new SessionStub(context);
            }
        } else if (className.equals(RemoteApplication.class.getName())) {
            if (context == null) {
                return new ApplicationStub();
            } else {
                return new ApplicationStub(context);
            }
        }

        throw new IllegalArgumentException("unknown reference: '" + className
                + "'");
    }
}
