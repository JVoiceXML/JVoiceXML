/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/SystemOutput.java $
 * Version: $LastChangedRevision: 628 $
 * Date:    $Date: 2008-01-22 23:10:58 +0100 (Di, 22 Jan 2008) $
 * Author:  $LastChangedBy: lyncher $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * A provider for audio file output devices.
 *
 * @author Dirk Schnelle
 *
 * @since 0.6
 */
public interface AudioFileOutputProvider {
    /**
     * Retrieves the audio file output device.
     * @return the audio file output device.
     * @throws NoresourceError
     *         Error obtaining the device.
     */
    AudioFileOutput getAudioFileOutput() throws NoresourceError;
}
