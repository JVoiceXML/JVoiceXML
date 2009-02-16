/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi20;

import java.util.Collection;
import java.util.Iterator;

import javax.speech.SpeechEventExecutor;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link SynchronousSpeechEventExecutor}.
 * @author Dirk Schnelle-Walka
 *
 */
public final class TestSynchronousSpeechEventExecutor {
    /** The test object. */
    private SpeechEventExecutor executor;

    /**
     * Setup the test environment.
     * @throws java.lang.Exception
     *         setup failed
     */
    @Before
    public void setUp() throws Exception {
        executor = new SynchronousSpeechEventExecutor();
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi20.SynchronousSpeechEventExecutor#execute(java.lang.Runnable)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testExecute() throws Exception {
        final Collection<Integer> list = new java.util.ArrayList<Integer>();
        final Runnable runnable1 = new Runnable() {
            public void run() {
                list.add(new Integer(1));
            }
        };
        final Runnable runnable2 = new Runnable() {
            public void run() {
                list.add(new Integer(2));
            }
        };
        executor.execute(runnable1);
        executor.execute(runnable2);
        Assert.assertEquals(2, list.size());
        final Iterator<Integer> iterator = list.iterator();
        Assert.assertEquals(new Integer(1), iterator.next());
        Assert.assertEquals(new Integer(2), iterator.next());
    }

}
