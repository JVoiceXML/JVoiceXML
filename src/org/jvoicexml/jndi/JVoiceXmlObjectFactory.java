/*
 * File:    $RCSfile: JVoiceXmlObjectFactory.java,v $
 * Version: $Revision: 1.4 $
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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.jvoicexml.ApplicationRegistry;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentRepository;

/**
 * Object factory to create the stubs on the client side.
 *
 * <p>
 * The stubs are those objects that are known in the local JNDI space.
 * They enable a remote connection via RMI to the skeleton objects.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.4 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.4
 */
public final class JVoiceXmlObjectFactory
        implements ObjectFactory {
    /**
     * Constructs a new object.
     */
    public JVoiceXmlObjectFactory() {
    }

    /**
     * {@inheritDoc}
     */
    public Object getObjectInstance(final Object obj, final Name name,
                                    final Context nameCtx,
                                    final Hashtable environment)
            throws Exception {
        if (obj instanceof Reference) {
            final Reference ref = (Reference) obj;
            final String className = ref.getClassName();

            return resolveReference(className);
        }

        return null;
    }

    /**
     * Retrives an object referencing the given class name.
     * @param className SName of the class to resolve.
     * @return Resolved stub, <code>null</code> if the name does not match
     *         a known stub.
     *
     * @since 0.5
     */
    private Object resolveReference(final String className) {
        if (className.equals(ApplicationRegistry.class.getName())) {
            return new ApplicationRegistryStub();
        } else if (className.equals(
                MappedDocumentRepository.class.getName())) {
            return new MappedDocumentRepositoryStub();
        } else if (className.equals(JVoiceXml.class.getName())) {
            return new JVoiceXmlStub();
        } else if (className.equals(Session.class.getName())) {
            return new SessionStub();
        }

        System.err.println("unknown reference: '" + className + "'");

        return null;
    }
}
