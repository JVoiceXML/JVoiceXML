/*
 * File:    $RCSfile: UnsupportedElementError.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.event.error;

import org.jvoicexml.event.ErrorEvent;


/**
 * The platform does not support the given element, where element is a VoiceXML
 * element defined in this specification. For instance, if a platform does not
 * implement <code>&lt;transfer&gt;</code>, it must throw
 * <code>error.unsupported.transfer</code>. This allows an author to use
 * event handling to adapt to different platform capabilities.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
@SuppressWarnings("serial")
public class UnsupportedElementError
        extends ErrorEvent {
    /** The detail message. */
    private static final String EVENT_TYPE = "error.unsupported";

    /** The unsupported element. */
    private final String element;

    /**
     * Constructs a new event with the event type as its detail message. The
     * cause is not initialized.
     *
     * <p>
     * The <code>unsupported</code> element is used to construct the event type.
     * </p>
     *
     * @param unsupported
     *        The name of the unsupported element.
     *
     * @see #getEventType()
     */
    public UnsupportedElementError(final String unsupported) {
        super();

        element = unsupported;
    }

    /**
     * Constructs a new event with the specified detail message. the given
     * detail message is expanded to the form
     * <code>&lt;EVENT_TYPE&gt>: &lt;message&gt;</code>.
     * The cause is not initialized.
     *
     * <p>
     * The <code>unsupported</code> element is used to construct the event type.
     * </p>
     *
     * @param unsupported
     *        The name of the unsupported element.
     * @param message
     *        The detail message.
     *
     * @see #getEventType()
     */
    public UnsupportedElementError(final String unsupported,
            final String message) {
        super(message);

        element = unsupported;
    }

    /**
     * Constructs a new event with the specified cause and a detail message of
     * <code>(cause==null ? getEventType() : cause.toString())</code> (which
     * typically contains the class and detail message of cause).
     *
     * <p>
     * The <code>unsupported</code> element is used to construct the event type.
     * </p>
     *
     * @param unsupported
     *        The name of the unsupported element.
     * @param cause
     *        The cause.
     *
     * @see #getEventType()
     */
    public UnsupportedElementError(final String unsupported,
            final Throwable cause) {
        super(cause);

        element = unsupported;
    }

    /**
     * Constructs a new event with the specified detail message and cause.
     *
     * <p>
     * The <code>unsupported</code> element is used to construct the event type.
     * </p>
     *
     * @param unsupported
     *        The name of the unsupported element.
     * @param message
     *        The detail message.
     * @param cause
     *        The cause.
     */
    public UnsupportedElementError(final String unsupported,
            final String message, final Throwable cause) {
        super(message, cause);

        element = unsupported;
    }

    /**
     * Retrieve the name of the unsupported element.
     *
     * @return name of the unsupported element.
     */
    public final String getElement() {
        return element;
    }

    /**
     * Constructs an event type with the event type as specification.
     * The event type has the following form
     *
     * <p>
     * <code>
     * &lt;EVENT_TYPE&gt;.&lt;element&gt;
     * </code>
     * </p>
     *
     * @see #EVENT_TYPE
     * @see #getElement()
     *
     * {@inheritDoc}
     */
    @Override
    public final String getEventType() {
        final StringBuilder event = new StringBuilder();

        event.append(EVENT_TYPE);
        event.append('.');
        event.append(element);

        return event.toString();
    }
}
