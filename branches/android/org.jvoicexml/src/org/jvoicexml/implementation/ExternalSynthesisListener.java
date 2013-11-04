/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/implementation/ExternalSynthesisListener.java $
 * Version: $LastChangedRevision: 3839 $
 * Date:    $Date: 2013-07-17 09:37:34 +0200 (Wed, 17 Jul 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

/**
 * Listener for events from the Synthesizer to be used in external
 * applications, e.g. to perform facial animation in a VHML environment
 *
 * @author Renato Cassaca
 * @version $Revision: 3839 $
 * @see org.jvoicexml.UserInput
 * @since 0.6
 */
public interface ExternalSynthesisListener extends SynthesizedOutputListener {
    /**
     * Starts this external synthesized output listener.
     * 
     * @since 0.7.5
     */
    void start();
    
    /**
     * Stops this external synthesized output listener.
     * 
     * @since 0.7.5
     */
    void stop();
}
