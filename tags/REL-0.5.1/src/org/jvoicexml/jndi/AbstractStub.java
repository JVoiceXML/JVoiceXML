/*
 * File:    $RCSfile: AbstractStub.java,v $
 * Version: $Revision: 1.3 $
 * Date:    $Date: 2006/06/07 07:40:49 $
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

import java.rmi.Remote;

import javax.naming.NamingException;
import javax.naming.Reference;

import org.jvoicexml.event.error.ErrorEvent;

/**
 * Base class for stubs.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.3 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5
 *
 * @param <T> Remote interface.
 */
public abstract class AbstractStub<T extends Remote>
        implements Stub {
    /** The skeleton for remote method calls. */
    private T skeleton;

    /**
     * Retrieves the type of the remote interface.
     * @return Type of the remote interface.
     */
    protected abstract Class<T> getRemoteClass();

    /**
     * Retrieves the type of the local interface.
     * @return Type of the local interface.
     */
    protected abstract Class getLocalClass();

    /**
     * {@inheritDoc}
     */
    public final Reference getReference()
            throws NamingException {
        final Class localClass = getLocalClass();

        return new Reference(localClass.getName(),
                             JVoiceXmlObjectFactory.class.getName(), null);
    }


    /**
     * Lazy instantiation of the skeleton.
     *
     * <p>
     * This method tries to lookup the skeleton with the following
     * name<br>
     * <code>
     * getRemoteClass().getSimpleName().suffix[0].suffix[1]...<br>
     * </code>
     * If no suffixes are given the simple class name is used instead.
     * </p>
     *
     *
     * @param suffix List of suffixes to be appended.
     * @return The skeleton to use for remote method calls,
     *         <code>null</code> in case of an error.
     */
    protected final T getSkeleton(final String ...suffix) {
        if (skeleton != null) {
            return skeleton;
        }

        final Class<T> remoteClass = getRemoteClass();

        try {
            String name = remoteClass.getSimpleName();
            for (int i = 0; i < suffix.length; i++) {
                name += ".";
                name += suffix[i];
            }

            final Object remote = java.rmi.Naming.lookup(name);
            skeleton = remoteClass.cast(remote);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return skeleton;
    }

    /**
     * Clears a known remote reference to the skeleton.
     *
     * <p>
     * This method must be called in case of a remote exception. As
     * a consequence a following method call will try to retreive a new
     * fresh reference to the skeleton.
     * </p>
     */
    protected final void clearSkeleton() {
        skeleton = null;
    }

    /**
     * Digs into the throwable and tries to find an <code>ErrorEvent</code>
     * in the root cause hierarchy.
     *
     * @param throwable The throwable to examine.
     * @return Detected <code>ErrorEvent</code> or <code>null</code> if there
     *         is none.
     */
    protected final ErrorEvent getErrorEvent(final Throwable throwable) {
        if (throwable == null) {
            return null;
        }

        if (throwable instanceof ErrorEvent) {
            return (ErrorEvent) throwable;
        }

        final Throwable cause = throwable.getCause();

        return getErrorEvent(cause);
    }
}