/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.text;

import org.jvoicexml.implementation.PlatformFactory;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.Telephony;

/**
 * Platform factory for the text based implementation platform.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TextPlatformFactory implements PlatformFactory {
    /** Number of instances that the factories will create. */
    private int instances;

    /**
     * {@inheritDoc}
     */
    public ResourceFactory<SpokenInput> getSpokeninput() {
        final TextSpokenInputFactory factory =
            new TextSpokenInputFactory();
        factory.setInstances(instances);
        return factory;
    }

    /**
     * {@inheritDoc}
     */
    public ResourceFactory<SynthesizedOutput> getSynthesizedoutput() {
        final TextSynthesizedOutputFactory factory =
            new TextSynthesizedOutputFactory();
        factory.setInstances(instances);
        return factory;
    }

    /**
     * {@inheritDoc}
     */
    public ResourceFactory<Telephony> getTelephony() {
        final TextTelephonyFactory factory = new TextTelephonyFactory();
        factory.setInstances(instances);
        return factory;
    }

    /**
     * Sets the number of instances that the factories will create.
     * @param number Number of instances to create.
     */
    public void setInstances(final int number) {
        instances = number;
    }
}
