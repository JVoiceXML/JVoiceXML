/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.implementation.jtapi/src/org/jvoicexml/implementation/jtapi/JtapiTelephonyFactory.java $
 * Version: $LastChangedRevision: 2355 $
 * Date:    $LastChangedDate: 2010-10-08 01:28:03 +0700 (Fri, 08 Oct 2010) $
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

package org.jvoicexml.implementation.mobicents;

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.Telephony;

/**
 * Demo implementation of a
 * {@link org.jvoicexml.implementation.ResourceFactory} for the
 * {@link Telephony} based on JTAPI 1.3.1.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2355 $
 * @since 0.6
 */
public final class MobicentsTelephonyFactory
    implements ResourceFactory<Telephony> {
    /** Number of instances that this factory will create. */
    private int instances;

    /** Type of resources that this factory will create. */
    private final String type;

    /**
     * Constructs a new object.
     */
    public MobicentsTelephonyFactory() {
      type = "mobicents";
    }

    /**
     * {@inheritDoc}
     */
    public Telephony createResource()
        throws NoresourceError {
        return new MobicentsTelephony();
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
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public Class<Telephony> getResourceType() {
        return Telephony.class;
    }
}
