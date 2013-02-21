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
package org.jvoicexml.xml.ssml;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test cases for {@link Prosody}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 */
public final class TestProsody {

    /**
     * Test method for {@link org.jvoicexml.xml.ssml.Prosody#setRate(float)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testSetRateFloat() throws Exception {
        final SsmlDocument document = new SsmlDocument();
        final Speak speak = document.getSpeak();
        final Prosody prosody = speak.appendChild(Prosody.class);
        final float rate = 73f;
        prosody.setRate(rate);
        Assert.assertEquals(rate + "%", prosody.getRate());
        Assert.assertEquals(rate, prosody.getRateFloat());
    }

}
