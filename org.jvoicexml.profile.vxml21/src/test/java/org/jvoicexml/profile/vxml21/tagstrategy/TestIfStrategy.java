/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011-2021 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.profile.vxml21.tagstrategy;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.GenericVoiceXmlEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.profile.Profile;
import org.jvoicexml.profile.TagStrategyFactory;
import org.jvoicexml.xml.ccxml.Else;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.If;
import org.jvoicexml.xml.vxml.Throw;
import org.mockito.Mockito;

/**
 * This class provides a test case for the {@link IfStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.5
 */
public final class TestIfStrategy extends TagStrategyTestBase {
    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.IfStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                fest failed.
     * @exception JVoiceXMLEvent
     *                test failed
     */
    @Test
    public void testExecute() throws Exception, JVoiceXMLEvent {
        
        final Block block = createBlock();
        final If ifNode = block.appendChild(If.class);
        ifNode.setCond("test == 'horst'");
        final Throw throwTag = ifNode.appendChild(Throw.class);
        throwTag.setEvent("ifentered");

        final Profile profile = getContext().getProfile();
        final TagStrategyFactory tagFactory = profile.getTagStrategyFactory();
        Mockito.when(tagFactory.getTagStrategy(throwTag)).thenReturn(new ThrowStrategy());
        
        final DataModel model = getDataModel();
        Mockito.when(model.evaluateExpression(ifNode.getCond(), Boolean.class))
                .thenReturn(true);

        final IfStrategy strategy = new IfStrategy();
        JVoiceXMLEvent event = null;
        try {
            executeTagStrategy(ifNode, strategy);
        } catch (GenericVoiceXmlEvent e) {
            event = e;
        }
        Assert.assertNotNull(event);
        Assert.assertEquals(throwTag.getEvent(), event.getMessage());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.tagstrategy.IfStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * .
     * 
     * @exception Exception
     *                fest failed.
     * @exception JVoiceXMLEvent
     *                test failed
     */
    @Test
    public void testExecuteElse() throws Exception, JVoiceXMLEvent {
        
        final Block block = createBlock();
        final If ifNode = block.appendChild(If.class);
        ifNode.setCond("test == 'horst'");
        final Throw throwTag = ifNode.appendChild(Throw.class);
        throwTag.setEvent("ifentered");
        final Else elseTag = ifNode.appendChild(Else.class);
        final Throw throwElse = ifNode.appendChild(Throw.class);
        throwElse.setEvent("elseentered");
                
        final Profile profile = getContext().getProfile();
        final TagStrategyFactory tagFactory = profile.getTagStrategyFactory();
        Mockito.when(tagFactory.getTagStrategy(throwTag)).thenReturn(new ThrowStrategy());
        Mockito.when(tagFactory.getTagStrategy(throwElse)).thenReturn(new ThrowStrategy());
        
        final DataModel model = getDataModel();
        Mockito.when(model.evaluateExpression(ifNode.getCond(), Boolean.class))
                .thenReturn(false);

        final IfStrategy strategy = new IfStrategy();
        JVoiceXMLEvent event = null;
        try {
            executeTagStrategy(ifNode, strategy);
        } catch (GenericVoiceXmlEvent e) {
            event = e;
        }
        Assert.assertNotNull(event);
        Assert.assertEquals(throwElse.getEvent(), event.getMessage());
    }
}
