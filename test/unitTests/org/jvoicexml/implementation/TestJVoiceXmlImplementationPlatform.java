/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision:  $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
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

import org.jvoicexml.RemoteClient;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.test.DummyRemoteClient;
import org.jvoicexml.test.implementationplatform.DummyAudioFileOutputFactory;
import org.jvoicexml.test.implementationplatform.DummySynthesizedOutputFactory;

import junit.framework.TestCase;

/**
 * Test cases for {@link KeyedResourcePool}.
 * @author Dirk Schnelle
 */
public final class TestJVoiceXmlImplementationPlatform extends TestCase {
    /** The test object. */
    private JVoiceXmlImplementationPlatform platform;

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();
        KeyedResourcePool<SynthesizedOutput> synthesizerPool =
            new KeyedResourcePool<SynthesizedOutput>();
        DummySynthesizedOutputFactory synthesizedOutputFactory =
            new DummySynthesizedOutputFactory();
        synthesizedOutputFactory.setInstances(1);
        synthesizerPool.addResourceFactory(synthesizedOutputFactory);
        KeyedResourcePool<AudioFileOutput> fileOutputPool =
            new KeyedResourcePool<AudioFileOutput>();
        DummyAudioFileOutputFactory audioFileOutputFactory =
            new DummyAudioFileOutputFactory();
        audioFileOutputFactory.setInstances(1);
        fileOutputPool.addResourceFactory(audioFileOutputFactory);
        RemoteClient client = new DummyRemoteClient();
        platform = new JVoiceXmlImplementationPlatform(null, synthesizerPool,
                fileOutputPool, null, client);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowSystemOutput()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    public void testBorrowSystemOutput() throws Exception, NoresourceError {
        final SystemOutput output1 = platform.borrowSystemOutput();
        assertNotNull(output1);
        platform.returnSystemOutput(output1);
        final SystemOutput output2 = platform.borrowSystemOutput();
        assertNotNull(output2);
        platform.returnSystemOutput(output2);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowSystemOutput()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    public void testBorrowSystemOutputNoresource()
        throws Exception, NoresourceError {
        final SystemOutput output1 = platform.borrowSystemOutput();
        assertNotNull(output1);
        NoresourceError error = null;
        try {
            platform.borrowSystemOutput();
        } catch (NoresourceError e) {
            error = e;
        }
        assertNotNull("second call should have failed", error);
        platform.returnSystemOutput(output1);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.JVoiceXmlImplementationPlatform#borrowSystemOutput()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    public void testBorrowSystemOutputDelayedReturn()
        throws Exception, NoresourceError {
        final SystemOutput output1 = platform.borrowSystemOutput();
        assertNotNull(output1);
        final Runnable runnable = new Runnable() {
            public void run() {
                SystemOutput output2 = null;
                try {
                    output2 = platform.borrowSystemOutput();
                } catch (NoresourceError e) {
                   fail(e.getMessage());
                }
                assertNotNull(output2);
                platform.returnSystemOutput(output2);
            }
        };
        final Thread thread = new Thread(runnable);
        thread.start();
        Thread.sleep(2000);
        platform.returnSystemOutput(output1);
    }
}
