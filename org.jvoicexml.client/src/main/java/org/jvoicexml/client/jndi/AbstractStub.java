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

import java.rmi.Remote;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;

import org.jvoicexml.event.ErrorEvent;

/**
 * Base class for stubs.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.5
 *
 * @param <T> Remote interface.
 */
abstract class AbstractStub<T extends Remote>
        implements Stub {
    /** The JNDI context. */
    private Context context;

    /** The skeleton for remote method calls. */
    private T skeleton;

    /**
     * Retrieves the type of the remote interface.
     * @return Type of the remote interface.
     */
    protected abstract Class<T> getRemoteClass();

    /**
     * Creates a new object.
     * @since 0.6
     */
    AbstractStub() {
        try {
            context = new InitialContext();
        } catch (javax.naming.NamingException ne) {
            ne.printStackTrace();

            context = null;
        }
    }

    /**
     * Creates a new object with the given context.
     * @param ctx The context to use.
     * @since 0.6
     */
    AbstractStub(final Context ctx) {
        context = ctx;
    }

    /**
     * Sets the JNDI context.
     * @param ctx The context to use.
     *
     * @since 0.6
     */
    public final void setContext(final Context ctx) {
        context = ctx;
    }

    /**
     * Retrieves the context to use.
     * @return The context to use.
     */
    public Context getContext() {
        return context;
    }

    /**
     * Retrieves the type of the local interface.
     * @return Type of the local interface.
     */
    protected abstract Class<?> getLocalClass();

    /**
     * {@inheritDoc}
     */
    @Override
    public final Reference getReference()
            throws NamingException {
        final Class<?> localClass = getLocalClass();

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
     * @throws NamingException 
     *          skeleton could not be found
     */
    protected final T getSkeleton(final String ...suffix)
            throws NamingException {
        if (skeleton != null) {
            return skeleton;
        }

        final Class<T> remoteClass = getRemoteClass();
        String name = remoteClass.getSimpleName();
        for (int i = 0; i < suffix.length; i++) {
            name += ".";
            name += suffix[i];
        }

        final Object remote = context.lookup(name);
        skeleton = remoteClass.cast(remote);

        return skeleton;
    }

    /**
     * Clears a known remote reference to the skeleton.
     *
     * <p>
     * This method must be called in case of a remote exception. As
     * a consequence a following method call will try to retrieve a new
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
