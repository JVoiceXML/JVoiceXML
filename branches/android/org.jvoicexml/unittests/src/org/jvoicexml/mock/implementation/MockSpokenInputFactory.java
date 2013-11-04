/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/unittests/src/org/jvoicexml/mock/implementation/MockSpokenInputFactory.java $
 * Version: $LastChangedRevision: 3829 $
 * Date:    $LastChangedDate: 2013-07-16 13:01:00 +0200 (Tue, 16 Jul 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.mock.implementation;

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SpokenInput;

/**
 * Demo implementation of a {@link SpokenInput}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3829 $
 */
public final class MockSpokenInputFactory
    implements ResourceFactory<SpokenInput> {
    /** Number of instances that this factory will create. */
    private int instances;

    /**
     * Constructs a new object.
     */
    public MockSpokenInputFactory() {
    }

    /**
     * {@inheritDoc}
     */
    public SpokenInput createResource()
        throws NoresourceError {
        return new MockSpokenInput();
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
    public Class<SpokenInput> getResourceType() {
        return SpokenInput.class;
    }
}
