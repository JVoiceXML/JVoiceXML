/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/DummyTelephonySupportFactory.java $
 * Version: $LastChangedRevision: 547 $
 * Date:    $LastChangedDate: 2007-11-05 12:28:23 +0100 (Mo, 05 Nov 2007) $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.implementation;

import org.jvoicexml.event.error.NoresourceError;

/**
 * Demo implementation of a
 * {@link org.jvoicexml.implementation.ResourceFactory} for the
 * {@link Telephony} interface.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 547 $
 * @since 0.5.5
 */
public final class DummyTelephonySupportFactory
    implements ResourceFactory<Telephony> {
    /** Number of instances that this factory will create. */
    private int instances;

    /**
     * Constructs a new object.
     */
    public DummyTelephonySupportFactory() {
    }

    /**
     * {@inheritDoc}
     */
    public Telephony createResource()
        throws NoresourceError {
        return new DummyTelephonySupport();
    }

    /**
     * Sets the number of instances that this factory will create.
     * @param number Number of instances to create.
     */
    public void setInstances(final int number) {
        instances = number;
    }

    /**
     * {@inheritDoc}
     */
    public int getInstances() {
        return instances;
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "dummy";
    }

    /**
     * {@inheritDoc}
     */
    public Class<Telephony> getResourceType() {
        return Telephony.class;
    }
}
