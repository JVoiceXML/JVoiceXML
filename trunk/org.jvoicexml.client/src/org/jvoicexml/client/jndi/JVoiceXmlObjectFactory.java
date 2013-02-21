/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/client/jndi/JVoiceXmlObjectFactory.java $
 * Version: $LastChangedRevision: 2129 $
 * Date:    $Date: 2010-04-09 11:33:10 +0200 (Fr, 09 Apr 2010) $
 * Author:  $java.LastChangedBy: schnelle $
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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

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
 * @version $Revision: 2129 $
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group - <a
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
                                    final Context context,
                                    final Hashtable<?, ?> environment)
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
     * @param className name of the class to resolve.
     * @param context the context to use.
     * @return Resolved stub, <code>null</code> if the name does not match
     *         a known stub.
     *
     * @since 0.5
     */
    private Object resolveReference(final String className,
                                    final Context context) {
        if (className.equals(
                MappedDocumentRepository.class.getName())) {
            if (context == null) {
                return new MappedDocumentRepositoryStub();
            } else {
                return new MappedDocumentRepositoryStub(context);
            }
        } else if (className.equals(JVoiceXml.class.getName())) {
            if (context == null) {
                return new JVoiceXmlStub();
            } else {
                return new JVoiceXmlStub(context);
            }
        } else if (className.equals(Session.class.getName())) {
            if (context == null) {
                return new SessionStub();
            } else {
                return new SessionStub(context);
            }
        }

        throw new IllegalArgumentException("unknown reference: '"
                + className + "'");
    }
}
