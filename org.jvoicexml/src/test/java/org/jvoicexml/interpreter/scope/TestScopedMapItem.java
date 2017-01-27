/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 */

package org.jvoicexml.interpreter.scope;

import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for org.jvoicexml.interpreter.scope.ScopedMapItem.
 *
 * @see org.jvoicexml.interpreter.scope.ScopedMapItem
 *
 * @author Dirk Schnelle
 * @version $Revision$
 */
public final class TestScopedMapItem {
    /**
     * Test method for
     * 'ScopedMapItem.getScope().
     *
     * @see ScopedMapItem#getScope()
     */
    @Test
    public void testGetScope() {
        ScopedMapItem<String> stringItem1 =
                new ScopedMapItem<String>(Scope.ANONYMOUS, "test1");
        Assert.assertEquals(Scope.ANONYMOUS, stringItem1.getScope());

        ScopedMapItem<String> stringItem2 =
                new ScopedMapItem<String>(Scope.ANONYMOUS, null);
        Assert.assertEquals(Scope.ANONYMOUS, stringItem2.getScope());

        ScopedMapItem<String> stringItem3 =
                new ScopedMapItem<String>(null, "test3");
        Assert.assertEquals(null, stringItem3.getScope());

        ScopedMapItem<String> stringItem4 =
                new ScopedMapItem<String>(null, null);
        Assert.assertEquals(null, stringItem4.getScope());

        ScopedMapItem<Long> longItem1 =
                new ScopedMapItem<Long>(Scope.ANONYMOUS, new Long(1));
        Assert.assertEquals(Scope.ANONYMOUS, longItem1.getScope());

        ScopedMapItem<Long> longItem2 =
                new ScopedMapItem<Long>(Scope.DIALOG, new Long(1));
        Assert.assertEquals(Scope.DIALOG, longItem2.getScope());
    }

    /**
     * Test method for
     * 'ScopedMapItem.getValue().
     *
     * @see ScopedMapItem#getValue()
     */
    @Test
    public void testGetValue() {
        ScopedMapItem<String> stringItem1 =
                new ScopedMapItem<String>(Scope.ANONYMOUS, "test1");
        Assert.assertEquals("test1", stringItem1.getValue());

        ScopedMapItem<String> stringItem2 =
                new ScopedMapItem<String>(Scope.ANONYMOUS, null);
        Assert.assertEquals(null, stringItem2.getValue());

        ScopedMapItem<String> stringItem3 =
                new ScopedMapItem<String>(null, "test3");
        Assert.assertEquals("test3", stringItem3.getValue());

        ScopedMapItem<String> stringItem4 =
                new ScopedMapItem<String>(null, null);
        Assert.assertEquals(null, stringItem4.getValue());

        ScopedMapItem<Long> longItem1 =
                new ScopedMapItem<Long>(Scope.ANONYMOUS, new Long(1));
        Assert.assertEquals(new Long(1), longItem1.getValue());

        ScopedMapItem<Long> longItem2 =
                new ScopedMapItem<Long>(Scope.DIALOG, new Long(2));
        Assert.assertEquals(new Long(2), longItem2.getValue());
    }
}
