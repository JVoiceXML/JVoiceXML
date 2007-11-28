/*
 * File:    $RCSfile: SystemOutput.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

import org.jvoicexml.event.error.NoresourceError;

/**
 * An output device for synthesized or audio file output.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.6
 */
public interface OutputDevice {
    /**
     * Wait until the current output has ended.
     *
     * <p>
     * Prompts may have both synthesized and file output. This method is used
     * to keep the order in which they are played back.
     * </p>
     * @exception NoresourceError
     *            The output resource is not available.
     */
    void waitOutputEnd() throws NoresourceError;

    /**
     * Cancels the current output from the TTS engine and queued audio
     * for all entries in the queue that allow barge-in.
     *
     * <p>
     * The implementation has to maintain a list of cancelable outputs
     * depending on the <code>barge-in</code> flag.
     * </p>
     *
     * @exception NoresourceError
     *            The output resource is not available.
     *
     * @since 0.5
     */
    void cancelOutput() throws NoresourceError;
}
