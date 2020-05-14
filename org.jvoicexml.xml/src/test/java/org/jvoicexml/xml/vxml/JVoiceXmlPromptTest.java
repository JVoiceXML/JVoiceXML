/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2020 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.xml.vxml;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link JVoiceXmlPrompt}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class JVoiceXmlPromptTest {
    /** The test object. */
    private JVoiceXmlPrompt prompt;

    /**
     * Set up the test environment.
     * @throws Exception set up failed
     */
    @Before
    public void setUp() throws Exception {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Block block = form.appendChild(Block.class);
        prompt = block.appendChild(JVoiceXmlPrompt.class);
    }

    @Test
    public void testGetPriorityDefaultValue() {
       final String priority = prompt.getPriority();
       Assert.assertEquals(PriorityType.APPEND.toString(), priority);
    }

    
    @Test
    public void testSetPriorityString() {
       prompt.setPriority("append");
       Assert.assertEquals(PriorityType.APPEND.getPriority(), prompt.getPriority());
       prompt.setPriority("prepend");
       Assert.assertEquals(PriorityType.PREPEND.getPriority(), prompt.getPriority());
       prompt.setPriority("clear");
       Assert.assertEquals(PriorityType.CLEAR.getPriority(), prompt.getPriority());
    }

    @Test
    public void testSetPriorityPriorityType() {
        prompt.setPriority(PriorityType.APPEND);
        Assert.assertEquals(PriorityType.APPEND, prompt.getPriorityAsPriorityType());
        prompt.setPriority(PriorityType.PREPEND);
        Assert.assertEquals(PriorityType.PREPEND, prompt.getPriorityAsPriorityType());
        prompt.setPriority(PriorityType.CLEAR);
        Assert.assertEquals(PriorityType.CLEAR, prompt.getPriorityAsPriorityType());
    }

}
