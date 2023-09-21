/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2023 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.junit.Test;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.ssml.Break;
import org.jvoicexml.xml.ssml.Emphasis;
import org.jvoicexml.xml.ssml.Mark;
import org.jvoicexml.xml.ssml.Phoneme;
import org.jvoicexml.xml.ssml.Prosody;
import org.jvoicexml.xml.ssml.SayAs;
import org.jvoicexml.xml.ssml.Sub;
import org.jvoicexml.xml.ssml.Voice;

/**
 * Test cases for {@link Foreach}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class ForeachTest {

    /**
     * Test case to append valid child nodes.
     * @throws Exception
     * @since 0.7.9
     */
    @Test
    public void testAppendChildClass() throws Exception {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Block block = form.appendChild(Block.class);
        final Foreach foreach = block.addChild(Foreach.class);
        Assert.assertNotNull(foreach.appendChild(Audio.class));
        Assert.assertNotNull(foreach.appendChild(Break.class));
        Assert.assertNotNull(foreach.appendChild(Enumerate.class));
        Assert.assertNotNull(foreach.appendChild(Value.class));
        Assert.assertNotNull(foreach.appendChild(Assign.class));
        Assert.assertNotNull(foreach.appendChild(Clear.class));
        Assert.assertNotNull(foreach.appendChild(Data.class));
        Assert.assertNotNull(foreach.appendChild(Disconnect.class));
        Assert.assertNotNull(foreach.appendChild(Emphasis.class));
        Assert.assertNotNull(foreach.appendChild(Exit.class));
        Assert.assertNotNull(foreach.appendChild(Foreach.class));
        Assert.assertNotNull(foreach.appendChild(Goto.class));
        Assert.assertNotNull(foreach.appendChild(If.class));
        Assert.assertNotNull(foreach.appendChild(Log.class));
        Assert.assertNotNull(foreach.appendChild(Mark.class));
        Assert.assertNotNull(foreach.appendChild(Metadata.class));
        Assert.assertNotNull(foreach.appendChild(Phoneme.class));
        Assert.assertNotNull(foreach.appendChild(Prompt.class));
        Assert.assertNotNull(foreach.appendChild(Prosody.class));
        Assert.assertNotNull(foreach.appendChild(Reprompt.class));
        Assert.assertNotNull(foreach.appendChild(Return.class));
        Assert.assertNotNull(foreach.appendChild(Script.class));
        Assert.assertNotNull(foreach.appendChild(Sub.class));
        Assert.assertNotNull(foreach.appendChild(Submit.class));
        Assert.assertNotNull(foreach.appendChild(Throw.class));
        Assert.assertNotNull(foreach.appendChild(Var.class));
        Assert.assertNotNull(foreach.appendChild(SayAs.class));
        Assert.assertNotNull(foreach.appendChild(Voice.class));
    }


    /**
     * Test case to append an invalid child node.
     * @throws Exception
     * @since 0.7.9
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAppendChildClassInvalid() throws Exception {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Block block = form.appendChild(Block.class);
        final Foreach foreach = block.addChild(Foreach.class);
        foreach.appendChild(Block.class);
    }
}
