/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision:  $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.dialog;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import org.jvoicexml.xml.vxml.AbstractCatchElement;
import org.jvoicexml.xml.vxml.Catch;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Filled;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Help;
import org.jvoicexml.xml.vxml.Noinput;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * This class tests the {@link ExecutablePlainForm}.
 *
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.7
 */
public class TestExecutablePlainForm {
    /**
     * Testcase for {@link ExecutalePlainForm#getFilledElements()}.
     * @exception Exception test failed.
     */
    @Test
    public void testGetFilledElements() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Filled filled = form.appendChild(Filled.class);
        form.appendChild(Noinput.class);
        form.appendChild(Help.class);
        final Catch catchNode = form.appendChild(Catch.class);
        catchNode.setEvent("test");

	final ExecutablePlainForm dialog = new ExecutablePlainForm(form);
	final Collection<Filled> elements = dialog.getFilledElements();
	Assert.assertEquals(1, elements.size());
	final Filled element = elements.iterator().next();
    }

    /**
     * Testcase for {@link ExecutalePlainForm#getFilledElements()}.
     * @exception Exception test failed.
     */
    @Test
    public void testGetCatchElements() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        form.appendChild(Filled.class);
        final Noinput noinput = form.appendChild(Noinput.class);
        final Help help = form.appendChild(Help.class);
        final Catch catchNode = form.appendChild(Catch.class);
        catchNode.setEvent("test");

	final ExecutablePlainForm dialog = new ExecutablePlainForm(form);
	final Collection<AbstractCatchElement> elements =
	    dialog.getCatchElements();
	Assert.assertEquals(3, elements.size());
	for (AbstractCatchElement element : elements) {
	    String tag = element.getTagName();
            if (tag.equals(Noinput.TAG_NAME)) {
		Assert.assertTrue("expected to find noinput element",
				  element.isEqualNode(noinput));
	    } else if (tag.equals(Help.TAG_NAME)) {
		Assert.assertTrue("expected to find help element",
				  element.isEqualNode(help));
	    } else if (tag.equals(Catch.TAG_NAME)) {
		Assert.assertTrue("expected to find catch element",
				  element.isEqualNode(catchNode));
	    } else {
		Assert.fail("unknown tag: '" + tag + "'");
	    }
	}
    }
}
