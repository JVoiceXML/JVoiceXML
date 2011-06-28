/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/interpreter/tagstrategy/TestAssignStrategy.java $
 * Version: $LastChangedRevision: 2715 $
 * Date:    $Date: 2011-06-21 19:23:54 +0200 (Di, 21 Jun 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.xml.vxml.Assign;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.If;

/**
 * This class provides a test case for the {@link IfStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2715 $
 * @since 0.7.5
 */
public final class TestIfStrategy extends TagStrategyTestBase {
    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.IfStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            fest failed.
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testExecute() throws Exception, JVoiceXMLEvent {
        final ScriptingEngine scripting = getScriptingEngine();
        scripting.eval("var test = 'horst';");
        final Block block = createBlock();
        final If ifNode = block.appendChild(If.class);
        ifNode.setCond("test == 'horst'");
        final Assign assign = ifNode.appendChild(Assign.class);
        assign.setName("test");
        assign.setExpr("'fritz'");

        Assert.assertEquals("horst", scripting.getVariable("test"));
        final IfStrategy strategy = new IfStrategy();
        try {
            executeTagStrategy(ifNode, strategy);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals("fritz", scripting.getVariable("test"));
    }
}
