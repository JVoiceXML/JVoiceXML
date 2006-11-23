/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
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

package org.jvoicexml.implementation.jsapi10.jvxml;

import org.jvoicexml.implementation.Platform;
import org.jvoicexml.implementation.PlatformFactory;

/**
 * Demo implementation of a
 * {@link org.jvoicexml.implementation.PlatformFactory}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @see org.jvoicexml.SystemOutput
 * @see org.jvoicexml.SpokenInput
 *
 * @since 0.5.1
 */
public final class JVoiceXmlPlatformFactory
        implements PlatformFactory {
    /** Number of instances that this factory will create. */
    private int instances;

    /** <code>true</code> if synthesis should be enabled in the platform. */
    private boolean enableSynthesis;

    /** <code>true</code> if recognition should be enabled in the platform. */
    private boolean enableRecognition;

    /**
     * Constructs a new object.
     */
    public JVoiceXmlPlatformFactory() {
    }

    /**
     * {@inheritDoc}
     */
    public Platform createPlatform() {
        final JVoiceXmlPlatform platform = new JVoiceXmlPlatform();

        platform.setOutput(enableSynthesis);
        platform.setInput(enableRecognition);

        return platform;
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
        return JVoiceXmlPlatform.TYPE;
    }

    /**
     * Toggle support of system output.
     * @param enable <code>true</code> if system output is enabled.
     */
    public void setOutput(final boolean enable) {
        enableSynthesis = enable;
    }

    /**
     * Toggle support of user input.
     * @param enable <code>true</code> if user input is enabled.
     */
    public void setInput(final boolean enable) {
        enableRecognition = enable;
    }
}
