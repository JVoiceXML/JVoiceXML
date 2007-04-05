/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/test/unitTests/org/jvoicexml/interpreter/tagstrategy/TestVarStrategy.java $
 * Version: $LastChangedRevision: 283 $
 * Date:    $Date: 2007-04-04 20:21:26 +0200 (Mi, 04 Apr 2007) $
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
 */

package org.jvoicexml.interpreter.tagstrategy;

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Disconnect;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * This class provides a test case for the {@link DisconnectStrategy}.
 *
 * @author Dirk Schnelle
 * @version $Revision: 283 $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestDisconnectStrategy extends TagStrategyTestBase {
    /**
     * Create the VoiceXML document.
     *
     * @return Created VoiceXML document, <code>null</code> if an error
     * occurs.
     */
    private Block createBlock() {
        final VoiceXmlDocument document = createDocument();

        final Vxml vxml = document.getVxml();
        final Form form = vxml.addChild(Form.class);
        return form.addChild(Block.class);
    }

    /**
     * Test method for {@link InDisconnectStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     */
    public void testExecute() throws Exception {
        final Block block = createBlock();
        final Disconnect disconnect = block.addChild(Disconnect.class);

        final VoiceXmlInterpreter interpreter = getInterpreter();

        assertFalse(interpreter.isInFinalProcessingState());

        final DisconnectStrategy strategy = new DisconnectStrategy();
        ConnectionDisconnectHangupEvent event = null;
        try {
            executeTagStrategy(disconnect, strategy);
        } catch (ConnectionDisconnectHangupEvent e) {
            event = e;
        } catch (JVoiceXMLEvent e) {
            fail(e.getMessage());
        }

        assertNotNull(event);
        assertTrue(interpreter.isInFinalProcessingState());
    }
}
