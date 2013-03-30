/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/event/plain/jvxml/SubdialogResultEvent.java $
 * Version: $LastChangedRevision: 2493 $
 * Date:    $Date: 2011-01-10 04:25:46 -0600 (lun, 10 ene 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.event.plain.jvxml;

/**
 * Result of an <code>&lt;subdialog&gt;</code> call..
 *
 * @author Dirk Schnelle-walka
 * @version $Revision: 2493 $
 * @since 0.7.4
 */
public final class SubdialogResultEvent
        extends AbstractInputEvent {
    /** The serial version UID. */
    private static final long serialVersionUID = 777914058767858586L;

    /** The detail message. */
    public static final String EVENT_TYPE =
        SubdialogResultEvent.class.getName();

    /** The result of the subdialog call. */
    private final Object result;

    /**
     * Constructs a new object.
     * @param methodResult result of the method call.
     */
    public SubdialogResultEvent(final Object methodResult) {
        result = methodResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getInputResult() {
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }
}
