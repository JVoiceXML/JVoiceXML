/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.test.implementation;

import java.util.List;

import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;

/**
 * {@link SynthesizedOutputListener} for test purposes.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class DummySynthesizedOutputListener
        implements SynthesizedOutputListener {
    /** Collected events. */
    private List<SynthesizedOutputEvent> occur;

    /**
     * Constructs a new object.
     */
    public DummySynthesizedOutputListener() {
        occur = new java.util.ArrayList<SynthesizedOutputEvent>();
    }

    /**
     * Retrieves the caught events.
     * @return caught events.
     */
    public List<SynthesizedOutputEvent> events() {
        return occur;
    }

    /**
     * Retrieves the number of caught events.
     * @return number of events.
     */
    public int size() {
        return occur.size();
    }

    /**
     * Retrieves the event at the given position.
     * @param index the position of the event to retrieve
     * @return event at the given position
     */
    public SynthesizedOutputEvent get(final int index) {
        return occur.get(index);
    }

    /**
     * {@inheritDoc}
     */
    public void outputStatusChanged(final SynthesizedOutputEvent event) {
        Object para = event.getParam();

        StringBuffer report = new StringBuffer();
        report.append("outputStatusChanged: ");
        if (para != null) {
            report.append(para.toString());
        }
        report.append(event.getEvent());
        System.out.println(report);
        occur.add(event);
    }

}
