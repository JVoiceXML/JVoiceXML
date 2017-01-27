/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/formitem/FieldShadowVarContainer.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager.mmi;

import java.util.UUID;

import org.junit.Test;
import org.jvoicexml.callmanager.mmi.mock.MockETLProtocolAdapter;
import org.jvoicexml.callmanager.mmi.xml.XmlExtensionNotificationDataConverter;
import org.jvoicexml.event.plain.implementation.QueueEmptyEvent;

public class MmiDetailedSessionListenerTest {

    @Test
    public void testSessionEventQueueEMpty() throws Exception {
        final MMIContext context = new MMIContext(UUID.randomUUID().toString());
        final XmlExtensionNotificationDataConverter converter = new XmlExtensionNotificationDataConverter();
        final MockETLProtocolAdapter adapter = new MockETLProtocolAdapter();
        final MmiDetailedSessionListener listener = new MmiDetailedSessionListener(
                adapter, context, converter);
        final QueueEmptyEvent empty = new QueueEmptyEvent(null, null);
        listener.sessionEvent(null, empty);
        System.out.println(adapter.getMmiAsString());
    }

}
