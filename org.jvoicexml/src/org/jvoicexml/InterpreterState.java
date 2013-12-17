/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.voicexmlunit/unittests/src/org/jvoicexml/voicexmlunit/TestCall.java $
 * Version: $LastChangedRevision: 4071 $
 * Date:    $Date: 2013-12-16 09:46:03 +0100 (Mon, 16 Dec 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

/**
 * States of JVoiceXML that are passed throughout the lifecycle.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public enum InterpreterState {
    /**
     * The interpreter started.
     */
    STARTED,

    /**
     * The interpreter is allocating resources and prepares to run.
     */
    ALLOCATING_RESOURCES,

    /**
     * The interpreter is running and able to process calls.
     */
    RUNNING,

    /**
     * The interpreter is shutting down. Therefore, all resources are
     * deallocated. Calls can no longer be processed.
     */
    DEALLOCATING_RESOURCES,

    /**
     * The interpreter stopped.
     */
    STOPPED
}
