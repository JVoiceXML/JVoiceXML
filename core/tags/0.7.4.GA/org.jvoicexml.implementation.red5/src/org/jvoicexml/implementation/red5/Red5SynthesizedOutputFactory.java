/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.implementation.jsapi20/src/org/jvoicexml/implementation/jsapi20/Jsapi20SynthesizedOutputFactory.java $
 * Version: $LastChangedRevision: 2355 $
 * Date:    $Date: 2010-10-07 20:28:03 +0200 (Do, 07 Okt 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date: 2010-10-07 20:28:03 +0200 (Do, 07 Okt 2010) $, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.implementation.red5;

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SynthesizedOutput;

/**
 * Demo implementation of a {@link org.jvoicexml.implementation.ResourceFactory}
 * for the {@link org.jvoicexml.implementation.SynthesizedOutput} based on
 * JSAPI 2.0.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2355 $
 * @since 0.5.5
 */
public final class Red5SynthesizedOutputFactory
        implements ResourceFactory<SynthesizedOutput> {
    /** Number of instances that this factory will create. */
    private int instances;

    /** Type of the created resources. */
    private String type;

    /**
     * Constructs a new object.
     * @param engineFactory class name of the engine list factory.
     */
    public Red5SynthesizedOutputFactory() {
        type = "red5";
    }

    /**
     * {@inheritDoc}
     */
    public SynthesizedOutput createResource() throws NoresourceError {
        final Red5SynthesizedOutput output = new Red5SynthesizedOutput();
        output.setType(type);
        return output;
    }

    /**
     * Sets the number of instances that this factory will create.
     *
     * @param number
     *                Number of instances to create.
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
     * Sets the type of the created resource.
     * @param typeName
     */
    void setType(final String typeName) {
        type = typeName;
    }

    /**
     * {@inheritDoc}
     */
    public Class<SynthesizedOutput> getResourceType() {
        return SynthesizedOutput.class;
    }
}
