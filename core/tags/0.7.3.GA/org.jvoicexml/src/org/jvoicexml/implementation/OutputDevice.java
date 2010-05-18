/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * An output device for synthesized or audio file output.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2007-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.6
 */
public interface OutputDevice {
    /**
     * Checks if this device is busy with output.
     * @return <code>true</code> if the device is busy.
     */
    boolean isBusy();

    /**
     * Checks if this implementation platform supports barge-in.
     * @return <code>true</code> if barge-in is supported.
     * @since 0.7.1
     */
    boolean supportsBargeIn();

    /**
     * Cancels the current output from the TTS engine and queued audio
     * for all entries in the queue that allow barge-in. Does nothing if the
     * current output can not be interrupted using barge-in.
     *
     * <p>
     * This method is only called if {@link #supportsBargeIn()} returns
     * <code>true</code>.
     * </p>
     *
     * <p>
     * The implementation has to maintain a list of cancelable outputs
     * depending on the <code>barge-in</code> flag of
     * {@link org.jvoicexml.xml.Prompt}.
     * </p>
     *
     * @exception NoresourceError
     *            The output resource is not available.
     *
     * @since 0.5
     */
    void cancelOutput() throws NoresourceError;
}
