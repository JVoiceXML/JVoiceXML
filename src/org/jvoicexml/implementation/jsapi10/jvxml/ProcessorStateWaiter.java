/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/jsapi10/jvxml/RtpServer.java $
 * Version: $LastChangedRevision: 432 $
 * Date:    $LastChangedDate: 2007-09-05 15:57:34 +0200 (Mi, 05 Sep 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * Original copyright:
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * Large portions of this software are based upon public domain software
 * https://sip-communicator.dev.java.net/
 *
 */
package org.jvoicexml.implementation.jsapi10.jvxml;

import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Processor;

/**
 * A general purpose RTP server based on JMF.
 *
 * @author Dirk Schnelle
 * @author Emil Ivov (http://www.emcho.com)
 * @version $Revision: 432 $
 *
 * <p>
 * Based on the ProcessorUtility of the GJTAPI project.
 * </p>
 *
 * <p>
 * Copyright &copy; 2003 LSIIT laboratory (http://lsiit.u-strasbg.fr)
 * </p>
 * <p>
 * Copyright &copy; 2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.6
 */
final class ProcessorStateWaiter implements ControllerListener {
    /** The locking object. */
    private final Integer stateLock = new Integer(0);

    /** <code>true</code> if the processor has been closed. */
    private boolean failed;

    /**
     * Constructs a new object.
     */
    public ProcessorStateWaiter() {
    }

    /**
     * {@inheritDoc}
     */
    public void controllerUpdate(final ControllerEvent event) {
        // If there was an error during configure or
        // realize, the processor will be closed
        if (event instanceof ControllerClosedEvent) {
            failed = true;
        }
        // All controller events, send a notification
        // to the waiting thread in waitForState method.
        if (event instanceof ControllerEvent) {
            synchronized (stateLock) {
                stateLock.notifyAll();
            }
        }
    }

    /**
     * Waits until the given state is reached by the processor.
     * @param processor the processor
     * @param state the state to reach
     * @return <code>true</code> if the state was reached.
     */
    synchronized boolean waitForState(final Processor processor,
            final int state) {
        processor.addControllerListener(this);
        failed = false;
        // Call the required method on the processor
        // Wait until we get an event that confirms the
        // success of the method, or a failure event.
        while ((processor.getState() != state) && !failed) {
            synchronized (stateLock) {
                try {
                    stateLock.wait();
                } catch (InterruptedException ie) {
                    return false;
                }
            }
        }

        processor.removeControllerListener(this);

        return !failed;
    }
}
