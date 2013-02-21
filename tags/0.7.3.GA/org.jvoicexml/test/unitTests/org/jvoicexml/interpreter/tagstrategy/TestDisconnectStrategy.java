/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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
 */

package org.jvoicexml.interpreter.tagstrategy;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Disconnect;

/**
 * This class provides a test case for the {@link DisconnectStrategy}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 */
public final class TestDisconnectStrategy extends TagStrategyTestBase {
    /**
     * Test method for {@link DisconnectStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testExecute() throws Exception, JVoiceXMLEvent {
        final Block block = createBlock();
        final Disconnect disconnect = block.appendChild(Disconnect.class);

        final VoiceXmlInterpreter interpreter = getInterpreter();

        Assert.assertFalse(interpreter.isInFinalProcessingState());

        final DisconnectStrategy strategy = new DisconnectStrategy();
        ConnectionDisconnectHangupEvent event = null;
        try {
            executeTagStrategy(disconnect, strategy);
        } catch (ConnectionDisconnectHangupEvent e) {
            event = e;
        }

        Assert.assertNotNull(event);
        Assert.assertTrue(interpreter.isInFinalProcessingState());
    }
}
